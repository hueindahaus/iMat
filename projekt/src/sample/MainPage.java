package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.util.List;
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
        IMatDataHandler dataHandler = IMatDataHandler.getInstance();
        if(dataHandler.getOrders().size() > 0){
            List<ShoppingItem> items = dataHandler.getOrders().get(dataHandler.getOrders().size()-1).getItems();
            for(int i = 0; i < 3; i++){
                if(i < items.size()){
                    mostBought.getChildren().add(productListItemMap.get(items.get(i).getProduct().getName()));
                }else {
                    break;
                }
            }
        }
    }
}
