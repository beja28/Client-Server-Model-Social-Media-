package cliente.vista;


import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import java.io.FileInputStream;

import cliente.Cliente;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class PostearFotoView extends JDialog {

	private final MenuPrincipalView menu;
    private final Cliente cliente;

    private final JTextField fieldNombre = new JTextField(30);
    private final JTextField fieldRuta = new JTextField(100);
    private final JButton botonCancelar = this.botonCancelar();
    private final JButton botonGuardar = this.botonGuardar();

    
    public PostearFotoView(MenuPrincipalView menu, Cliente cliente) {
        super(menu);
        this.menu = menu;
        this.cliente = cliente;
        this.setLocationRelativeTo(menu);
        this.initGUI();
    }

    
    // --- Funciones ---
    private void seleccionarRuta() {
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(new File(System.getProperty("user.home")));
        selector.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".JPEG");
            }

            @Override
            public String getDescription() {
                return "JPEGs";
            }
        });
        int resultado = selector.showDialog(this, "Seleccionar");
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File file = selector.getSelectedFile();
            String ruta = file == null ? "" : file.getAbsolutePath();
            this.fieldRuta.setText(ruta);
        }
    }

    private void guardar() {
        String nombre = this.fieldNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre no es valido",
                    "No se ha podido agregar la foto",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        File file = new File(this.fieldRuta.getText());
        if (!file.isFile() || !file.getName().endsWith(".JPEG")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato de archivo incorrecto",
                    "No se ha podido agregar la foto",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        this.botonCancelar.setEnabled(false);
        this.botonGuardar.setEnabled(false);

        String ruta = file.getAbsolutePath();
        try {
            this.cliente.registrarFoto(nombre, ruta);
        } catch (Exception ex) {
            // Excepcion en consola
            System.err.println("No se ha podido agregar la foto");
            ex.printStackTrace();

            // Mensaje para el usuario
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al agregar la foto",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        this.dispose();
        this.menu.refrescar();
    }


    
    private void initGUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Nombre: "), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 2.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.textField(this.fieldNombre), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Archivo: "), constraints);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 2.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.textField(this.fieldRuta), constraints);
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 0, 0, 0);
        JButton seleccionarRuta = new JButton("...");
        seleccionarRuta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	PostearFotoView.this.seleccionarRuta();
            }
        });
        panel.add(seleccionarRuta, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 0, 0);
        panel.add(this.getBotonera(), constraints);

        this.setLayout(new BorderLayout());
        this.add(new MargenPanel(panel, 10, 10, 10, 10));

        this.setTitle("Agregar foto");
        this.setVisible(true);
        this.setResizable(false);
         BufferedImage icono;
        try (FileInputStream stream = new FileInputStream("fotos/logo.png")) {
             icono = ImageIO.read(stream);
        } catch (IOException ex) {
            icono = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        this.setIconImage(icono);
        this.setSize(new Dimension(400, 150));
    }

    private JPanel getBotonera() {
        JPanel panel = new JPanel();
        panel.add(this.botonCancelar);
        panel.add(this.botonGuardar);
        return panel;
    }

    private JButton botonCancelar() {
        JButton cancelar = new JButton("Cancelar");
        cancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	PostearFotoView.this.dispose();
            }
        });
        return cancelar;
    }

    private JButton botonGuardar() {
        JButton guardar = new JButton("Guardar");
        guardar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PostearFotoView.this.guardar();
            }
        });
        return guardar;
    }

    private JPanel textField(JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(field);
        return panel;
    }
}
