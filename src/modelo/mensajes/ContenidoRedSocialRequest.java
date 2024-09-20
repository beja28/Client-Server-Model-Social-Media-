package modelo.mensajes;

import modelo.conexiones.ConexionGeneral;

//Mensaje de Cliente a Servidor para solicitar el contenido de la redsocial
public class ContenidoRedSocialRequest implements Mensaje {

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
    	conexion.leer(this);
    }
}
