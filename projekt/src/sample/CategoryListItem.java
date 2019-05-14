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
        categoryButton.setText(category.name());

        this.category = category;
        categoryButton.setToggleGroup(parentController.toggleGroup);        //Lägger till varje kategoriknapp i en och samma togglegroup

        categoryButton.getStyleClass().remove("radio-button");
        categoryButton.getStyleClass().add("toggle-button");        //Gör så att det inte ser ut som en radiobutton, istället ser det ut som en togglebutton
        categoryButton.getStyleClass().add("lightBlue");
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

}
