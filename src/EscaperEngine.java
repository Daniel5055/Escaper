import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

public class EscaperEngine implements ActionListener
{
    // GUI components
    private JTextField inputBox;
    private JTextArea cityLog;
    private CityMap cityMap;
    private JFrame frame;

    // Logic and data attributes
    private CityEngine cityEngine;

    private int travelRange;
    private String currentCity;

    public EscaperEngine()
    {
        cityEngine = new CityEngine("United Kingdom");

        travelRange = 50;
        currentCity = "Dundee";

        initialiseGUI();
        cityMap.addCity(currentCity, cityEngine.getCityPoint(currentCity));
    }

    private void initialiseGUI()
    {
        // Initialise JFrame
        frame = new JFrame();

        // Initialise content pane
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setLayout(new GridBagLayout());
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialise cityMap
        try
        {
            cityMap = new CityMap();
            cityMap.setTravelRadius(travelRange);
        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }

        // Initialise cityLog
        cityLog = new JTextArea(20, 20);
        JScrollPane pane = new JScrollPane(cityLog);
        cityLog.setEditable(false);

        // Initialise inputBox
        inputBox = new JTextField(20);
        inputBox.addActionListener(this);

        // Create layout constraints and add components to frame
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight=2;
        frame.add(cityMap, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx=0;
        c.weighty=0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight=1;
        frame.add(pane, c );

        c.gridx = 1;
        c.gridy = 1;
        frame.add(inputBox, c);

        // Show frame
        frame.setSize(580, 580);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Implemented method from ActionListener
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // Get text input and clear box
        String text = inputBox.getText();
        inputBox.setText("");

        Point2D.Double point = null;

        point = cityEngine.getCityPoint(text);

        // If valid else
        if (point != null)
        {
            // Set as proper name
            String properName = cityEngine.getProperCityName(text);
            // Check if within distance using good ol' pythagoras
            Point2D.Double past = cityMap.getLastCityPoint();

            // Must convert points to relative to compare though
            if (Math.sqrt(Math.pow((past.x - cityMap.getRelativeXFromLong(point.x)) * cityMap.getMapWidth(), 2)
                    + Math.pow((past.y - cityMap.getRelativeYFromLat(point.y)) * cityMap.getMapHeight(), 2)) <= travelRange)
            {
                // Must not be current city either
                if (!currentCity.equalsIgnoreCase(properName))
                {
                    cityMap.addCity(properName, point);
                    cityLog.append(currentCity + " -> " + properName  + "\n");
                    currentCity = properName;
                }
                else
                {
                    cityLog.append("You are already in " + currentCity + "\n");

                }
            }
            else
            {
                cityMap.addFarCity(properName, point);
                cityLog.append(properName + " is too far to travel to!\n");
            }
        }
        else
        {
            // Invalid city put
            cityLog.append(text + " is not a valid city\n");
        }
    }
}
