package business;

import dao.DataLayer;
import util.CustomerTM;

import java.util.List;

public class BusinessLogic {

    public static String getNewCustomerId(){
        String lastCustomerId = DataLayer.getLastCustomerId();
        if (lastCustomerId == null){
            return "C001";
        }else{
            int maxId=  Integer.parseInt(lastCustomerId.replace("C",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            System.out.println("---"+id);
            return id;
        }
    }

    public static List<CustomerTM> getAllCustomers(){
        return DataLayer.getAllCustomers();
    }

    public static boolean saveCustomer(String id, String name, String address){
        return DataLayer.saveCustomer(new CustomerTM(id,name,address));
    }

    public static boolean updateCustomer(String name, String address, String customerId){
        return DataLayer.updateCustomer(new CustomerTM(customerId, name, address));
    }
    public static boolean deleteCustomer(String customerId){
        return DataLayer.deleteCustomer(customerId);
    }
}
