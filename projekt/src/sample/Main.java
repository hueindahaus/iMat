package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import se.chalmers.cse.dat216.project.IMatDataHandler;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("imat.fxml"));
        primaryStage.setTitle("IMat");
        primaryStage.initStyle(StageStyle.TRANSPARENT);                     //gör titlebar (header) osynlig
        primaryStage.setScene(new Scene(root, 1440, 900));
        primaryStage.show();


        Font.loadFont(getClass().getResourceAsStream("src/fonts/Roboto-Regular.ttf") , 14);     //loadar font som finns i mappen "fonts"
    }




    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> IMatDataHandler.getInstance().shutDown(), "Shutdown-thread"));
        launch(args);
    }
}
