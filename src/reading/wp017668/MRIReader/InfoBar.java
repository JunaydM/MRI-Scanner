package reading.wp017668.MRIReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class InfoBar extends JLabel {

    private IntegerSelection xSelect, ySelect, zSelect, illuminationSelect;
    private JLabel xLabel, yLabel, zLabel, illuminationLabel;
    private JButton lines;
    private int xVal, yVal, zVal, xmax, ymax, zmax, illuminationval;
    private boolean guidelines = false;
    private final int baseNumberIntegerSelection = 0, coordsStartX = 150, coordsY = 30, integerSelectionWidth = 50, integerSelectionHeight = 30, breakSize = 4, labelX = 150, labelY = 30;
    //Breaksize: the proportional distance between each button
    //IntegerSelectionWidth: the width of the integer selection boxes
    public InfoBar(int width, int height) {
        Font font = new Font("SansSerif", Font.BOLD, 20);
        setBounds(0, 0, width, height);
        setOpaque(true);
        setBackground(Color.DARK_GRAY);
        xLabel = new JLabel("Coronal (X): ");
        yLabel = new JLabel("Axial (Y): ");
        zLabel = new JLabel("Sagittal (Z): ");
        lines = new JButton("Guidelines: OFF");
        lines.setBackground(new Color(255,255,255));
        xLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        yLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        zLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lines.setBounds(width-labelX-30, coordsY, labelX, labelY); //setting the x, y, width, height for the guidelines button
        illuminationLabel = new JLabel("Illumation: ");
        illuminationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xLabel.setBounds(coordsStartX-labelX, coordsY, labelX, labelY);
        yLabel.setBounds(coordsStartX + (int)(integerSelectionWidth*breakSize) - labelX, coordsY, labelX, labelY);
        zLabel.setBounds(coordsStartX + (int)(integerSelectionWidth*breakSize*2)-labelX, coordsY, labelX, labelY);
        illuminationLabel.setBounds(coordsStartX + (integerSelectionWidth*breakSize*3)-(labelX*4), coordsY, labelX*4, labelY);
        xLabel.setFont(font);
        yLabel.setFont(font);
        zLabel.setFont(font);
        illuminationLabel.setFont(font);
        xLabel.setForeground(Color.BLACK);
        yLabel.setForeground(Color.BLACK);
        zLabel.setForeground(Color.BLACK);
        illuminationLabel.setForeground(Color.BLACK);
        xSelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX, coordsY, integerSelectionWidth, integerSelectionHeight);
        ySelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX + (int)(integerSelectionWidth*breakSize), coordsY, integerSelectionWidth, integerSelectionHeight);
        zSelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX + (int)(integerSelectionWidth*breakSize*2), coordsY, integerSelectionWidth, integerSelectionHeight);
        illuminationSelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX + (integerSelectionWidth*breakSize*3), coordsY, integerSelectionWidth, integerSelectionHeight);

        Font buttonFont = new Font("SansSerif", Font.BOLD, 15);
        lines.setFont(buttonFont);
        lines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(guidelines) {
                    lines.setText("Guidelines: OFF");
                    guidelines = false;
                } else {
                    lines.setText("Guidelines: ON");
                    guidelines = true;
                }
            }
        });

        xSelect.addFocusListener(new FocusListener() {//works out if anything else is above it or not

            @Override
            public void focusGained(FocusEvent e) {
                //Your code here
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateX() > xmax) xSelect.setText(Integer.toString(xmax));
                xVal = updateX();
            }
        });

        ySelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateY() > ymax) ySelect.setText(Integer.toString(ymax));
                yVal = updateY();
            }
        });

        zSelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateZ() > zmax) zSelect.setText(Integer.toString(zmax));
                zVal = updateZ();
            }
        });

        illuminationSelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateZ() > zmax) zSelect.setText(Integer.toString(zmax));
                illuminationval = updateill();
            }
        });

        Thread T = new Thread() {
          @Override
          public void start() {
              if(xVal!=xSelect.getNum() || yVal!=ySelect.getNum() || zVal!=zSelect.getNum() || illuminationval!=illuminationSelect.getNum()) checkForStrings();
              try {
                  Thread.sleep(50);
              } catch(Exception e) { e.printStackTrace(); }
          }
        };
        T.start();
    }

    public void checkForStrings() {
        xSelect.checkForString();
        ySelect.checkForString();
        zSelect.checkForString();
        illuminationSelect.checkForString();
        xVal = updateX();
        yVal = updateY();
        zVal = updateZ();
        illuminationval = updateill();
    }

    public void addItems(JFrame f) {
        f.add(xLabel);
        f.add(yLabel);
        f.add(zLabel);
        f.add(illuminationLabel);
        f.add(lines);
        xSelect.addItems(f);
        ySelect.addItems(f);
        zSelect.addItems(f);
        illuminationSelect.addItems(f);
        f.add(this);
    }

    public boolean getGuidelines() {
        return guidelines;
    }

    public int updateX() {
        if(xSelect.getText().equals("")) return 0;
        return Integer.parseInt(xSelect.getText());
    }

    public int updateY() {
        if(ySelect.getText().equals("")) return 0;
        return Integer.parseInt(ySelect.getText());
    }

    public int updateZ() {
        if(zSelect.getText().equals("")) return 0;
        return Integer.parseInt(zSelect.getText());
    }

    public int getxVal() { return xVal; }

    public int getyVal() { return yVal; }

    public int getzVal() { return zVal; }

    public int updateill() { if(illuminationSelect.getText().equals("")) return 0;
    return Integer.parseInt(illuminationSelect.getText()); }

    public int getIlluminationval() { return illuminationval; }


    public void setMax(int xm, int ym, int zm) {
        xmax = xm;
        ymax = ym;
        zmax = zm;
    }

}
