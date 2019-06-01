package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Cart extends AnchorPane {

    @FXML
    private FlowPane flowPane;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label sumLabel;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");


    private static Cart singleton;
    ProductSearchController parentController;
    ShoppingCart shoppingCart = IMatDataHandler.getInstance().getShoppingCart();    //ShoppingCart är en singleton
    Map<String,CartListItem> cartListItemMap = new HashMap<String,CartListItem>();

    public static Cart getInstance(ProductSearchController parentController){      //Cart är en singleton (detta är som en constructor)
        if(singleton == null){
            singleton = new Cart(parentController);
        }
        return singleton;
    }



    private Cart(ProductSearchController parentController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try{
            fxmlLoader.load();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

        this.parentController = parentController;

        scrollPane.setPrefViewportWidth(260);       //sätter viewportwidth så att ScrollBar i ScrollPane hamnar utanför det synliga området
        scrollPane.setMinViewportWidth(260);

        scrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {         //disablar horizontell scroll
            @Override
            public void handle(ScrollEvent event) {     //scrollevent
                if(event.getDeltaX() != 0){             //om eventets hastighet i x-led inte är 0
                    event.consume();                    //så ska eventet consumas
                }
            }
        });


        shoppingCart.addShoppingCartListener(new ShoppingCartListener() {           //lägger till en listener på ShoppingCart - Metoden ska updatera flowPane varje gång ShoppingCart ändras
            @Override
            public void shoppingCartChanged(CartEvent cartEvent) {
                updateFlowPane();

                if(shoppingCart.getItems().isEmpty() && parentController.isCheckoutMode() && !parentController.paymentWizard.isInReceiptMode()){     //om vi är i checkoutmode och cart blir tom så ska vi åter tillbaka till sortimentet (man ska inte kunna utföra ett köp när man inte har några varor i varukorgen) däremot töms cart när man place:ar en order, i detta fallet ska vi inte tillbaka till sortimentet, utan man ska fortsätta till kvittot
                    parentController.checkoutModeSwitch();
                }

                if(!shoppingCart.getItems().isEmpty()){                         //om det finns saker i varukorgen ska man kunna ta sig till betalning. Annars så ska man inte kunna göra det
                    parentController.disableCheckOutButton(false);
                } else if(shoppingCart.getItems().isEmpty()){
                    parentController.disableCheckOutButton(true);
                }




            }
        });

        flowPane.setVgap(8);

        shoppingCart.getItems().clear();
        updateFlowPane();

    }

    private void updateFlowPane(){                      //uppdaterar flowpane med endast de produkter som finns i ShoppingCart (uppdaterar även antal av varje produkt)
        flowPane.getChildren().clear();
        double sum = 0;
        for(ShoppingItem item : shoppingCart.getItems()){
            CartListItem cartListItem = cartListItemMap.get(item.getProduct().getName());
            flowPane.getChildren().add(0,cartListItem);
            cartListItem.updateTextField();
            sum += item.getTotal();
        }
        sumLabel.setText("Summa: " + decimalFormat.format(sum) + " kr");
    }


    public void addToCart(ShoppingItem shoppingItem, double amount){        //lägger till ett ShoppingItem i ShoppingCart eller så lägger vi bara på antalet av en produkt - denna metoden körs när vi trycker på varukorgsknappen i ett ProductListItem
        if(shoppingCart.getItems().contains(shoppingItem)){
            shoppingItem.setAmount(shoppingItem.getAmount() + amount);      //om produkten redan finns i varukorgen så ska vi bara addera amount när vi "lägger till flera" av en produkt
        } else {
            shoppingCart.addItem(shoppingItem);
            cartListItemMap.get(shoppingItem.getProduct().getName()).slideIn.play();
            shoppingItem.setAmount(amount);     //om produkten inte finns i varukorgen ska lägga till den i ShoppingCart och även sätta antalet som vi har valt
        }
        updateFlowPane();
    }

    public void increaseAmount(ShoppingItem shoppingItem){
        shoppingItem.setAmount(shoppingItem.getAmount() + 1);
        updateFlowPane();
    }

    public void decreaseAmount(ShoppingItem shoppingItem){
        if(shoppingItem.getAmount() > 1) {                          //om antalet är mer än 1 ska antalet subtraheras med 1
            shoppingItem.setAmount(shoppingItem.getAmount() - 1);
        } else {                                                    //om antalet är 1 eller mindre ska item:et bara försvinna ur varukorgen
            shoppingCart.removeItem(shoppingItem);
        }
        updateFlowPane();
    }

    public void removeAllOfItem(ShoppingItem shoppingItem){
        shoppingCart.removeItem(shoppingItem);
        shoppingItem.setAmount(1);
        updateFlowPane();
    }

    public Map<String,CartListItem> getCartListItemMap(){
        return cartListItemMap;
    }

}
