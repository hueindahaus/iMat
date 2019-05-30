package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.*;

import java.net.URL;
import java.util.*;
import java.util.List;

public class ProductSearchController implements Initializable {

    IMatDataHandler database = IMatDataHandler.getInstance();
    FilterEngine filterEngine = FilterEngine.getInstance();
    Map<String, ProductListItem> productListItemMap = new HashMap<String, ProductListItem>();
    Map<String, ShoppingItem> shoppingItemMap = new HashMap<String, ShoppingItem>();
    Cart cart = Cart.getInstance(this);

    ToggleGroup toggleGroup = new ToggleGroup();    //togglegroup för knapparna på vänstra sidan

    @FXML
    private FlowPane mainFlowPane;      //Detta är huvudrutan i mitten, där varor tar plats
    @FXML
    private FlowPane categoryFlowPane;      //Detta är rutan till vänster där man filtrerar varor utifrån kategorier
    @FXML
    private AnchorPane cartAnchorPane;
    @FXML
    private ScrollPane categoryScrollPane;  //ScrollPane för categoruFlowPane
    @FXML
    private ScrollPane mainScrollPane; //ScrollPane för huvudfönstret
    @FXML
    private TextField searchBar;        //Själva textrutan(sökrutan)
    @FXML
    private ImageView cartButton;       //knappen för att gömma/visa varukorgen
    @FXML
    private Label cartLabel;            //Texten "Varukorg" uppe o högre hörnet
    @FXML
    private AnchorPane paymentAnchorPane;   //Anchorpane för alla betalningsvyerna
    @FXML
    private AnchorPane comingSoonPane;          //Rutan för inköpslistor, (för nuvarande en coming soon-etikett)
    @FXML
    private Button checkoutButton;       //Knappen i nedre högra hörnet för att ta sig till betalning/avsluta betalning
    @FXML
    public ImageView step1;
    @FXML
    public ImageView step2;
    @FXML
    public ImageView step3;



    double x, y;

    private boolean cartIsHidden = false;
    private boolean isCheckoutMode = false;

    Timeline hideCart;
    Timeline showCart;
    Timeline showWizard;
    Timeline hideWizard;

    PaymentWizard paymentWizard = new PaymentWizard(this);

    Customer customer = IMatDataHandler.getInstance().getCustomer();
    @FXML
    private RadioButton favorit;
    @FXML
    private RadioButton listButton;
    @FXML
    private RadioButton userButton;
    @FXML
    private RadioButton historyButton;

    @FXML
    private ImageView homeButton;

