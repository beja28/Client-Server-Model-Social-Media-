package modelo.mensajes;

import modelo.conexiones.*;

/**
 * Mensaje de Cliente a Servidor para añadir un libro
 * Esta clase encapsula la información necesaria para registrar una foto en el servidor.
 */
public class PostearFoto implements Mensaje {

    // Nombre de la foto a registrar.
    private final String nombreFoto;
    // Ruta de la imagen en el sistema de archivos o en un almacenamiento.
    private final String imagePath;

    /**
     * Constructor que inicializa un nuevo mensaje de postear una nueva foto
     */
    public PostearFoto(String nombre, String ruta) {
        this.nombreFoto = nombre;
        this.imagePath = ruta;
    }

    /**
     * Obtiene el nombre de la foto que se está registrando
     */
    public String getNombre() {
        return this.nombreFoto;
    }

    /**
     * Obtiene la ruta de almacenamiento de la imagen
     */
    public String getRuta() {
        return this.imagePath;
    }

    
    /*
     * Implementamos la funcion leer de la interfaz mensaje
     * Al ser llamado, utiliza la conexión proporcionada para leer y procesar la información contenida en este mensaje
     * específico de registrar una foto
     * */

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
        conexion.leer(this);
    }
}

