package sample;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import se.chalmers.cse.dat216.project.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;


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
                LocalDate today = LocalDate.now().plusDays(2);

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        datePicker.setShowWeekNumbers(false);

    }

    @FXML
    public void customerInfoPaneToFront(){
        autofillCustomerInfo();
        customerInfoPane.toFront();
    }

    @FXML
    public void deliveryInfoPaneToFront(){
        if(isCustomerInfoComplete()) {
            setCustomerInfoValues();
            autofillDeliveryInfo();
            deliveryInfoPane.toFront();
            customerInfoErrorLabel.setVisible(false);
        } else {
            customerInfoErrorLabel.setVisible(true);
        }
    }

    @FXML
    public void paymentInfoPaneToFront(){
        if(isDeliveryInfoComplete()) {
            setDeliveryInfoValues();
            paymentInfoPane.toFront();
            deliveryInfoErrorLabel.setVisible(false);
        } else {
            deliveryInfoErrorLabel.setVisible(true);
        }
    }

    @FXML
    public void overviewPaneToFront(){
        if(isPaymentInfoComplete()) {
            setPaymentInfoValues();
            overviewPane.toFront();
            fillOrderFlowPane();
            placeOrder();
            fillOrderOverviewInfo();
            parentController.disableCheckOutButton(true);
            paymentInfoErrorLabel.setVisible(false);
        } else{
            paymentInfoErrorLabel.setVisible(true);
        }
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


    public boolean isCustomerInfoComplete(){        //returnar true om inga textrutor är tomma
        return !firstNameTextField.getText().isEmpty() && !lastNameTextField.getText().isEmpty() && !emailTextField.getText().isEmpty() && (!mobileNumberTextField.getText().isEmpty() || !homeNumberTextField.getText().isEmpty()) && !emailTextField.getText().isEmpty() && !addressTextField.getText().isEmpty();
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
        return !postAddressTextField.getText().isEmpty() && !postCodeTextField.getText().isEmpty() && !datePicker.editorProperty().getValue().getText().isEmpty();
    }

    //------------------------------------------------------------------------------------------------------------------ Payment Info

    @FXML private ToggleButton mastercardButton;
    @FXML private ToggleButton visacardButton;
    @FXML private ToggleButton americanExpressButton;
    @FXML private TextField cardHolderNameTextField;
    @FXML private TextField validMonthTextField;
    @FXML private TextField validYearTextField;
    @FXML private TextField cardnumberTextField;
    @FXML private TextField cvcTextField;
    @FXML private Label paymentInfoErrorLabel;

    public void setPaymentInfoValues(){
        creditCard.setHoldersName(cardHolderNameTextField.getText());
        creditCard.setValidMonth(Integer.valueOf(validMonthTextField.getText()));
        creditCard.setValidYear(Integer.valueOf(validYearTextField.getText()));
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

    private boolean containsDigitsOnly(TextField textField){        //metod som kollar om texten i en textfield endast består av siffror
        char[] chars = textField.getText().toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(!Character.isDigit(chars[i])){
                return false;
            }
        }
        return true;
    }

    private boolean isPaymentInfoComplete(){            //return:ar true om inga textrutor är tomma och om de textrutor som kräver endast siffror inte har bokstäver i sig. (dessutom måste man ha valt typ av kreditkort)
        return !getCreditCardType().isEmpty() && !cardHolderNameTextField.getText().isEmpty() && !validMonthTextField.getText().isEmpty() && !validYearTextField.getText().isEmpty() && !cardnumberTextField.getText().isEmpty() && !cvcTextField.getText().isEmpty() && containsDigitsOnly(validMonthTextField) && containsDigitsOnly(validYearTextField) && containsDigitsOnly(cvcTextField);
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
        validMonthTextField.setText("");
        validYearTextField.setText("");
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

    private void fillOrderOverviewInfo(){
        overviewNameLabel.setText("Mottagare:   " + customer.getFirstName() + " " + customer.getLastName());
        overviewDeliveryPlaceLabel.setText("Leveransadress:   " + customer.getPostAddress() + "   " + "(" + customer.getPostCode() +")" );
        overviewTimeLabel.setText("Ordertid:   "  + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "   " + "(" + LocalDate.now().toString() + ")");
        overviewOrdernumberLabel.setText("Order#:   " + String.valueOf(IMatDataHandler.getInstance().getOrders().get(IMatDataHandler.getInstance().getOrders().size() - 1).getOrderNumber()));
    }

    public void fillOrderFlowPane(){
        orderFlowPane.getChildren().clear();

        for(ShoppingItem shoppingItem : IMatDataHandler.getInstance().getShoppingCart().getItems()){
            orderFlowPane.getChildren().add(new OrderOverviewItem(shoppingItem));
        }
        totalPriceLabel.setText("Totala priset: " + String.valueOf((int)IMatDataHandler.getInstance().getShoppingCart().getTotal()) + " kr");
    }


    @FXML
    public void checkoutComplete(){
        parentController.disableCheckOutButton(false);
        resetPaymentInfoValues();
        parentController.checkoutModeSwitch();
    }




}