    private HistoryManager historyManager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {



        categoryScrollPane.setPrefViewportWidth(260);   //sätter viewportdith så att ScrollBar i ScrollPane hamnar utanför det synliga området
        categoryScrollPane.setMinViewportWidth(260);

        categoryScrollPane.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    categoryScrollPane.setPrefViewportWidth(255);
                    categoryScrollPane.setMinViewportWidth(255);
                } else {
                    categoryScrollPane.setPrefViewportWidth(260);
                    categoryScrollPane.setMinViewportWidth(260);
                }
            }
        });



        cartAnchorPane.getChildren().add(cart); //lägger till klassen "Cart" som en child i den Anchorpane som avser varukorgen

        mainFlowPane.setHgap(28);
        mainFlowPane.setVgap(28);

        categoryScrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {      //disablar horizontell scroll
            @Override
            public void handle(ScrollEvent event) {     //scrollevent
                if (event.getDeltaX() != 0) {           //om eventets hastighet i x-led inte är 0
                    event.consume();                    //så ska eventet consumas
                }
            }
        });


        mainScrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {         //disablar horizontell scroll
            @Override
            public void handle(ScrollEvent event) {     //scrollevent
                if (event.getDeltaX() != 0) {             //om eventets hastighet i x-led inte är 0
                    event.consume();                    //så ska eventet consumas
                }
            }
        });


        for (Product product : database.getProducts()) {//loopar igenom samtliga produkter som finns i appen
            ShoppingItem shoppingItem = new ShoppingItem(product, 1);
            shoppingItemMap.put(shoppingItem.getProduct().getName(), shoppingItem);          //lägger alla shoppingItems i en Map
            ProductListItem productListItem = new ProductListItem(shoppingItem, this);     //skapar ett ProductListItem för varje produkt
            CartListItem cartListItem = new CartListItem(shoppingItem, this, getCart());        //skapar ett CartListItem för varje produkt
            cart.getCartListItemMap().put(product.getName(), cartListItem);          //lägger varje CartListItem i en Map som finns i Cart
            productListItemMap.put(product.getName(), productListItem);          //stoppar in listitem:et som vi nyss skapat i vår hashmap och kopplar den till namnet på produkten
        }

        populateCategoryView();

        searchBar.setOnAction(event -> {

            removeToggleFromSidePanel();
            filterEngine.resetFilterEngine();

            filterEngine.setSearchString(searchBar.getText());     //sätter det nya värdet i sökrutan i filterEngine
            if (searchBar.getText().isEmpty()) {                     //om det nya värdet är tomt så hamnar man i startvyn
                mainFlowPane.getChildren().clear();
                mainFlowPane.getChildren().add(new MainPage(productListItemMap, this));
            } else {
                update();
            }
        });

        searchBar.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när sökrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Platform.runLater(new Runnable() {      // Gör det möjligt att selecta allt när sökrutan får fokus (förstår mig inte helt på denna)
                        public void run() {
                            searchBar.selectAll();
                        }
                    });
                }
            }
        });

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {     //gör så att comingSoonPane endast visas när den är togglad  OBS om man togglar den och sedan trycker på loggan så försvinner den ej. Detta är åtgärdat i metoden som behandlar klick på loggan
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue.getToggleGroup().getSelectedToggle().equals(listButton)){
                    comingSoonPane.toFront();
                } else {
                    comingSoonPane.toBack();
                }
            }
        });



        hideCart = new Timeline(                                                                                        //animation för när man gömmer varukorgen
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartAnchorPane.layoutXProperty(), 1440)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(mainScrollPane.prefWidthProperty(), 1440)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(mainFlowPane.prefWidthProperty(), 1180)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(comingSoonPane.prefWidthProperty(), 1180)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartButton.layoutXProperty(), 1354)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartLabel.layoutXProperty(), 1440))
        );
        showCart = new Timeline(                                                                                         //animation för när man tar fram varukorgen
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartAnchorPane.layoutXProperty(), 1180)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(mainScrollPane.prefWidthProperty(), 920)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(mainFlowPane.prefWidthProperty(), 920)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(comingSoonPane.prefWidthProperty(), 920)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartButton.layoutXProperty(), 1206)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cartLabel.layoutXProperty(), 1266))
        );

        paymentAnchorPane.translateXProperty().setValue(-1310);

        showWizard = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(paymentAnchorPane.translateXProperty(), -130))
        );

        hideWizard = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(paymentAnchorPane.translateXProperty(), -1310))
        );

        paymentAnchorPane.getChildren().add(paymentWizard);

        disableCheckOutButton(true);                //när man startar programmet ska man inte kunna ta sig till betalning utan att ha lagt produkter i varukorgen

        historyManager = new HistoryManager(productListItemMap, mainFlowPane, this);
        implementSideBar();
        mainFlowPane.getChildren().clear();
        mainFlowPane.getChildren().add(new MainPage(productListItemMap, this));




    }


    private void implementSideBar() {
        fixRadioButtonStyle(favorit);
        fixRadioButtonStyle(listButton);
        fixRadioButtonStyle(historyButton);
        fixRadioButtonStyle(userButton);

        historyButton.setOnMouseClicked(e -> historyManager.getHistory());
        userButton.setOnMouseClicked(event -> {
            mainFlowPane.getChildren().clear();
            mainFlowPane.getChildren().add(new ChangeUserInfoWindow());
        });
        homeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            comingSoonPane.toBack();
            removeToggleFromSidePanel();                //tar bort togglen(om den finns) i knapparna på vänstra sidan
            mainFlowPane.getChildren().clear();
            mainFlowPane.getChildren().add(new MainPage(productListItemMap, this));
        });
    }

    private void fixRadioButtonStyle(RadioButton button) {
        button.getStyleClass().remove("radio-button");
        button.getStyleClass().add("toggle-button");
        button.setToggleGroup(toggleGroup);
    }

    @FXML
    public void displayFavourites() {
        mainFlowPane.getChildren().clear();
        for (Product product : database.favorites()) {
            if (productListItemMap.containsKey(product.getName())) {
                mainFlowPane.getChildren().add(productListItemMap.get(product.getName()));
                productListItemMap.get(product.getName()).flipCardToFront();                                       //  flippar på kortet så att framsidan alltid visas när man uppdaterar sidan
                productListItemMap.get(product.getName()).changeFavIcon();
            }
        }
    }

    private void removeToggleFromSidePanel(){
        ObservableList<Toggle> toggles= toggleGroup.getToggles();
        for(Toggle toggle: toggles){
            if(toggle.isSelected()){
                toggle.setSelected(false);
            }
        }
    }

    public void update() {                                                       //uppdaterar mainFlowPane (uppdaterar produktrutan, basically)
        mainFlowPane.getChildren().clear();                                     //clearar mainFlowPane
        List<Product> filteredProductList = filterEngine.filter();      //vi får en lista med de varor som ska visas i rutan från filterEngine

        for (Product product : filteredProductList) {
            ProductListItem listItem = productListItemMap.get(product.getName());   //vi extraherar productListem från vår "Map". Detta gör det möjligt att inte behöva göra nya ProductListItems varje gång vi uppdaterar vyn
            listItem.flipCardToFront();                                                     //  flippar på kortet så att framsidan alltid visas när man uppdaterar sidan
            mainFlowPane.getChildren().add(listItem);
            listItem.changeFavIcon();
        }

    }


    public void populateCategoryView() {
        categoryFlowPane.getChildren().clear();
        for (ProductCategory category : ProductCategory.values()) {        //loopar genom alla kategorier som är enum
            CategoryListItem categoryListItem = new CategoryListItem(category, this);
            categoryFlowPane.getChildren().add(categoryListItem);
        }
    }


    public void filterByCategoryAndUpdate(ProductCategory category) {
        searchBar.clear();                          //om man har text i sökrutan och trycker på en kategori ska texten i sökrutan försvinna och vi filtrerar endast utifrån vilken kategori som vi tryckt på
        filterEngine.resetFilterEngine();
        filterEngine.setSearchCategory(category);
        update();
    }

    @FXML
    public void dragHeader(MouseEvent event) {   // Om man drar i whitespace i den övre panelen där sökrutan finns så sla hela fönstret också dras med
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void pressHeader(MouseEvent event) {  // denna metoden behövs för att kunna dra runt rutan genom att dra runt den övre panelen
        x = event.getSceneX();
        y = event.getSceneY();
    }

    public Cart getCart() {
        return cart;
    }


    @FXML
    public void toggleCart() {
        if (!cartIsHidden && !isCheckoutMode) {             //om varukorgen inte är gömd(dvs syns) och om vi inte är i betalningsläge så ska vi gömma den (detta sker genom att sätta varukorgen på ett x-värde där den inte syns)
            hideCart.play();
            cartIsHidden = true;
        } else {                        //om varukorgen är gömd så ska vi visa den
            showCart.play();
            cartIsHidden = false;
        }
    }

    @FXML
    public void checkoutModeSwitch() {
        if (!isCheckoutMode) {
            paymentAnchorPane.toFront();
            showWizard.play();
            paymentWizard.customerInfoPaneToFront();
            checkoutButton.textProperty().setValue("Till Sortiment");   //ändrar text på button
            searchBar.setVisible(false);
            homeButton.setDisable(true);
            isCheckoutMode = true;
        } else {
            //paymentAnchorPane.toBack();
            showWizard.setAutoReverse(true);
            hideWizard.play();
            checkoutButton.textProperty().setValue("Till Betalning");
            step1.setVisible(false);
            step2.setVisible(false);
            step3.setVisible(false);
            searchBar.setVisible(true);
            homeButton.setDisable(false);
            isCheckoutMode = false;
        }
        checkoutButtonSwitch();
    }




    protected void checkoutButtonSwitch() {       //bytar färgtema på knappen som tar en till/från varukorgen när hover inte är aktiv
        if (!isCheckoutMode) {
            checkoutButton.getStyleClass().remove("to-stock-button");
            checkoutButton.getStyleClass().add("to-checkout-button");

        } else if (isCheckoutMode) {
            checkoutButton.getStyleClass().add("to-checkout-button");
            checkoutButton.getStyleClass().add("to-stock-button");
        }
    }


    protected void disableCheckOutButton(boolean value) {
        checkoutButton.setDisable(value);
    }

    Map<String, ShoppingItem> getShoppingItemMap() {
        return shoppingItemMap;
    }

    @FXML
    public void setLogoToColored(){
        homeButton.setImage(new Image(getClass().getResource("../icons/imatLogoColor.png").toExternalForm()));
    }

    @FXML
    public void setLogoToWhite(){
        homeButton.setImage(new Image(getClass().getResource("../icons/imatLogo.png").toExternalForm()));
    }

    protected boolean isCheckoutMode(){
        return isCheckoutMode;
    }

}
