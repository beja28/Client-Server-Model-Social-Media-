package cliente;

import modelo.conexiones.ConexionRuntimeException;
import modelo.conexiones.ConexionGeneral;
import modelo.mensajes.ContenidoRedSocialReply;
import modelo.mensajes.InicioSesionReply;


/*
 * Esta clase representa una conexión especifica entre un cliente y un servidor
 * Se utiliza para la comunicacion entre un cliente y el servidor de la aplicacion
 */
public class ConexionClienteConServidor extends ConexionGeneral {
		
	//Cliente al que pertence la conexion
	private final Cliente cliente;

	
	// --- Constructor --- Se le pasa la ip del "localhost"
    public ConexionClienteConServidor(int puerto, Cliente cliente) throws ConexionRuntimeException {
        super(ConexionGeneral.conectar("127.0.0.1", puerto));
        this.cliente = cliente;
    }

    
    // --- Funciones ---
    //Funcion para leer un mensaje de respuesta de inicio de sesion
    @Override
    public void leer(InicioSesionReply mensaje) {
        this.cliente.inicioSesion(mensaje.getUsuario());
    }
    
    
    //Funciones para leer un mensaje de respuesta de contenido de la RedSocial
    @Override
    public void leer(ContenidoRedSocialReply mensaje) {
        this.cliente.nuevaRedSocial(mensaje.getRedSocial());
    }
    

    //Desconecta la conexion
    @Override
    public boolean desconectar() {
        if (super.desconectar()) {
            this.cliente.servidorDesconectado();	// Informa al cliente que el servidor se ha desconectado
            return true;
        }
        return false;
    }
	
}
