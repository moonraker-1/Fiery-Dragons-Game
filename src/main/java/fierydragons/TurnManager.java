package fierydragons;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class TurnManager  {

    private static Integer move = 1;
    private static Integer turn = 0;



    //private static Player currPlayer; // UNUSED

    private static Integer currPlayerIndex;

    //turn timer
    private static Timeline turnTimer;
    //turn timer label
    private static Label timerLabel = new Label();
    private static int remainingTime = 20; //remaining time on turn timer

    public static Integer getMove() {
        return move;
    }


    public static Integer getTurn() {
        return turn;
    }

    // UNUSED
//    public static Player getCurrPlayer() {
//        return currPlayer;
//    }
//
//    public static void setCurrPlayer(Player player) {
//        currPlayer = player;
//    }

//    public static Integer getCurrPlayerIndex() {
//        return currPlayerIndex;
//    }


    public static void setCurrPlayerIndex(Integer index) {
        currPlayerIndex = index;
    }

    public static void incrementMove() {
        move++;
    }
    public static void resetMove() {
        move = 0;
    }

    public static void incrementTurn() {
        turn++;
    }

    public static void resetTurn() {
        turn = 0;
    }

    public static void initializeTimerLabel(Pane root) {
        // Initialize the timer label
        timerLabel = new Label("20");
        timerLabel.setStyle("-fx-font-size: 20; -fx-text-fill: red;");
        timerLabel.setLayoutX(10);  // Position X (adjust as needed)
        timerLabel.setLayoutY(10);  // Position Y (adjust as needed)
        root.getChildren().add(timerLabel);
    }
    public static void startTurnTimer() {
        // Stop any existing timer
        if (turnTimer != null) {
            turnTimer.stop();
        }

        // Reset the timer label text
        timerLabel.setText("20");
        remainingTime = 20;

        // Create a new Timeline for 20 seconds
        turnTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            timerLabel.setText(Integer.toString(remainingTime));
            if (remainingTime <= 0) {
                turnTimer.stop();
                System.out.println("Timer expired, ending turn.");
                Platform.runLater(TurnManager::changeTurn); // Ensure changeTurn is called correctly
            }
        }));

        turnTimer.setCycleCount(Timeline.INDEFINITE);
        turnTimer.playFromStart();
    }

    public static void resetTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
        startTurnTimer();
    }

    public static void pauseTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
            System.out.println("Timer paused with " + remainingTime + " seconds remaining.");
        }
    }

    public static void resumeTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }

        turnTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            timerLabel.setText(Integer.toString(remainingTime));
            if (remainingTime <= 0) {
                turnTimer.stop();
                System.out.println("Timer expired, ending turn.");
                Platform.runLater(TurnManager::changeTurn); // Ensure changeTurn is called correctly
            }
        }));

        turnTimer.setCycleCount(remainingTime);
        turnTimer.playFromStart();
    }

    public static void playMove(ChitCard chitCard) {

        incrementMove();

        Board board = Board.getInstance();
        Player currPlayer = board.getPlayers().get(currPlayerIndex);
        DragonToken token = currPlayer.getToken();

        List<VolcanoCard> cutVolcanoCards = board.getVolcanoArray(true);
        List<VolcanoCard> uncutVolcanoCards = board.getVolcanoArray(false);

        Integer totalSteps = cutVolcanoCards.size() * cutVolcanoCards.get(0).getSquares().size() + uncutVolcanoCards.size() * uncutVolcanoCards.get(0).getSquares().size() + 2;

        Integer steps = chitCard.getCount();

        Position currPosition = token.getPosition();
        Position nextPosition = currPosition;

        Boolean forwardMovement = false;


        // Backward movement
        // If the chitcard is a dragon pirate and the token is not standing on its cave
         if (chitCard.getCreature() == CreatureType.DRAGON_PIRATE && currPosition != token.getStartingCave() ) {

             nextPosition = lookAhead(token, steps, totalSteps, currPosition, nextPosition, false);

        }

         // If the chit card is Devil
         else if (chitCard.getCreature() == CreatureType.DEVIL && currPosition != token.getStartingCave() ) {
             Boolean flag = false;

             while (flag == false) {


             //while (!(nextPosition instanceof Cave)) {
                 steps++;

                 //Move to cave
                 if ((nextPosition.getPrevPos().size() > 1) && nextPosition.getPrevPos().get(1).isOccupied() == false) {


                     //Set the next position to the cave
                     nextPosition = nextPosition.getPrevPos().get(1);
                     flag = true;
                     break;

                 }
                 else {

                     //Set the next position to the next volcano square
                     nextPosition = nextPosition.getPrevPos().get(0);
                 }

                 //if (nextPosition.isOccupied()) {

                     //changeTurn();
                     //return;
                 //}
             }
         }

         // Forward movement
         // if the chitcard is not a dragon pirate and the chitcard's creature matches the position's creature
         else if (chitCard.getCreature() != CreatureType.DRAGON_PIRATE && chitCard.getCreature() == currPosition.getCreature()) {

             forwardMovement = true;

             nextPosition = lookAhead(token, steps, totalSteps, currPosition, nextPosition, true);


         }
         // Non-matching creature
         else {
             changeTurn();
             return;
         }


        // If the next position is occupied or will move past cave, do not move
        if (nextPosition.isOccupied() || token.getForwardStepsTaken() + steps > totalSteps ) {

            changeTurn();
            return;
        }
        // If the token can move
        else {

            // Move the token's position
            moveToken(token, nextPosition);

            // Update the token's steps
            if (forwardMovement) {
                token.addSteps(steps);
            } else {
                token.addSteps(steps * -1);


                // End the turn if the token moved backwards
                if (chitCard.getCreature() != CreatureType.DEVIL){
                    changeTurn();
                    return;
                }


            }

            // Update chit card turn
            if (chitCard.getCreature() == CreatureType.DRAGON_PIRATE) {
                changeTurn();
                return;
            }


            // Victory check
            // If the token is at its starting cave and it has moved a whole lap around the board
            if (token.getPosition() == token.getStartingCave() && Objects.equals(token.getForwardStepsTaken(), totalSteps)) {
                Display.showVictory(currPlayerIndex+1);
                resetTurn(); // Reset turn to 0 for a new game
            } else {
                //reset timer after valid move
                resetTurnTimer();
            }
        }

    }

    public static void changeTurn() {

        // Needed to create a pause between chit card flip and reset
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("This is a pop-up dialog");
        alert.setContentText("Ending turn.");
        alert.setX(580);
        alert.setY(10);

        alert.showAndWait();

        Platform.runLater(TurnManager::completeTurnChange);

//        // Show the dialog
//        Platform.runLater(alert::showAndWait);
//
//        // Reset the chit cards
//        Board.getInstance().resetChitCards();
//
//        switchCurrentPlayer();
//        resetMove();
//        incrementTurn();
//
//        //start timer
//        startTurnTimer();
    }

    private static void completeTurnChange() {
        // Reset the chit cards
        Board.getInstance().resetChitCards();

        switchCurrentPlayer();
        resetMove();
        incrementTurn();

        // Start the timer for the next player's turn
        startTurnTimer();
    }

    public static void switchCurrentPlayer() {
        if (currPlayerIndex == Board.getInstance().getPlayers().size() - 1) {
           currPlayerIndex = 0;
        } else {
            currPlayerIndex++;
        }
        Display.updateCurrentPlayer(currPlayerIndex+1);
    }

    public static void moveToken(DragonToken token, Position nextPosition) {
        token.getPosition().setOccupied(false);
        token.setPosition(nextPosition);
        Display.moveToken(token, nextPosition);
        token.getPosition().setOccupied(true);
    }


    public static Position lookAhead(DragonToken token, Integer steps, Integer totalSteps, Position currPosition, Position nextPosition, Boolean forward) {

        if (forward) {
            for (int k = 0; k < steps; k++) {

                // if the option to move to a cave exists and the token did not start on a cave or come from a cave this turn,
                // and the available cave is the token's starting cave and the token has done a full lap
                if ((nextPosition.getNextPos().size() > 1 && nextPosition != token.getStartingCave() && currPosition != token.getStartingCave())
                        && nextPosition.getNextPos().get(1) == token.getStartingCave() && (token.getForwardStepsTaken() + steps == totalSteps)) {

                    // Set the next position to the cave
                    nextPosition = nextPosition.getNextPos().get(1);

                    break;

                }
                else {
                    // Set the next position to the next volcano square
                    nextPosition = nextPosition.getNextPos().get(0);
                }

            }
        }
        else {
            for (int k = 0; k < steps; k++) {
                nextPosition =  nextPosition.getPrevPos().get(0);
            }
        }

        return nextPosition;


    }

    public static void setMove(Integer move) {
        TurnManager.move = move;
    }

    public static void setTurn(Integer turn) {
        TurnManager.turn = turn;
    }

    public static String save() {

        return "turnManager=" +
                turn +
                ";" +
                move +
                ";" +
                currPlayerIndex
                + ";\n";
    }




}
