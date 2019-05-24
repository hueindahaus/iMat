package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    @FXML
    TextField textField;
    @FXML
    private Label price;


    public CartListItem(ShoppingItem shoppingItem, ProductSearchController parentController, Cart cart){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cart_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.parentController = parentController;
        this.shoppingItem = shoppingItem;
        image.setImage(IMatDataHandler.getInstance().getFXImage(shoppingItem.getProduct()));
        title.setText(shoppingItem.getProduct().getName());
        price.setText(shoppingItem.getTotal() + " kr");
    }

    public ShoppingItem getShoppingItem(){
        return shoppingItem;
    }

    public void updateTextField(){          //sätter strängen i textField till t.ex.: "1 kg"
        price.setText(shoppingItem.getTotal() + " kr");
        textField.textProperty().setValue(String.valueOf(shoppingItem.getAmount()) + " " + shoppingItem.getProduct().getUnitSuffix());
    }

    @FXML
    public void addOneToAmount(){                                   //
        parentController.getCart().increaseAmount(shoppingItem);
    }

    @FXML
    public void subtractOneFromAmount(){
        parentController.getCart().decreaseAmount(shoppingItem);

    }
}
