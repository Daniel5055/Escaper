import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

public class EscaperState extends State implements ActionListener
{
    // GUI components
    private JTextField inputBox;
    private JTextArea cityLog;
    private CityMap cityMap;
    private JFrame frame;
    private JDialog winDialog;
    private JLabel dialogCityCount;
    private JLabel dialogCityPlurality;

    private CityEngine cityEngine;

    private int travelRange;
    private String currentCity;
    private String endCity;
    private String startCity;
    private int numberOfCities;

    public EscaperState(EscaperEngine engine, int travelRange, String startCity, String endCity)
    {
        // Initialise engine
        cityEngine = engine.getCityEngine();

        // Set the state data
        this.startCity = startCity;
        this.currentCity = startCity;
        this.endCity = endCity;
        this.travelRange = travelRange;
        this.numberOfCities = 0;

        frame = engine.getFrame();

        // Initialise content pane
        JPanel content = new JPanel();
        content.setBackground(EscaperTheme.oceanGray);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setLayout(new GridBagLayout());
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialise cityMap
        try
        {
            cityMap = new CityMap();
            cityMap.setTravelRadius(travelRange);
            cityMap.addCity(currentCity, cityEngine.getCityPoint(currentCity));
            cityMap.setBackground(EscaperTheme.oceanGray);
            cityMap.addEndCity(endCity, cityEngine.getCityPoint(endCity));
        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }

        // Initialise cityLog
        cityLog = new JTextArea(20, 20);
        JScrollPane pane = new JScrollPane(cityLog);
        pane.setBorder(null);
        pane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        pane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        cityLog.setEditable(false);
        cityLog.setBorder(new CompoundBorder(new MatteBorder(0, 1, 0, 0, EscaperTheme.lightGray),
                new EmptyBorder(3, 3, 3, 3)));

        cityLog.setFont(EscaperTheme.mediumFont);
        cityLog.setForeground(EscaperTheme.lightGray);
        cityLog.setBackground(EscaperTheme.oceanGray);

        // Initialise inputBox
        inputBox = new JTextField(8);
        inputBox.setFocusable(true);
        inputBox.requestFocusInWindow();
        inputBox.addActionListener(this);
        inputBox.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, EscaperTheme.lightGray),
                new EmptyBorder(3, 3, 3, 3)));

        inputBox.setFont(EscaperTheme.largeFont);
        inputBox.setBackground(EscaperTheme.oceanGray);
        inputBox.setForeground(EscaperTheme.lightGray);

        // Initialise start label
        JLabel startCityLabel = new JLabel("You are in " + startCity, SwingConstants.CENTER);
        startCityLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        startCityLabel.setFont(EscaperTheme.largeFont);
        startCityLabel.setForeground(Color.lightGray);

        // Initialise location label
        JLabel endCityLabel = new JLabel("Get to " + endCity + "!", SwingConstants.CENTER);
        endCityLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        endCityLabel.setFont(EscaperTheme.largeFont);
        endCityLabel.setForeground(Color.lightGray);

        // Create layout constraints and add components to frame
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 10);
        frame.add(cityMap, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx=0;
        c.weighty=0;
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight=1;
        frame.add(pane, c );

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        frame.add(inputBox, c);

        c.gridy = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        frame.add(startCityLabel, c);


        c.gridy = 1;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        frame.add(endCityLabel, c);

        // Show frame
        frame.setVisible(true);

        // Dialog box initialisation
        winDialog = new JDialog(frame);

        JLabel dialogWinStatement = new JLabel("You Escaped!");
        dialogWinStatement.setBorder(new EmptyBorder(0, 0, 10, 0));
        dialogWinStatement.setFont(EscaperTheme.largeFont);
        dialogWinStatement.setForeground(EscaperTheme.lightGray);
        dialogWinStatement.setAlignmentX(Component.CENTER_ALIGNMENT);
        winDialog.add(dialogWinStatement);

        JLabel dialogCityCountText = new JLabel("You escaped through");
        dialogCityCountText.setFont(EscaperTheme.mediumFont);
        dialogCityCountText.setForeground(EscaperTheme.lightGray);
        dialogCityCountText.setAlignmentX(Component.CENTER_ALIGNMENT);
        winDialog.add(dialogCityCountText);

        dialogCityCount = new JLabel("" + numberOfCities);
        dialogCityCount.setForeground(EscaperTheme.lightGray);
        dialogCityCount.setFont(EscaperTheme.largeFont);
        dialogCityCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        winDialog.add(dialogCityCount);

        dialogCityPlurality = new JLabel("cities");
        dialogCityPlurality.setFont(EscaperTheme.mediumFont);
        dialogCityPlurality.setForeground(EscaperTheme.lightGray);
        dialogCityPlurality.setAlignmentX(Component.CENTER_ALIGNMENT);
        winDialog.add(dialogCityPlurality);

        JPanel dialogButtonWrapper = new JPanel();
        dialogButtonWrapper.setBackground(EscaperTheme.oceanGray);
        dialogButtonWrapper.setLayout(new FlowLayout());

        JButton dialogReplayButton = new JButton("Play again");
        dialogReplayButton.setContentAreaFilled(false);
        dialogReplayButton.setFont(EscaperTheme.mediumFont);
        dialogReplayButton.setForeground(EscaperTheme.lightGray);
        dialogReplayButton.setBorder(new EmptyBorder(20, 0, 10, 0));
        dialogReplayButton.setPreferredSize(new Dimension(95, 30));

        dialogReplayButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Play again
                winDialog.setVisible(false);
                String[] cities = engine.getDistantCities();
                engine.nextState(new EscaperState(engine, 50, cities[0], cities[1]));

            }
        });

        dialogReplayButton.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (dialogReplayButton.getModel().isPressed())
                {
                    dialogReplayButton.setForeground(EscaperTheme.landGray);
                }
                else if (dialogReplayButton.getModel().isRollover())
                {
                    dialogReplayButton.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    dialogReplayButton.setForeground(EscaperTheme.lightGray);
                }
            }
        });

        JButton dialogExitButton = new JButton("Exit");
        dialogExitButton.setContentAreaFilled(false);
        dialogExitButton.setFont(EscaperTheme.mediumFont);
        dialogExitButton.setForeground(EscaperTheme.lightGray);
        dialogExitButton.setBorder(new EmptyBorder(20, 0, 10, 0));
        dialogExitButton.setPreferredSize(new Dimension(95, 30));

        dialogExitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Just close
                System.exit(0);
            }
        });

        dialogExitButton.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (dialogExitButton.getModel().isPressed())
                {
                    dialogExitButton.setForeground(EscaperTheme.landGray);
                }
                else if (dialogExitButton.getModel().isRollover())
                {
                    dialogExitButton.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    dialogExitButton.setForeground(EscaperTheme.lightGray);
                }
            }
        });

        dialogButtonWrapper.add(dialogReplayButton);
        dialogButtonWrapper.add(dialogExitButton);
        winDialog.add(dialogButtonWrapper);

        winDialog.setSize(200, 250);
        winDialog.setResizable(false);
        winDialog.setLocation(frame.getLocation().x + 200, frame.getLocation().y + 200);
        winDialog.setLayout(new BoxLayout(winDialog.getContentPane(), BoxLayout.Y_AXIS));

        winDialog.getContentPane().setBackground(EscaperTheme.oceanGray);
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
                if (currentCity.equalsIgnoreCase(properName))
                {

                }
                else
                {
                    // Can move to city
                    cityMap.addCity(properName, point);
                    cityLog.append(currentCity + " > > > " + properName  + "\n");
                    currentCity = properName;
                    numberOfCities++;

                    // If win condition
                    if (currentCity.equalsIgnoreCase(endCity))
                    {
                        winDialog.setVisible(true);
                        dialogCityCount.setText("" + numberOfCities);
                        if (numberOfCities == 1)
                        {
                            dialogCityPlurality.setText("city");
                        }
                    }
                }
            }
            else
            {
                cityMap.addFarCity(properName, point);
            }
        }
        else
        {
            cityLog.append(text + " ?\n");
        }
    }

}
