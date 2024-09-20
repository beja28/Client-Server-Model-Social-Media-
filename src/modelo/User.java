package modelo;

import java.io.Serializable;

/**
 * Clase User que representa a un usuario en el sistema.
 * Esta clase implementa Serializable para permitir que los objetos User
 * sean enviados a través de redes o almacenados en archivos o bases de datos de forma serializada.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L; // Identificador de versión para la serialización.

    // Campo 'id' es un identificador único para cada usuario.
    private final int id;
    // Campo 'name' es el nombre del usuario.
    private final String name;
    // Campo 'ip' almacena la dirección IP del usuario, que puede cambiar.
    private String ip;

    /**
     * Constructor para crear un nuevo usuario.
     * id: Identificador único del usuario.
     * nombre: Nombre del usuario.
     * ip: Dirección IP inicial del usuario.
     */
    public User(int id, String nombre, String ip) {
        this.id = id;
        this.name = nombre;
        this.ip = ip;
    }

    /**
     * Devuelve el identificador único del usuario.
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
     * Devuelve la dirección IP actual del usuario.
     * retorna na cadena de texto que representa la dirección IP del usuario.
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Establece o actualiza la dirección IP del usuario.
     * declara una nueva dirección IP del usuario.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
}