package hu.petrik.etlap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
    @FXML
    private Button newFoodBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Spinner<Integer> percentageSpinner;
    @FXML
    private Button percentageUpBtn;
    @FXML
    private Spinner<Integer> priceSpinner;
    @FXML
    private Button priceUpBtn;
    @FXML
    private TableView<Etel> menu;
    @FXML
    private TableColumn<Etel, String> nameCol;
    @FXML
    private TableColumn<Etel, String> categoryCol;
    @FXML
    private TableColumn<Etel, Integer> priceCol;
    @FXML
    private ListView<String> desc;
    private EtelDB db;
    private List<Etel> food = new ArrayList<Etel>();
    private int updateId;
    @FXML
    public void initialize(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nev"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("ar"));
        percentageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5,50,10, 5));
        priceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50,3000,2000, 50));
        try {
            db = new EtelDB();
            readFood();
        } catch (SQLException e) {
            Platform.runLater(() -> {
                alert(Alert.AlertType.WARNING, "Hiba történt a kapcsolat kialakításakor!",
                        e.getMessage());
            });
        }
    }

    private void readFood() throws SQLException {
        List<Etel> mealsList = db.readFood();
        menu.getItems().clear();
        menu.getItems().addAll(mealsList);

        this.food.addAll(mealsList);
    }

    private void sqlAlert(SQLException e) {
        Platform.runLater(() -> {
            alert(Alert.AlertType.WARNING, "Hiba történt az adatbázis kapcsolat kialakításakor!",
                    e.getMessage());
        });
    }

    private Optional<ButtonType> alert(Alert.AlertType alertType, String headerText, String contentText){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert.showAndWait();
    }
    @FXML
    public void newFoodAdd(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("etel-form-view.fxml"));
        Scene scene = null;

        try {
            scene = new Scene(fxmlLoader.load(), 400, 300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Étel hozzáadása");
        stage.setScene(scene);
        EtelForm eForm = fxmlLoader.getController();
        stage.show();
        try {
            readFood();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void deleteClick(ActionEvent actionEvent) {
        Etel selected = getSelectedFood();
        if (selected == null) return;

        if (priceUpConfirm("Törölni szeretné a kiválasztott ételt?")) return;
        try {
            if (db.deleteFood(selected.getId())) {
                alert(Alert.AlertType.WARNING, "Sikeresen törölte!", "");
            }else{
                alert(Alert.AlertType.WARNING, "Sikertelen törlés!", "");
            }
            readFood();
        } catch (SQLException e) {
            sqlAlert(e);
        }
        desc.getItems().clear();
        //TODO: megoldani hogy lefrissüljenek az elemek.
    }
    private Etel getSelectedFood() {
        int selectedIndex = menu.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1){
            alert(Alert.AlertType.WARNING, "Válasszon ki egy ételt!","");

            return null;
        }
        Etel selected = menu.getSelectionModel().getSelectedItem();

        return selected;
    }

    @FXML
    public void percentageUp(ActionEvent actionEvent) {
        int selectedIndex = menu.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1){
            if (priceUpConfirm("Emelni szeretné a kiválasztott étel árát?")) return;
            Etel selected = menu.getSelectionModel().getSelectedItem();
            updateId = selected.getId();
            Etel etel = new Etel(updateId, selected.getNev(), selected.getLeiras(), selected.getAr(), selected.getKategoria());
            double percentage = getPercentage();
            try{
                if (db.updatePercentage(etel, percentage)){
                    alert(Alert.AlertType.WARNING, "Sikeresen módosította!", "");
                    readFood();
                }else{
                    alert(Alert.AlertType.WARNING, "Sikertelen módosítás!", "");
                }
            }catch (SQLException e){
                sqlAlert(e);
            }
        }else{
            if (priceUpConfirm("Emelni szeretné az ételek árát?")) return;
            double percentage = getPercentage();
            boolean succesfulUpd = false;
            int i = 0;
            while(food.size() > i){
                Etel etel = new Etel (food.get(i).getId(), food.get(i).getNev(),  food.get(i).getLeiras(), food.get(i).getAr(), food.get(i).getKategoria());
                try {
                    if(db.updatePercentage(etel, percentage)) {
                        succesfulUpd = true;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
            if (succesfulUpd){
                alert(Alert.AlertType.WARNING, "Sikeresen módosította!", "");
                try {
                    readFood();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                alert(Alert.AlertType.WARNING, "Sikertelen módosítás!", "");
            }
        }
    }
    private double getPercentage() {
        double percentage = percentageSpinner.getValue();
        percentage = 1 + (percentage/100);
        return percentage;
    }

    @FXML
    public void priceUpdate(ActionEvent actionEvent) {
        int selectedIndex = menu.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1){
            if (priceUpConfirm("Emelni szeretné a kiválasztott étel árát?")) return;
            Etel selected = menu.getSelectionModel().getSelectedItem();
            updateId = selected.getId();
            int ar = priceSpinner.getValue();

            Etel etel = new Etel(updateId, selected.getNev(), selected.getLeiras(), selected.getAr(), selected.getKategoria());
            try{
                if (db.updatePrice(etel, ar)){
                    alert(Alert.AlertType.WARNING, "Sikeresen módosította!", "");
                    readFood();
                }else{
                    alert(Alert.AlertType.WARNING, "Sikertelen módosítás!", "");
                }
            }catch (SQLException e){
                sqlAlert(e);
            }

        }else{
            if (priceUpConfirm("Emelni szeretné az ételeket?")) return;
            int ar = priceSpinner.getValue();
            boolean succelsfulUpd = false;
            int i = 0;
            while(food.size() > i){
                Etel etel = new Etel (food.get(i).getId(), food.get(i).getNev(),  food.get(i).getLeiras(), food.get(i).getAr(), food.get(i).getKategoria());
                try {
                    if(db.updatePrice(etel, ar)) {
                        succelsfulUpd = true;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
            if (succelsfulUpd){
                alert(Alert.AlertType.WARNING, "Sikeresen módosította!", "");
                try {
                    readFood();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                alert(Alert.AlertType.WARNING, "Sikertelen módosítás!", "");
            }
        }
    }

    private boolean priceUpConfirm(String headerText) {
        Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION, headerText,"");
        if (optionalButtonType.isEmpty() || !optionalButtonType.get().equals(ButtonType.OK) && !optionalButtonType.get().equals(ButtonType.YES)){

            return true;
        }
        return false;
    }
    @FXML
    public void tableViewClick(Event event) {
        desc.getItems().clear();
        desc.getItems().add(getSelectedFood().getLeiras());
    }
    @FXML
    public void sortList(Event event) {
        menu.getOnSort();
    }
}