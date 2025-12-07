package co.fitlife.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

// ¡IMPORTANTE! Debe implementar Serializable
public class ClaseGrupal implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String nombreClase;
    private LocalDateTime horario;
    private int capacidadMaxima;
    private int reservasActuales;

    public ClaseGrupal(String nombreClase, LocalDateTime horario, int capacidadMaxima) {
        this.id = UUID.randomUUID();
        this.nombreClase = nombreClase;
        this.horario = horario;
        this.capacidadMaxima = capacidadMaxima;
        this.reservasActuales = 0;
    }

    // Método para la Regla de Negocio (RN02)
    public boolean hayCupo() {
        return reservasActuales < capacidadMaxima;
    }

    public void agregarReserva() {
        if (hayCupo()) {
            reservasActuales++;
        }
    }

    // Getters y Setters
    public String getNombreClase() { return nombreClase; }
    public LocalDateTime getHorario() { return horario; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public int getReservasActuales() { return reservasActuales; }
}