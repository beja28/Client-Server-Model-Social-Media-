package modelo.mensajes;

import modelo.conexiones.ConexionGeneral;


/**
 * Representa una solicitud de inicio de sesión enviada al servidor.
 * Implementa la interfaz Mensaje
 */
public class InicioSesionRequest implements Mensaje {

    private final String usuario;

    /**
     * Constructor que crea una solicitud de inicio de sesión con el nombre de usuario especificado.
     */
    public InicioSesionRequest(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Devuelve el nombre de usuario asociado con esta solicitud de inicio de sesión.
     */
    public String getUsuario() {
        return this.usuario;
    }

    /**
     * implementamos procesarMensaje
     */
    @Override
    public void procesarMensaje(ConexionGeneral lector) {
        lector.leer(this);
    }
}

