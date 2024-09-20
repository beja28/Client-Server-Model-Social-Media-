package cliente.vista;


import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import cliente.Cliente;
import modelo.User;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.IOException;

public class InicioSesionView extends JFrame implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;

    private final Cliente cliente;

    private final JTextField usuario = this.getUsuario();
    private final JButton confirmar = this.getConfirmar();

    public InicioSesionView(Cliente cliente) {
        this.cliente = cliente;
        this.initGUI();
        this.cliente.setVista(this);
    }

    private void initGUI() {
        this.setTitle("Iniciar sesion");
        this.add(new MargenPanel(this.getMenu(), 40, 40, 5, 5));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        BufferedImage icono;
        try (FileInputStream stream = new FileInputStream("fotos/escudofdi.png")) {
             icono = ImageIO.read(stream);
        } catch (IOException ex) {
            icono = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        this.setIconImage(icono);
        this.setResizable(false);
        this.setVisible(true);
    }

    private JPanel getMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(this.panelUsuario());
        panel.add(this.getLogo());
        panel.add(this.panelConfirmar());
        return panel;
    }

    private void iniciarSesion(ActionEvent event) {
        String id = this.usuario.getText();
        this.confirmar.setEnabled(false);
        try {
            this.cliente.iniciarSesion(id);
        } catch (Exception ex) {
            this.confirmar.setEnabled(true);

            // Excepción en consola
            System.err.println("No se ha podido iniciar sesión");
            ex.printStackTrace();

            // Mensaje para el usuario
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al iniciar sesión",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private JPanel panelUsuario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this.usuario);

        JPanel borde = new JPanel();
        borde.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), "Usuario:"));
        borde.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        borde.add(panel, gbc);
        return borde;
    }

    private JPanel panelConfirmar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(this.confirmar);
        return panel;
    }

    private JTextField getUsuario() {
        JTextField usuario = new JTextField();
        usuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    InicioSesionView.this.confirmar.doClick();
                }
            }
        });
        return usuario;
    }

    public JButton getConfirmar() {
        JButton confirmar = new JButton("Iniciar sesión");
        confirmar.addActionListener(this::iniciarSesion);
        return confirmar;
    }

    private JPanel getLogo() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        BufferedImage logo;
        try (FileInputStream stream = new FileInputStream("fotos/escudofdigrande.png")) {
            logo = ImageIO.read(stream);
       } catch (IOException ex) {
           logo = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
       }
        panel.add(new JLabel(new ImageIcon(logo.getScaledInstance(230, 230, Image.SCALE_SMOOTH))));
        return panel;
    }

    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            if (evt.getPropertyName().equals("user")) {
                User user = (User) evt.getNewValue();

                if (user != null) {
                    // Cerramos la ventana y abrimos el menú principal
                    this.dispose();
                    new MenuPrincipalView(this.cliente, (User) evt.getNewValue());
                } else {
                    // Conexión perdida
                    this.confirmar.setEnabled(true);
                    JOptionPane.showMessageDialog(
                            InicioSesionView.this,
                            "Se ha perdido la conexión con el servidor",
                            "Error en el inicio de sesión",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
}
