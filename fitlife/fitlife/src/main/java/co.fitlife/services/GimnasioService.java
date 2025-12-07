package co.fitlife.services;

import co.fitlife.model.ClaseGrupal;
import co.fitlife.model.Socio;
import co.fitlife.persistence.GestorArchivos;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicios: Contiene la lógica de negocio y centraliza el acceso a los datos.
 */
public class GimnasioService {

    private List<Socio> listaSocios;
    private List<ClaseGrupal> listaClases;

    // Nombres de archivos para persistencia
    private final String FILE_SOCIOS = "socios.dat";
    private final String FILE_CLASES = "clases.dat";

    public GimnasioService() {
        // Cargar datos al iniciar desde los archivos binarios (Serialización)
        this.listaSocios = GestorArchivos.cargarDatos(FILE_SOCIOS);
        this.listaClases = GestorArchivos.cargarDatos(FILE_CLASES);
    }

    // --- Métodos de Acceso a Datos ---

    public List<Socio> getSocios() {
        return listaSocios;
    }

    public List<ClaseGrupal> getClases() {
        return listaClases;
    }

    /**
     * Guarda los cambios en ambos archivos de persistencia. (RF03, RNF02)
     * @throws IOException Si ocurre un error de entrada/salida al escribir el archivo.
     */
    public void guardarCambios() throws IOException {
        GestorArchivos.guardarDatos(FILE_SOCIOS, listaSocios);
        GestorArchivos.guardarDatos(FILE_CLASES, listaClases);
    }

    // --- Lógica del Módulo Socios ---

    /**
     * RF01: Registra un nuevo socio aplicando la validación de unicidad.
     * @param cedula El documento de identidad único.
     * @param nombre El nombre completo del socio.
     * @throws Exception Si el socio con esa cédula ya existe.
     */
    public void registrarSocio(String cedula, String nombre) throws Exception {
        // Búsqueda de unicidad (RF01)
        boolean existe = listaSocios.stream().anyMatch(s -> s.getCedula().equals(cedula));
        if (existe) {
            throw new Exception("El socio con cédula " + cedula + " ya existe.");
        }

        listaSocios.add(new Socio(cedula, nombre));
        guardarCambios();
    }

    // --- Lógica del Módulo Clases y Reservas ---

    /**
     * RF02: Crea y registra una nueva clase grupal.
     */
    public void registrarClase(String nombre, LocalDateTime horario, int cupo) throws IOException {
        listaClases.add(new ClaseGrupal(nombre, horario, cupo));
        guardarCambios();
    }

    /**
     * RF04: Realiza una reserva aplicando las reglas de negocio críticas.
     * @param socio El socio que intenta reservar.
     * @param clase La clase seleccionada.
     * @throws Exception Si falla alguna Regla de Negocio (RN01 o RN02).
     */
    public void realizarReserva(Socio socio, ClaseGrupal clase) throws Exception {
        // RN01: Acceso Denegado (Membresía activa)
        if (!socio.esActivo()) {
            throw new Exception("Acceso Denegado: La membresía del socio está vencida. Renueve el plan.");
        }

        // RN02: Control de Aforo
        if (!clase.hayCupo()) {
            throw new Exception("Control de Aforo: La clase no tiene cupos disponibles (Clase Llena).");
        }

        // Si ambas validaciones pasan, se realiza la reserva
        clase.agregarReserva();
        guardarCambios();
    }
}