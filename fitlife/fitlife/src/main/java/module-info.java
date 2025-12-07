module fitlife {
    // 1. Requerir los módulos de JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // 2. Abrir paquetes a JavaFX para que pueda acceder a las clases
    // El paquete de controladores necesita ser abierto para que el FXMLLoader lo use
    opens co.fitlife.controllers to javafx.fxml;

    // El paquete del modelo necesita ser abierto para que los TableView puedan acceder a los getters
    opens co.fitlife.model to javafx.base;

    // 3. Exportar el paquete de la aplicación para que la clase App.java pueda ser lanzada
    exports co.fitlife.app;
}