package co.fitlife.controllers;

import co.fitlife.model.ClaseGrupal;
import co.fitlife.model.Socio;
import co.fitlife.services.GimnasioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainController {

    // Componentes del Módulo SOCIOS
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TableView<Socio> tablaSocios;
    @FXML private ComboBox<Socio> comboSocios; // Para la reserva

    // Componentes del Módulo CLASES
    @FXML private TextField txtClaseNombre;
    @FXML private DatePicker dateClase;
    @FXML private TextField txtClaseHora;
    @FXML private TextField txtClaseCupo;
    @FXML private TableView<ClaseGrupal> tablaClases;

    private GimnasioService servicio;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    @FXML
    public void initialize() {
        servicio = new GimnasioService();

        // Inicialización de Columnas (Separación de lógica de FXML)
        configurarTablaSocios();
        configurarTablaClases();

        // Datos de prueba iniciales si está vacío
        if (servicio.getClases().isEmpty()) {
            servicio.getClases().add(new ClaseGrupal("Yoga Matinal", LocalDateTime.now().plusHours(2), 20));
            servicio.getClases().add(new ClaseGrupal("CrossFit Tarde", LocalDateTime.now().plusHours(5), 10));
            try {
                servicio.guardarCambios();
            } catch (IOException e) {
                // Ignorar en initialize
            }
        }

        refrescarTablas();
    }

    // --- Configuración de Tablas ---

    private void configurarTablaSocios() {
        TableColumn<Socio, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCedula()));

        TableColumn<Socio, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));

        TableColumn<Socio, String> colVencimiento = new TableColumn<>("Vence");
        colVencimiento.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaVencimiento().toString()));

        TableColumn<Socio, String> colEstado = new TableColumn<>("Estado");
        // Indicador visual de estado (RN01)
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().esActivo() ? "ACTIVO ✅" : "VENCIDO ❌"));

        tablaSocios.getColumns().addAll(colCedula, colNombre, colVencimiento, colEstado);
    }

    private void configurarTablaClases() {
        TableColumn<ClaseGrupal, String> colClase = new TableColumn<>("Clase");
        colClase.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreClase()));

        TableColumn<ClaseGrupal, String> colHorario = new TableColumn<>("Horario");
        colHorario.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getHorario().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))));

        TableColumn<ClaseGrupal, String> colCupo = new TableColumn<>("Cupo");
        colCupo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getReservasActuales() + "/" + cell.getValue().getCapacidadMaxima()));

        tablaClases.getColumns().addAll(colClase, colHorario, colCupo);
    }

    // --- Módulo Socios ---

    @FXML
    private void handleRegistrarSocio() {
        try {
            // Validación de ingreso de datos: Campos obligatorios
            if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Cédula y Nombre son obligatorios.");
            }

            servicio.registrarSocio(txtCedula.getText(), txtNombre.getText());
            refrescarTablas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Socio registrado correctamente.");
            txtCedula.clear(); txtNombre.clear();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            // Manejo de excepciones de unicidad (RF01)
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", e.getMessage());
        }
    }

    @FXML
    private void handleRenovar() {
        Socio seleccionado = tablaSocios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionado.renovarMembresia(30); // 30 días por defecto
            try {
                servicio.guardarCambios(); // RF03: Persistencia
                refrescarTablas();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Pago Exitoso", "Membresía de " + seleccionado.getNombre() + " renovada por 30 días.");
            } catch (IOException e) {
                // Manejo de Excepciones: IO
                mostrarAlerta(Alert.AlertType.ERROR, "Error IO", "No se pudo guardar el cambio en el archivo.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un socio de la tabla para renovar.");
        }
    }

    // --- Módulo Clases y Reservas ---

    @FXML
    private void handleCrearClase() {
        try {
            // 1. Obtener y validar datos
            String nombre = txtClaseNombre.getText();
            LocalDate fecha = dateClase.getValue();
            String horaStr = txtClaseHora.getText();
            int cupo = Integer.parseInt(txtClaseCupo.getText());

            if (nombre.isEmpty() || fecha == null || horaStr.isEmpty() || cupo <= 0) {
                throw new IllegalArgumentException("Todos los campos de la clase son obligatorios y el cupo debe ser positivo.");
            }

            // 2. Parsear hora y crear LocalDateTime
            LocalTime hora = LocalTime.parse(horaStr, TIME_FORMATTER);
            LocalDateTime horario = LocalDateTime.of(fecha, hora);

            // RN03: Validación Temporal
            if (horario.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("No se pueden programar clases en el pasado.");
            }

            // 3. Registrar clase
            servicio.registrarClase(nombre, horario, cupo);
            refrescarTablas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase programada: " + nombre);
            txtClaseNombre.clear(); dateClase.setValue(null); txtClaseHora.clear(); txtClaseCupo.clear();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El cupo máximo debe ser un número entero.");
        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El formato de hora debe ser HH:MM (ej: 08:30).");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al crear la clase: " + e.getMessage());
        }
    }

    @FXML
    private void handleReservar() {
        Socio socio = comboSocios.getValue();
        ClaseGrupal clase = tablaClases.getSelectionModel().getSelectedItem();

        if (socio != null && clase != null) {
            try {
                // Validación y lógica en el servicio (RN01 y RN02)
                servicio.realizarReserva(socio, clase);
                refrescarTablas();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Exitosa", "Cupo reservado para " + socio.getNombre() + " en " + clase.getNombreClase());
            } catch (Exception e) {
                // Muestra las Reglas de Negocio fallidas (RN01, RN02)
                mostrarAlerta(Alert.AlertType.WARNING, "Error de Reserva", e.getMessage());
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan Datos", "Seleccione un socio del desplegable y una clase de la tabla.");
        }
    }

    // --- Utilidades ---

    private void refrescarTablas() {
        tablaSocios.setItems(FXCollections.observableArrayList(servicio.getSocios()));
        tablaClases.setItems(FXCollections.observableArrayList(servicio.getClases()));
        // Importante: El ComboBox debe refrescarse para mostrar nuevos socios
        comboSocios.setItems(FXCollections.observableArrayList(servicio.getSocios()));
        tablaSocios.refresh();
        tablaClases.refresh();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}