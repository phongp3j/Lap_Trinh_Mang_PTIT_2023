package com.RMClient.GUI;

import com.RMClient.connection.RMClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class IpAddressInputController {
    private RMClient client;
    private Stage primaryStage;

    @FXML
    private TextField ipInput;

    @FXML
    private void connect(ActionEvent event) {
        String ipAddress = ipInput.getText();
        if(RMClient.connect(ipAddress,5000)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kết nối thành công");
            alert.setHeaderText(null);
            alert.setContentText("Kết nối đến máy chủ thành công!");
            alert.showAndWait();

            try {
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
                Stage newStage = new Stage();
                Parent root = FXMLLoader.load(App.class.getResource("app.fxml"));
                Scene newScene = new Scene(root);
                newStage.setScene(newScene);
                newStage.setTitle("Remote desktop");
                newStage.show();
                primaryStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert =  new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi kết nối");
            alert.setHeaderText(null);
            alert.setContentText("Không thể kết nối đến máy chủ.");
            alert.showAndWait();
        }
    }

    @FXML
    void onKeyRelased(KeyEvent event) {
//        if (event.getCode() == KeyCode.ENTER){
//            connect(event);
//        }
    }
}
