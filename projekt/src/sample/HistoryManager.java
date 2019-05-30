package sample;

import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryManager {
    private IMatDataHandler database = IMatDataHandler.getInstance();
    private Map<String,ProductListItem> productListItemMap;

    private Map<Integer,HistoryItem> historyItemMap = new HashMap<>();

    public FlowPane mainFlowPane;

    ProductSearchController parent;

    public HistoryManager(Map<String, ProductListItem> productListItemMap, FlowPane mainFlowPane, ProductSearchController parent){
        this.productListItemMap = productListItemMap;
        this.mainFlowPane = mainFlowPane;
        this.parent = parent;
    }

    public void getHistory(){
        mainFlowPane.getChildren().clear();
       // database.getOrders().forEach(e -> e.getItems().forEach(f -> mainFlowPane.getChildren().add(productListItemMap.get(f.getProduct().getName())))); // fyller fönstret med alla objekts som har köpts lite meme kommer ändra snart
        List<Order> orders = sortHistory();
        for(int i = 0; i < orders.size(); i++){
            Order order = orders.get(i);
            if(!historyItemMap.containsKey(order.getOrderNumber())){
                historyItemMap.put(order.getOrderNumber(),new HistoryItem(productListItemMap,order,parent));
            }
            mainFlowPane.getChildren().add(historyItemMap.get(order.getOrderNumber()));
        }
//        database.getOrders().forEach(e -> mainFlowPane.getChildren().add(new HistoryItem(productListItemMap,e,parent)));
    }

    private List<Order> sortHistory(){
        List<Order> orders = database.getOrders();

        int n = orders.size();
        boolean swapped;
        do{
            swapped = false;
            for(int i = 1; i <= n-1; i++){
                if(orders.get(i).getDate().after(orders.get(i-1).getDate())){
                    swapped = true;
                    Order tmp = orders.get(i);
                    orders.set(i,orders.get(i-1));
                    orders.set(i-1,tmp);
                }
            }
        }while(swapped);

        return orders;
    }
}
