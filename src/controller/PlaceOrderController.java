package controller;

import business.BusinessLogic;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import util.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderController {


    public TextField txtOrderId;
    public Label txtDate;
    public ComboBox<CustomerTM> cmbCutId;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtQty;
    public Button btnAdd;
  //  public TableView<OrderTM> tblOrder;
    public Label lblTotal;
    public Button btnPlaceOrder;
    public ComboBox<ItemTM> cmbItemCode;
    public TextField txtDescription;
    public TextField txtCustName;
    public Label lblDate;
    public TableView<OrderDetailTM> tblOrder;

    private void loadAllItems() {
        cmbItemCode.getItems().clear();
        cmbItemCode.setItems(FXCollections.observableArrayList(BusinessLogic.getAllItems()));
        LocalDate today = LocalDate.now();
        lblDate.setText(today.toString());
    }

    private void loadAllCustomers() {
        cmbCutId.getItems().clear();
        cmbCutId.setItems(FXCollections.observableArrayList(BusinessLogic.getAllCustomers()));
    }
    private void calculateQtyOnHand(ItemTM item) {
        txtQtyOnHand.setText(item.getQtyOnHand() + "");
        ObservableList<OrderDetailTM> orderDetails = tblOrder.getItems();
        for (OrderDetailTM orderDetail : orderDetails) {
            if (orderDetail.getCode().equals(item.getCode())) {
                int displayQty = item.getQtyOnHand() - orderDetail.getQty();
                txtQtyOnHand.setText(displayQty + "");
                break;
            }
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Qty can't be empty", ButtonType.OK).show();
            return;
        }
        int qty = Integer.parseInt(txtQty.getText());
        if (qty < 1 || qty > Integer.parseInt(txtQtyOnHand.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty.", ButtonType.OK).show();
            return;
        }

        ItemTM selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        ObservableList<OrderDetailTM> orderDetails = tblOrder.getItems();

        if (btnAdd.getText().equals("Add")) {
            boolean exist = false;
            for (OrderDetailTM orderDetail : orderDetails) {
                if (orderDetail.getCode().equals(selectedItem.getCode())) {
                    exist = true;
                    orderDetail.setQty(orderDetail.getQty() + qty);
                    tblOrder.refresh();
                    break;
                }
            }

            if (!exist) {
                Button btnDelete = new Button("Delete");
                OrderDetailTM orderDetail = new OrderDetailTM(selectedItem.getCode(),
                        selectedItem.getDescription(),
                        qty,
                        selectedItem.getUnitPrice(), qty * selectedItem.getUnitPrice(), btnDelete);
                btnDelete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tblOrder.getSelectionModel().clearSelection();
                        cmbItemCode.getSelectionModel().clearSelection();
                        cmbItemCode.setDisable(false);
                        txtQty.clear();
                        orderDetails.remove(orderDetail);
                        cmbItemCode.requestFocus();
                    }
                });
                orderDetails.add(orderDetail);
            }

            calculateTotal();
            cmbItemCode.getSelectionModel().clearSelection();
            txtQty.clear();
            cmbItemCode.requestFocus();
        } else {

            OrderDetailTM selectedOrderDetail = tblOrder.getSelectionModel().getSelectedItem();
            selectedOrderDetail.setQty(qty);
            selectedOrderDetail.setTotal(qty * selectedOrderDetail.getUnitPrice());
            tblOrder.refresh();

            tblOrder.getSelectionModel().clearSelection();
            btnAdd.setText("Add");
            cmbItemCode.setDisable(false);
            cmbItemCode.getSelectionModel().clearSelection();
            txtQty.clear();
            calculateTotal();
            cmbItemCode.requestFocus();
        }
    }
    public void calculateTotal() {
        ObservableList<OrderDetailTM> orderDetails = tblOrder.getItems();
        double netTotal = 0;
        for (OrderDetailTM orderDetail : orderDetails) {
            netTotal += orderDetail.getTotal();
        }
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        numberInstance.setMinimumFractionDigits(2);
        numberInstance.setGroupingUsed(false);
        String formattedText = numberInstance.format(netTotal);
        lblTotal.setText("Total: " + formattedText);
    }

    public void onActionPlaceOrder(ActionEvent actionEvent) {
        if (cmbCutId.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "You need to select a customer", ButtonType.OK).show();
            cmbCutId.requestFocus();
            return;
        }

        if (tblOrder.getItems().size() == 0) {
            new Alert(Alert.AlertType.ERROR, "You need to select a Item", ButtonType.OK).show();
            cmbItemCode.requestFocus();
            return;
        }

        OrderTM orderTM = new OrderTM();
        orderTM.setOrderId(txtOrderId.getText());
        // orderTM.setOrderDate(LocalDate.now());
        orderTM.setCustomerId(cmbCutId.getValue().getId());

        ObservableList<OrderDetailTM> olOrderDetails = tblOrder.getItems();
        List<OrderDetailTM> orderDetailTMS = new ArrayList<>();
        for (OrderDetailTM orderDetail : olOrderDetails) {
            OrderDetailTM orderDetailTM = new OrderDetailTM();
            orderDetailTM.setCode(orderDetail.getCode());
            orderDetailTM.setDescription(orderDetail.getDescription());
            orderDetailTM.setQty(orderDetail.getQty());
            orderDetailTM.setUnitPrice(orderDetail.getUnitPrice());
            orderDetailTMS.add(orderDetailTM);
        }

     boolean result  =BusinessLogic.placeOrder(orderTM, orderDetailTMS);
        if(!result){
            new Alert(Alert.AlertType.ERROR, "Failed to Add order", ButtonType.OK).show();
        }

    }



}
