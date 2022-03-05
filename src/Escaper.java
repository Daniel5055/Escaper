public class Escaper
{
    public static void main(String[] args) throws Exception
    {
        CityEngine engine = new CityEngine("United Kingdom");
        System.out.println(engine.getCity("Liverpool")[1]);

    }
}
