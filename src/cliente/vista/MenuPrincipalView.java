package cliente.vista;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import cliente.Cliente;
import modelo.RedSocial;
import modelo.User;
import utils.Foto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;

public class MenuPrincipalView extends JFrame implements PropertyChangeListener {

    private final Cliente cliente;
    private final User user;

    private final JTable tablaFotosUser;
    private final JTable tablaFotosRed;
    private final JTable tablaUsuarios;
    private final JLabel botonRefrescar;
    private final JLabel botonCerrarSesion;

    private final Timer timerRefresco;

    private boolean sesionCerrada;

    public MenuPrincipalView(Cliente cliente, User user) {
        this.cliente = cliente;
        this.cliente.setVista(this);
        this.user = user;
        this.tablaFotosUser = this.getTablaFotos(true);
        this.tablaFotosRed = this.getTablaFotos(false);
        this.tablaUsuarios = this.getTablaUsuarios();
        this.botonRefrescar = this.botonRefrescar();
        this.botonCerrarSesion = this.botonCerrarSesion();

        this.timerRefresco = new Timer(2000, e -> this.refrescar());
        this.timerRefresco.setRepeats(true);
        this.timerRefresco.start();

        this.initGUI();
        this.refrescar();
    }

    // Funciones

    public void refrescar() {
        if (this.botonRefrescar.isEnabled()) {
            this.botonRefrescar.setEnabled(false);
            try {
                this.cliente.actualizarRedSocial();
            } catch (Exception ex) {
                this.error(ex, "refrescar la redSocial", true);
            }
        }
    }

