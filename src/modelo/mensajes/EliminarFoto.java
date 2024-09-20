package modelo.mensajes;

import modelo.conexiones.*;

//Mensaje de Cliente a Servidor para eliminar una foto
public class EliminarFoto implements Mensaje {

    private final int id;

    public EliminarFoto(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
    	conexion.leer(this);
    }
}
