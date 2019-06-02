package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml_files/history_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        orderDate.setText("Datum: " + order.getDate().getDate() + " " +  translateMonth(order.getDate().getMonth()) + " " + (order.getDate().getYear()+1900));
        orderNumber.setText("Ordernummer: " + Integer.toString(order.getOrderNumber()));
        order.getItems().forEach(e -> orderMain.getChildren().add(new HistoryListItem(e,parent.getShoppingItemMap().get(e.getProduct().getName()),parent)));      //skapar nytt productlistitems eftersom att vi behöver duplicates (men de refererar till samma oldShoppingItem som de representerar)


        this.order = order;
        isExpanded = false;
        implementAllToolTips();
    }

    @FXML
    public void changeHistorySize(){
        if(isExpanded){
            historyScroll.setPrefHeight(200);
            expandButton.setImage(new Image(getClass().getResource("../icons/baseline_expand_more_black_18dp.png").toExternalForm()));
            isExpanded = false;
        } else{
            int height = ((order.getItems().size()/3))*300 + 50;    //obs eventuellt fixa
            if(order.getItems().size() % 3 != 0){                   //adderar 300dp om antalet items i en order inte är delbart med 3
                height += 300;
            }
            historyScroll.setPrefHeight(height);
            expandButton.setImage(new Image(getClass().getResource("../icons/baseline_expand_less_black_18dp.png").toExternalForm()));
            isExpanded = true;
        }
    }

    private String translateMonth(int month){
        switch (month){
            case 0:
                return "januari";
            case 1:
                return "februari";
            case 2:
                return "mars";
            case 3:
                return "april";
            case 4:
                return "maj";
            case 5:
                return "juni";
            case 6:
                return "juli";
            case 7:
                return "augusti";
            case 8:
                return "september";
            case 9:
                return "oktober";
            case 10:
                return "november";
            case 11:
                return "december";
            default:
                return "odefinerad månad";
        }
    }


    private void implementAllToolTips(){
        installTooltipOnNode(expandButton, "Klicka för att se hela ordern");
    }




    private void installTooltipOnNode(Node node, String message){
        Tooltip tooltip = new Tooltip(message);
        tooltip.setFont(new Font("Roboto-Regular",18));
        Tooltip.install(node,tooltip);
    }
}
