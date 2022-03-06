import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CityMap extends JPanel
{
    // Borders of the map in long and lat
    private static final double LONG_MAX = 2.2;
    private static final double LONG_MIN = -10.762;
    private static final double LAT_MAX = 59.1565;
    private static final double LAT_MIN = 49.86;

    // Map URL
    private static final String MAP_PATH = "uk-coool.png";


    // Converted 2d coords
    private static double X_MAX;
    private static double X_MIN;
    private static double Y_MIN;
    private static double Y_MAX;

    // Size of points
    private static final int POINT_SIZE = 4;

    // Map related data
    private final Image mapImage;
    private final HashMap<String, Point2D.Double> mapCities;
    private final HashMap<String, Point2D.Double> farCities;
    private final ArrayList<Line2D.Double> routes;

    // Game related data
    private int travelRadius;
    private String highlightedCity;
    private Point2D.Double endCity;

    public CityMap() throws IOException
    {
        // Initialise and scale image
        BufferedImage rawImage = ImageIO.read(new File(MAP_PATH));
        mapImage = rawImage.getScaledInstance(350, 433, Image.SCALE_SMOOTH);

        // Initialise hash maps and arrays
        mapCities = new HashMap<>();
        farCities = new HashMap<>();
        routes = new ArrayList<>();

        travelRadius = 0;

        // Calculate the relatvie points
        Y_MAX = getYFromLat(LAT_MAX);
        X_MAX = getXFromLong(LONG_MAX);
        Y_MIN = getYFromLat(LAT_MIN);
        X_MIN = getXFromLong(LONG_MIN);
    }

    public void addFarCity(String city, Point2D.Double point) throws IndexOutOfBoundsException
    {
        addFarCity(city, point.x, point.y);
    }
    public void addFarCity(String city, double longitude, double latitude) throws IndexOutOfBoundsException
    {
        // if inbounds of map
        if (longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX)
        {
            // Convert to mercator web friendly
            // convert to relative position based from 0 to 1
            double relX = (getXFromLong(longitude) - X_MIN) / (X_MAX - X_MIN);
            double relY = (Y_MAX - getYFromLat(latitude)) / (Y_MAX - Y_MIN);

            // place in far points list
            farCities.put(city, new Point2D.Double(relX, relY));

            // Repaint list
            repaint();
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }
    public void addCity(String city, Point2D.Double point) throws IndexOutOfBoundsException
    {
        addCity(city, point.x, point.y);
    }
    public void addCity(String city, double longitude, double latitude) throws IndexOutOfBoundsException
    {
        // if inbounds of map
        System.out.println(latitude + " " + longitude);
        if (longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX)
        {
            // Convert to mercator web friendly
            // convert to relative position based from 0 to 1
            // Place in points city
            mapCities.put(city, new Point2D.Double(getRelativeXFromLong(longitude), getRelativeYFromLat(latitude)));

            // Add route if previous city exists
            if (highlightedCity != null)
            {
                routes.add(new Line2D.Double(mapCities.get(highlightedCity), mapCities.get(city)));
            }

            // Make that the highlighted city
            highlightedCity = city;

            // As city found, far city points can be removed
            farCities.clear();

            // Repaint list
            repaint();
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public void setTravelRadius(int radius)
    {
        travelRadius = radius;
        repaint();
    }

    public void addEndCity(String city, Point2D.Double point) throws IndexOutOfBoundsException
    {
        addEndCity(city, point.x, point.y);
    }
    public void addEndCity(String city, double longitude, double latitude) throws IndexOutOfBoundsException
    {
        // if inbounds of map
        if (longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX)
        {
            // Convert to mercator web friendly
            // convert to relative position based from 0 to 1
            double relX = (getXFromLong(longitude) - X_MIN) / (X_MAX - X_MIN);
            double relY = (Y_MAX - getYFromLat(latitude)) / (Y_MAX - Y_MIN);

            // place in far points list
            endCity = new Point2D.Double(relX, relY);

            // Repaint list
            repaint();
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public Point2D.Double getLastCityPoint()
    {
        return mapCities.get(highlightedCity);
    }

    public int getMapWidth()
    {
        return mapImage.getWidth(null);
    }

    public int getMapHeight()
    {
        return mapImage.getHeight(null);
    }

    public double getRelativeXFromLong(double longitude)
    {
        return (getXFromLong(longitude) - X_MIN) / (X_MAX - X_MIN);
    }

    public double getRelativeYFromLat(double latitude)
    {
        return (Y_MAX - getYFromLat(latitude)) / (Y_MAX - Y_MIN);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(mapImage, 0, 0, null);

        // Draw routes
        for (Line2D.Double route : routes)
        {
            g.setColor(EscaperTheme.pastGray);
            g.drawLine((int)(route.x1 * mapImage.getWidth(null)), (int)(route.y1 * mapImage.getHeight(null)),
                    (int)(route.x2 * mapImage.getWidth(null)), (int)(route.y2 * mapImage.getHeight(null)));
        }

        // Draw end city
        g.setColor(EscaperTheme.rightGreen);
        g.fillOval((int) (endCity.x * mapImage.getWidth(null) - POINT_SIZE / 2 ),
                (int) (endCity.y * mapImage.getHeight(null) - POINT_SIZE / 2),
                POINT_SIZE, POINT_SIZE);

        // Draw cities
        g.setColor(EscaperTheme.wrongRed);
        drawPoints(g, farCities);

        g.setColor(EscaperTheme.pastGray);
        drawPoints(g, mapCities);

        // Draw highlighted city
        if (highlightedCity != null)
        {
            g.setColor(EscaperTheme.lightGray);
            g.fillOval((int) (mapCities.get(highlightedCity).x * mapImage.getWidth(null) - POINT_SIZE / 2 ),
                    (int) (mapCities.get(highlightedCity).y * mapImage.getHeight(null) - POINT_SIZE / 2),
                    POINT_SIZE, POINT_SIZE);

            // Draw travel radius if highlighted city exists
            if (travelRadius > 0)
            {
                g.setColor(Color.lightGray);
                g.drawOval((int) (mapCities.get(highlightedCity).x * mapImage.getWidth(null) - travelRadius),
                        (int) (mapCities.get(highlightedCity).y * mapImage.getHeight(null) - travelRadius),
                        travelRadius * 2, travelRadius * 2);
            }
        }

    }

    private void drawPoints(Graphics g, HashMap<String, Point2D.Double> points)
    {
        // Draw City Points
        for (String point : points.keySet())
        {
            g.fillOval((int) (points.get(point).x * mapImage.getWidth(null) - POINT_SIZE / 2 ),
                    (int) (points.get(point).y * mapImage.getHeight(null) - POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);
        }
    }

    private double getXFromLong(double longitude)
    {
        // Everything is relative so this is technically not necessary
        return longitude;
    }

    private double getYFromLat(double latitude)
    {
        return Math.log(Math.tan(Math.PI/4 + latitude * Math.PI / 360));
    }
}
