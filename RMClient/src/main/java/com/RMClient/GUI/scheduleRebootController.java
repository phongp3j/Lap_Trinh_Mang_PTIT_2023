package com.RMClient.GUI;

import com.RMClient.connection.RMClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class scheduleRebootController implements Initializable {
    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private Text text;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23);
        hourValueFactory.setValue(0);
        hourSpinner.setValueFactory(hourValueFactory);

        SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59);
        minuteValueFactory.setValue(0);
        minuteSpinner.setValueFactory(minuteValueFactory);
        changeText();

        hourSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                changeText();
            }
        });

        minuteSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                changeText();
            }
        });

    }

    void changeText(){
        int hourValue = hourSpinner.getValue();
        int minuteValue = minuteSpinner.getValue();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime newTime = currentTime.plusHours(hourValue).plusMinutes(minuteValue);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H 'giờ' m 'phút' 'ngày' dd/MM/yyyy");
        text.setText("Máy tính sẽ khởi động lúc: "+ newTime.format(formatter));
    }

    @FXML
    void cancel(ActionEvent event) {
        try{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Lấy ra primaryStage hiện tại
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(App.class.getResource("schedule.fxml"));
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
    void sendRequest(ActionEvent event) {
        int hourValue = hourSpinner.getValue();
        int minuteValue = minuteSpinner.getValue();
        int timeInSeconds = hourValue*60*60 + minuteValue*60;
        if(RMClient.rebootRequest(timeInSeconds)){
            String response = RMClient.receiveResponse();
            if(response != null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText(response);
                alert.showAndWait();
                cancel(event);
            }else {
                error();
                cancel(event);
            }
        }else {
            error();
            cancel(event);
        }
    }

    public void error(){
        Alert alert =  new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi kết nối");
        alert.setHeaderText(null);
        alert.setContentText("Không thể kết nối đến máy chủ.");
        alert.showAndWait();
    }

}
