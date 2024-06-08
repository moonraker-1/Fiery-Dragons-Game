package fierydragons;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.Style;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DareScreen {

    private List<ChitCard> chitCards = new ArrayList<>(3);

    private Stage stage;
    private Pane darePane;
    private Scene dareScene;

    private List<ImageView> backImages = new ArrayList<>(3);
    private List<ImageView> frontImages = new ArrayList<>(3);

    private List<Text> labels = new ArrayList<>(3);

    private CompletableFuture<ChitCard> future;

    public DareScreen(){
        stage = new Stage();
        stage.setTitle("DARE");
        Image backgroundImage = new Image("orange.jpg");

        // Set the background image
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        darePane = new Pane();
        darePane.setBackground(new Background(background));
        dareScene = new Scene(darePane, 600, 300);
    }
    public CompletableFuture<ChitCard> callScreen(Position position){
        // A variable needed to keep the result of the operation after the screen closes
        future = new CompletableFuture<>();
        // Set up the scene
        setDareLabel();
        stage.setScene(dareScene);
        stage.show();
        // Retrieve and position chit cards on the new scene
        positionChitCards(position);

        return future;
    }

    private void positionChitCards(Position position){
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Get the chit card that matches the character on the current square/cave
        chitCards.add(getRightCard(position));
        // Get the other two chit cards that depict different characters
        chitCards.addAll(getOtherCards(position));
        // Randomize their positions in the list
        Collections.shuffle(chitCards);

        // Get graphical representation of the chit cards
        for (int i = 0; i < 3; i++){
            String spritePath = chitCards.get(i).getCreature().getSpritePath();

            Circle innerCircle = new Circle(); // white circle
            Circle transparentCircle = new Circle(); // invisible click box (collider) on top the chitcard for clicking functionality
            innerCircle.setFill(Color.NAVAJOWHITE); //was WHITE
            innerCircle.setEffect(new Lighting());
            transparentCircle.setFill(Color.TRANSPARENT);
            innerCircle.setRadius(50);
            transparentCircle.setRadius(50);

            // Retrieving the chit card sprites
            Image backImage = null;
            Image frontImage = null;

            try {
                // Get the image of the character and the image of the fire
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


                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(20);
                    dropShadow.setSpread(0.5);
                    dropShadow.setColor(Color.BLUE);


                    innerCircle.setEffect(dropShadow);
                    backImageView.setScaleX(0.65);
                    backImageView.setScaleY(0.65);
                    frontImageView.setScaleX(0.5);
                    frontImageView.setScaleY(0.5);

                    Integer xCoord = 150 * i + 75 + i * 37 + 37;
                    Integer yCoord = 150;

                    Integer imageOffsetX = 47;
                    Integer imageOffsetY = 47;
                    backImageView.setLayoutX(xCoord - (double) imageOffsetX / 2);
                    backImageView.setLayoutY(yCoord - (double) imageOffsetY / 2);
                    frontImageView.setLayoutX(xCoord - imageOffsetX - 10);
                    frontImageView.setLayoutY(yCoord - imageOffsetY);


                    innerCircle.setLayoutX(xCoord);
                    innerCircle.setLayoutY(yCoord);
                    transparentCircle.setLayoutX(xCoord);
                    transparentCircle.setLayoutY(yCoord);

                    Text label = new Text(String.valueOf(chitCards.get(i).getCount()));
                    label.setFont(new Font("Verdana", 25));
                    label.setFill(Color.DARKRED);
                    label.setLayoutX(xCoord + 15);
                    label.setLayoutY(yCoord + 10);


                    //backImageView.setVisible(false); // can uncomment for testing
                    frontImageView.setVisible(false); // can set to true for testing
                    label.setVisible(false); //  can set to true for testing


                    int index = i;
                    // Adding click functionality to chitcard click box
                    transparentCircle.setOnMouseClicked(event -> {

                        // Can comment these for testing
                        backImageView.setVisible(false);
                        transparentCircle.setVisible(false);
                        frontImageView.setVisible(true);
                        label.setVisible(true);

                        if (chitCards.get(index).getCreature().equals(position.getCreature())){
                            dropShadow.setColor(Color.GREEN);
                        }
                        else{
                            dropShadow.setColor(Color.RED);
                        }
                        innerCircle.setEffect(dropShadow);

                        future.complete(chitCards.get(index));


                        // Create a Timeline to turn the cards in a second
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                for (int i = 0; i < 3; i++){
                                    backImages.get(i).setVisible(false);
                                    frontImages.get(i).setVisible(true);
                                    labels.get(i).setVisible(true);
                                }

                            }
                        }));
                        timeline.setCycleCount(1);
                        timeline.play();

                        // Create another Timeline to turn close the screen in two seconds
                        timeline = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent e){
                                stage.close();
                            }
                        }));
                        timeline.setCycleCount(1);
                        timeline.play();

                        // TurnManager.playMove(chitCards.get(i));
                    });

                    chitCards.get(i).addNode(backImageView);
                    chitCards.get(i).addNode(frontImageView);
                    chitCards.get(i).addNode(label);
                    chitCards.get(i).addNode(transparentCircle);

                    darePane.getChildren().add(innerCircle);
                    darePane.getChildren().add(backImageView);
                    darePane.getChildren().add(frontImageView);
                    darePane.getChildren().add(label);
                    darePane.getChildren().add(transparentCircle);

                    backImages.add(backImageView);
                    frontImages.add(frontImageView);
                    labels.add(label);

                    chitCardInputStream.close();
                    spritePathInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ChitCard getRightCard(Position position){
        boolean nextOccupied = position.getNextPos().get(0).isOccupied();
        boolean nextNextOccupied = position.getNextPos().get(0).getNextPos().get(0).isOccupied();
        int minNumber;
        if (nextOccupied && nextNextOccupied){
            minNumber = 3;
        }
        else if (nextOccupied && !nextNextOccupied){
            minNumber = new Random().nextBoolean() ? 2 : 3;
        }
        else if (!nextOccupied && nextNextOccupied){
            minNumber = new Random().nextBoolean() ? 1 : 3;
        }
        else {
            minNumber = new Random().nextInt(3) + 1;
        }
        return new ChitCard(position.getCreature(), minNumber);
    }

    // Return
    private List<ChitCard> getOtherCards(Position position){
        CreatureType[] creatures = CreatureType.values();

        int rightCreatureIndex = 0;
        for (int i = 0; i < creatures.length; i++){
            if (creatures[i] == position.getCreature()){
                rightCreatureIndex = i;
                break;
            }
        }

        Random random = new Random();
        int number1;
        int number2;

        do {
            number1 = random.nextInt(4); // Generates a number from 0 to 3
        } while (number1 == rightCreatureIndex);

        // Generate the second number that is not equal to the provided number and number1
        do {
            number2 = random.nextInt(4); // Generates a number from 0 to 3
        } while (number2 == rightCreatureIndex || number2 == number1);

        CreatureType cr1 = CreatureType.values()[number1];
        CreatureType cr2 = CreatureType.values()[number2];
        ChitCard one = new ChitCard(cr1, random.nextInt(3) + 1);
        ChitCard two = new ChitCard(cr2, random.nextInt(3) + 1);
        List<ChitCard> otherChitCards = new ArrayList<>(2);
        otherChitCards.add(one);
        otherChitCards.add(two);
        return otherChitCards;
    }

    private void setDareLabel(){
        Text t = new Text();
        t.setFont(new Font("Verdana", 25));
        t.setFill(Color.GOLD);
        t.setEffect(new DropShadow(0, 3.0, 3.0, Color.GRAY));
        t.setEffect(new Lighting());

        t.setX(15);
        t.setY(25);
        t.setText("DARE YOUR LUCK - PICK ONE OUT OF THREE");
        darePane.getChildren().add(t);
    }
}
