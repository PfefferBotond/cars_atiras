package hu.petrik.etlap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class EtelForm
{
    @FXML
    private TextField nameField;
    @FXML
    private Button addBtn;
    @FXML
    private Spinner<Integer> priceSpinner;
    @FXML
    private TextArea descField;
    @FXML
    private MenuButton menuBtn;
    @FXML
    private MenuItem starterMenuItem;
    @FXML
    private MenuItem mainMenuItem;
    @FXML
    private MenuItem dessertMenuItem;
    public void initialize(){
        priceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1000,2000,3000, 5));
    }
    @FXML
    public void addBtnClick(ActionEvent actionEvent) {
        String nev = nameField.getText().trim();
        String leiras = descField.getText().trim();
        int ar = priceSpinner.getValue();
        String kategoria = menuBtn.getText().trim();

        Etel etel = new Etel(nev, leiras, ar, kategoria);
        EtelDB db = null;
        try {
            db = new EtelDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (db.createFood(etel)){
                alert(Alert.AlertType.WARNING, "Sikeres felvétel!", "");
            }else{
                alert(Alert.AlertType.WARNING, "Sikertelen felvétel!", "");
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                alert(Alert.AlertType.WARNING, "Hiba történt a kapcsolat kialakításakor!",
                        e.getMessage());
            });
        }
        Node node = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) node.getScene().getWindow();
        stage.close();
    }
    private Optional<ButtonType> alert(Alert.AlertType alertType, String headerText, String contentText){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert.showAndWait();
    }
    @FXML
    public void firstSelect(ActionEvent actionEvent) {
        menuBtn.setText(starterMenuItem.getText());
    }
    @FXML
    public void scndSelect(ActionEvent actionEvent) {
        menuBtn.setText(mainMenuItem.getText());
    }
    @FXML
    public void thirdSelect(ActionEvent actionEvent) {
        menuBtn.setText(dessertMenuItem.getText());
    }
}
