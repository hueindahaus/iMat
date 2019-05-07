package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("imat.fxml"));
        primaryStage.setTitle("IMat");
        primaryStage.setScene(new Scene(root, 1440, 900));
        primaryStage.show();

        Font.loadFont(getClass().getResourceAsStream("src/fonts/Roboto-Regular.ttf") , 14);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
