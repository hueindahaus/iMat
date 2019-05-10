package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class CartListItem extends AnchorPane {

    ShoppingItem shoppingItem;
    ProductSearchController parentController;

    @FXML
    ImageView image;
    @FXML
    Label title;


    public CartListItem(Product product, int amount, ProductSearchController parentController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cart_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.parentController = parentController;
        shoppingItem = new ShoppingItem(product,amount);
        image.setImage(IMatDataHandler.getInstance().getFXImage(product));
        title.setText(product.getName());

    }
}
