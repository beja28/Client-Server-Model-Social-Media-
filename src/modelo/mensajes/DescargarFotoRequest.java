package modelo.mensajes;

import modelo.conexiones.*;

//Mensaje de Cliente a Cliente para solicitar una foto
public class DescargarFotoRequest implements Mensaje {

    private final int id;
    private final String imagePath;

    public DescargarFotoRequest(int id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    public int getId() {
        return this.id;
    }

    public String getRuta() {
        return this.imagePath;
    }

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
    	conexion.leer(this);
    }
}
