package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private List<Product> getMostBought (List<Order> orders){
        Map<Product, Integer> productIntegerMap = new HashMap<>();
        List<Product> products = new ArrayList<>();
        for(Order order: orders){
            for(ShoppingItem item: order.getItems()){
                if(!productIntegerMap.containsKey(item.getProduct())){
                    productIntegerMap.put(item.getProduct(),1);
                }else{
                    productIntegerMap.put(item.getProduct(),1);
                    products.add(item.getProduct());
                }
            }
        }

        for(int i = 0; i < products.size()-1; i++){
            for(int j = i +1; j < products.size(); j++ ){
                if(productIntegerMap.get(products.get(i)) < productIntegerMap.get(products.get(j))){
                    Product tmp = products.get(i);
                    products.set(i,products.get(j));
                    products.set(j,tmp);
                }
            }
        }

        return products.subList(0,2);
    }
}
