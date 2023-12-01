package com.RMClient.GUI;

import com.RMClient.connection.RMClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ScreenSharingController {

    @FXML
    private ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    @FXML
    void back(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("app.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        RMClient.stopSharing();
    }


    @FXML
    void sendKey(KeyEvent event) {
//        RMClient.sendKey(event);
    }

    @FXML
    void sendMouse(MouseEvent event) {
//        RMClient.sendMouse(event);
    }
}

