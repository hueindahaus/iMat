package sample;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import se.chalmers.cse.dat216.project.*;

import javax.swing.*;
import javax.tools.Tool;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PaymentWizard extends StackPane {

    @FXML
    private AnchorPane customerInfoPane;
    @FXML
    private AnchorPane deliveryInfoPane;
    @FXML
    private AnchorPane paymentInfoPane;
    @FXML
    private AnchorPane overviewPane;

    private ProductSearchController parentController;

    private Customer customer = IMatDataHandler.getInstance().getCustomer();

    private CreditCard creditCard = IMatDataHandler.getInstance().getCreditCard();



    public PaymentWizard(ProductSearchController parentController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("payment_wizard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

        this.parentController = parentController;

        autofillCustomerInfo();


        datePicker.setDayCellFactory(picker -> new DateCell() {             //disable:ar alla datum som har varit i datepicker
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate day = LocalDate.now().plusDays(2);

                setDisable(empty || date.compareTo(day) < 0 );
            }
        });

        datePicker.setShowWeekNumbers(false);
        linkElementWithErrorIcon();



        visacardButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                errorMeasureIfCardNotSelected();
            }
        });

        mastercardButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                errorMeasureIfCardNotSelected();
            }
        });

        americanExpressButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                errorMeasureIfCardNotSelected();
            }
        });


        cvcTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 3){
                    cvcTextField.setText(oldValue);
                }
            }
        });



        postCodeTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 6){
                    postCodeTextField.setText(oldValue);
                }
            }
        });

        cardnumberTextField.textProperty().addListener(new ChangeListener<String>() {       //fixar formatet på kredikort  TODO OBSSSS  skickar runtime exception när man tar bort en siffra så att ett bindestreck automatiskt försvinner (Programmet fungerar ändå)
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() < 20){
                    cardnumberTextField.setText(toCreditcardFormat(newValue));     //skapar illegalargumentexception när man tar bort ett bindestreck
                    /*
                    Platform.runLater(() -> {
                        String inputInCardformat = toCreditcardFormat(newValue);
                        cardnumberTextField.setText(inputInCardformat);
                        cardnumberTextField.positionCaret(inputInCardformat.length());
                    });
                    */
                }   else {
                    cardnumberTextField.setText(oldValue);
                }
            }
        });

        validMonthComboBox.selectionModelProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                errorMeasureIfComboBoxNotSelected(validMonthComboBox);
            }
        });

        validYearComboBox.selectionModelProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                errorMeasureIfComboBoxNotSelected(validYearComboBox);
            }
        });


        fillValidMonthComboBox();
        fillValidYearBox();

        implementAllToolTips();


    }

    private void fillValidMonthComboBox(){
        validMonthComboBox.getItems().add("12");
        validMonthComboBox.getItems().add("11");
        validMonthComboBox.getItems().add("10");
        validMonthComboBox.getItems().add("09");
        validMonthComboBox.getItems().add("08");
        validMonthComboBox.getItems().add("07");
        validMonthComboBox.getItems().add("06");
        validMonthComboBox.getItems().add("05");
        validMonthComboBox.getItems().add("04");
        validMonthComboBox.getItems().add("03");
        validMonthComboBox.getItems().add("02");
        validMonthComboBox.getItems().add("01");
        validMonthComboBox.visibleRowCountProperty().setValue(12);
    }

    private void fillValidYearBox(){
        int currentYear = LocalDate.now().getYear();
        for(int i = currentYear+10; i > currentYear; i--){
            String year = String.valueOf(i);
            year = String.valueOf(year.charAt(2)) + String.valueOf(year.charAt(3));
            validYearComboBox.getItems().add(year);
        }
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


    private String toCreditcardFormat(String str){
        String numbers = extractDigits(str);
        StringBuilder sb = new StringBuilder();
        if(numbers.length() > 0) {
            for (int i = 0; i < numbers.length(); i++) {
                if (i == 4 || i == 8 || i == 12 || i == 16) {
                    sb.append("-");
                }
                sb.append(numbers.charAt(i));
            }
        }
        return sb.toString();

    }

    private String extractDigits(String string){           //metod som extraherar alla nummer ur en sträng och returnar det som en sträng
        if(!string.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isDigit(c)) {
                    builder.append(c);
                }
            }
            return builder.toString();
        } else {
            return "";
        }

    }

    @FXML
    public void customerInfoPaneToFront(){
        autofillCustomerInfo();
        customerInfoPane.toFront();

        parentController.step1.setVisible(true);
        parentController.step2.setVisible(false);
        parentController.step3.setVisible(false);
    }

    @FXML
    public void deliveryInfoPaneToFront(){
        if(isCustomerInfoComplete()) {
            setCustomerInfoValues();
            autofillDeliveryInfo();
            deliveryInfoPane.toFront();
            customerInfoErrorLabel.setVisible(false);

            parentController.step1.setVisible(false);
            parentController.step2.setVisible(true);
            parentController.step3.setVisible(false);
        } else {
            customerInfoErrorLabel.setVisible(true);
        }
        errorMeasureCustomerInfo();                     //error-messages visas om isCostumerInfoComplete returnar false
    }

    @FXML
    public void paymentInfoPaneToFront(){
        if(isDeliveryInfoComplete()) {
            setDeliveryInfoValues();
            paymentInfoPane.toFront();
            deliveryInfoErrorLabel.setVisible(false);

            parentController.step1.setVisible(false);
            parentController.step2.setVisible(false);
            parentController.step3.setVisible(true);
        } else {
            deliveryInfoErrorLabel.setVisible(true);
        }
        errorMeasureDeliveryInfo();                     //error-messages visas om isDeliveryInfoComplete returnar false

    }

    @FXML
    public void overviewPaneToFront(){
        if(isPaymentInfoComplete()) {
            //setPaymentInfoValues();
            resetPaymentInfoValues();
            overviewPane.toFront();
            fillOrderFlowPane();
            isInReceiptMode = true;
            placeOrder();
            fillOrderOverviewInfo();
            parentController.disableCheckOutButton(true);
            paymentInfoErrorLabel.setVisible(false);
        } else{
            paymentInfoErrorLabel.setVisible(true);
        }
        errorMeasurePaymentInfo();

        //setErrorIconVisibleValidMonthOrValidYear();     //speciellt eftersom att en Error-Icon används för två olika TextField
    }


    //------------------------------------------------------------------------------------------------------------------  Customer Info

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField homeNumberTextField;
    @FXML
    private TextField mobileNumberTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private Label customerInfoErrorLabel;


    public void autofillCustomerInfo(){
        emailTextField.textProperty().setValue(customer.getEmail());
        mobileNumberTextField.textProperty().setValue(customer.getMobilePhoneNumber());
        homeNumberTextField.textProperty().setValue(customer.getPhoneNumber());
        lastNameTextField.textProperty().setValue(customer.getLastName());
        firstNameTextField.textProperty().setValue(customer.getFirstName());
        addressTextField.textProperty().setValue(customer.getAddress());
    }

    public void setCustomerInfoValues(){
        customer.setFirstName(firstNameTextField.getText());
        customer.setLastName(lastNameTextField.getText());
        customer.setPhoneNumber(homeNumberTextField.getText());
        customer.setMobilePhoneNumber(mobileNumberTextField.getText());
        customer.setEmail(emailTextField.getText());
        customer.setAddress(addressTextField.getText());
    }




    public boolean isCustomerInfoComplete(){        //returnar true om inga textrutor är tomma om de rutor som endast kräver digits innehåller digits och om email-fältet innehåller "@"
        return !firstNameTextField.getText().isEmpty() && !lastNameTextField.getText().isEmpty() && !emailTextField.getText().isEmpty() && isInEmailForm(emailTextField) && !mobileNumberTextField.getText().isEmpty() && !homeNumberTextField.getText().isEmpty() && !emailTextField.getText().isEmpty() && !addressTextField.getText().isEmpty() && containsDigitsOnly(homeNumberTextField) && containsDigitsOnly(mobileNumberTextField);
    }
    //------------------------------------------------------------------------------------------------------------------  Deliver Info

    @FXML
    private TextField postAddressTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField postCodeTextField;
    @FXML
    private Label deliveryInfoErrorLabel;

    public void autofillDeliveryInfo(){
        postAddressTextField.textProperty().setValue(customer.getAddress());
        postCodeTextField.textProperty().set(customer.getPostCode());
    }


    public void setDeliveryInfoValues(){
        customer.setPostAddress(postAddressTextField.getText());
        customer.setPostCode(postCodeTextField.getText());
    }

    public String getDeliveryDate(){
        return datePicker.getValue().toString();        //returnerar vilken dag man har valt i datepicker
    }

    private boolean isDeliveryInfoComplete(){           //returnerar true om inga textrutor är tomma
        return !postAddressTextField.getText().isEmpty() && !postCodeTextField.getText().isEmpty() && !datePicker.editorProperty().getValue().getText().isEmpty() && containsDigitsOnly(postCodeTextField);
    }

    //------------------------------------------------------------------------------------------------------------------ Payment Info

    @FXML private ToggleButton mastercardButton;
    @FXML private ToggleButton visacardButton;
    @FXML private ToggleButton americanExpressButton;
    @FXML private TextField cardHolderNameTextField;
    @FXML private ComboBox validMonthComboBox;
    @FXML private ComboBox validYearComboBox;
    @FXML private TextField cardnumberTextField;
    @FXML private TextField cvcTextField;
    @FXML private Label paymentInfoErrorLabel;

    public void setPaymentInfoValues(){
        creditCard.setHoldersName(cardHolderNameTextField.getText());
        creditCard.setValidMonth(Integer.valueOf((String)validMonthComboBox.selectionModelProperty().get()));
        creditCard.setValidYear(Integer.valueOf((String)validYearComboBox.selectionModelProperty().get()));
        creditCard.setCardNumber(cardHolderNameTextField.getText());
        creditCard.setVerificationCode(Integer.valueOf(cvcTextField.getText()));
        creditCard.setCardType(getCreditCardType());
    }

    private String getCreditCardType(){
        if(mastercardButton.isSelected()){
            return "Mastercard";
        } else if(visacardButton.isSelected()){
            return "Visa";
        } else if(americanExpressButton.isSelected()){
            return "American Express";
        } else {
            return "";
        }
    }



    private boolean isPaymentInfoComplete(){            //return:ar true om inga textrutor är tomma och om de textrutor som kräver endast siffror inte har bokstäver i sig. (dessutom måste man ha valt typ av kreditkort)
        return !getCreditCardType().isEmpty() && !cardHolderNameTextField.getText().isEmpty() && !validMonthComboBox.getSelectionModel().isEmpty() && !validYearComboBox.getSelectionModel().isEmpty() && !cardnumberTextField.getText().isEmpty() && !cvcTextField.getText().isEmpty() && containsDigitsOnly(cvcTextField) && containsDigitsOnly(cardnumberTextField);
    }

    public void placeOrder(){
        IMatDataHandler.getInstance().placeOrder(true);
        //endast test för att kolla om ordersystemet funkar  System.out.println(IMatDataHandler.getInstance().getOrders().get(0).getItems().get(0).getProduct().getName());
    }

    private void resetPaymentInfoValues(){
        mastercardButton.setSelected(false);
        visacardButton.setSelected(false);
        americanExpressButton.setSelected(false);
        cardHolderNameTextField.setText("");
        validMonthComboBox.getSelectionModel().clearSelection();
        validYearComboBox.getSelectionModel().clearSelection();
        cardnumberTextField.setText("");
        cvcTextField.setText("");
    }


    //------------------------------------------------------------------------------------------------------------------ Order Overview

    @FXML private Label overviewNameLabel;
    @FXML private Label overviewDeliveryPlaceLabel;
    @FXML private Label overviewOrdernumberLabel;
    @FXML private Label overviewTimeLabel;
    @FXML private FlowPane orderFlowPane;
    @FXML private Label totalPriceLabel;
    private boolean isInReceiptMode = false;


    private void fillOrderOverviewInfo(){
        overviewNameLabel.setText(customer.getFirstName() + " " + customer.getLastName());
        overviewDeliveryPlaceLabel.setText(customer.getPostAddress() + " " + "(" + customer.getPostCode() +")" );
        overviewTimeLabel.setText(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + " " + "(" + LocalDate.now().toString() + ")");
        overviewOrdernumberLabel.setText("#" + String.valueOf(IMatDataHandler.getInstance().getOrders().get(IMatDataHandler.getInstance().getOrders().size() - 1).getOrderNumber()));
    }

    public void fillOrderFlowPane(){
        orderFlowPane.getChildren().clear();

        for(ShoppingItem shoppingItem : IMatDataHandler.getInstance().getShoppingCart().getItems()){
            orderFlowPane.getChildren().add(new OrderOverviewItem(shoppingItem));
        }
        totalPriceLabel.setText("Totala priset: " + String.valueOf((int)IMatDataHandler.getInstance().getShoppingCart().getTotal()) + " kr");
    }

    protected boolean isInReceiptMode(){
        return isInReceiptMode;
    }


    @FXML
    public void checkoutComplete(){
        isInReceiptMode = false;
        parentController.disableCheckOutButton(true);
        parentController.checkoutModeSwitch();
    }



    //------------------------------------------------------------------------------------------------------------------ Felmeddelande-hantering
    public void errorMeasureCustomerInfo(){
        errorMeasureIfEmpty(firstNameTextField);
        errorMeasureIfEmpty(lastNameTextField);
        errorMeasureIfEmpty(addressTextField);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(homeNumberTextField);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(mobileNumberTextField);
        errorMeasureIfNotInEmailForm(emailTextField);
    }

    public void errorMeasureDeliveryInfo(){
        errorMeasureIfEmpty(postAddressTextField);
        errorMeasureIfEmpty(datePicker.getEditor());
        errorMeasureIfOnlyDigitsRequiredOrEmpty(postCodeTextField);
    }

    public void errorMeasurePaymentInfo(){
        errorMeasureIfEmpty(cardHolderNameTextField);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(cardnumberTextField);
        errorMeasureIfComboBoxNotSelected(validMonthComboBox);
        errorMeasureIfComboBoxNotSelected(validYearComboBox);
        errorMeasureIfOnlyDigitsRequiredOrEmpty(cvcTextField);
        errorMeasureIfCardNotSelected();
    }

    public void errorMeasureIfEmpty(TextField textField){
        if(textField.getText().isEmpty()){
            textField.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(textField,true);
            setErrorMessageOnIcon(textField);
        } else {
            textField.setStyle("");
            setErrorIconVisible(textField,false);
        }
    }

    private void errorMeasureIfComboBoxNotSelected(ComboBox comboBox){
        if(comboBox.getSelectionModel().isEmpty()){
            comboBox.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(comboBox,true);
            setErrorMessageOnIcon(comboBox);
        } else {
            comboBox.setStyle("");
            setErrorIconVisible(comboBox,false);
        }
    }

    public void errorMeasureIfOnlyDigitsRequiredOrEmpty(TextField textField){
        if(!containsDigitsOnly(textField) || textField.getText().isEmpty()){
            textField.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(textField,true);
            setErrorMessageOnIcon(textField);
        } else {
            textField.setStyle("");
            setErrorIconVisible(textField,false);
        }
    }

    public void errorMeasureIfNotInEmailForm(TextField textField){
        if(!isInEmailForm(textField)|| textField.getText().isEmpty()) {
            textField.setStyle("-fx-border-width: 3px; -fx-border-color: #FF0000;");
            setErrorIconVisible(textField,true);
            setErrorMessageOnIcon(textField);
        } else {
            textField.setStyle("");
            setErrorIconVisible(textField,false);
        }
    }



    public void errorMeasureIfCardNotSelected(){
        if(getCreditCardType().isEmpty()){
            visacardButton.getStyleClass().add("card-button-error");
            mastercardButton.getStyleClass().add("card-button-error");
            americanExpressButton.getStyleClass().add("card-button-error");
            setErrorIconCardTypeVisible(true);
            setErrorMessageOnIconCardType();
        } else {
            visacardButton.getStyleClass().remove("card-button-error");
            mastercardButton.getStyleClass().remove("card-button-error");
            americanExpressButton.getStyleClass().remove("card-button-error");
            setErrorIconCardTypeVisible(false);
        }
    }

    //----------------------------------errorIcons & tooltips
    private Map<Node, ImageView>errorMap = new HashMap<>();

    private void linkElementWithErrorIcon(){
        errorMap.put(firstNameTextField,errorFirstNameIcon);
        errorMap.put(lastNameTextField,errorLastNameIcon);
        errorMap.put(homeNumberTextField,errorPhoneIcon);
        errorMap.put(mobileNumberTextField,errorMobileIcon);
        errorMap.put(emailTextField,errorEmailIcon);
        errorMap.put(addressTextField, errorAdressIcon);
        errorMap.put(postAddressTextField, errorPostAdressIcon);
        errorMap.put(postCodeTextField,errorPostCodeIcon);
        errorMap.put(datePicker.getEditor(),errorDeliveryDayIcon);
        errorMap.put(cardnumberTextField,errorCardNumberIcon);
        errorMap.put(cardHolderNameTextField,errorHolderNameIcon);
        errorMap.put(cvcTextField,errorCvcIcon);
        errorMap.put(validMonthComboBox, errorValidMonth);
        errorMap.put(validYearComboBox,errorValidYearFront);
    }

    @FXML ImageView errorFirstNameIcon;
    @FXML ImageView errorLastNameIcon;
    @FXML ImageView errorPhoneIcon;
    @FXML ImageView errorMobileIcon;
    @FXML ImageView errorEmailIcon;
    @FXML ImageView errorAdressIcon;
    @FXML ImageView errorPostAdressIcon;
    @FXML ImageView errorPostCodeIcon;
    @FXML ImageView errorDeliveryDayIcon;
    @FXML ImageView errorCardNumberIcon;
    @FXML ImageView errorHolderNameIcon;
    @FXML ImageView errorCvcIcon;
    @FXML ImageView errorValidYearFront;
    @FXML ImageView errorValidMonth;
    @FXML ImageView errorCardTypeIcon;

    private void setErrorIconCardTypeVisible(boolean value){
        errorCardTypeIcon.setVisible(value);
    }

    private void setErrorMessageOnIconCardType(){
        Tooltip tooltip = new Tooltip();
        String message = "Korttyp måste väljas";
        tooltip.setText(message);
        tooltip.setFont(new Font("Roboto-regular", 18));

        Tooltip.install(errorCardTypeIcon, tooltip);
        Tooltip.install(visacardButton, tooltip);
        Tooltip.install(mastercardButton, tooltip);
        Tooltip.install(americanExpressButton, tooltip);
    }


    private void setErrorIconVisible(Node node, boolean value){
        ImageView errorIcon = errorMap.get(node);
        errorIcon.setVisible(value);
    }



    private void setErrorMessageOnIcon(Node node){
        StringBuilder messageBuilder = new StringBuilder();

        if(node instanceof TextField) {

            TextField textField = (TextField) node;

            if (isEmpty(textField)) {
                messageBuilder.append("Fältet kan inte vara tomt\n");
            }
            if (textField.equals(homeNumberTextField) || textField.equals(mobileNumberTextField) || textField.equals(postCodeTextField) || textField.equals(cvcTextField) || textField.equals(cardnumberTextField)) {
                if (!containsDigitsOnly(textField)) {
                    messageBuilder.append("Fältet får endast innehålla siffror, bindestreck och mellanslag\n");
                }
            }
            if (textField.equals(emailTextField)) {
                if (!isInEmailForm(textField)) {
                    messageBuilder.append("Fältet måste ha formen: email@email.com\n");
                }
            }
        } else if(node instanceof ComboBox){

            ComboBox comboBox = (ComboBox) node;

            if(comboBox.getSelectionModel().isEmpty()){
                messageBuilder.append("Fältet kan inte vara tomt\n");
            }
        }

        ImageView errorIcon = errorMap.get(node);

        Tooltip tooltip = new Tooltip();
        tooltip.setText(messageBuilder.toString());
        tooltip.setFont(new Font("Roboto-regular", 18));
        Tooltip.install(errorIcon,tooltip);
        Tooltip.install(node,tooltip);
    }

    private boolean isEmpty(TextField textField){
        return textField.getText().isEmpty();
    }

    private boolean isInEmailForm(TextField textField){
        return textField.getText().contains("@") && textField.getText().contains(".");
    }





    //------------------------------------------------------------------------------------------------------------------ sätter tooltip på buttons

    @FXML
    private Button backToDeliveryButton;
    @FXML
    private Button confirmOrderButton;
    @FXML
    private Button goToPaymentButton;
    @FXML
    private Button backToUserInfoButton;
    @FXML
    private Button goToDeliveryInfoButton;
    @FXML
    private Button goToStoreButton;




    private void implementAllToolTips(){
        installTooltipOnNode(backToDeliveryButton, "Klicka för att gå tillbaka till leveransinformation");
        installTooltipOnNode(confirmOrderButton, "Klicka för att slutföra köpet!");
        installTooltipOnNode(goToPaymentButton, "Klicka för att fortsätta till betalningsinformation");
        installTooltipOnNode(backToUserInfoButton, "Klicka för att gå tillbaka till användarinformation");
        installTooltipOnNode(goToDeliveryInfoButton, "Klicka för att fortsätta till leveransinformation");
        installTooltipOnNode(goToStoreButton, "Klicka för att komma tillbaka till sortimentet");
    }




    private void installTooltipOnNode(Node node, String message){
        Tooltip tooltip = new Tooltip(message);
        tooltip.setFont(new Font("Roboto-Regular",18));
        Tooltip.install(node,tooltip);
    }








}
