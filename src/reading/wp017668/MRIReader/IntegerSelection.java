package reading.wp017668.MRIReader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//makes the structure on the text box with the up and down arrows

public class IntegerSelection extends JTextArea {//this handles the x,y,z

    private int num, x, y, width, height;
    BasicArrowButton up, down;

    public IntegerSelection(int num, int x, int y, int width, int height) {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setBounds(x, y, width, height);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setOpaque(true);
        setText(Integer.toString(num));
        addButtons();
        Font font = new Font("SansSerif", Font.PLAIN, 20);
        setFont(font);
    }
//for storing the information in each one
    public int getNum() {
        return num;
    }

    private void addButtons() {
        up = new BasicArrowButton(BasicArrowButton.NORTH);
        down = new BasicArrowButton(BasicArrowButton.SOUTH);
        up.setBackground(Color.WHITE);
        down.setBackground(Color.WHITE);
        up.setBounds(x+width, y, height/2, height/2);
        down.setBounds(x+width, y+(height/2), height/2, height/2);

        up.addActionListener(new ActionListener() {
            @Override//for when the up or down buttons are pressed
            public void actionPerformed(ActionEvent e) {
                num++;
                setText(Integer.toString(num));
            }
        });
        down.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                num--;
                setText(Integer.toString(num));
            }
        });
    }

    public void checkForString() {//reads the string in the text box to ensure it is a number
        if(getText().length() > 0) {
            if(!getText().matches("-?\\d+")) setText(Integer.toString(num));
            else num = Integer.parseInt(getText());
        }
    }

    public void addItems(JFrame f) {//adds the text box buttons
        f.add(this);
        f.add(up);
        f.add(down);
    }
}
