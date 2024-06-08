package fierydragons;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Game extends Application {

    private Pane root;


    @Override
    public void start(Stage stage) {

        Config.readConfig();

        root = new Pane();
        // Load the background image
        Image backgroundImage = new Image("orange.jpg");

        // Set the background image
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        root.setBackground(new Background(background));
//        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));


        Scene scene = new Scene(root, Double.parseDouble(Config.getConfig("game.windowWidth")), Double.parseDouble(Config.getConfig("game.windowHeight")));
        stage.setScene(scene);
        stage.setTitle("Fiery Dragons");
        stage.show();



        Board board = Board.getInstance();

        board.initChitCards(Integer.valueOf(Config.getConfig("game.maxSteps")),
                Integer.valueOf(Config.getConfig("game.piratesPerStep")),
                Integer.valueOf(Config.getConfig("game.maxPirateSteps"))
                );

        board.initPlayers(Integer.valueOf(Config.getConfig("game.numOfPlayers")));

        board.initCreatures(Config.getConfig("game.creatures").split(","));

        Integer iterator = 1;
        while (Config.getConfig("game.cutVolcanoCard" + iterator) != null) {
            board.addToVolcano(Config.getConfig("game.cutVolcanoCard" + iterator), true);
            iterator++;
        }

        iterator = 1;
        while (Config.getConfig("game.uncutVolcanoCard" + iterator) != null) {
            board.addToVolcano(Config.getConfig("game.uncutVolcanoCard" + iterator), false);
            iterator++;
        }

        board.setup(true);

        Display.drawBoard(root, board);

        Display.drawUI(root, board);

        // Initialize and add the timer label to the root pane
        TurnManager.initializeTimerLabel(root);

        // Start the turn timer for the first player
        TurnManager.startTurnTimer();
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static void saveGame(Board board) {

        System.out.println("Save game");

        // new string builder
        StringBuilder saveString = new StringBuilder();

        //saveString.append("testing TEESTING");

        String boardString = board.save();
        saveString.append(boardString);

       // saveString.append("|");

        //String displayString = Display.save();
        //saveString.append(displayString);

        String turnString = TurnManager.save();
        saveString.append(turnString);

        String dareString = DareFeature.save();
        saveString.append(dareString);


        // create new file called save.txt
        Path path = Paths.get("save.txt");

        // write to new file
        try {
            // Delete the file if it exists
            Files.deleteIfExists(path);

            // Write to the file, creating a new file if it doesn't exist
            Files.write(path, (saveString + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE);
            System.out.println("Successfully wrote to the file.");
            System.out.println(saveString);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // call save() from each object, builds string. Add comma between objects (save() calls)
        // convert string builder to string

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("This is a pop-up dialog");
        alert.setContentText("Game saved.");
        alert.setX(580);
        alert.setY(10);

        // Show the dialog
        alert.showAndWait();

    }

    public static void loadGame(Pane root, Board board) {

        System.out.println("Load game");

        String fileName = "save.txt";
        Map<String, String[]> map = new HashMap<>();

        Path path = Paths.get(fileName);

        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                // Split each line by the key-value delimiter
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String[] values = parts[1].trim().split(";");
                    map.put(key, values);
                } else {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Print the map to verify its contents
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " => " + Arrays.toString(entry.getValue()));
        }
        // process string


        board.reset();


        // Change config to read from save file
        board.initChitCards(Integer.valueOf(map.get("maxSteps")[0]),
                Integer.valueOf(map.get("piratesPerStep")[0]),
                Integer.valueOf(map.get("maxPirateSteps")[0])
        );

        board.initPlayers(map.get("players").length);

        board.initCreatures(Config.getConfig("game.creatures").split(",")); ///////////




        Integer iterator;

        String[] cutVolcanoCards = map.get("cutVolcanoCards");
        if (cutVolcanoCards != null) {
            for (iterator = 0; iterator < cutVolcanoCards.length; iterator++) {
                if (cutVolcanoCards[iterator] != null) {
                    board.addToVolcano(cutVolcanoCards[iterator], true); //.replace(".", ",")
                }
            }
        }


        String[] uncutVolcanoCards = map.get("uncutVolcanoCards");
        if (uncutVolcanoCards != null) {
            for (iterator = 0; iterator < uncutVolcanoCards.length; iterator++) {
                if (uncutVolcanoCards[iterator] != null) {
                    board.addToVolcano(uncutVolcanoCards[iterator], false); //.replace(".", ",")
                }
            }
        }


        String[] chitCards = map.get("chitCards");
        if (chitCards != null) {
            for (iterator = 0; iterator < chitCards.length; iterator++) {
                if (chitCards[iterator] != null) {
                    board.addToChitCards(chitCards[iterator]);
                    //board.addToVolcano(cutVolcanoCards[iterator], true); //.replace(".", ",")
                }
            }
        }

        String[] caves = map.get("caves");
        if (caves != null) {
            for (iterator = 0; iterator < caves.length; iterator++) {
                if (caves[iterator] != null) {
                    board.addToCaves(caves[iterator]);
                    //board.addToVolcano(cutVolcanoCards[iterator], true); //.replace(".", ",")
                }
            }
        }



        board.setup(false);



        Display.clear(root);

        Display.drawBoard(root, board);

        Display.drawUI(root, board);


        String[] daredPlayers = map.get("daredPlayers");

        if (daredPlayers != null) {

            if (Integer.parseInt(daredPlayers[0]) >= 0) {
                System.out.println(Arrays.toString(daredPlayers));

                for (int i = 0; i < daredPlayers.length; i++) {
                    DareFeature.disableFromPlayer(Integer.parseInt(daredPlayers[i]));
                }
            }
        }





        List<VolcanoCard> cutVolcanoCardsArray = board.getVolcanoArray(true);
        List<VolcanoCard> uncutVolcanoCardsArray = board.getVolcanoArray(false);
        Integer totalSteps = cutVolcanoCardsArray.size() * cutVolcanoCardsArray.get(0).getSquares().size() + uncutVolcanoCardsArray.size() * uncutVolcanoCardsArray.get(0).getSquares().size() + 2;


        DragonToken token;
        Position currPosition, nextPosition;
        Integer forwardSteps;
        Integer backwardSteps;
        //Integer positionId;



        // for each token
        for (int i = 0; i < board.getTokens().size(); i++) {
            token = board.getTokens().get(i);
            forwardSteps = Integer.valueOf(map.get("tokens")[i].split(",")[0]);
            backwardSteps = Integer.valueOf(map.get("tokens")[i].split(",")[1]);

            currPosition = token.getPosition();
            nextPosition = currPosition;
            nextPosition = TurnManager.lookAhead(token, forwardSteps, totalSteps, currPosition, nextPosition, true);
            TurnManager.moveToken(token, nextPosition);
            token.addSteps(forwardSteps);

            if (backwardSteps < 0) {
                currPosition = token.getPosition();
                nextPosition = currPosition;
                nextPosition = TurnManager.lookAhead(token, -1*backwardSteps, totalSteps, currPosition, nextPosition, false);
                TurnManager.moveToken(token, nextPosition);
                token.addSteps(backwardSteps);

            }
        }




        TurnManager.setTurn(Integer.valueOf(map.get("turnManager")[0]));
        TurnManager.setMove(Integer.valueOf(map.get("turnManager")[1]));
        TurnManager.setCurrPlayerIndex(Integer.valueOf(map.get("turnManager")[2])); // turn info


        Display.updateCurrentPlayer(Integer.parseInt(map.get("turnManager")[2]) + 1); //turn info


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("This is a pop-up dialog");
        alert.setContentText("Game loaded.");
        alert.setX(580);
        alert.setY(10);

        // Show the dialog
        alert.showAndWait();

    }

    // Start over method
    public void startOver() {

        Board board = Board.getInstance();

        board.reset();

        board.setup(false);

        Display.clear(root);

        Display.drawBoard(root, board);

        Display.drawUI(root, board);
    }

}
