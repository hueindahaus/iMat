package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
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

    @FXML
    private FlowPane mostBought;
    @FXML
    private FlowPane weekOffers;

    public MainPage (Map<String, ProductListItem> productListItemMap,ProductSearchController parent){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainPage.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        weekOffers.getChildren().clear();
        weekOffers.getChildren().add(productListItemMap.get("Kaffe"));
        weekOffers.getChildren().add(productListItemMap.get("Västerbotten"));
        weekOffers.getChildren().add(productListItemMap.get("Fanta flaska"));

        mostBought.getChildren().clear();
        IMatDataHandler dataHandler = IMatDataHandler.getInstance();
        if(dataHandler.getOrders().size() > 0){
            List<Product> mostBoughtProducts = getMostBought(dataHandler.getOrders());
            System.out.println(mostBoughtProducts.size());
            for(int i = 0; i < mostBoughtProducts.size(); i++){
                mostBought.getChildren().add(new ProductListItem(new ShoppingItem(mostBoughtProducts.get(i),1),parent));
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
                    products.add(item.getProduct());
                }else{
                    productIntegerMap.put(item.getProduct(),productIntegerMap.get(item.getProduct())+1);
                }
            }
        }
       // System.out.println("Nr of Orders " + orders.size() + " and nr of products " + products.size());
       // products.forEach(e -> System.out.println("Name : "  +  e.getName() + " Nr: " + productIntegerMap.get(e)));
        sortProducts(productIntegerMap,products);
        if(products.size() >= 3) {
            System.out.println("many");
            return products.subList(0, 3);
        }
        else {
            System.out.println("few");
            return products.subList(0, products.size() - 1);
        }
    }

    private void sortProducts(Map<Product,Integer> productIntegerMap, List<Product> products){
        int n = products.size();
        boolean swapped;
        do{
            swapped = false;
            for(int i = 1; i < n-1; i++){
                if(productIntegerMap.get(products.get(i - 1)) < productIntegerMap.get(products.get(i))){
                    swapped = true;
                    Product tmp = products.get(i);
                    products.set(i,products.get(i-1));
                    products.set(i-1,tmp);
                }
            }
        }while(swapped);
    }
}
