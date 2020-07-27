package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashbordController {

    public AnchorPane rootpane;

    public void customerOnMouseClicked(ActionEvent actionEvent) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerFrom.fxml"));
        Scene scene = new Scene(root);
        Stage mainStage = (Stage)(rootpane.getScene().getWindow());
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
    }

    public void itemOnMouseClicked(ActionEvent actionEvent) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/ItemForm.fxml"));
        Scene scene = new Scene(root);
        Stage mainStage = (Stage)(rootpane.getScene().getWindow());
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
    }

    public void orderOnMouseClicked(ActionEvent actionEvent) {
    }
}
