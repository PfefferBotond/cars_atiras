module hu.petrik.etlap {
    requires javafx.controls;
    requires javafx.fxml;


    opens hu.petrik.etlap to javafx.fxml;
    exports hu.petrik.etlap;
}