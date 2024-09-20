package utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Foto implements Serializable {
	 private final int id;
	    private final String nombre;
	    private final String ruta;
	    private final int idPropietario;

	    public Foto(int id, String nombre, String ruta, int idPropietario) {
	        this.id = id;
	        this.nombre = nombre;
	        this.ruta = ruta;
	        this.idPropietario = idPropietario;
	    }

	    public int getId() {
	        return this.id;
	    }

	    public String getNombre() {
	        return this.nombre;
	    }

	    public String getRuta() {
	    	System.out.println(this.ruta);
	        return this.ruta;
	    }

	    public int getIdPropietario() {
	        return this.idPropietario;
	    }
}
