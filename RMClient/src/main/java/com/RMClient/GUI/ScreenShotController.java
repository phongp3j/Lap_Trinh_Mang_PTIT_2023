package com.RMClient.GUI;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ScreenShotController{

    @FXML
    private Button cancelBtn;

    @FXML
    private Button chooseFileBtn;

    @FXML
    private ImageView imageView;


    @FXML
    private Button saveBtn;

    @FXML
    private TextField textField;

    private FileChooser fileChooser = new FileChooser();
    private File selectedDirectory;
    private Image image;

    public void setImg(Image img) {
        this.image = img;
        this.imageView.setImage(img);
    }


    @FXML
    void back(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("app.fxml"));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.setTitle("Remote desktop");
            newStage.show();
            primaryStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void chooseDirectory(ActionEvent event) {
        fileChooser.setTitle("Chọn đường dẫn");
        fileChooser.setInitialFileName("screenshot.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        selectedDirectory = fileChooser.showSaveDialog(new Stage());
        if (selectedDirectory != null) {
            String directoryPath = selectedDirectory.getAbsolutePath();
            textField.setText(directoryPath);
        }
    }

    @FXML
    void save(ActionEvent event) {
        if(selectedDirectory == null){
            Alert alert =  new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Đường dẫn không hợp lệ.");
            alert.showAndWait();
        }else {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", selectedDirectory);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Lưu ảnh thành công!");
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
                    error();
                }
            } catch (IOException e) {
                e.printStackTrace();
                error();
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

