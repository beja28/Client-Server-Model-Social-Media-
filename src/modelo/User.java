package modelo;

import java.io.Serializable;

/**
 * Clase User que representa a un usuario en el sistema.
 * Esta clase implementa Serializable para permitir que los objetos User
 * sean enviados a trav�s de redes o almacenados en archivos o bases de datos de forma serializada.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L; // Identificador de versi�n para la serializaci�n.

    // Campo 'id' es un identificador �nico para cada usuario.
    private final int id;
    // Campo 'name' es el nombre del usuario.
    private final String name;
    // Campo 'ip' almacena la direcci�n IP del usuario, que puede cambiar.
    private String ip;

    /**
     * Constructor para crear un nuevo usuario.
     * id: Identificador �nico del usuario.
     * nombre: Nombre del usuario.
     * ip: Direcci�n IP inicial del usuario.
     */
    public User(int id, String nombre, String ip) {
        this.id = id;
        this.name = nombre;
        this.ip = ip;
    }

    /**
     * Devuelve el identificador �nico del usuario.
     * Retorna n entero que representa el ID del usuario.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Devuelve el nombre del usuario.
     * retorna una cadena de texto que representa el nombre del usuario.
     */
    public String getNombre() {
        return this.name;
    }

    /**
     * Devuelve la direcci�n IP actual del usuario.
     * retorna na cadena de texto que representa la direcci�n IP del usuario.
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Establece o actualiza la direcci�n IP del usuario.
     * declara una nueva direcci�n IP del usuario.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
}