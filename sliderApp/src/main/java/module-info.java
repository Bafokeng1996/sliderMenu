module com.example.sliderapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sliderapp to javafx.fxml;
    exports com.example.sliderapp;
}