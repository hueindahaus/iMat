package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ShoppingListPage extends AnchorPane {

    private static ShoppingListPage singleton;

    private ShoppingListPage(){

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml_files/shopping_list_page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }

    protected static ShoppingListPage getInstance(){
        if(singleton == null){
            singleton = new ShoppingListPage();
        }
        return singleton;
    }



}
