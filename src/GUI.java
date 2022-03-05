import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame implements ActionListener
{
    JTextField inputBox;
    JTextArea cityLog;
    JLabel cityMap;

    public GUI()
    {
        // Layout and frame stuff
        getContentPane().setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        // Image of map
        try
        {
            BufferedImage mapImage = ImageIO.read(new File("uk.png"));
            Image scaledImage = mapImage.getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            cityMap = new JLabel(new ImageIcon(scaledImage));

            cityMap.setBorder(new CompoundBorder(cityMap.getBorder(), new EmptyBorder(10, 10, 10, 10)));
        }
        catch (IOException e)
        {
            System.out.println("Map not found");
        }

        // City log with scroll
        cityLog = new JTextArea(20, 20);
        JScrollPane pane = new JScrollPane(cityLog);
        cityLog.setEditable(false);

        // Input field
        inputBox = new JTextField(20);
        inputBox.addActionListener(this);

        // Layout and adding
        c.gridx=0;
        c.gridy=0;
        add(cityMap, c);

        c.gridx = 1;
        c.gridy = 0;
        add(pane, c );

        c.gridx = 1;
        c.gridy = 1;
        add(inputBox, c);

        // Show frame
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String text = inputBox.getText();
        inputBox.setText("");
        cityLog.append(text + "\n");
        System.out.println(text);
    }
}
