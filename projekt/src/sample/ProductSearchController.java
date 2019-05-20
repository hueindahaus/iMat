package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ProductCategory;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductSearchController implements Initializable {




    IMatDataHandler database = IMatDataHandler.getInstance();
    FilterEngine filterEngine = FilterEngine.getInstance();
    Map<String,ProductListItem> productListItemMap = new HashMap<String,ProductListItem>();
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

    double x,y;

    @FXML
    private RadioButton favorit;
    @FXML
    private RadioButton listButton;
    @FXML
    private RadioButton userButton;
    @FXML
    private RadioButton historyButton;

    private HistoryManager historyManager;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        cartAnchorPane.getChildren().add(cart);

        mainFlowPane.setHgap(28);
        mainFlowPane.setVgap(28);

        categoryScrollPane.addEventFilter(ScrollEvent.SCROLL,new EventHandler<ScrollEvent>() {      //disablar horizontell scroll
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
                if(event.getDeltaX() != 0){             //om eventets hastighet i x-led inte är 0
                    event.consume();                    //så ska eventet consumas
                }
            }
        });


        for(Product product : database.getProducts()){//loopar igenom samtliga produkter som finns i appen
            ShoppingItem shoppingItem = new ShoppingItem(product,1);
            ProductListItem productListItem = new ProductListItem(shoppingItem, this);     //skapar ett ProductListItem för varje produkt
            CartListItem cartListItem = new CartListItem(shoppingItem, this, getCart());        //skapar ett CartListItem för varje produkt
            cart.getCartListItemMap().put(product.getName(),cartListItem);          //lägger varje CartListItem i en Map som finns i Cart
            productListItemMap.put(product.getName(),productListItem);          //stoppar in listitem:et som vi nyss skapat i vår hashmap och kopplar den till namnet på produkten
        }
        update();


        populateCategoryView();

        searchBar.textProperty().addListener(new ChangeListener<String>() {     //changelistener för sökrutan
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                filterEngine.setSearchCategory(null);       //när man söker vill man inte bara söka inom t.ex. kategorin som vi valt, vi resettar dessa så att alla produkter som man söker på kommer upp
                filterEngine.setSearchIsEcological(false);
                filterEngine.setSearchPriceMax(0);
                filterEngine.setSearchPriceMin(0);

                filterEngine.setSearchString(newValue);     //sätter det nya värdet i sökrutan i filterEngine
                if(newValue.isEmpty()){                     //om det nya värdet är tomt så resettar vi searchString i filterEngine
                    filterEngine.setSearchString(null);
                }
                update();
            }
        });

        searchBar.focusedProperty().addListener(new ChangeListener<Boolean>() {     //när sökrutan får fokus ska allt i rutan selectas
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    Platform.runLater(new Runnable() {      // Gör det möjligt att selecta allt när sökrutan får fokus (förstår mig inte helt på denna)
                        public void run() {
                            searchBar.selectAll();
                        }
                    });
                }
            }
        });

        historyManager = new HistoryManager(productListItemMap, mainFlowPane,this);
        implementSideBar();

        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(87)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(30)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(77)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(8)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(34)));
        database.placeOrder();
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(87)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(30)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(77)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(8)));
        database.getShoppingCart().addItem(new ShoppingItem(database.getProduct(34)));
        database.placeOrder();
        System.out.println(database.getOrders().size());
    }


    private void implementSideBar(){
        fixRadioButtonStyle(favorit);
        fixRadioButtonStyle(listButton);
        fixRadioButtonStyle(historyButton);
        fixRadioButtonStyle(userButton);

        historyButton.setOnMouseClicked(e -> historyManager.getHistory());
        userButton.setOnMouseClicked(event -> {
          mainFlowPane.getChildren().clear();
          mainFlowPane.getChildren().add(new ChangeUserInfoWindow());
        });
    }

    private void fixRadioButtonStyle(RadioButton button){
        button.getStyleClass().remove("radio-button");
        button.getStyleClass().add("toggle-button");
        button.setToggleGroup(toggleGroup);
    }

    @FXML
    public void displayFavourites(){
        mainFlowPane.getChildren().clear();
        for(Product product: database.favorites()){
            if(productListItemMap.containsKey(product.getName())){
                mainFlowPane.getChildren().add(productListItemMap.get(product.getName()));
            }
        }
    }

    public void update(){                                                       //uppdaterar mainFlowPane (uppdaterar produktrutan, basically)
        mainFlowPane.getChildren().clear();                                     //clearar mainFlowPane
        List<Product> filteredProductList = filterEngine.filter();      //vi får en lista med de varor som ska visas i rutan från filterEngine

        for(Product product: filteredProductList){
            ProductListItem listItem = productListItemMap.get(product.getName());   //vi extraherar productListem från vår "Map". Detta gör det möjligt att inte behöva göra nya ProductListItems varje gång vi uppdaterar vyn
            mainFlowPane.getChildren().add(listItem);
        }

    }



    public void populateCategoryView(){
        categoryFlowPane.getChildren().clear();
        for(ProductCategory category: ProductCategory.values()){        //loopar genom alla kategorier som är enum
            CategoryListItem categoryListItem = new CategoryListItem(category, this);
            categoryFlowPane.getChildren().add(categoryListItem);
        }
    }


    public void filterByCategoryAndUpdate(ProductCategory category){
        searchBar.clear();                          //om man har text i sökrutan och trycker på en kategori ska texten i sökrutan försvinna och vi filtrerar endast utifrån vilken kategori som vi tryckt på
        filterEngine.setSearchCategory(category);
        update();
    }

    @FXML
    public void dragHeader(MouseEvent event){   // Om man drar i whitespace i den övre panelen där sökrutan finns så sla hela fönstret också dras med
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void pressHeader(MouseEvent event){  // denna metoden behövs för att kunna dra runt rutan genom att dra runt den övre panelen
        x = event.getSceneX();
        y = event.getSceneY();
    }

    public Cart getCart(){
        return cart;
    }





}

