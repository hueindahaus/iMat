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
import java.text.DecimalFormat;

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

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

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


        textField.setOnAction(new EventHandler<ActionEvent>() {       //actionhandler som agerar när man trycker enter i en textfield
            @Override
            public void handle(ActionEvent event) {
                String input = textField.getText();   //vi sparar det som står i textfield i en variabel
                if(input.isEmpty()){                    //om strängen är empty ska vi sätta 1 som default värde
                    shoppingItem.setAmount(1);
                } else {                                //annars ska vi extrahera ut endast siffrorna i textfield och sätta currentAmount till vad som står
                    shoppingItem.setAmount(handleInput(input));
                }
                updateTextField();                     //display:ar t.ex. "1 kg"
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när textrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    textField.clear();
                }  else if(!newValue) {
                    updateTextField();
                }
            }
        });





    }

    public ShoppingItem getShoppingItem(){
        return shoppingItem;
    }

    public void updateTextField(){          //sätter strängen i textField till t.ex.: "1 kg"
        price.setText(decimalFormat.format(shoppingItem.getTotal()) + " kr");



        double amount = shoppingItem.getAmount();
        String output = "";
        String unitSuffix = shoppingItem.getProduct().getUnitSuffix();

        if(unitSuffix.equals("förp") || unitSuffix.equals("st") || unitSuffix.equals("burk") || unitSuffix.equals("påse")){
            output = String.valueOf((int)amount) + " " + unitSuffix;
        } else {
            output = String.valueOf(amount) + " " + unitSuffix;
        }

        textField.textProperty().setValue(output); //sätter texten i inputrutan till t.ex. "1 kg"
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


    private double extractDigits(String string){           //metod som extraherar alla nummer ur en sträng och returnar det som en int
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                builder.append(c);
            }
        }
        return Double.valueOf(builder.toString());
    }



    private double handleInput(String value){       //metod som tar hänsyn till om det är förp,st eller kg som produkten säljs i. Den avrundar om det är kr/st eller kr/förp

        double amount = extractDigits(value);

        String unitSuffix = shoppingItem.getProduct().getUnitSuffix();

        if((unitSuffix.equals("förp") || unitSuffix.equals("st") || unitSuffix.equals("burk") || unitSuffix.equals("påse"))){
            amount = Math.round(amount);
        } else{
            if (amount < 0.1){
                amount = 0.1;
            }
        }


        if(amount > 0){
            return amount;
        } else {
            return 1;
        }
    }

}
