package cliente.vista;

import modelo.RedSocial;
import utils.Foto;
import modelo.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FotosTableModel extends AbstractTableModel {

    private final boolean local;

    private final Map<Integer, User> users = new HashMap<>();
    private final List<Foto> libros = new ArrayList<>();

    public FotosTableModel(boolean local) {
        this.local = local;
    }

    public FotosTableModel(RedSocial redSocial, User user, boolean local) {
        this.local = local;

        if (local) {
            for (Foto foto : redSocial.getFotos().values()) {
                if (foto.getIdPropietario() == user.getId()) {
                    this.libros.add(foto);
                }
            }
        } else {
            this.users.putAll(redSocial.getUsuarios());
            for (Foto foto : redSocial.getFotos().values()) {
                if (foto.getIdPropietario() != user.getId()) {
                    this.libros.add(foto);
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        return this.libros.size();
    }

    @Override
    public int getColumnCount() {
        return this.local ? 2 : 4;
    }

    @Override
    public String getColumnName(int column) {
        if (this.local) {
            switch (column) {
                case 0:
                    return "ID";
                case 1:
                    return "Nombre";
            }
        } else {
            switch (column) {
                case 0:
                    return "ID";
                case 1:
                    return "Nombre";
                case 2:
                    return "Propietario";
                case 3:
                    return "Estado";
            }
        }

        return "???";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Foto foto = this.libros.get(rowIndex);
        if (this.local) {
            return columnIndex == 0 ? "#" + foto.getId() : foto.getNombre();
        }

        switch (columnIndex) {
            case 0:
                return "#" + foto.getId();
            case 1:
                return foto.getNombre();
            case 2: {
                User user = this.users.get(foto.getIdPropietario());
                return user.getNombre();
            }
            case 3: {
                User user = this.users.get(foto.getIdPropietario());
                return user.getIp() != null ? "Disponible" : "No disponible";
            }
        }

        return "???";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Foto getFoto(int fila) {
        if (fila < 0 || this.libros.size() <= fila) {
            return null;
        }
        return this.libros.get(fila);
    }
}
