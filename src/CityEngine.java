import java.awt.geom.Point2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class CityEngine
{
      // Static Constants
      private final static String DATA_PATH = "cities.db";

      // Database connection
      private Connection connection;

      // Scope
      private ArrayList<String> countries;

      public CityEngine()
      {
            countries = new ArrayList<>();

            // Make connection
            try
            {
                  String dbUrl = "jdbc:sqlite:" + DATA_PATH;
                  connection = DriverManager.getConnection(dbUrl);
            }
            catch (SQLException e)
            {
                  System.out.println("Error with SQL");
            }
      }

      public void addCountryConstraint(String country)
      {
            countries.add(country);
      }

      public Point2D.Double getCityPoint(String name)
      {
            try
            {
                  StringBuilder sb = new StringBuilder();
                  sb.append("SELECT lat, lng FROM cities WHERE country IN ( ");

                  for (int i = 0; i < countries.size(); i++)
                  {
                        sb.append("?");
                        if (i != countries.size() - 1)
                        {
                              sb.append(", ");
                        }
                  }
                  sb.append(" ) AND ( LOWER(city) = LOWER(?) OR LOWER(city_ascii) = LOWER(?) ) " +
                          "ORDER BY population");

                  PreparedStatement statement = connection.prepareStatement(sb.toString());

                  int i;
                  for (i = 1; i <= countries.size(); i++)
                  {
                        statement.setString(i, countries.get(i - 1));
                  }
                  statement.setString(i, name);
                  statement.setString(i+1, name);

                  ResultSet coords = statement.executeQuery();

                  // Get coords, take first (most po
                  if (coords.next())
                  {
                        return new Point2D.Double(coords.getDouble(2), coords.getDouble(1));
                  }
            }
            catch (SQLException e)
            {
                  System.out.println(e.getStackTrace());
            }

            // Else return null
            return null;
      }

      public String getProperCityName(String name)
      {
            try
            {
                  StringBuilder sb = new StringBuilder();
                  sb.append("SELECT city FROM cities WHERE country IN (");

                  for (int i = 0; i < countries.size(); i++)
                  {
                        sb.append("?");
                        if (i != countries.size() - 1)
                        {
                              sb.append(", ");
                        }
                  }

                  sb.append(" ) AND ( LOWER(city) = LOWER(?) OR LOWER(city_ascii) = LOWER(?) ) " +
                          "ORDER BY population");

                  PreparedStatement statement = connection.prepareStatement(sb.toString());

                  int i;
                  for (i = 1; i <= countries.size(); i++)
                  {
                        statement.setString(i, countries.get(i - 1));
                  }
                  statement.setString(i, name);
                  statement.setString(i+1, name);

                  ResultSet properName = statement.executeQuery();

                  // Get name, take first (most populous)
                  if (properName.next())
                  {
                        return properName.getString(1);
                  }

            }
            catch (SQLException e)
            {
                  System.out.println(e.getStackTrace());
            }

            // Else return null
            return null;
      }

      public String getRandomCity()
      {
            try
            {
                  StringBuilder sb = new StringBuilder();
                  sb.append("SELECT city FROM cities WHERE country IN ( ");

                  for (int i = 0; i < countries.size(); i++)
                  {
                        sb.append("?");
                        if (i != countries.size() - 1)
                        {
                              sb.append(", ");
                        }
                  }
                  sb.append(" ) ORDER BY population");

                  PreparedStatement statement = connection.prepareStatement(sb.toString());

                  int i;
                  for (i = 1; i <= countries.size(); i++)
                  {
                        statement.setString(i, countries.get(i - 1));
                  }

                  // Transfer Result Set to list
                  ArrayList<String> results = new ArrayList<>();
                  ResultSet names = statement.executeQuery();
                  while (names.next())
                  {
                        results.add(names.getString(1));
                  }

                  // Pick random city from list
                  Random random = new Random();

                  return results.get(random.nextInt(results.size()));
            }
            catch (SQLException e)
            {
                  System.out.println(e.getStackTrace());
            }

            // Else return null
            return null;
      }

}
