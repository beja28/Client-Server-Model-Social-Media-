package modelo.mensajes;

import modelo.conexiones.*;

// Mensaje Cliente->Cliente para descargar un libro.
public class DescargarFotoReply implements Mensaje {

    private final String nombre;
    private final String ruta;
    private final byte[] foto;

    public DescargarFotoReply(String nombre, String ruta, byte[] foto) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.foto = foto;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getRuta() {
        return this.ruta;
    }

    public byte[] getFoto() {
        return this.foto;
    }

    @Override
    public void procesarMensaje(ConexionGeneral conexion) {
    	conexion.leer(this);
    }
}
