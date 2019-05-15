package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class ProductListItem extends AnchorPane {       //TODO att fixa så att om man redan har produkten i varukorgen så ska inte ett nytt CartListItem skapas, utan istället ska amount bara adderas


    ShoppingItem shoppingItem;
    ProductSearchController parentController;
    int currentAmount = 1;

    @FXML
    private AnchorPane cardBack;
    @FXML
    private AnchorPane cardFront;

    @FXML
    private Label listItemTitle;
    @FXML
    private ImageView listItemImage;
    @FXML
    private Label priceAndUnit;
    @FXML
    private TextField inputAmount;
    @FXML
    private TextArea cardDescription;
    @FXML
    private TextArea cardIngredients;
    @FXML
    private ImageView favouriteIcon;



    public ProductListItem(ShoppingItem shoppingItem, ProductSearchController parentController, FavouriteManager favouriteManager) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.shoppingItem = shoppingItem;
        listItemImage.setImage(IMatDataHandler.getInstance().getFXImage(this.shoppingItem.getProduct()));               //produktens bild hittas genom IMatDataHandler
        listItemTitle.setText(shoppingItem.getProduct().getName());                                   //produktens titel
        priceAndUnit.setText(shoppingItem.getProduct().getPrice() + " " + shoppingItem.getProduct().getUnit());         //exempelvis  34 kr/kg
        displayToTextField();
        this.parentController=parentController;
        populateBack();


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

        listItemImage.addEventHandler(MouseEvent.MOUSE_CLICKED,e -> flipCardToBack());

        favouriteIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(favouriteManager.isFavourite(shoppingItem.getProduct())){
                    favouriteManager.removeFavourite(shoppingItem.getProduct());
                    favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_border_black_18dp.png").toExternalForm()));
                } else{
                    favouriteManager.addFavourite(shoppingItem.getProduct());
                    favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_black_18dp.png").toExternalForm()));
                }
            }
        });  //lägger till en listener till bilden på kortet


    }

    private void populateBack(){
        StringBuilder desc = new StringBuilder();
        desc.append(" " + shoppingItem.getProduct().getName() + "\n\nEkologisk: ");
        if(shoppingItem.getProduct().isEcological())
            desc.append("Ja\n");
        else
            desc.append("Nej\n");
        desc.append("Svensk: Kanske");
        cardDescription.appendText(desc.toString());
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
        inputAmount.textProperty().setValue(String.valueOf(currentAmount) + " " + shoppingItem.getProduct().getUnitSuffix()); //sätter texten i inputrutan till t.ex. "1 kg"
    }

    @FXML
    private void onClickAddToCart(){      //när man trycker på varukorgsknappen i ett ProductListItem
        parentController.getCart().addToCart(shoppingItem,currentAmount);
    }

    @FXML
    private void flipCardToBack(){  //metod som flippar kort
        cardBack.toFront();
    }

    @FXML
    private void flipCardToFront(){  //metd som flippar tillbaka
        cardFront.toFront();
    }
}