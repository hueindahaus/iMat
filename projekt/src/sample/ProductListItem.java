package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.awt.*;
import java.io.IOException;

public class ProductListItem extends AnchorPane {       //TODO att fixa så att om man redan har produkten i varukorgen så ska inte ett nytt CartListItem skapas, utan istället ska amount bara adderas


    ShoppingItem shoppingItem;
    ProductSearchController parentController;
    double currentAmount = 1;

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
    @FXML
    private ImageView subButton;

    Image checkmark = new Image("images/cart48.png");



    Timeline transitionToCartIcon;
    Timeline transitionToImage;

    SequentialTransition transition;


    public ProductListItem(ShoppingItem shoppingItem, ProductSearchController parentController) {
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
                    currentAmount = handleInput(input);
                }
                displayToTextField();                     //display:ar t.ex. "1 kg"
            }
        });

        inputAmount.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när textrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    inputAmount.clear();
                }  else if(!newValue) {
                   displayToTextField();
                }
            }
        });

       

        //listItemImage.addEventHandler(MouseEvent.MOUSE_CLICKED,e -> flipCardToBack());  //lägger till en listener till bilden på kortet


        transitionToCartIcon = new Timeline(                                                                       //del 1 av animation när man lägger till en produkt i varukorgen
                new KeyFrame(Duration.millis(0.5), new KeyValue(listItemImage.imageProperty(), checkmark)),
                new KeyFrame(Duration.millis(0.5), new KeyValue(listItemImage.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(listItemImage.opacityProperty(), 1))
        );

        transitionToImage = new Timeline(                                                                           //del 2 av animation när man lägger till en produkt i varukorgen
                new KeyFrame(Duration.millis(0.5), new KeyValue(listItemImage.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(listItemImage.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(0.5), new KeyValue(listItemImage.imageProperty(), IMatDataHandler.getInstance().getFXImage(shoppingItem.getProduct())))
        );

        transition = new SequentialTransition(transitionToCartIcon,transitionToImage);      //lägger ihop de 2 olika timelines till en en timeline är de spelas efter varandra
        favouriteIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                IMatDataHandler dataHandler = IMatDataHandler.getInstance();
                if(dataHandler.isFavorite(shoppingItem.getProduct())){
                    dataHandler.removeFavorite(shoppingItem.getProduct());

                } else{
                    dataHandler.addFavorite(shoppingItem.getProduct());
                }
                changeFavIcon();
            }
        });  //lägger till en listener till bilden på kortet

        listItemImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                flipCardToBack();
            }
        });

        changeFavIcon();
    }
    
    public void changeFavIcon(){
        if(!IMatDataHandler.getInstance().isFavorite(shoppingItem.getProduct()))
            favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_border_black_18dp.png").toExternalForm()));
        else
            favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_black_18dp.png").toExternalForm()));

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
        disableSubButtonIfLowestAmount();


        double amount = currentAmount;
        String output = "";
        String unitSuffix = shoppingItem.getProduct().getUnitSuffix();

        if(unitSuffix.equals("förp") || unitSuffix.equals("st") || unitSuffix.equals("burk")){
            output = String.valueOf((int)amount) + " " + unitSuffix;
        } else {
            output = String.valueOf(currentAmount) + " " + unitSuffix;
        }

        inputAmount.textProperty().setValue(output); //sätter texten i inputrutan till t.ex. "1 kg"
    }

    @FXML
    private void onClickAddToCart(){      //när man trycker på varukorgsknappen i ett ProductListItem
        parentController.getCart().addToCart(shoppingItem,currentAmount);
        resetAmount();
        transition.play();
    }

    private void resetAmount(){
        currentAmount = 1;
        displayToTextField();
    }

    private void disableSubButtonIfLowestAmount(){
        if(currentAmount < 2){
            subButton.setDisable(true);
            subButton.setOpacity(0.5);
        } else {
            subButton.setDisable(false);
            subButton.setOpacity(1);
        }
    }

    @FXML
    protected void flipCardToBack(){  //metod som flippar kort
        cardBack.toFront();
    }

    @FXML
    protected void flipCardToFront(){  //metod som flippar tillbaka
        cardFront.toFront();
    }


    private double handleInput(String value){       //metod som tar hänsyn till om det är förp,st eller kg som produkten säljs i. Den avrundar om det är kr/st eller kr/förp

        double amount = extractDigits(value);

        String unitSuffix = shoppingItem.getProduct().getUnitSuffix();

        if((unitSuffix.equals("förp") || unitSuffix.equals("st") || unitSuffix.equals("burk"))){
            amount = Math.round(amount);
        } else if(unitSuffix.equals("kg") || unitSuffix.equals("l")){
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