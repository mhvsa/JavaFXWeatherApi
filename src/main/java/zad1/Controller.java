package zad1.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class Controller {

    @FXML
    private JFXTextField cityField;
    @FXML
    private JFXTextField countryField;
    @FXML
    private JFXButton okButton;


    @FXML
    public void load(ActionEvent event){
        System.out.println("Co");
    }

    @FXML
    public void txt(ActionEvent event){
        System.out.println("xxxxddd");
    }

    @FXML
    public void ok(ActionEvent event){
        System.out.println("asdadsds");
    }
}
