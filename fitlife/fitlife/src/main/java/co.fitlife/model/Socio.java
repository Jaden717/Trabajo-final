package co.fitlife.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

// ¡IMPORTANTE! Debe implementar Serializable
public class Socio implements Serializable {

    // SerialVersionUID es una buena práctica para serialización
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String cedula;
    private String nombre;
    private LocalDate fechaVencimiento;

    // Constructor (asumo que se da membresía inicial de 30 días)
    public Socio(String cedula, String nombre) {
        this.id = UUID.randomUUID();
        this.cedula = cedula;
        this.nombre = nombre;
        // La membresía inicia o se renueva por 30 días (fecha actual + 30 días)
        this.fechaVencimiento = LocalDate.now().plusDays(30);
    }

    // Método para la Regla de Negocio (RN01) y renovación
    public boolean esActivo() {
        return fechaVencimiento.isAfter(LocalDate.now());
    }

    // Método para la renovación (RF03)
    public void renovarMembresia(int dias) {
        // Renueva a partir de la fecha actual si está vencido, o extiende la fecha de vencimiento.
        if (esActivo()) {
            this.fechaVencimiento = this.fechaVencimiento.plusDays(dias);
        } else {
            this.fechaVencimiento = LocalDate.now().plusDays(dias);
        }
    }

    // Getters y Setters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    @Override
    public String toString() {
        return nombre + " (" + cedula + ") - " + (esActivo() ? "ACTIVO" : "VENCIDO");
    }
}