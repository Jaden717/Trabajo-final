package co.fitlife.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase genérica para manejar la serialización de listas a archivos binarios (.dat).
 */
public class GestorArchivos {

    /**
     * Guarda una lista de objetos serializables en un archivo binario.
     * @param nombreArchivo El nombre del archivo de destino (ej: socios.dat).
     * @param datos La lista de objetos a guardar.
     * @throws IOException Si ocurre un error de escritura.
     */
    public static <T> void guardarDatos(String nombreArchivo, List<T> datos) throws IOException {
        // Usamos try-with-resources para asegurar que el ObjectOutputStream se cierre
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(datos);
        }
    }

    /**
     * Carga una lista de objetos serializables desde un archivo binario.
     * @param nombreArchivo El nombre del archivo a leer.
     * @return Una lista de objetos cargados, o una lista vacía si el archivo no existe o hay error.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> cargarDatos(String nombreArchivo) {
        // Inicializamos una lista vacía para retornar si falla
        List<T> datos = new ArrayList<>();
        File archivo = new File(nombreArchivo);

        // Verificamos si el archivo existe antes de intentar leerlo
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                // Leemos el objeto y lo casteamos a una lista genérica
                datos = (List<T>) ois.readObject();
            } catch (FileNotFoundException e) {
                // Este caso es poco probable porque ya comprobamos .exists()
                System.err.println("Archivo no encontrado: " + nombreArchivo);
            } catch (IOException | ClassNotFoundException e) {
                // Manejo de Excepciones: Si el archivo está corrupto o la clase no existe
                System.err.println("Error cargando el archivo de datos: " + e.getMessage());
                // Retornamos lista vacía para que el programa pueda continuar
                return new ArrayList<>();
            }
        }
        // Si el archivo no existía, retorna la lista vacía inicializada
        return datos;
    }
}