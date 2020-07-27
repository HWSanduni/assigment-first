package dao;

import db.DBConnection;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataLayer {

    public static String getLastCustomerId(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer ORDER BY id DESC  LIMIT 1");
            System.out.println(rst);
            if(rst.next()){
                return rst.getString(1);
            }else{
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<CustomerTM> getAllCustomers(){
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
            ArrayList<CustomerTM> customerTMS = new ArrayList<>();
            while (rst.next()){
                customerTMS.add(new CustomerTM(rst.getString(1),
                        rst.getString(2),
                        rst.getString(3)));
            }
            return customerTMS;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    public static boolean saveCustomer(CustomerTM customer){

        try{
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?)");
            preparedStatement.setObject(1, customer.getId());
            preparedStatement.setObject(2, customer.getName());
            preparedStatement.setObject(3, customer.getAddress());
            return preparedStatement.executeUpdate() > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }



    }
    public static boolean updateCustomer(CustomerTM customer){
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
            preparedStatement.setObject(1, customer.getId());
            preparedStatement.setObject(2, customer.getName());
            preparedStatement.setObject(3, customer.getAddress());
            return preparedStatement.executeUpdate() > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCustomer(String customerId){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
            pstm.setObject(1, customerId);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getLastItemId(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item ORDER BY id DESC  LIMIT 1");
            if (rst.next()){
                return rst.getString(1);
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ItemTM> getAllItems(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item");
            ArrayList<ItemTM> items = new ArrayList<>();
            while (rst.next()){
                items.add(new ItemTM(rst.getString(1),
                        rst.getString(2),
                        rst.getInt(4),
                        rst.getDouble(3)));
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveItem(ItemTM item){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");
            pstm.setObject(1, item.getCode());
            pstm.setObject(2, item.getDescription());
            pstm.setObject(3, item.getQtyOnHand());
            pstm.setObject(4, item.getUnitPrice());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static boolean updateItem(ItemTM item){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, qtyOnHand=?, unitPrice=? WHERE code=?");
            pstm.setObject(1, item.getDescription());
            pstm.setObject(2, item.getQtyOnHand());
            pstm.setObject(3, item.getUnitPrice());
            pstm.setObject(4, item.getCode());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static boolean deleteItem(String itemCode){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Item WHERE code=?");
            pstm.setObject(1, itemCode);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
    public static boolean placeOrder(OrderTM order, List<OrderDetailTM> orderDetails){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?)");
            pstm.setObject(1, order.getOrderId());
            pstm.setObject(2, order.getOrderDate());
            pstm.setObject(3, order.getCustomerId());
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }

            for (OrderDetailTM orderDetail: orderDetails) {
                pstm = connection.prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?)");
                pstm.setObject(1, order.getOrderId());
                pstm.setObject(2, orderDetail.getCode());
                pstm.setObject(3, orderDetail.getQty());
                pstm.setObject(4, orderDetail.getUnitPrice());
                affectedRows = pstm.executeUpdate();

                if (affectedRows == 0){
                    connection.rollback();
                    return false;
                }

                pstm = connection.prepareStatement("UPDATE Item SET qtyOnHand=qtyOnHand-? WHERE code=?");
                pstm.setObject(1, orderDetail.getQty());
                pstm.setObject(2, orderDetail.getCode());
                affectedRows = pstm.executeUpdate();

                if (affectedRows == 0){
                    connection.rollback();
                    return false;
                }
            }
            connection.commit();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    public static boolean saveOrder(OrderTM order) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?)");
            pstm.setObject(1, order.getOrderId());
            pstm.setObject(2, order.getOrderDate());
            pstm.setObject(3, order.getCustomerId());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public static boolean saveOrderDetails(OrderTM order,List<OrderDetailTM> orderDetailTMS) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            for (OrderDetailTM orderDetail : orderDetailTMS) {
                PreparedStatement pstm = connection.prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?)");
                pstm.setObject(1, order.getOrderId());
                pstm.setObject(2, orderDetail.getCode());
                pstm.setObject(3, orderDetail.getQty());
                pstm.setObject(4, orderDetail.getUnitPrice());
                return pstm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static boolean updateQty (List<OrderDetailTM> orderDetailTMS){
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            for (OrderDetailTM orders:orderDetailTMS){
                PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET qtyOnHand=qtyOnHand-? WHERE code=?)");
                pstm.setObject(1, orders.getQty());
                pstm.setObject(2, orders.getCode());
                return pstm.executeUpdate() > 0;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
