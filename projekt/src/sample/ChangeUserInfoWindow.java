package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import se.chalmers.cse.dat216.project.Customer;
import se.chalmers.cse.dat216.project.IMatDataHandler;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChangeUserInfoWindow extends AnchorPane {

    @FXML private TextField forNameChange;
    @FXML private TextField lastNameChange;
    @FXML private TextField emailChange;
    @FXML private TextField adrChange;
    @FXML private TextField postNrChange;
    @FXML private TextField phoneChange;
    @FXML private TextField mobilChange;

    @FXML private ImageView errorMailIcon;
    @FXML private ImageView errorPostCodeIcon;
    @FXML private ImageView errorPhoneIcon;
    @FXML private ImageView errorMobileIcon;
    @FXML private Label saveMessage;

    Map<TextField, ImageView> errorIconMap = new HashMap<TextField,ImageView>();

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

        errorIconMap.put(emailChange,errorMailIcon);
        errorIconMap.put(postNrChange, errorPostCodeIcon);
        errorIconMap.put(phoneChange, errorPhoneIcon);
        errorIconMap.put(mobilChange, errorMobileIcon);

        emailChange.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                errorMeasureIfNotInEmailForm(emailChange);
            }
        });

        postNrChange.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                errorMeasureIfOnlyDigitsRequiredOrEmpty(postNrChange);
            }
        });

        phoneChange.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                errorMeasureIfOnlyDigitsRequiredOrEmpty(phoneChange);
            }
        });


        mobilChange.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                errorMeasureIfOnlyDigitsRequiredOrEmpty(mobilChange);
            }
        });

        saveButton.setOnMouseClicked(e -> saveInfo());
    }

    private void saveInfo(){
        Customer customer = IMatDataHandler.getInstance().getCustomer();

        handleErrors();

        customer.setFirstName(forNameChange.getText());
        setGreenBorder(forNameChange);
        customer.setLastName(lastNameChange.getText());
        setGreenBorder(lastNameChange);
        customer.setAddress(adrChange.getText());
        setGreenBorder(adrChange);

        if(isInEmailForm(emailChange) || emailChange.getText().isEmpty()) {
            customer.setEmail(emailChange.getText());
            setGreenBorder(emailChange);
        }

        if(containsDigitsOnly(postNrChange) || postNrChange.getText().isEmpty()) {
            customer.setPostCode(postNrChange.getText());
            setGreenBorder(postNrChange);
        }
        if(containsDigitsOnly(phoneChange) || phoneChange.getText().isEmpty()) {
            customer.setPhoneNumber(phoneChange.getText());
            setGreenBorder(phoneChange);
        }
        if(containsDigitsOnly(mobilChange) || mobilChange.getText().isEmpty()) {
            customer.setMobilePhoneNumber(mobilChange.getText());
            setGreenBorder(mobilChange);
        }




    }

    private void loadUserInfo(){
        Customer customer = IMatDataHandler.getInstance().getCustomer();
        forNameChange.setText(customer.getFirstName());
        resetBorder(forNameChange);
        lastNameChange.setText(customer.getLastName());
        resetBorder(lastNameChange);
        emailChange.setText(customer.getEmail());
        resetBorder(emailChange);
        adrChange.setText(customer.getAddress());
        resetBorder(adrChange);
        postNrChange.setText(customer.getPostCode());
        resetBorder(postNrChange);
        phoneChange.setText(customer.getPhoneNumber());
        resetBorder(phoneChange);
        mobilChange.setText(customer.getMobilePhoneNumber());
        resetBorder(mobilChange);
    }



    private boolean isInEmailForm(TextField textField){
        return (textField.getText().contains("@") && textField.getText().contains(".") || textField.getText().isEmpty());
    }

    private boolean containsDigitsOnly(TextField textField){        //metod som kollar om texten i en textfield endast består av siffror
        char[] chars = textField.getText().toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(!Character.isDigit(chars[i]) && !String.valueOf(chars[i]).equals(" ") && !String.valueOf(chars[i]).equals("-")){
                return false;
            }
        }
        return true;
    }

    private void handleErrors(){
        errorMeasureIfNotInEmailForm(emailChange);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(postNrChange);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(mobilChange);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(phoneChange);
    }


    public void errorMeasureIfOnlyDigitsRequiredOrEmpty(TextField textField){
        if(!containsDigitsOnly(textField)){
            textField.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(textField,true);
            setErrorMessageOnIcon(textField);
        } else {
            textField.setStyle("");
            setErrorIconVisible(textField,false);
        }
    }

    public void errorMeasureIfNotInEmailForm(TextField textField){
        if(!isInEmailForm(textField)) {
            textField.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(textField,true);
            setErrorMessageOnIcon(textField);
        } else {
            textField.setStyle("");
            setErrorIconVisible(textField,false);
        }
    }

    private void setErrorMessageOnIcon(TextField textField){
        StringBuilder messageBuilder = new StringBuilder();



        if(textField.equals(phoneChange) || textField.equals(mobilChange) || textField.equals(postNrChange)){
            if(!containsDigitsOnly(textField)){
                messageBuilder.append("Fältet får endast innehålla siffror, bindestreck och mellanslag\n");
            }
        }
        if(textField.equals(emailChange)) {
            if (!isInEmailForm(textField)) {
                messageBuilder.append("Fältet måste ha formen: email@email.com\n");
            }
        }

        ImageView errorIcon = errorIconMap.get(textField);

        Tooltip tooltip = new Tooltip();
        tooltip.setText(messageBuilder.toString());
        tooltip.setFont(new Font("Roboto-regular", 18));
        Tooltip.install(errorIcon,tooltip);
        Tooltip.install(textField,tooltip);
    }

    private void setErrorIconVisible(TextField textField, boolean value){
        errorIconMap.get(textField).setVisible(value);
    }

    private void setGreenBorder(TextField textField){
        textField.setStyle("-fx-border-width: 3px;  -fx-border-color: #7cb342;");
        saveMessage.setVisible(true);
    }


    private void resetBorder(TextField textField){
        textField.setStyle("");
    }
}
