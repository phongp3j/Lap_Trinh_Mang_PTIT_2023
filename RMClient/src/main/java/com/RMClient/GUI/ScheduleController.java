package com.RMClient.GUI;

import com.RMClient.connection.RMClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ScheduleController {
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
    }

    @FXML
    void scheduleOpenApp(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("ScheduleOpenApp.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void scheduleReboot(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("scheduleReboot.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void scheduleShutdown(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("scheduleShutdown.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void cancelSchedule(ActionEvent event) {
        if(RMClient.cancelSchedule()){
            String response = RMClient.receiveResponse();
            if(response != null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText(response);
                alert.showAndWait();
            }else {
                error();
            }
        }else {
            error();
        }
    }

    void error(){
        Alert alert =  new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi kết nối");
        alert.setHeaderText(null);
        alert.setContentText("Không thể kết nối đến máy chủ.");
        alert.showAndWait();
    }
}
