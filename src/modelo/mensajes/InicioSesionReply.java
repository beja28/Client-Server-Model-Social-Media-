package modelo.mensajes;

import modelo.User;
import modelo.conexiones.ConexionGeneral;

/**
 * Clase que representa una respuesta a una solicitud de inicio de sesión.
 * Implementa la interfaz Mensaje
 */
public class InicioSesionReply implements Mensaje {

    // Usuario asociado con la respuesta, puede incluir más datos del usuario tras un inicio de sesión exitoso.
    private final User user;

    /**
     * Constructor que inicializa la respuesta de inicio de sesión con un objeto User.
     */
    public InicioSesionReply(User user) {
        this.user = user;
    }

    /**
     * Devuelve el usuario relacionado con esta respuesta de inicio de sesión.
     */
    public User getUsuario() {
        return this.user;
    }

    /**
     * implementamos procesarMensaje
     */
    @Override
    public void procesarMensaje(ConexionGeneral lector) {
        lector.leer(this);
    }
}

