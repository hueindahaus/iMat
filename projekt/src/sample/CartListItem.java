package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.awt.event.MouseEvent;
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
    @FXML
    private ImageView clearIcon;

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


        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String input = textField.getText();   //vi sparar det som står i textfield i en variabel
                if(input.isEmpty()){                    //om strängen är empty ska vi ta bort item från cart
                    clearItem();
                } else {                                //annars ska vi extrahera ut endast siffrorna i textfield och sätta currentAmount till vad som står
                    shoppingItem.setAmount(extractDigits(input));
                }
                updateTextField();
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när textrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    Platform.runLater(new Runnable() {      // Gör det möjligt att selecta allt när textrutan får fokus (förstår mig inte helt på denna)
                        public void run() {
                            textField.selectAll();
                        }
                    });
                }
            }
        });

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

    @FXML
    public void clearItem(){
        parentController.getCart().removeAllOfItem(shoppingItem);
    }


    private int extractDigits(String string){           //metod som extraherar alla nummer ur en sträng och returnar det som en int
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < string.length(); i++){
            char c = string.charAt(i);
            if(Character.isDigit(c)){
                builder.append(c);
            }
        }
        return Integer.valueOf(builder.toString());
    }
}
