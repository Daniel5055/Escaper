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

    // Engines
    private CityEngine cityEngine;
    private EscaperEngine engine;

    // Data
    private int travelRange;
    private String currentCity;
    private String endCity;
    private String startCity;
    private int numberOfCities;

    // Map specific data, UK excluded as already added
    private final String[] europeCountries = new String[]{
            "Spain", "Portugal", "Ireland", "France", "Andorra", "Monaco", "Italy", "Vatican City",
            "San Marino", "Cyprus", "Greece", "Albania", "Montenegro", "Serbia", "Kosovo", "Macedonia",
            "Bosnia And Herzegovina", "Croatia", "Slovenia", "Bulgaria", "Hungary", "Malta", "Romania", "Moldova",
            "Turkey", "Ukraine", "Belarus", "Russia", "Estonia", "Latvia", "Lithuania", "Finland", "Norway", "Sweden",
            "Denmark", "Germany", "Netherlands", "Belgium", "Luxembourg", "Switzerland", "Austria", "Czechia", "Slovakia",
            "Liechtenstein", "Poland"};

    // Constructor
    public EscaperState(EscaperEngine engine, int travelRange, String startCity, String endCity, String region)
     {
        this.region = region;

        // Initialise engine
        cityEngine = engine.getCityEngine();
        this.engine = engine;

        // Set the state data
        this.startCity = startCity;
        this.currentCity = startCity;
        this.endCity = endCity;
        this.travelRange = travelRange;
        this.numberOfCities = 0;

        frame = engine.getFrame();
    }

    public void start(String region)
    {
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
            // Adding the countries to cityEngine if Europe selected
            if (region.equalsIgnoreCase("Europe"))
            {
                for (String country : europeCountries)
                {
                    cityEngine.addCountryConstraint(country);
                }
            }

            cityMap = new CityMap(region);
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

        // Initialisation of Dialog text
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

        // Initialisation of the play button and exit button, wrapped in panel
        JPanel dialogButtonWrapper = new JPanel();
        dialogButtonWrapper.setBackground(EscaperTheme.oceanGray);
        dialogButtonWrapper.setLayout(new FlowLayout());

        JButton dialogReplayButton = new JButton("Play again");
        dialogReplayButton.setContentAreaFilled(false);
        dialogReplayButton.setFont(EscaperTheme.mediumFont);
        dialogReplayButton.setForeground(EscaperTheme.lightGray);
        dialogReplayButton.setBorder(new EmptyBorder(20, 0, 10, 0));
        dialogReplayButton.setPreferredSize(new Dimension(95, 30));

        // On press
        dialogReplayButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Play again
                winDialog.dispose();
                String[] cities = engine.getDistantCities();
                engine.nextState(new EscaperState(engine, travelRange, cities[0], cities[1], region));

            }
        });

        // On state change
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

        // on press
        dialogExitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Just close
                System.exit(0);
            }
        });

        // On state change
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
        winDialog.setUndecorated(true);
        winDialog.getContentPane().setBackground(EscaperTheme.oceanGray);
    }


    public void popUpStartDialog()
    {
        // Initialisation of start dialog box
        JDialog dialog = new JDialog(frame);

        dialog.setSize(400, 500);
        dialog.setResizable(false);
        dialog.setLocation(frame.getLocation().x + 200, frame.getLocation().y + 200);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.setUndecorated(true);

        dialog.getContentPane().setBackground(EscaperTheme.oceanGray);

        // Title
        JLabel title = new JLabel("Escaper");
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        title.setForeground(EscaperTheme.lightGray);
        title.setFont(EscaperTheme.largeFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(title);

        // Context
        JLabel context = new JLabel("<html> <p align=\"center\">You (an undercover murderer!) are on the run in the UK. The police are starting to catch " +
                "up on your tail, and so you've got to go into hiding. However, you can only go so far when travelling " +
                "between cities. Travel through cities close enough to reach to arrive at your destination! " +
                "The less cities the better!</p> </html>");
        context.setForeground(EscaperTheme.lightGray);
        context.setFont(EscaperTheme.mediumFont);
        context.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(context);

        // Region choice
        JButton playInEurope = new JButton("Play in Europe");
        playInEurope.setContentAreaFilled(false);
        playInEurope.setFont(EscaperTheme.largeFont);
        playInEurope.setForeground(EscaperTheme.lightGray);
        playInEurope.setBorder(new EmptyBorder(20, 0, 10, 0));
        playInEurope.setAlignmentX(Component.CENTER_ALIGNMENT);
        playInEurope.setPreferredSize(new Dimension(300, 60));

        playInEurope.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                region = "Europe";
                startCity = "London";
                endCity = "Istanbul";
            }
        });

        playInEurope.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (playInEurope.getModel().isPressed())
                {
                    playInEurope.setForeground(EscaperTheme.landGray);
                }
                else if (playInEurope.getModel().isRollover())
                {
                    playInEurope.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    playInEurope.setForeground(EscaperTheme.lightGray);
                }
            }
        });
        dialog.add(playInEurope);

        // Difficulty selection
        JLabel difficultySelect = new JLabel("Select your cup of tea:");
        difficultySelect.setForeground(EscaperTheme.lightGray);
        difficultySelect.setFont(EscaperTheme.largeFont);
        difficultySelect.setBorder(new EmptyBorder(50, 0, 20, 0));
        difficultySelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(difficultySelect);

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setBackground(EscaperTheme.oceanGray);
        buttonWrapper.setLayout(new FlowLayout());

        JButton hard = new JButton("Hard");
        hard.setContentAreaFilled(false);
        hard.setFont(EscaperTheme.largeFont);
        hard.setForeground(EscaperTheme.lightGray);
        hard.setBorder(new EmptyBorder(20, 0, 10, 0));
        hard.setPreferredSize(new Dimension(100, 50));

        hard.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                travelRange = 30;
                start(region);
                dialog.dispose();
            }
        });

        hard.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (hard.getModel().isPressed())
                {
                    hard.setForeground(EscaperTheme.landGray);
                }
                else if (hard.getModel().isRollover())
                {
                    hard.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    hard.setForeground(EscaperTheme.lightGray);
                }
            }
        });

        JButton normal = new JButton("Normal");
        normal.setContentAreaFilled(false);
        normal.setFont(EscaperTheme.largeFont);
        normal.setForeground(EscaperTheme.lightGray);
        normal.setBorder(new EmptyBorder(20, 0, 10, 0));
        normal.setPreferredSize(new Dimension(150, 50));

        normal.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Normal
                travelRange = 50;
                start(region);
                dialog.dispose();
            }
        });

        normal.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (normal.getModel().isPressed())
                {
                    normal.setForeground(EscaperTheme.landGray);
                }
                else if (normal.getModel().isRollover())
                {
                    normal.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    normal.setForeground(EscaperTheme.lightGray);
                }
            }
        });

        JButton easy = new JButton("Easy");
        easy.setContentAreaFilled(false);
        easy.setFont(EscaperTheme.largeFont);
        easy.setForeground(EscaperTheme.lightGray);
        easy.setBorder(new EmptyBorder(20, 0, 10, 0));
        easy.setPreferredSize(new Dimension(100, 50));

        easy.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //Easy
                travelRange = 80;
                start(region);
                dialog.dispose();
            }
        });

        easy.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (easy.getModel().isPressed())
                {
                    easy.setForeground(EscaperTheme.landGray);
                }
                else if (easy.getModel().isRollover())
                {
                    easy.setForeground(EscaperTheme.pastGray);
                }
                else
                {
                    easy.setForeground(EscaperTheme.lightGray);
                }
            }
        });

        buttonWrapper.add(hard);
        buttonWrapper.add(normal);
        buttonWrapper.add(easy);

        dialog.add(buttonWrapper);
        dialog.setVisible(true);
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
                    // May add logging here
                }
                else
                {
                    // Can move to city
                    try
                    {
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
                    catch (IndexOutOfBoundsException ex)
                    {
                        // City entered is not on map
                        cityLog.append(properName + " is not visible on the map :'(\n");
                    }
                }
            }
            else
            {
                try
                {
                    cityMap.addFarCity(properName, point);
                }
                catch (IndexOutOfBoundsException ex)
                {
                    cityLog.append(properName + " is not visible on the map :'(\n");
                }
            }
        }
        else
        {
            // Not valid city
            cityLog.append(text + " ?\n");
        }
    }
}
