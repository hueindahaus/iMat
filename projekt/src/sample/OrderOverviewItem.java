package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class OrderOverviewItem extends AnchorPane {

    ShoppingItem shoppingItem;

    @FXML
    private ImageView image;
    @FXML
    private Label title;
    @FXML
    private Label price;


    public OrderOverviewItem(ShoppingItem shoppingItem){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml_files/order_overviewitem.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.shoppingItem = shoppingItem;

        image.setImage(IMatDataHandler.getInstance().getFXImage(shoppingItem.getProduct()));

        if(shoppingItem.getProduct().getUnitSuffix().equals("förp") || shoppingItem.getProduct().getUnitSuffix().equals("st") || shoppingItem.getProduct().getUnitSuffix().equals("burk")) {
            title.setText(String.valueOf((int)shoppingItem.getAmount()) + " " + shoppingItem.getProduct().getUnitSuffix() + " x " + shoppingItem.getProduct().getName());
        } else {
            title.setText(String.valueOf(shoppingItem.getAmount()) + " " + shoppingItem.getProduct().getUnitSuffix() + " x " + shoppingItem.getProduct().getName());

        }


        price.setText(String.valueOf((int)shoppingItem.getTotal() + " kr"));
    }



}
