package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Map;

public class MainPage extends AnchorPane {

    @FXML AnchorPane mostBought;
    @FXML AnchorPane weeksOffers;

    public MainPage (Map<String, ProductListItem> productListItemMap){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainPage.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        weeksOffers.getChildren().add(productListItemMap.get("Kaffe"));
        weeksOffers.getChildren().add(productListItemMap.get("Västerbotten"));
        weeksOffers.getChildren().add(productListItemMap.get("Grapefruit"));
    }
}
