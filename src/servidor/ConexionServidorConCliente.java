package servidor;

import java.net.Socket;


import modelo.User;
import modelo.conexiones.ConexionGeneral;
import modelo.mensajes.*;
import modelo.RedSocial;

public class ConexionServidorConCliente extends ConexionGeneral {
	private final Servidor servidor;
    private User user;

    public ConexionServidorConCliente(Servidor servidor, Socket socket) {
        super(socket);
        this.servidor = servidor;
    }

    @Override
    public void leer(InicioSesionRequest mensaje) {
        this.user = this.servidor.reconocerUsuario(mensaje.getUsuario(), this.socket.getInetAddress().getHostAddress());
        if (this.user == null) {
            this.desconectar();
        } else {
            this.escribir(new InicioSesionReply(this.user));
        }
    }

    
    
    @Override
    public void leer(ContenidoRedSocialRequest mensaje) {
        this.comprobarInicioSesion();
        RedSocial redSocial = this.servidor.getRedSocial();
        this.escribir(new ContenidoRedSocialReply(redSocial.getUsuarios(), redSocial.getFotos()));
    }

    @Override
    public void leer(PostearFoto mensaje) {
        this.comprobarInicioSesion();
        this.servidor.anyadirFoto(mensaje.getNombre(), mensaje.getRuta(), this.user.getId());
    }

    @Override
    public void leer(EliminarFoto mensaje) {
        this.comprobarInicioSesion();
        this.servidor.eliminarFoto(mensaje.getId(), this.user.getId());
    }

    private void comprobarInicioSesion() {
        if (this.user == null) {
            throw new IllegalStateException("Sesion no iniciada");
        }
    }

    @Override
    public boolean desconectar() {
        if (super.desconectar()) {
            if (this.user != null) {
                this.servidor.desconectar(this.user);
            }
            return true;
        }
        return false;
    }
}
