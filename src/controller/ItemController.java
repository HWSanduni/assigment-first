package controller;

import business.BusinessLogic;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import util.ItemTM;

import java.util.Optional;

public class ItemController {

    @FXML
    private Button btnAddNewItem;
    @FXML
    private Button btnItemAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtCode;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txttxtQtyOnHand;
    @FXML
    private TextField txtUnitPrice;

    @FXML
    private TableView<ItemTM> tblItem;

    public void initialize(){
        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    private void loadAllItem() {
        ObservableList<ItemTM> items = tblItem.getItems();
        items.clear();
        items = FXCollections.observableArrayList(BusinessLogic.getAllItems());
        tblItem.setItems(items);
    }


    public void btnAddItemOnAction(ActionEvent actionEvent) {


        if (txtDescription.getText().trim().isEmpty() ||
                txttxtQtyOnHand.getText().trim().isEmpty() ||
                txtUnitPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Description, Qty. on Hand or Unit Price can't be empty").show();
            return;
        }

        int qtyOnHand = Integer.parseInt(txttxtQtyOnHand.getText().trim());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());

        if (qtyOnHand < 0 || unitPrice <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty. or UnitPrice").show();
            return;
        }

        if (btnItemAdd.getText().equals("Add")) {

            boolean result = BusinessLogic.saveItem(txtCode.getText(), txtDescription.getText(), qtyOnHand, unitPrice);
            if (!result){
                new Alert(Alert.AlertType.ERROR, "Failed to save the item", ButtonType.OK).show();
            }
            btnAddNewItemOnAction(actionEvent);
        } else {
            ItemTM selectedItem = tblItem.getSelectionModel().getSelectedItem();

            boolean result = BusinessLogic.updateItem(txtDescription.getText(),  qtyOnHand, unitPrice, selectedItem.getCode());
            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the Item").show();
            }
            tblItem.refresh();
            btnAddNewItemOnAction(actionEvent);
        }
        loadAllItem();
    }





    public void btnRemoveItemOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this item?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            ItemTM selectedItem = tblItem.getSelectionModel().getSelectedItem();
            boolean result = BusinessLogic.deleteItem(selectedItem.getCode());
            if (!result){
                new Alert(Alert.AlertType.ERROR, "Failed to delete the item", ButtonType.OK).show();
            }else{
                tblItem.getItems().remove(selectedItem);
                tblItem.getSelectionModel().clearSelection();
            }
        }

    }

    public void btnAddNewItemOnAction(ActionEvent actionEvent) {
        System.out.println("------------");
        String a = BusinessLogic.getNewItemId();
        System.out.println(a);
        txtCode.setText(BusinessLogic.getNewItemId());
    }
}
