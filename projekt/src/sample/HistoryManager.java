package sample;

import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.util.List;
import java.util.Map;

public class HistoryManager {
    private IMatDataHandler database = IMatDataHandler.getInstance();
    private Map<String,ProductListItem> productListItemMap;

    public FlowPane mainFlowPane;

    public HistoryManager(Map<String, ProductListItem> productListItemMap, FlowPane mainFlowPane){
        this.productListItemMap = productListItemMap;
        this.mainFlowPane = mainFlowPane;
    }

    public void getHistory(){
        mainFlowPane.getChildren().clear();
       // database.getOrders().forEach(e -> e.getItems().forEach(f -> mainFlowPane.getChildren().add(productListItemMap.get(f.getProduct().getName())))); // fyller fönstret med alla objekts som har köpts lite meme kommer ändra snart
        database.getOrders().forEach(e -> mainFlowPane.getChildren().add(new HistoryItem(productListItemMap,e)));
    }
}