    private void error(Exception ex, String accion, boolean fatal) {
        // Excepción en consola
        System.err.println("No se ha podido " + accion);
        ex.printStackTrace();

        // Mensaje para el usuario
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error al " + accion,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (fatal) {
            this.sesionCerrada = true;
            this.dispose();
            new InicioSesionView(this.cliente);
        }
    }

    private void actualizarContenido(RedSocial redSocial) {
        this.tablaUsuarios.setModel(new DefaultTableModel());
        this.tablaUsuarios.setModel(new UsersTableModel(redSocial.getUsuarios().values()));
        this.tablaFotosRed.setModel(new FotosTableModel(redSocial, this.user, false));
        this.tablaFotosUser.setModel(new FotosTableModel(redSocial, this.user, true));
    }

    private void cerrarSesion() {
        if (!this.sesionCerrada) {
            int resultado = JOptionPane.showConfirmDialog(
                    this,
                    "¿Quieres cerrar la sesión? ",
                    "¿Deseas continuar?",
                    JOptionPane.YES_NO_OPTION
            );

            if (resultado == JOptionPane.YES_OPTION) {
                this.setEnabled(false);
                this.sesionCerrada = true;
                try {
                    this.cliente.cerrarSesion();
                } catch (Exception ex) {
                    this.error(ex, "cerrar sesión", true);
                }
            }
        }
    }

    private void postearFoto() {
        new PostearFotoView(this, this.cliente);
    }

    private void eliminarFoto(ActionEvent event) {
        this.tablaFotosUser.setEnabled(false);
        FotosTableModel model = (FotosTableModel) this.tablaFotosUser.getModel();
        Foto foto = model.getFoto(this.tablaFotosUser.getSelectedRow());
        if (foto != null) {
            try {
                this.cliente.eliminarFoto(foto);
            } catch (Exception ex) {
                this.error(ex, "eliminar la publicacion", true);
            }
            this.refrescar();
        }
    }
    

    private void descargarFoto(ActionEvent event) {
        FotosTableModel model = (FotosTableModel) this.tablaFotosRed.getModel();
        Foto foto = model.getFoto(this.tablaFotosRed.getSelectedRow());
        if (foto != null) {
            JFileChooser selector = new JFileChooser();
            selector.setCurrentDirectory(new File(System.getProperty("user.home")));
            selector.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".JPEG");
                }

                @Override
                public String getDescription() {
                    return "JPEGs";
                }
            });
            int seleccion = selector.showSaveDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File ruta = selector.getSelectedFile();
                if (ruta.isDirectory()) {
                    ruta = new File(ruta, foto.getNombre() + ".JPEG");
                } else if (!ruta.getName().endsWith(".JPEG")) {
                    ruta = new File(ruta.getAbsolutePath() + ".JPEG");
                }
                try {
                    if (!this.cliente.descargarFoto(foto, ruta.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(
                                this,
                                "El usuario no se encuentra dispoible para la descarga",
                                "No se ha podido descargar la foto",
                                JOptionPane.ERROR_MESSAGE
                        );
                        this.refrescar();
                    }
                } catch (Exception ex) {
                    this.error(ex, "descargar la foto", false);
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            if (evt.getPropertyName().equals("user")) {
                if (evt.getNewValue() != null) {
                    throw new IllegalStateException("Inicio de sesion multiple");
                }

                if (!this.sesionCerrada) {
                    this.sesionCerrada = true;
                    JOptionPane.showMessageDialog(
                            this,
                            "Se ha perdido la conexion con el servidor",
                            "Conexion perdida",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                this.dispose();
                new InicioSesionView(this.cliente);
            } else if (evt.getPropertyName().equals("redSocial")) {
                this.botonRefrescar.setEnabled(true);
                this.actualizarContenido((RedSocial) evt.getNewValue());
            } else if (evt.getPropertyName().equals("foto_descargada")) {
                String foto = (String) evt.getNewValue();
                if (foto != null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Descarga de " + foto + " completada",
                            "Foto descargada!",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "El usuario no se encuentra conectado",
                            "Error al descargar la imagen",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                this.refrescar();
            }
        });
    }

    @Override
    public void dispose() {
        this.timerRefresco.stop();
        super.dispose();
    }

    
   
    private void initGUI() {
        this.setTitle("FdiSocial");
        this.setResizable(true);
        this.setVisible(true);
        BufferedImage logo;
        try (FileInputStream stream = new FileInputStream("fotos/escudofdi.png")) {
            logo = ImageIO.read(stream);
       } catch (IOException ex) {
           logo = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
       }
        this.setIconImage(logo);
        
        //Listener al cerrar la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!MenuPrincipalView.this.sesionCerrada) {
                    MenuPrincipalView.this.cerrarSesion();
                }
            }
        });
        
        //Listener para abrir las fotos con doble click en la tabla de las fotos del Cliente
        this.tablaFotosUser.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verificar si el evento es un doble clic (clic count = 2)
                if (e.getClickCount() == 2) {
                	
                	FotosTableModel model = (FotosTableModel) tablaFotosUser.getModel();
                    Foto foto = model.getFoto(tablaFotosUser.getSelectedRow());
                    if (foto != null) {
                    	
                    	new visualizarFotoView(foto);
                    }
                }
            }
        });
        
        
        //Listener para abrir las fotos con doble click en la tabla de las fotos en el Servidor
        this.tablaFotosRed.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verificar si el evento es un doble clic (clic count = 2)
                if (e.getClickCount() == 2) {
                	
                	//Si la foto esta disponible, se visualiza
                	int filaSeleccionada = tablaFotosRed.getSelectedRow();
                	if(filaSeleccionada != -1 && tablaFotosRed.getModel().getValueAt(filaSeleccionada, 3) == "Disponible") {
                		FotosTableModel model = (FotosTableModel) tablaFotosRed.getModel();
                        Foto foto = model.getFoto(tablaFotosRed.getSelectedRow());
                        if (foto != null) {
                        	
                        	new visualizarFotoView(foto);
                        }
                	}else {
                		//Mensaje de Error
                		JOptionPane.showMessageDialog(null, "La foto de este usuario no esta disponible", "Error", JOptionPane.ERROR_MESSAGE);
                	}
                	
                }
            }
        });
                

        this.add(this.getMenu());

        this.setBounds(100, 100, 1200, 700);
        this.setSize(new Dimension(1100, 700));
        this.setPreferredSize(new Dimension(1100, 700));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    
    private JPanel getMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(this.getInfoUsuario(), BorderLayout.NORTH);
        panel.add(this.getMenuLocal(), BorderLayout.CENTER);
        panel.add(this.getTablasServidor(), BorderLayout.EAST);
        
        return panel;
    }

    
    private JPanel getTablasServidor() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        
        panel.add(this.scrollPane(this.tablaUsuarios, "Usuarios"));
        panel.add(this.scrollPane(this.tablaFotosRed, "Fotos"));
        return panel;
    }

    
    private JPanel getMenuLocal() {
    	JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(this.getTablaFotosUser(), gbc);

        //Configuraciones para el boton
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton postearFotoButton = new JButton("Postear foto");
        postearFotoButton.setPreferredSize(new Dimension(320, 30));

        //Listener
        postearFotoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuPrincipalView.this.postearFoto();
            }
        });

        panel.add(postearFotoButton, gbc);
        return panel;
    }

    
    private JPanel getTablaFotosUser() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(this.scrollPane(this.tablaFotosUser, "Tus fotos"));
        return panel;
    }

    
    private JPanel getInfoUsuario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Logo
        JPanel logo = new JPanel();
        logo.setLayout(new FlowLayout(FlowLayout.LEFT));
        logo.add(Box.createRigidArea(new Dimension(440, 0))); // Añadir espacio horizontal (200 es un ejemplo)
        BufferedImage logoLateral;
        try (FileInputStream stream = new FileInputStream("fotos/logoLateral.png")) {
            logoLateral = ImageIO.read(stream);
       } catch (IOException ex) {
           logoLateral = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
       }
        logo.add(new JLabel(new ImageIcon(logoLateral.getScaledInstance(250, 96, Image.SCALE_SMOOTH))), BorderLayout.EAST);

        panel.add(logo);
        
        JPanel perfil = new JPanel();
        perfil.setLayout(new FlowLayout(FlowLayout.RIGHT));

        
        
        JPanel infoUsuario = new JPanel(new GridLayout(2, 1));
        // Nombre de usuario
        
        JLabel nombre = new JLabel("Usuario: " + this.user.getNombre());
        nombre.setBackground(Color.WHITE);
        nombre.setFont(new Font("Verdana", Font.BOLD, 25));
        infoUsuario.add(nombre);
        perfil.add(infoUsuario);
        panel.add(perfil);
      
        // Botones
        JPanel botones = new JPanel();
        botones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        botones.add(this.botonRefrescar);
        botones.add(this.botonCerrarSesion);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 20, 5); // 15 pixeles de separación horizontal, 5 pixeles de separación vertical
        botones.setLayout(flowLayout);
        infoUsuario.add(botones);
        
          return panel;
        
    }

    private JLabel botonCerrarSesion() {
        JLabel boton = new JLabel();
        boton.setSize(new Dimension(32, 32));
        BufferedImage cerrar;
        try (FileInputStream stream = new FileInputStream("fotos/cerrarSesion.png")) {
            cerrar = ImageIO.read(stream);
       } catch (IOException ex) {
           cerrar = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
       }
        boton.setIcon(new ImageIcon(cerrar.getScaledInstance(boton.getWidth(), boton.getHeight(), Image.SCALE_SMOOTH)));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuPrincipalView.this.cerrarSesion();
            }
        });
        return boton;
    }

    private JLabel botonRefrescar() {
        JLabel boton = new JLabel();
        boton.setSize(new Dimension(32, 32));
        BufferedImage refrescar;
        try (FileInputStream stream = new FileInputStream("fotos/recargar.png")) {
            refrescar = ImageIO.read(stream);
       } catch (IOException ex) {
           refrescar = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
       }
        boton.setIcon(new ImageIcon(refrescar.getScaledInstance(boton.getWidth(), boton.getHeight(), Image.SCALE_SMOOTH)));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuPrincipalView.this.refrescar();
            }
        });
        return boton;
    }

    private JTable getTablaFotos(boolean local) {
        JTable table = new JTable();
        table.getTableHeader().setReorderingAllowed(false);
        table.setModel(new FotosTableModel(local));
        if (local) {
            table.setComponentPopupMenu(this.getOptFotosUser());
        } else {
            table.setComponentPopupMenu(this.getOptFotosRed());
        }

        return table;
    }

    
    private JPopupMenu getOptFotosUser() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem eliminarFoto = new JMenuItem("Eliminar publicación");
        eliminarFoto.addActionListener(this::eliminarFoto);
        menu.add(eliminarFoto);
        menu.addSeparator();
        JMenuItem postearFoto = new JMenuItem("Postear foto");
        postearFoto.addActionListener((e) -> this.postearFoto());
        menu.add(postearFoto);
        return menu;
    }
    
    
	private JPopupMenu getOptFotosRed() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem descargaFoto = new JMenuItem("Descargar imagen");
        descargaFoto.addActionListener(this::descargarFoto);
        menu.add(descargaFoto);
        return menu;
    }

    private JTable getTablaUsuarios() {
        JTable table = new JTable();
        table.getTableHeader().setReorderingAllowed(false);
        table.setModel(new UsersTableModel(Collections.emptyList()));
        return table;
    }

    private JPanel scrollPane(JComponent component, String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(new JScrollPane(component));
        return panel;
    }
}
