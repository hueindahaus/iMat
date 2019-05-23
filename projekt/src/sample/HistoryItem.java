package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.Order;

import java.io.IOException;
import java.util.Map;

public class HistoryItem extends AnchorPane {
    @FXML
    private Label orderNumber;
    @FXML
    private Label orderDate;
    @FXML
    private FlowPane orderMain;

    @FXML
    private ImageView expandButton;
    @FXML
    private ScrollPane historyScroll;

    boolean isExpanded;

    private Order order;

    public HistoryItem(Map<String, ProductListItem> productListItemMap, Order order, ProductSearchController parent){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("history_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        orderDate.setText("Datum: " + order.getDate().toString());
        orderNumber.setText("Ordernummer: " + Integer.toString(order.getOrderNumber()));
        order.getItems().forEach(e -> orderMain.getChildren().add(new ProductListItem(parent.getShoppingItemMap().get(e.getProduct().getName()),parent)));      //skapar nytt productlistitems eftersom att vi behöver duplicates (men de refererar till samma shoppingItem som de representerar)


        this.order = order;
        isExpanded = false;
    }

    @FXML
    public void changeHistorySize(){
        if(!isExpanded){
            historyScroll.setPrefHeight(350);
            expandButton.setImage(new Image(getClass().getResource("../icons/baseline_expand_more_black_18dp.png").toExternalForm()));
            isExpanded = true;
        } else{
            int height = ((order.getItems().size()/3))*300 + 50;    //obs eventuellt fixa
            if(order.getItems().size() % 3 != 0){                   //adderar 300dp om antalet items i en order inte är delbart med 3
                height += 300;
            }
            historyScroll.setPrefHeight(height);
            expandButton.setImage(new Image(getClass().getResource("../icons/baseline_expand_less_black_18dp.png").toExternalForm()));
            isExpanded = false;
        }
    }
}
