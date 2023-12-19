module com.example.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.snake to javafx.fxml;
    exports com.example.snake;
}