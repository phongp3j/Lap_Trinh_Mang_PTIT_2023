package com.RMClient.GUI;

import com.RMClient.connection.RMClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;



import java.awt.image.BufferedImage;
import java.io.File;

public class AppController {

    @FXML
    void schedule(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("schedule.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void captureScreenshot(ActionEvent event) {
        try{
            BufferedImage image = RMClient.captureScreenShot();
            if(image != null){
                FXMLLoader loader = new FXMLLoader(App.class.getResource("screenshot.fxml"));
                Parent root = loader.load();

                ScreenShotController screenShotController = loader.getController();
//                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));

                screenShotController.setImg(SwingFXUtils.toFXImage(image, null));

                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Stage newStage = new Stage();
                Scene newScene = new Scene(root);
                newStage.setScene(newScene);
                newStage.show();
                primaryStage.close();
            }else {
                Alert alert =  new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi kết nối");
                alert.setHeaderText(null);
                alert.setContentText("Không thể kết nối đến máy chủ.");
                alert.showAndWait();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void uploadFile(ActionEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            System.out.println("File selected: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("No file selected.");
        }

        if(RMClient.upload(selectedFile.getAbsolutePath())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Upload file thành công");
                alert.showAndWait();
        }else {
            error();
        }
    }

    @FXML
    void remote(ActionEvent event) {
        if(RMClient.remote()){
            try{
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(App.class.getResource("screenSharing.fxml"));
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                newStage.setScene(newScene);
                newStage.show();
                primaryStage.close();
                ScreenSharingController controller = loader.getController();
                RMClient.startScreenSharing(controller.getImageView());
            } catch (Exception e){
                e.printStackTrace();
            }
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
