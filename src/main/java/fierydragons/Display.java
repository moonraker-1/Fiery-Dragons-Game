
package fierydragons;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Display  {

    // Volcano (board) radius
    private static final Integer volcanoRadius = Integer.valueOf(Config.getConfig("game.volcanoRadius"));

    // Board position on screen
    private static final Integer boardOffsetX = 750;
    private static final Integer boardOffsetY = 360;


    private static String imagesDir = Config.getConfig("game.images.directory"); // UNUSED

    private static String dragonImagesDir = "dragon"; // For retrieving the set of dragon token sprites

    private static String FILE_EXT = ".png";

    //for current player label
    private static Label playerLabel = new Label();
    private static IntegerProperty currentPlayer = new SimpleIntegerProperty(1);

    private static Button dareButton = new Button();
    private static boolean isDareOpen = false;

    // Add the DareButton on the screen at the beginning
    public static void addDareButton(Pane root, Board board) {

        // Set the DropShadow effect on the button
        dareButton.setEffect(new DropShadow(null, Color.GOLDENROD, 20, 0.5, 0, 0));
        dareButton.setMinWidth(150);
        dareButton.setLayoutX(boardOffsetX - 675);
        dareButton.setLayoutY(boardOffsetY - 140);

        dareButton.setBackground(new Background(new BackgroundFill(Color.GOLD, null,null)));
        dareButton.setText("Dare Luck");
        dareButton.setTextFill(Color.DARKRED);
        dareButton.setFont(new Font("Verdana", 20));

        root.getChildren().add(dareButton);
        dareButton.setOnAction(event->dareButtonClick(root, board));
    }

    public static void addSaveButton(Pane root, Board board) {

        System.out.println("Save button added");

        Button btn = new Button("Save Game");

        btn.setLayoutX(boardOffsetX - 675);
        btn.setLayoutY(boardOffsetY - 60);
        btn.setMinWidth(150);

        btn.setBackground(new Background(new BackgroundFill(Color.SEAGREEN, null,null)));
        btn.setEffect(new DropShadow(null, Color.GREEN, 10, 0.1, 0, 0));
        btn.setFont(new Font("Verdana", 20));

        btn.setOnAction(arg0 -> {
            System.out.println("Save clicked");
            Game.saveGame(board);

        });

        root.getChildren().add(btn);
    }

    public static void addLoadButton(Pane root, Board board) {

        System.out.println("Load button added");

        Button btn = new Button("Load Game");
        btn.setLayoutX(boardOffsetX - 675);
        btn.setLayoutY(boardOffsetY - 10);
        btn.setMinWidth(150);
        btn.setBackground(new Background(new BackgroundFill(Color.DIMGREY, null,null)));
        btn.setEffect(new DropShadow(null, Color.DARKGREY, 10, 0.3, 0, 0));
        btn.setFont(new Font("Verdana", 20));

        btn.setOnAction(arg0 -> {
            System.out.println("Load clicked");
            Game.loadGame(root, board);

        });

        root.getChildren().add(btn);
    }

    // Draws the UI on the screen
    public static void drawUI(Pane root, Board board) {

        initializeGameLabel(root);

        initializePlayerLabel(root);

        System.out.println("Draw UI");

        drawActivitySquare(root);

        addDareButton(root, board);

        addSaveButton(root, board);

        addLoadButton(root, board);

    }

    // Draws the board on the screen
    public static void drawBoard(Pane root, Board board) {

        drawVolcano(root, board);

        addTokens(root, board);

        drawChitCards(root, board);



    }


    // Adds the dragon tokens to the screen
    public static void addTokens(Pane root, Board board) {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Retrieving the image for each dragon token and add it to the screen
        for (int i = 0; i < board.getTokens().size(); i++) {
            DragonToken dragonToken = board.getTokens().get(i);
            Cave cave = board.getCaveArray().get(i);

            Image image = null;
            try {

                InputStream inputStream =
                        classloader.getResourceAsStream(dragonImagesDir + i + FILE_EXT);

                if (inputStream == null) {
                    System.err.println("Failed to read inputStream: "+dragonImagesDir + i + FILE_EXT);
                }
                image = new Image(inputStream);

                ImageView imageView = new ImageView(image);
                imageView.setScaleX(0.55);
                imageView.setScaleY(0.55);
                Integer imageOffsetX = 45;
                Integer imageOffsetY = 45;

                imageView.setLayoutX(cave.getNodes().get(0).getLayoutX() - imageOffsetX);
                imageView.setLayoutY(cave.getNodes().get(0).getLayoutY() - imageOffsetY);

                root.getChildren().add(imageView);

                dragonToken.setPosition(cave);

                dragonToken.addNode(imageView);

                inputStream.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    // Draws the volcano on the screen
    public static void drawVolcano(Pane root, Board board) {

        List<Cave> caves = board.getCaveArray();
        Integer caveCount = 0;
        List<VolcanoCard> cutVolcanoCards = board.getVolcanoArray(true);
        List<VolcanoCard> uncutVolcanoCards = board.getVolcanoArray(false);



        // Ensure we do not access out-of-bounds indexes
        int minCutVolcanoSize = Math.min(caves.size(), cutVolcanoCards.size());

        // The interval at which the cut and uncut volcano cards together are spread around the board
        Double volcanoCardInterval = (double) (360 / (cutVolcanoCards.size()));

        // Drawing the volcano cards and squares on the screen
        // For each cut volcano card
        for (int i = 0; i < minCutVolcanoSize; i++) {


            // The interval at which the cut and uncut volcano cards separately are spread around the board
            Double volcanoSquareInterval = (double) (360 / (2 * cutVolcanoCards.size()));

            // Positioning the squares on angles offset from the center of the volcano card
            Double angle = i * (volcanoCardInterval) + volcanoSquareInterval;
            Integer volcanoCardSize = cutVolcanoCards.get(0).getSquares().size();
            Double squareInterval = volcanoSquareInterval / volcanoCardSize;
            Double startAngle = angle - squareInterval * (volcanoCardSize / 2);

            Double currAngle;

            Double xCoord;
            Double yCoord;

            Rectangle square;
            String squareSpritePath;
            Image squareImage;

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            // For each square in the volcano card
            for (int j = 0; j < volcanoCardSize; j++) {

                square = new Rectangle();

                currAngle = startAngle + squareInterval * j;
                xCoord = Coordinate.toX(volcanoRadius, currAngle) + boardOffsetX;
                yCoord = Coordinate.toY(volcanoRadius, currAngle) + boardOffsetY;

                square.setFill(Color.MAROON); //was SANDYBROWN
                //square.setEffect(new Lighting());
                square.setLayoutX(xCoord);
                square.setLayoutY(yCoord);
                square.setWidth(squareInterval * 3);
                square.setHeight(squareInterval * 3);
                square.setRotate(currAngle);
                root.getChildren().add(square);

                // Attaching the square image to the volcano square
                cutVolcanoCards.get(i).getSquare(j).addNode(square);

                // Adding the creature sprite to the square
                squareSpritePath = cutVolcanoCards.get(i).getSquare(j).getCreature().getSpritePath();
                squareImage = null;
                try {
                    InputStream squareInputStream = classloader.getResourceAsStream(squareSpritePath);

                    if (squareInputStream == null) {
                        System.err.println("Failed to read squareInputStream: " + squareSpritePath);
                    } else {
                        squareImage = new Image(squareInputStream);

                        ImageView squareImageView = new ImageView(squareImage);

                        squareImageView.setScaleX(0.35);
                        squareImageView.setScaleY(0.35);

                        Integer squareImageOffsetX = 5;
                        Integer squareImageOffsetY = 5;

                        squareImageView.setLayoutX(xCoord - squareImageOffsetX);
                        squareImageView.setLayoutY(yCoord - squareImageOffsetY);
                        root.getChildren().add(squareImageView);

                        squareInputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Adding a cave to the screen with each cut volcano card
                if (caveCount < caves.size() && j == cutVolcanoCards.get(i).getCaveSquareIndex()) {
                    Cave cave = caves.get(caveCount);
                    String caveSpritePath = cave.getCreature().getSpritePath();

                    Image caveImage = null;
                    try {
                        InputStream caveInputStream = classloader.getResourceAsStream(caveSpritePath);

                        if (caveInputStream == null) {
                            System.err.println("Failed to read caveInputStream: " + caveSpritePath);
                        } else {
                            caveImage = new Image(caveInputStream);

                            ImageView caveImageView = new ImageView(caveImage);
                            caveImageView.setScaleX(0.45);
                            caveImageView.setScaleY(0.45);

                    caveCount++;
                    Circle circle = new Circle();
                    circle.setFill(Color.FIREBRICK); //was ROSYBROWN
                    circle.setEffect(new Lighting());
                    circle.setRadius(squareInterval * 2.5);

                            Integer caveRadiusOffset = 90;
                            Integer caveOffsetX = 25;
                            Integer caveOffsetY = 25;
                            xCoord = Coordinate.toX(volcanoRadius + caveRadiusOffset, currAngle) + boardOffsetX + caveOffsetX; // angle
                            yCoord = Coordinate.toY(volcanoRadius + caveRadiusOffset, currAngle) + boardOffsetY + caveOffsetY; // angle

                            Integer imageOffsetX = 20;
                            Integer imageOffsetY = 20;

                            circle.setLayoutX(xCoord);
                            circle.setLayoutY(yCoord);
                            caveImageView.setLayoutX(xCoord - imageOffsetX);
                            caveImageView.setLayoutY(yCoord - imageOffsetY);

                            root.getChildren().add(circle);
                            root.getChildren().add(caveImageView);
                            cave.addNode(circle);
                            cutVolcanoCards.get(i).setCaveJoin(cave);

                            caveInputStream.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("caveCount (" + caveCount + ") exceeds caves size (" + caves.size() + ")");
                }
            }
        }

        // Repeating the process for each uncut volcano card
        for (int i = 0; i < uncutVolcanoCards.size(); i++) {

            Double volcanoSquareInterval = (double) (360 / (2 * uncutVolcanoCards.size()));
            Double angle = i * (volcanoCardInterval);
            Integer volcanoCardSize = uncutVolcanoCards.get(0).getSquares().size();
            Double squareInterval = volcanoSquareInterval / volcanoCardSize;
            Double startAngle = angle - squareInterval * (volcanoCardSize / 2);
            Double currAngle;

            Double xCoord;
            Double yCoord;

            Rectangle square;
            String squareSpritePath;
            Image squareImage;

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            for (int j = 0; j < volcanoCardSize; j++) {

                square = new Rectangle();

                currAngle = startAngle + squareInterval * j;
                xCoord = Coordinate.toX(volcanoRadius, currAngle) + boardOffsetX;
                yCoord = Coordinate.toY(volcanoRadius, currAngle) + boardOffsetY;

                square.setFill(Color.SADDLEBROWN); // WAS SANDYBROWN
                square.setEffect(new Lighting());

                square.setLayoutX(xCoord);
                square.setLayoutY(yCoord);
                square.setWidth(squareInterval * 3);
                square.setHeight(squareInterval * 3);
                square.setRotate(currAngle);
                root.getChildren().add(square);

                // Attaching the square image to the volcano square
                uncutVolcanoCards.get(i).getSquare(j).addNode(square);

                squareSpritePath = uncutVolcanoCards.get(i).getSquare(j).getCreature().getSpritePath();

                squareImage = null;
                try {
                    InputStream squareInputStream = classloader.getResourceAsStream(squareSpritePath);

                    if (squareInputStream == null) {
                        System.err.println("Failed to read squareInputStream: " + squareSpritePath);
                    } else {
                        squareImage = new Image(squareInputStream);

                        ImageView squareImageView = new ImageView(squareImage);

                        squareImageView.setScaleX(0.35);
                        squareImageView.setScaleY(0.35);

                        Integer squareImageOffsetX = 5;
                        Integer squareImageOffsetY = 5;

                        squareImageView.setLayoutX(xCoord - squareImageOffsetX);
                        squareImageView.setLayoutY(yCoord - squareImageOffsetY);
                        root.getChildren().add(squareImageView);

                        squareInputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Draws the chit cards on the screen
    public static void drawChitCards(Pane root, Board board) {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Drawing the chit cards in rows and columns
        Integer rows = 4;
        Integer cols = 4;
        for (int i=0; i < rows; i++) {
            for (int j=0; j < cols; j++) {
                ChitCard chitCard = board.getChitCardsArray().get(4*i + j);
                String spritePath = chitCard.getCreature().getSpritePath();

                Circle innerCircle = new Circle(); // white circle
                Circle outerCircle = new Circle(); // black border of circle
                Circle transparentCircle = new Circle(); // invisible click box (collider) on top the chitcard for clicking functionality
                innerCircle.setFill(Color.NAVAJOWHITE); //was WHITE
                innerCircle.setEffect(new Lighting());
                outerCircle.setFill(Color.DARKRED);
                transparentCircle.setFill(Color.TRANSPARENT);
                innerCircle.setRadius(board.getChitCardsArray().size()*2 - 7);
                outerCircle.setRadius(board.getChitCardsArray().size()*2 - 4);
                transparentCircle.setRadius(board.getChitCardsArray().size()*2 - 4);

                // Retrieving the chit card sprites
                Image backImage = null;
                Image frontImage = null;

                try {
                    InputStream chitCardInputStream = classloader.getResourceAsStream("fire.png");//"chitCardBack.png");
                    InputStream spritePathInputStream = classloader.getResourceAsStream(spritePath);

                    if (chitCardInputStream == null) {
                        System.err.println("Failed to read: fire.png");
                    }
                    else if (spritePathInputStream == null) {
                        System.err.println("Failed to read spritePath: " + spritePath);
                    }
                    else {
                        backImage = new Image(chitCardInputStream);
                        frontImage = new Image(spritePathInputStream);

                        ImageView backImageView = new ImageView(backImage);
                        ImageView frontImageView = new ImageView(frontImage);

                        backImageView.setScaleX(0.5);
                        backImageView.setScaleY(0.5);
                        frontImageView.setScaleX(0.4);
                        frontImageView.setScaleY(0.4);

                        Integer chitCardOffsetX = 105;
                        Integer chitCardOffsetY = 105;

                        Integer xCoord = 85 * i + boardOffsetX - chitCardOffsetX;
                        Integer yCoord = 85 * j + boardOffsetY - chitCardOffsetY;

                        Integer imageOffsetX = 47;
                        Integer imageOffsetY = 49;
                        backImageView.setLayoutX(xCoord - (double) imageOffsetX / 2); // no /2
                        backImageView.setLayoutY(yCoord - (double) imageOffsetY / 2);// - 5 instead of /2
                        frontImageView.setLayoutX(xCoord - imageOffsetX - 10);
                        frontImageView.setLayoutY(yCoord - imageOffsetY);


                        innerCircle.setLayoutX(xCoord);
                        innerCircle.setLayoutY(yCoord);
                        outerCircle.setLayoutX(xCoord);
                        outerCircle.setLayoutY(yCoord);
                        transparentCircle.setLayoutX(xCoord);
                        transparentCircle.setLayoutY(yCoord);


                        // No Label if Devil
                        Text label = new Text(String.valueOf(chitCard.getCount()));
                        label.setFont(new Font("Verdana", 20));
                        label.setFill(Color.DARKRED);
                        label.setLayoutX(xCoord + 10);
                        label.setLayoutY(yCoord + 10);
                        if (chitCard.getCreature() == CreatureType.DEVIL) {
                            // Hide the label for Devil chit cards
                            label.setText("");
                            frontImageView.setLayoutX(xCoord - imageOffsetX - 1); //Move the devil icon to the left a bit
                            frontImageView.setLayoutY(yCoord - imageOffsetY + 4); //Move the devil icon down a bit
                        }

                        //backImageView.setVisible(false); // can uncomment for testing
                        frontImageView.setVisible(false); // can set to true for testing
                        label.setVisible(false); //  can set to true for testing

                        // Adding click functionality to chitcard click box
                        transparentCircle.setOnMouseClicked(event -> {

                            // Can comment these for testing
                            backImageView.setVisible(false);
                            transparentCircle.setVisible(false);
                            frontImageView.setVisible(true);
                            label.setVisible(true);



                            TurnManager.playMove(chitCard);
                        });

                        chitCard.addNode(backImageView);
                        chitCard.addNode(frontImageView);
                        chitCard.addNode(label);
                        chitCard.addNode(transparentCircle);

                        root.getChildren().add(outerCircle);
                        root.getChildren().add(innerCircle);
                        root.getChildren().add(backImageView);
                        root.getChildren().add(frontImageView);
                        root.getChildren().add(label);
                        root.getChildren().add(transparentCircle);

                    chitCardInputStream.close();
                    spritePathInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Moves a dragon token on the screen
    public static void moveToken(DragonToken token, Position nextPosition) {
        Integer tokenOffsetX = 30;
        Integer tokenOffsetY = 30;

        if (nextPosition instanceof Cave) {
            tokenOffsetX = 45;
            tokenOffsetY = 45;
        }


        token.getNodes().get(0).setLayoutX(nextPosition.getX() - tokenOffsetX);
        token.getNodes().get(0).setLayoutY(nextPosition.getY() - tokenOffsetY);
    }

    // Resets a chit card
    public static void resetChitCard(ChitCard chitCard) {
        Node backImage = chitCard.getNodes().get(0);
        Node frontImage = chitCard.getNodes().get(1);
        Node label = chitCard.getNodes().get(2);
        Node clickBox = chitCard.getNodes().get(3);

        // Can comment these for testing
        backImage.setVisible(true);
        clickBox.setVisible(true);
        frontImage.setVisible(false);
        label.setVisible(false);
    }


    // Shows the victory screen (popup alert currently)
    public static void showVictory(Integer winner) {
        Stage victoryStage = new Stage();
        victoryStage.initModality(Modality.APPLICATION_MODAL);
        victoryStage.setTitle("Victory!");

        Label victoryLabel = new Label("Player " + winner + " won!");
        victoryLabel.setFont(new Font(36)); // Increase font size
        victoryLabel.setTextFill(Color.DARKGREEN); // Set text color to dark green

        Button startOverButton = new Button("Start Over");
        startOverButton.setOnAction(arg0 -> {
            System.out.println("Start Over clicked");
            victoryStage.close(); // Close the victory stage
            //Game newGame = new Game();
            //newGame.startOver();
        });

        // Style the button
        startOverButton.setStyle("-fx-background-color: #ff8c00; -fx-text-fill: white; -fx-font-size: 18px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        startOverButton.setEffect(new DropShadow());

        VBox layout = new VBox(20); // Vertical box layout
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(victoryLabel, startOverButton);

        // Set background color to a gradient between brown, green, and orange
        Stop[] stops = new Stop[] { new Stop(0, Color.SADDLEBROWN), new Stop(0.5, Color.LIGHTGREEN), new Stop(1, Color.ORANGE) };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, null)));

        Scene scene = new Scene(layout, 600, 400); // Increase scene size
        victoryStage.setScene(scene);

        victoryStage.showAndWait();
    }



    //initialize label for current player
    public static void initializePlayerLabel(Pane root){
        playerLabel.textProperty().bind(Bindings.concat("Turn of Player ").concat(currentPlayer.asString()));
        playerLabel.setFont(new Font("Verdana", 30));
        playerLabel.setTextFill(Color.DARKRED);
        playerLabel.setEffect(new DropShadow(0, 3.0, 3.0, Color.GRAY));
        playerLabel.setEffect(new Lighting());

        playerLabel.setLayoutX(boardOffsetX - 700);
        playerLabel.setLayoutY(boardOffsetY - 270);
        root.getChildren().add(playerLabel);
    }

    // Add a title for the game
    public static void initializeGameLabel(Pane root){
        Text t = new Text();
        t.setFont(new Font("Verdana", 45));
        t.setFill(Color.GOLD);
        t.setEffect(new DropShadow(0, 3.0, 3.0, Color.GRAY));
        t.setEffect(new Lighting());

        t.setX(boardOffsetX - 700);
        t.setY(boardOffsetY - 300);
        t.setText("FIERY DRAGONS");
        root.getChildren().add(t);
    }

    // Updates the current player
    public static void updateCurrentPlayer(int playerIndex) {
        currentPlayer.set(playerIndex);

        if (DareFeature.isDisabledFor(currentPlayer.get())){

            dareButton.setDisable(true);
            dareButton.setVisible(false);

        }
        else{
            dareButton.setDisable(false);
            dareButton.setVisible(true);
        }
    }





    // Sprint 4 extension by Vlad
    public static void drawActivitySquare(Pane root){

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(200);
        rectangle.setHeight(250);
        rectangle.setStroke(Color.DARKRED);
        rectangle.setStrokeWidth(3);
        rectangle.setX(boardOffsetX - 700);
        rectangle.setY(boardOffsetY - 200);
        rectangle.setFill(Color.TRANSPARENT);
        root.getChildren().add(rectangle);

        Text activityText = new Text();
        activityText.setFont(new Font("Verdana", 25));
        activityText.setText("Available");
        activityText.setFill(Color.DARKRED);
        activityText.setEffect(new Lighting());
        activityText.setX(boardOffsetX - 660);
        activityText.setY(boardOffsetY - 160);

        root.getChildren().add(activityText);
    }


    // Unused
//    public String save() {
//        StringBuilder str = new StringBuilder();
//
//
//        return str.toString();
//    }

    public static void clear(Pane root) {
        root.getChildren().clear();
    }





    public static void dareButtonClick(Pane root, Board board){

            if (!isDareOpen){
                isDareOpen = true;
                root.setDisable(true);
                //pause timer
                TurnManager.pauseTimer();
//                CreatureType creatureType = board.getPlayers().get(currentPlayer.get() - 1).getToken().getPosition().getCreature();

                Position position = board.getPlayers().get(currentPlayer.get() - 1).getToken().getPosition();
                CompletableFuture<ChitCard> resultFuture = DareFeature.run(position);

                final ChitCard[] finalChitCard = new ChitCard[1];
                Task<ChitCard> task = new Task<>() {
                    @Override
                    protected ChitCard call() throws Exception {
                        return resultFuture.get();
                    }
                };

                task.setOnSucceeded(event -> {
                    finalChitCard[0] = task.getValue();
                    DareFeature.disableFromPlayer(currentPlayer.get());
                    //resume timer
                    TurnManager.resumeTimer();
                    root.setDisable(false);
                    dareButton.setDisable(true);
                    dareButton.setVisible(false);
                    TurnManager.playMove(finalChitCard[0]);
                    isDareOpen = false;
                });

                task.setOnFailed(event -> {
                    Throwable exception = task.getException();
                    exception.printStackTrace();
                });

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }


    }

}
