package modelo.mensajes;

import modelo.User;
import modelo.conexiones.ConexionGeneral;

/**
 * Clase que representa una respuesta a una solicitud de inicio de sesi�n.
 * Implementa la interfaz Mensaje
 */
public class InicioSesionReply implements Mensaje {

    // Usuario asociado con la respuesta, puede incluir m�s datos del usuario tras un inicio de sesi�n exitoso.
    private final User user;

    /**
     * Constructor que inicializa la respuesta de inicio de sesi�n con un objeto User.
     */
    public InicioSesionReply(User user) {
        this.user = user;
    }

    /**
     * Devuelve el usuario relacionado con esta respuesta de inicio de sesi�n.
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

