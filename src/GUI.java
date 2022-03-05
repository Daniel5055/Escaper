import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class GUI extends JFrame implements ActionListener
{
    private JTextField inputBox;
    private JTextArea cityLog;
    private CityMap cityMap;
    private CityEngine cityEngine;

    public GUI()
    {

        cityEngine = new CityEngine("France");


        // Layout and frame stuff
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        getContentPane().setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints c = new GridBagConstraints();

        // Image of map
        try
        {
            cityMap = new CityMap();
        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
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

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight=2;

        add(cityMap, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx=0;
        c.weighty=0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight=1;
        add(pane, c );

        c.gridx = 1;
        c.gridy = 1;
        add(inputBox, c);

        // Show frame
        setSize(570, 570);
        setResizable(false);
        setVisible(true);

        for (double x = -10; x <= 2; x+= 0.4)
        {
            for (double y = 49; y <= 59; y += 0.3 )
            {
                cityMap.addCity(x + "" + y, x, y);

            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // Get text input and clear box
        String text = inputBox.getText();
        inputBox.setText("");

        Point2D.Double point = null;

        try
        {
            point = cityEngine.getCity(text);
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getStackTrace());
        }

        if (point != null)
        {
            cityMap.addCity(text, point);
            cityLog.append(text + "\n");
        }
        else
        {
            // Invalid city put
            cityLog.append(text + " is not a valid city\n");
        }
    }
}
