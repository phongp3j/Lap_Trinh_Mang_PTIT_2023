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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ScheduleOpenAppController implements Initializable {
    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Text text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceBox.getItems().addAll("Chrome", "Word", "MySQL");
        choiceBox.setValue("Chrome");

        choiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                changeText();
            }
        });


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
    @FXML
    void back(ActionEvent event) {
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
    void schedule(ActionEvent event) {
        String app = null;
        if(choiceBox.getValue().equals("Chrome")) app = "chrome.exe";
        if(choiceBox.getValue().equals("Word")) app = "winword.exe";
        if(choiceBox.getValue().equals("MySQL")) app = "MySQLWorkbench.exe";
        RMClient.scheduleOpenApp(app, hourSpinner.getValue(), minuteSpinner.getValue());
    }

    void changeText(){
        String app = choiceBox.getValue();
        int hourValue = hourSpinner.getValue();
        int minuteValue = minuteSpinner.getValue();
        text.setText(app + " sẽ được bật lúc: "+ hourValue + "giờ " + minuteValue + "phút.");
    }
}
