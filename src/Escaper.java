public class Escaper
{
    public static void main(String[] args) throws Exception
    {
        GUI gui = new GUI();
        CityEngine engine = new CityEngine("United Kingdom");
        System.out.println(engine.getCity("Liverpool")[1]);

    }
}
