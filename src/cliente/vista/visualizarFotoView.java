package cliente.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cliente.Cliente;
import utils.Foto;

public class visualizarFotoView extends JFrame {
		 
	private final Foto foto;

    public visualizarFotoView(Foto foto) {
        this.foto = foto;
        this.initGUI();
    }

    private void initGUI() {
    	
    	String imagePath = this.foto.getRuta();
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        

        //Se obtiene el tamanyo de la imagen
        int imgWidth = image.getWidth(null);
        int imgHeight = image.getHeight(null);
        
        
        int minSizeThreshold = 500;

        //Si la imagen es demasiado pequela no se muestra
        if (imgWidth < minSizeThreshold || imgHeight < minSizeThreshold) {
        	JOptionPane.showMessageDialog(null, "Formato de la imagen muy pequenio, no se puede visualizar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
		

        //Se crea la ventana, con el nombre de la foto
        JFrame frame = new JFrame("Titulo: " + foto.getNombre());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        //Se ajustar el tamanyo de la ventana dependiendo del tama�o de la imagen, y se limita para que no sea muy grande
        int frameAncho = Math.min(imgWidth, 600);
        int frameAlto = Math.min(imgHeight, 600);
        frame.setSize(frameAncho, frameAlto);

        
        //Si es necesario --> se escala la imagen
        if (imgWidth > frameAncho || imgHeight > frameAlto) {
            image = image.getScaledInstance(frameAncho, frameAlto, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);
        }

        
        //Finalmente se muestra la foto
        JLabel label = new JLabel(imageIcon);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    	
    }	
}
