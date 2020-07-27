package controller;

import business.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import util.CustomerTM;

import java.util.List;
import java.util.Optional;

public class CustomerController {
    @FXML
    private Button btnCustomerAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtCustId;
    @FXML
    private TextField txtCustName;
    @FXML
    private TextField txtCustAddress;

    @FXML
    private TableView<CustomerTM> tblCust;

    public void initialize(){
        tblCust.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCust.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCust.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
//        loadAllCustomers();
    }

    private void loadAllCustomers() {
        tblCust.getItems().clear();
        List<CustomerTM> allCustomers = BusinessLogic.getAllCustomers();
        ObservableList<CustomerTM> customers = FXCollections.observableArrayList(allCustomers);
        tblCust.setItems(customers);
    }

    public void btnAddNewCustomerOnAction(ActionEvent actionEvent) {


        txtCustName.clear();
        txtCustAddress.clear();
        tblCust.getSelectionModel().clearSelection();
        txtCustName.setDisable(false);
        txtCustAddress.setDisable(false);
        txtCustName.requestFocus();
        btnCustomerAdd.setDisable(false);
       txtCustId.setText(BusinessLogic.getNewCustomerId());

    }

    public void btnAddCustomerOnAction(ActionEvent actionEvent) {

        String name = txtCustName.getText();
        String address = txtCustAddress.getText();
        if(name.trim().length() ==0 || address.trim().length() ==0){
            new Alert(Alert.AlertType.ERROR, "Customer Name, Address can't be empty", ButtonType.OK).show();
            return;
        }

        if(btnCustomerAdd.getText().equals("Add")){
            BusinessLogic.saveCustomer(txtCustId.getText(),
                    txtCustName.getText(),
                    txtCustAddress.getText());
            btnAddNewCustomerOnAction(actionEvent);
        }else{
            CustomerTM select= tblCust.getSelectionModel().getSelectedItem();
            boolean result = BusinessLogic.updateCustomer(txtCustName.getText(),txtCustAddress.getText(),txtCustId.getId());
            if(!result){
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer", ButtonType.OK).show();
            }
            tblCust.refresh();
            btnAddNewCustomerOnAction(actionEvent);
        }
        loadAllCustomers();
    }

    public void btnRemoveCustomerOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            CustomerTM selectedItem = tblCust.getSelectionModel().getSelectedItem();

            boolean result = BusinessLogic.deleteCustomer(selectedItem.getId());
            if (!result){
                new Alert(Alert.AlertType.ERROR, "Failed to delete the customer", ButtonType.OK).show();
            }else{
                tblCust.getItems().remove(selectedItem);
                tblCust.getSelectionModel().clearSelection();
            }
        }



    }


}
