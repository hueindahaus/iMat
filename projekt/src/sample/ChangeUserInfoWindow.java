package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.Customer;
import se.chalmers.cse.dat216.project.IMatDataHandler;


import java.io.IOException;

public class ChangeUserInfoWindow extends AnchorPane {

    @FXML private TextField forNameChange;
    @FXML private TextField lastNameChange;
    @FXML private TextField emailChange;
    @FXML private TextField adrChange;
    @FXML private TextField postNrChange;
    @FXML private TextField phoneChange;
    @FXML private TextField mobilChange;

    @FXML private Button saveButton;

    public ChangeUserInfoWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("changeUserInfoWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch(IOException exception){
            throw new RuntimeException(exception);
        }

        loadUserInfo();

        saveButton.setOnMouseClicked(e -> saveInfo());
    }

    private void saveInfo(){
        Customer customer = IMatDataHandler.getInstance().getCustomer();
        customer.setFirstName(forNameChange.getText());
        customer.setLastName(lastNameChange.getText());
        if(emailChange.getText().contains("@"))
            customer.setEmail(emailChange.getText());
        customer.setAddress(adrChange.getText());
        customer.setPostCode(postNrChange.getText());
        customer.setPhoneNumber(phoneChange.getText());
        customer.setMobilePhoneNumber(mobilChange.getText());
    }

    private void loadUserInfo(){
        Customer customer = IMatDataHandler.getInstance().getCustomer();
        forNameChange.setText(customer.getFirstName());
        lastNameChange.setText(customer.getLastName());
        emailChange.setText(customer.getEmail());
        adrChange.setText(customer.getAddress());
        postNrChange.setText(customer.getPostCode());
        phoneChange.setText(customer.getPhoneNumber());
        mobilChange.setText(customer.getMobilePhoneNumber());
    }
}
