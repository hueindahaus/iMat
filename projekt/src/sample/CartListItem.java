package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.text.DecimalFormat;

public class CartListItem extends AnchorPane {

    ShoppingItem shoppingItem;
    ProductSearchController parentController;

    Timeline slideIn;


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
    @FXML
    private ImageView decButton;
    @FXML
    private ImageView incButton;

    Image incButtonImage = new Image("icons/inc_button.png");
    Image incButtonImageGreen = new Image("icons/inc_button_green.png");
    Image decButtonImage = new Image("icons/dec_button.png");
    Image decButtonImageRed = new Image("icons/dec_button_red.png");


    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public CartListItem(ShoppingItem shoppingItem, ProductSearchController parentController, Cart cart){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml_files/cart_listitem.fxml"));
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

        translateXProperty().setValue(260);


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


        decButton.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    decButton.setImage(decButtonImageRed);
                } else {
                    decButton.setImage(decButtonImage);
                }
            }
        });

        incButton.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    incButton.setImage(incButtonImageGreen);
                } else {
                    incButton.setImage(incButtonImage);
                }
            }
        });


    slideIn = new Timeline(
            new KeyFrame(Duration.seconds(0.2), new KeyValue(this.translateXProperty(), 0))     //animation som slide:ar in varan i varukorgen. (Denna kallas i klassen Cart, i addToCart-metoden)
    );


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
