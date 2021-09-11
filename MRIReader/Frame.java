package reading.wp017668.MRIReader;

import com.ericbarnhill.niftijio.FourDimensionalArray;
import com.ericbarnhill.niftijio.NiftiVolume;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {//initiating variables

    private Dimension screenSize;
    private int width;
    private int height;
    private int currentX, currentY, currentZ;
    private int labelSize, labelStartX, labelStartY;
    private JLayeredPane pane;
    private JMenuBar menu;
    private JMenu file, help;
    private JMenuItem open, export, about;
    private JLabel axial, sagittal, coronal;
    private BufferedImage[] axialImage, sagittalImage, coronalImage;
    private InfoBar ib;
    private boolean niftiSet, guideLines = false;
    private double powerValue = 0.7, currentIllumation;
    private final int dimention = 0, lineSize = 4;
    private JLabel largeVirticle, smallVirticle, largeHorizontal, smallHorizontal;
    FourDimensionalArray data;
    NiftiVolume niftiVolume;

    /**
     * Constructor for Frame class, initiates all default variables and draws GUI
     */
    public Frame() {
        niftiSet = false; //nifti files have not been set yet
        currentX = 0; //sets the current sagittal value to be 0
        currentY = 0; //sets the current axial value to be 0
        currentZ = 0; //sets the current coronal value to be 0
        pane = new JLayeredPane(); //making a layeredPane, allowing for ordering of items on screen
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //Retrieving screen size
        width = (((int)screenSize.getWidth())/5)*3; //Setting width to 3/5 of the screen
        height = (width/5)*4; //Setting height to be 4/5 of the width
        ImageIcon icon = new ImageIcon("res/icon.png"); //gets the icon for the window
        setIconImage(icon.getImage()); //sets the icon for the window
        setTitle("MRI Reader"); //sets the window title
        setSize(width, height);//setting size of frame
        setLocationRelativeTo(null);//setting frame to be in centre of screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //setting all threads to close on JFrame close
        setContentPane(pane); //sets the frame to use the layeredpane
        setBackground();
        setGUI();
        setVisible(true); //sets the frame to be visible
        checkIntegerThread();
    }

    /**
     * sets the background image of the frame to be plain black
     */
    private void setBackground() {
        JLabel bg = new JLabel(); //creating a JLabel to be filled with plain black, and take up the entire screen
        bg.setBackground(Color.BLACK);
        bg.setOpaque(true);
        bg.setBounds(0, 0, screenSize.width, screenSize.height);
        pane.add(bg, new Integer(-10));
    }

    /**
     * set the screens GUI, e.g. menu at top, big lines
     */
    private void setGUI() {
        addMenu();
        setDefaultImages();
        largeVirticle = new JLabel();
        largeHorizontal = new JLabel();
        smallVirticle = new JLabel();
        smallHorizontal = new JLabel();
        largeVirticle.setSize(lineSize, labelSize*2); //positions the lines to be at their respective points
        largeHorizontal.setSize(labelSize*2, lineSize);
        smallHorizontal.setSize(labelSize, lineSize);
        smallVirticle.setSize(lineSize, labelSize);
        largeVirticle.setLocation(labelStartX, labelStartY);
        largeHorizontal.setLocation(labelStartX, labelStartY);
        smallVirticle.setLocation(labelStartX+labelSize, labelStartY);
        smallHorizontal.setLocation(labelStartX, labelStartY+labelSize);
        largeVirticle.setBackground(Color.CYAN);
        smallVirticle.setBackground(Color.CYAN);
        largeHorizontal.setBackground(Color.CYAN);
        smallHorizontal.setBackground(Color.CYAN);
        largeVirticle.setOpaque(true);
        smallVirticle.setOpaque(true);
        largeHorizontal.setOpaque(true);
        smallHorizontal.setOpaque(true);
        pane.add(largeHorizontal, new Integer(5));
        pane.add(smallHorizontal, new Integer(5));
        pane.add(largeVirticle, new Integer(5));
        pane.add(smallVirticle, new Integer(5));
        ib = new InfoBar(width, height/13); //makes use of the InfoBar Class
        ib.addItems(this);

        smallHorizontal.setVisible(false);
        largeHorizontal.setVisible(false);
        largeVirticle.setVisible(false);
        smallVirticle.setVisible(false);
    }

    /**
     * adds the menu at the top, uses a JMenuBar for the "file, help etc".
     */
    private void addMenu() {
        menu = new JMenuBar();
        file = new JMenu("File");
        help = new JMenu("Help");
        open = new JMenuItem(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                openPressed();
            }
        });
        export = new JMenuItem(new AbstractAction("Export Images") {
            public void actionPerformed(ActionEvent e) {
                exportPressed();
            }
        });
        about = new JMenuItem(new AbstractAction("About") {
            public void actionPerformed(ActionEvent e) {
                aboutPressed();
            }
        });
        menu.add(file);
        menu.add(help);
        file.add(open);
        file.addSeparator();
        file.add(export);
        help.add(about);
        setJMenuBar(menu);
    }

    /**
     * setting the default images where the axial, coronal and sagittal will be, blank and black
     */
    private void setDefaultImages() {
        axial = new JLabel();
        sagittal = new JLabel();
        coronal = new JLabel();
        labelSize = (height/2) - (height/10);
        labelStartX = (width/2) - labelSize;
        labelStartY = height/10;
        axial.setLocation(labelStartX, labelStartY+labelSize);
        sagittal.setLocation(labelStartX+labelSize, labelStartY);
        coronal.setLocation(labelStartX, labelStartY);
        axial.setSize(labelSize, labelSize);
        sagittal.setSize(labelSize, labelSize);
        coronal.setSize(labelSize, labelSize);

        /**TODO: remove
        axial.setOpaque(true);
        sagittal.setOpaque(true);
        coronal.setOpaque(true);
        axial.setBackground(Color.BLACK);
        sagittal.setBackground(Color.BLUE);
        coronal.setBackground(Color.RED);
        **/

        pane.add(axial, new Integer(0));
        pane.add(sagittal, new Integer(1));
        pane.add(coronal, new Integer(2));

    }

    /**making use of library we downloaded,
     * opens the nifti files and reads teh data into a 3 dimensional array
     */
    private void openNiftiFiles() {
        data = niftiVolume.data;
        axialImage = new BufferedImage[data.sizeY()];
        sagittalImage = new BufferedImage[data.sizeZ()];
        coronalImage = new BufferedImage[data.sizeX()];
        fillAxial();
        fillSagittal();
        fillCoronal();
        niftiSet = true;
    }

    /**
     * fills images depending on the data in 3d array
     */
    private void fillSagittal() {
        for(int z = 0; z < data.sizeZ(); z++) { //looping through each slice
            sagittalImage[z] = new BufferedImage(data.sizeX(), data.sizeY(),
                    BufferedImage.TYPE_INT_RGB);
            for(int x = 0; x < data.sizeX(); x++) { //looping through x position in images
                for(int y = 0; y < data.sizeY(); y++) { //looping through y position in images
                    sagittalImage[z].setRGB(x, data.sizeY()-1-y, convertToPixel(data.get(x, y, z, dimention))); //setting pixel at x,y to be value at x,y from array
                }
            }
            //sagittalImage[z] = resize(sagittalImage[z], labelSize, labelSize);
        }
    }

    private void fillCoronal() {
        for(int x = 0; x < data.sizeX(); x++) {
            coronalImage[x] = new BufferedImage(data.sizeZ(), data.sizeY(), BufferedImage.TYPE_INT_RGB);
            for(int z = 0; z < data.sizeZ(); z++) {
                for(int y = 0; y < data.sizeY(); y++) {
                    coronalImage[x].setRGB(z, data.sizeY()-1-y, convertToPixel(data.get(x, y, z, dimention)));
                }
            }
            //coronalImage[x] = resize(coronalImage[x], labelSize, labelSize);
        }
    }

    private void fillAxial() {
        for(int y = 0; y < data.sizeY(); y++) {
            axialImage[y] = new BufferedImage(data.sizeZ(), data.sizeX(), BufferedImage.TYPE_INT_RGB);
            for(int z = 0; z < data.sizeZ(); z++) {
                for(int x = 0; x < data.sizeX(); x++) {
                    axialImage[y].setRGB(z, x, convertToPixel(data.get(x, y,z, dimention)));
                }
            }
            //axialImage[y] = resize(axialImage[y], labelSize, labelSize);
            System.out.println("FILLED AXIAL");
        }
    }

    /**
     * convert value read from nifti into pixel
     * @param value
     * @return
     */
    private int convertToPixel(double value) {
        int valueSquared = (int)(Math.pow(value, powerValue)); //increasing power by 0.7
        if(valueSquared>255) valueSquared = 255; //making sure it is between 0 and 255 as 255 is maximum pixel value
        Color c = new Color(valueSquared, valueSquared, valueSquared); //creating new pixel value from this
        return c.getRGB();
    }

    /**
     * selecting file for you to open
     */
    private void openPressed() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);//opens window to press button
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                niftiVolume = niftiVolume.read(file.getName());//data read from file
                openNiftiFiles();
                setNiftFiles();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * setting the pictures to be what was read in previous functions
     */
    private void setNiftFiles() {//setting the images for filling
        powerValue = 0.7+(currentIllumation/500);
        fillImages();
        axial.setIcon(new ImageIcon(axialImage[currentY]));
        sagittal.setIcon(new ImageIcon(sagittalImage[currentZ]));
        coronal.setIcon(new ImageIcon(coronalImage[currentX]));
        System.out.println("CHANGED ICON FOR + " + currentX);
    }

    private void fillImages() {
        sagittalImage[currentZ] = new BufferedImage(data.sizeX(), data.sizeY(),
                BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < data.sizeX(); x++) {
            for(int y = 0; y < data.sizeY(); y++) {
                sagittalImage[currentZ].setRGB(x, data.sizeY()-1-y, convertToPixel(data.get(x, y, currentZ, dimention)));
            }
        }
        sagittalImage[currentZ] = resize(sagittalImage[currentZ], labelSize, labelSize);


        coronalImage[currentX] = new BufferedImage(data.sizeZ(), data.sizeY(), BufferedImage.TYPE_INT_RGB);
            for(int z = 0; z < data.sizeZ(); z++) {
                for(int y = 0; y < data.sizeY(); y++) {
                    coronalImage[currentX].setRGB(z, data.sizeY()-1-y, convertToPixel(data.get(currentX, y, z, dimention)));
                }
            }
            coronalImage[currentX] = resize(coronalImage[currentX], labelSize, labelSize);

        axialImage[currentY] = new BufferedImage(data.sizeZ(), data.sizeX(), BufferedImage.TYPE_INT_RGB);
        for(int z = 0; z < data.sizeZ(); z++) {
            for(int x = 0; x < data.sizeX(); x++) {
                axialImage[currentY].setRGB(z, x, convertToPixel(data.get(x, currentY,z, dimention)));
            }
        }
        axialImage[currentY] = resize(axialImage[currentY], labelSize, labelSize);
    }

    /**export pressed
     * turn the images into savable file, and save them all individually
     */
    private void exportPressed() {
        JFrame f = new JFrame("Export Image");
        f.setSize(400, 150);
        f.setLocationRelativeTo(null);//this centers the window option to the middle of th screen
        f.setContentPane(new JLayeredPane());
        JLabel bg = new JLabel();
        bg.setBounds(0, 0, 500, 200);
        bg.setBackground(Color.LIGHT_GRAY);
        bg.setOpaque(true);
        f.getContentPane().add(bg, new Integer(0));
        JLabel label = new JLabel("Please select which images you would like to save");
        label.setBounds(10, 10, 300, 50);
        f.getContentPane().add(label, new Integer(1));
        JCheckBox ax = new JCheckBox("Axial", true);
        JCheckBox sag = new JCheckBox("Saggital", true);
        JCheckBox cor = new JCheckBox("Coronal", true);
        ax.setBounds(10, 50, 100, 50);// this is the x,y width and height position
        ax.setBackground(Color.LIGHT_GRAY);
        sag.setBounds(100, 50, 100, 50);
        sag.setBackground(Color.LIGHT_GRAY);
        cor.setBounds(200, 50, 100, 50);
        cor.setBackground(Color.LIGHT_GRAY);
        f.getContentPane().add(ax, new Integer(1));//adding to the window
        f.getContentPane().add(sag, new Integer(1));
        f.getContentPane().add(cor, new Integer(1));
        JButton save = new JButton("Export");
        save.setBounds(300, 60, 80 ,40);
        f.getContentPane().add(save, new Integer(1));
        f.setVisible(true);

        save.addActionListener(new ActionListener() {// when you press export
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();//window that lets you choose the file
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showSaveDialog(null);
                File file = fc.getSelectedFile();
                System.out.println(file.getPath());
                try {
                    File fileax = new File(file.getPath() + "\\axial.png");
                    File filesag = new File(file.getPath() + "\\sagittal.png");
                    File filecor = new File(file.getPath() + "\\coronal.png");
                    System.out.println(fileax.getPath());
                    if(ax.isSelected()) ImageIO.write(axialImage[currentY], "png", fileax);//tick boxes
                    if(sag.isSelected()) ImageIO.write(sagittalImage[currentZ], "png", filesag);
                    if(cor.isSelected()) ImageIO.write(coronalImage[currentX], "png", filecor);
                } catch(IOException ex) { ex.printStackTrace(); }
            }
        } );

    }

    private void aboutPressed() {

    }

    /**
     * is constantly looping, updating the x y and z values from what is read in the info bar
     */
    private void checkIntegerThread() {
        Thread t = new Thread() {
            @Override
            public void start() {
                while(true) {
                    ib.checkForStrings();
                    if(currentX!=ib.getxVal() || currentY!=ib.getyVal() || currentZ!=ib.getzVal() || currentIllumation!=ib.getIlluminationval()) niftiSet = true;
                    currentX = ib.getxVal();
                    currentY = ib.getyVal();
                    currentZ = ib.getzVal();
                    updateLines();
                    currentIllumation = ib.getIlluminationval();
                    if(niftiSet) {
                        setNiftFiles();
                        niftiSet = false;
                    }
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    /**
     * updates the lines position depending on the x y and z values
     */
    private void updateLines() {
        //smallHorizontal.setLocation(labelStartX+get)
        if(ib.getGuidelines() && !guideLines) {
            smallHorizontal.setVisible(true);
            largeHorizontal.setVisible(true);
            largeVirticle.setVisible(true);
            smallVirticle.setVisible(true);
            guideLines = true;
        } else if(!ib.getGuidelines() && guideLines) {
            smallHorizontal.setVisible(false);
            largeHorizontal.setVisible(false);
            largeVirticle.setVisible(false);
            smallVirticle.setVisible(false);
            guideLines = false;
        }
        if(currentZ>0) {
            float z = currentZ;
            float proportion = z/data.sizeZ();
            largeVirticle.setLocation(labelStartX + (int)(proportion * labelSize), labelStartY);
        }
        if(currentY>0) {
            float y = currentY;
            float proportion = y/data.sizeY();
            largeHorizontal.setLocation(labelStartX, labelStartY+labelSize-((int)(proportion * labelSize)));
        }
        if(currentX>0) {
            float x = currentX;
            float proportion = x/data.sizeX();
            smallHorizontal.setLocation(labelStartX, labelStartY+labelSize+((int)(proportion * labelSize)));
            smallVirticle.setLocation(labelStartX+labelSize+((int)(proportion*labelSize)), labelStartY);
        }

        /*if(currentX>0) {
            float x = currentX;
            float proportion = x/data.sizeX();
            largeVirticle.setLocation(labelStartX + (int)(proportion * labelSize), labelStartY);
        }*/
        repaint();
    }

    /**
     * resizes the images to be all the same size
     * @param img
     * @param height
     * @param width
     * @return
     */
    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
