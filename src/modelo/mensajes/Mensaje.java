package modelo.mensajes;

import java.io.Serializable;

import modelo.conexiones.ConexionGeneral;



public interface Mensaje extends Serializable {

    void procesarMensaje(ConexionGeneral lector);
}

