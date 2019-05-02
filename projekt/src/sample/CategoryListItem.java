package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ProductCategory;

import java.io.IOException;

public class CategoryListItem extends AnchorPane {

    ProductSearchController parentController;
    IMatDataHandler dataHandler = IMatDataHandler.getInstance();

    @FXML
    public Label CategoryTitle;

    @FXML
    public ImageView CategoryImage;

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
        CategoryTitle.setText(category.name());

        this.category = category;
    }

    public ProductCategory getCategory(){
        return category;
    }

    @FXML
    public void onClickCategory(){
        parentController.filterByCategoryAndUpdate(category);
    }   //när man trycker på en kategori i listan så ska uppdatera produktrutan


}
