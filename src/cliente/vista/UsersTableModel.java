package cliente.vista;

import javax.swing.table.AbstractTableModel;

import modelo.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class UsersTableModel extends AbstractTableModel {

    private final List<User> users = new ArrayList<>();

    public UsersTableModel(Collection<User> users) {
        this.users.addAll(users);
        this.users.sort(Comparator.comparingInt(User::getId));
    }

    @Override
    public int getRowCount() {
        return this.users.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "ID";
            case 1:
                return "Nombre";
            case 2:
                return "Estado";
        }

        return "???";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = this.users.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return "#" + user.getId();
            case 1:
                return user.getNombre();
            case 2: {
                return user.getIp() != null ? "Conectado" : "Desconectado";
            }
        }

        return "???";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
