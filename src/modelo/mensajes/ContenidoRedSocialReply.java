package modelo.mensajes;

import modelo.RedSocial;
import modelo.conexiones.*;
import utils.Foto;
import modelo.User;

import java.util.Map;

//Mensaje de Servidor a Cliente para enviar el contenido de la biblioteca.
public class ContenidoRedSocialReply implements Mensaje {

    private final Map<Integer, User> users;
    private final Map<Integer, Foto> fotos;

    public ContenidoRedSocialReply(Map<Integer, User> users, Map<Integer, Foto> fotos) {
        this.users = users;
        this.fotos = fotos;
    }

    public RedSocial getRedSocial() {
        return new RedSocial(this.users, this.fotos);
    }

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
    	conexion.leer(this);
    }
}
