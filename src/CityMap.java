import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CityMap extends JPanel
{
    private static final double LAT_MAX = 3;
    private static final double LAT_MIN = -11;
    private static final double LONG_MAX = 61;
    private static final double LONG_MIN = 49;
    private static final int POINT_SIZE = 4;


    private final Image mapImage;
    private final HashMap<String, Point2D.Double> mapCities;

    private int travelRadius;
    private String highlightedCity;

    public CityMap() throws IOException
    {
        BufferedImage rawImage = ImageIO.read(new File("uk.png"));
        mapImage = rawImage.getScaledInstance(350, 500, Image.SCALE_SMOOTH);

        mapCities = new HashMap<>();

        travelRadius = 0;
    }

    public void addCity(String city, Point2D.Double point)
    {
        addCity(city, point.x, point.y);
    }
    public void addCity(String city, double longitude, double latitude)
    {
        // if inbounds of map
        System.out.println(latitude + " " + longitude);
        if (longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX)
        {
            // convert to relative position based from 0 to 1
            double relLongitude = (LONG_MAX - longitude) / (LONG_MAX - LONG_MIN);
            double relLatitude = (latitude - LAT_MIN) / (LAT_MAX - LAT_MIN);

            // place in points list
            mapCities.put(city, new Point2D.Double(relLatitude, relLongitude));

            // Make that the highlighted city
            highlightedCity = city;

            // Repaint list
            repaint();
        }
        else
        {
            throw new RuntimeException("Point out of range");
        }
    }

    public void setTravelRadius(int raidus)
    {
        travelRadius = raidus;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(mapImage, 0, 0, null);

        // Draw Points
        for (String city : mapCities.keySet())
        {
            if (city.equals(highlightedCity))
            {
                g.setColor(Color.magenta);
            }

            g.fillOval((int) (mapCities.get(city).x * getWidth() - POINT_SIZE / 2 ),
                    (int) (mapCities.get(city).y * getHeight() - POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);

            g.setColor(Color.BLACK);
        }

        // Draw radius
        if (travelRadius > 0 && highlightedCity != null)
        {
            g.setColor(Color.red);
            g.drawOval((int) (mapCities.get(highlightedCity).x * getWidth() - travelRadius),
                    (int) (mapCities.get(highlightedCity).y * getHeight() - travelRadius),
                    travelRadius * 2, travelRadius * 2);
        }
    }
}
