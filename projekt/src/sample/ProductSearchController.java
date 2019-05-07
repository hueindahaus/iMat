package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ProductCategory;

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

    @FXML
    private FlowPane mainFlowPane;      //Detta är huvudrutan i mitten, där varor tar plats
    @FXML
    private FlowPane categoryFlowPane;      //Detta är rutan till vänster där man filtrerar varor utifrån kategorier
    @FXML
    private ScrollPane categoryScrollPane;  //ScrollPane för categoruFlowPane
    @FXML
    private ScrollPane chartScrollPane; //ScrollPane för varukorgen
    @FXML
    private TextField searchBar;        //Själva textrutan(sökrutan)





    @Override
    public void initialize(URL url, ResourceBundle rb){



        for(Product product : database.getProducts()){                           //loopar igenom samtliga produkter som finns i appen
            ProductListItem productListItem = new ProductListItem(product, this);     //skapar ett listitem för varje produkt
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






}

