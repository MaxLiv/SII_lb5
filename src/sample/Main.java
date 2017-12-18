package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public class Main extends Application {

    private File file;
    private Image image = new Image("blackcircle.png");
    private ImageView imageView = new ImageView();
    private NeyronNetwork network = new NeyronNetwork();
    private HopfieldNN hopfieldNN = new HopfieldNN();

    Label ship = new Label();
    Label car = new Label();
    Label man = new Label();
    Label house = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //open image
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif")); // limit chooser options to image files

        Button openButton = new Button("Open image");
        openButton.setLayoutY(310);
        openButton.setOnAction(
                e -> {
                    file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        try {
                            image = new Image(file.toURI().toURL().toString());
                            imageView.setImage(image);
                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

        Button learnButton = new Button("Teach network");
        learnButton.setLayoutY(310);
        learnButton.setLayoutX(120);
        learnButton.setOnAction(e -> {
            try {
                learnButton.setText("Wait...");
                network.learning();
                learnButton.setText("Done!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        Button recognizeButton = new Button("Recognize image");
        recognizeButton.setLayoutY(310);
        recognizeButton.setLayoutX(240);
        recognizeButton.setOnAction(e -> {
            String path;
            if (file != null)
                path = file.getPath();
            else
                path = "/home/developer/Java/SII_lb5/src/blackcircle.png";
            Map<String, Double> element = network.findElement(path);
            ship.setText(String.valueOf(element.get("ship"))+"%");
            car.setText(String.valueOf(element.get("car"))+"%");
            man.setText(String.valueOf(element.get("man"))+"%");
            house.setText(String.valueOf(element.get("house"))+"%");
        });

        Button hopfieldButton = new Button("сеть Хопфилда");
        hopfieldButton.setLayoutY(310);
        hopfieldButton.setLayoutX(380);
        hopfieldButton.setOnAction(e ->{
            imageView.setImage(hopfieldNN.search("d1"));
        });



        imageView.setImage(image);
        imageView.setLayoutY(0);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);

        // Obtain PixelReader
//        PixelReader pixelReader = image.getPixelReader();
//        System.out.println("Image Width: " + image.getWidth());
//        System.out.println("Image Height: " + image.getHeight());
//        System.out.println("Pixel Format: " + pixelReader.getPixelFormat());
//
//        // Determine the color of each pixel in the image
//        for (int readY = 0; readY < 100; readY++) {
//            for (int readX = 0; readX < 100; readX++) {
//                Color color = pixelReader.getColor(readX, readY);
//                System.out.println("\nPixel color at coordinates ("
//                        + readX + "," + readY + ") "
//                        + color.toString());
//                System.out.println("R = " + color.getRed());
//                System.out.println("G = " + color.getGreen());
//                System.out.println("B = " + color.getBlue());
//                System.out.println("Opacity = " + color.getOpacity());
//                System.out.println("Saturation = " + color.getSaturation());
//            }
//        }

        // Display image on screen
        StackPane root = new StackPane();
        Pane pane = new Pane();
        pane.getChildren().addAll(imageView, openButton, learnButton, recognizeButton,hopfieldButton ,getGrid());
        root.getChildren().add(pane);
        Scene scene = new Scene(root, 530, 350);
        primaryStage.setTitle("Image recognize test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane getGrid() {
        Label shipLabel = new Label("Корабль: ");
        Label carLabel = new Label("Машина: ");
        Label manLabel = new Label("Человек: ");
        Label houseLabel = new Label("Дом: ");

        GridPane gridPane = new GridPane();

        gridPane.add(shipLabel, 0, 0);
        gridPane.add(ship, 1, 0);

        gridPane.add(carLabel, 0, 1);
        gridPane.add(car, 1, 1);

        gridPane.add(manLabel, 0, 2);
        gridPane.add(man, 1, 2);

        gridPane.add(houseLabel, 0, 3);
        gridPane.add(house, 1, 3);

        gridPane.setLayoutX(300);
        gridPane.setLayoutY(0);

        return gridPane;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
