package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ProductCategory;

import java.io.IOException;

public class CategoryListItem extends AnchorPane {

    ProductSearchController parentController;

    @FXML
    public RadioButton categoryButton;

    private ProductCategory category;

    public CategoryListItem(ProductCategory category,ProductSearchController parentController ) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }
        this.parentController = parentController;
        categoryButton.setText(tranlateCategoryName(category));

        this.category = category;
        categoryButton.setToggleGroup(parentController.toggleGroup);        //Lägger till varje kategoriknapp i en och samma togglegroup

        categoryButton.getStyleClass().remove("radio-button");
        categoryButton.getStyleClass().add("toggle-button");        //Gör så att det inte ser ut som en radiobutton, istället ser det ut som en togglebutton
    }

    public ProductCategory getCategory(){
        return category;
    }

    @FXML
    public void onClickCategory(){
        parentController.filterByCategoryAndUpdate(category);
    }   //när man trycker på en kategori i listan så ska uppdatera produktrutan

    public ToggleButton getCategoryButton(){
        return categoryButton;
    }

    public String tranlateCategoryName(ProductCategory category){
        switch (category.name()){
            case "POD":
                return "Böner, ärtor och linser";
            case "BREAD":
                return "Bröd";
            case "BERRY":
                return "Bär";
            case "CITRUS_FRUIT":
                return "Citrusfrukter";
            case "HOT_DRINKS":
                return "Varma drycker";
            case "COLD_DRINKS":
                return "Kalla drycker";
            case "EXOTIC_FRUIT":
                return "Exotiska frukter";
            case "FISH":
                return "Fisk";
            case "VEGETABLE_FRUIT":
                return "Grönsaker";
            case "CABBAGE":
                return "Kål";
            case "MEAT":
                return "Kött";
            case "DAIRIES":
                return "Mejeri";
            case "MELONS":
                return "Meloner";
            case "FLOUR_SUGAR_SALT":
                return "Mjöl, socker och salt";
            case "NUTS_AND_SEEDS":
                return "Nötter och frön";
            case "PASTA":
                return "Pasta";
            case "POTATO_RICE":
                return "Potatis och ris";
            case "ROOT_VEGETABLE":
                return "Rotfrukter";
            case "FRUIT":
                return "Frukt";
            case "SWEET":
                return "Godis";
            case "HERB":
                return "Örter";
            default:
                return "catergori namn saknas";
        }
    }
}
