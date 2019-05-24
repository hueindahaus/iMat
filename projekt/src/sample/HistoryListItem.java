package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class HistoryListItem extends AnchorPane {       //TODO att fixa så att om man redan har produkten i varukorgen så ska inte ett nytt CartListItem skapas, utan istället ska amount bara adderas


    private ShoppingItem oldShoppingItem;
    private ShoppingItem shoppingItem;
    private ProductSearchController parentController;
    private int currentAmount = 1;

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
    private TextArea cardDescription;
    @FXML
    private TextArea cardIngredients;
    @FXML
    private ImageView favouriteIcon;
    @FXML
    private Label amountLabel;

    Image checkmark = new Image("images/cart48.png");

    Timeline transitionToCartIcon;
    Timeline transitionToImage;

    SequentialTransition transition;


    public HistoryListItem(ShoppingItem oldShoppingItem, ShoppingItem shoppingItem, ProductSearchController parentController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("history_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.oldShoppingItem = oldShoppingItem;
        this.shoppingItem = shoppingItem;
        listItemImage.setImage(IMatDataHandler.getInstance().getFXImage(this.oldShoppingItem.getProduct()));               //produktens bild hittas genom IMatDataHandler
        listItemTitle.setText(oldShoppingItem.getProduct().getName());                                   //produktens titel
        priceAndUnit.setText(oldShoppingItem.getProduct().getPrice() + " " + oldShoppingItem.getProduct().getUnit());         //exempelvis  34 kr/kg
        this.parentController = parentController;
        populateBack();

        amountLabel.setText(oldShoppingItem.getAmount() + " " + oldShoppingItem.getProduct().getUnitSuffix());

        transitionToCartIcon = new Timeline(                                                                       //del 1 av animation när man lägger till en produkt i varukorgen
                new KeyFrame(Duration.millis(1), new KeyValue(listItemImage.imageProperty(), checkmark)),
                new KeyFrame(Duration.millis(1), new KeyValue(listItemImage.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(listItemImage.opacityProperty(), 1))
        );

        transitionToImage = new Timeline(                                                                           //del 2 av animation när man lägger till en produkt i varukorgen
                new KeyFrame(Duration.millis(1), new KeyValue(listItemImage.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(listItemImage.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1), new KeyValue(listItemImage.imageProperty(), IMatDataHandler.getInstance().getFXImage(shoppingItem.getProduct())))
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

        changeFavIcon();
    }
    
    public void changeFavIcon(){
        if(!IMatDataHandler.getInstance().isFavorite(oldShoppingItem.getProduct()))
            favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_border_black_18dp.png").toExternalForm()));
        else
            favouriteIcon.setImage(new Image(getClass().getResource("../icons/baseline_favorite_black_18dp.png").toExternalForm()));

    }

    private void populateBack(){
        StringBuilder desc = new StringBuilder();
        desc.append(" " + oldShoppingItem.getProduct().getName() + "\n\nEkologisk: ");
        if(oldShoppingItem.getProduct().isEcological())
            desc.append("Ja\n");
        else
            desc.append("Nej\n");
        desc.append("Svensk: Kanske");
        cardDescription.appendText(desc.toString());
    }

    @FXML
    private void onClickAddToCart(){      //när man trycker på varukorgsknappen i ett ProductListItem
        parentController.getCart().addToCart(shoppingItem,(int)oldShoppingItem.getAmount());
        transition.play();

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