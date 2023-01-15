module com.vrnsystem {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.vrnsystem to javafx.fxml;
    exports com.vrnsystem;
}
