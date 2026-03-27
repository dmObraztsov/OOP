module com.example.task_2_3_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.task_2_3_1 to javafx.fxml;
    opens com.example.task_2_3_1.model to javafx.fxml;
    opens com.example.task_2_3_1.controller to javafx.fxml;

    exports com.example.task_2_3_1;
    exports com.example.task_2_3_1.model;
    exports com.example.task_2_3_1.controller;
}