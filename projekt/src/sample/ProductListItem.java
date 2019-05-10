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

import java.io.IOException;

public class ProductListItem extends AnchorPane {       //TODO att fixa så att om man redan har produkten i varukorgen så ska inte ett nytt CartListItem skapas, utan istället ska amount bara adderas


    Product product;
    ProductSearchController parentController;


    @FXML
    private Label listItemTitle;
    @FXML
    private ImageView listItemImage;
    @FXML
    private Label priceAndUnit;
    @FXML
    private TextField inputAmount;

    private int currentAmount = 1;


    public ProductListItem(Product product, ProductSearchController parentController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.product = product;
        listItemImage.setImage(IMatDataHandler.getInstance().getFXImage(this.product));               //produktens bild hittas genom IMatDataHandler
        listItemTitle.setText(product.getName());                                   //produktens titel
        priceAndUnit.setText(product.getPrice() + " " + product.getUnit());         //exempelvis  34 kr/kg
        displayToTextField();
        this.parentController=parentController;


        inputAmount.setOnAction(new EventHandler<ActionEvent>() {       //actionhandler som agerar när man trycker enter i en textfield
            @Override
            public void handle(ActionEvent event) {
                String input = inputAmount.getText();   //vi sparar det som står i textfield i en variabel
                if(input.isEmpty()){                    //om strängen är empty ska vi sätta 1 som default värde
                    currentAmount = 1;
                } else {                                //annars ska vi extrahera ut endast siffrorna i textfield och sätta currentAmount till vad som står
                    currentAmount = extractDigits(input);
                }
                displayToTextField();                     //display:ar t.ex. "1 kg"
            }
        });

        inputAmount.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när textrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    Platform.runLater(new Runnable() {      // Gör det möjligt att selecta allt när textrutan får fokus (förstår mig inte helt på denna)
                        public void run() {
                            inputAmount.selectAll();
                        }
                    });
                }
            }
        });

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


    @FXML
    private void addOne(){      //adderar en produkt i amount
        currentAmount++;
        displayToTextField();
    }

    @FXML
    private void subtractOne(){     //subtraherar en produkt i amount
        if(currentAmount > 1) {
            currentAmount--;
            displayToTextField();
        }
    }

    private void displayToTextField(){
        inputAmount.textProperty().setValue(String.valueOf(currentAmount) + " " + product.getUnitSuffix()); //sätter texten i inputrutan till t.ex. "1 kg"
    }

    @FXML
    private void addToCart(){                   //metod som skapar ett nytt Cart-listitem och sedan använder parentcontrollerns metod "addToCartFlowPane" för att lägga det nyskapta objektet i flowpane
        CartListItem item = new CartListItem(product,currentAmount,parentController);
        parentController.addToCartFlowPane(item);
    }
}