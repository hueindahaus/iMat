package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;

public class ProductListItem extends AnchorPane {

    IMatDataHandler dataHandler = IMatDataHandler.getInstance();
    Product product;
    ProductSearchController parentController;

    @FXML
    private Label listItemTitle;
    @FXML
    private ImageView listItemImage;
    @FXML
    private Label priceAndUnit;


    public ProductListItem(Product product, ProductSearchController parentController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        this.product = product;
        listItemImage.setImage(dataHandler.getFXImage(this.product));
        listItemTitle.setText(product.getName());
        priceAndUnit.setText(product.getPrice() + " " + product.getUnit());
        this.parentController=parentController;

    }


}