import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

public class EscaperEngine
{
    // Logic and data attributes
    State state;
    JFrame frame;
    CityEngine cityEngine;

    public EscaperEngine()
    {
        cityEngine = new CityEngine();
        cityEngine.addCountryConstraint("United Kingdom");

        frame = new JFrame();
        frame.setMinimumSize(new Dimension(700, 650));
        frame.setResizable(true);
        frame.setVisible(true);

        String[] cities = getDistantCities();
        state = new EscaperState(this, 50, cities[0], cities[1]);
    }

    public String[] getDistantCities()
    {
        // First get random city
        String[] cities = new String[2];

        cities[0] = cityEngine.getRandomCity();

        Point2D.Double startPoint = cityEngine.getCityPoint(cities[0]);
        Point2D.Double endPoint = null;

        // Iterate through random cities until far enough
        do
        {
            cities[1] = cityEngine.getRandomCity();
            endPoint = cityEngine.getCityPoint(cities[1]);
        }
        while (Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2)) < 5);

        return cities;
    }

    public void nextState(State state)
    {
        this.state = state;
    }


    public JFrame getFrame()
    {
        return frame;
    }

    public CityEngine getCityEngine()
    {
        return cityEngine;
    }
}
