package cliente.vista;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

public class MargenPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MargenPanel(Component interior, int izq, int der, int arriba, int abajo) {
        this.setLayout(new BorderLayout());

        if (izq > 0) {
            this.add(this.getMargen(izq, false), BorderLayout.EAST);
        }

        if (der > 0) {
            this.add(this.getMargen(der, false), BorderLayout.WEST);
        }

        if (arriba > 0) {
            this.add(this.getMargen(arriba, true), BorderLayout.NORTH);
        }

        if (abajo > 0) {
            this.add(this.getMargen(abajo, true), BorderLayout.SOUTH);
        }

        this.add(interior, BorderLayout.CENTER);
    }

    private JPanel getMargen(int margen, boolean vertical) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, vertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));

        if (vertical) {
            panel.add(Box.createVerticalStrut(margen));
        } else {
            panel.add(Box.createHorizontalStrut(margen));
        }

        return panel;
    }
}
