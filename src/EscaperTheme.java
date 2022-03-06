import java.awt.*;
import java.io.File;
import java.io.IOException;

public class EscaperTheme
{
    // Colours
    public static final Color lightGray = new Color(147, 159, 155);
    public static final Color oceanGray = new Color(32, 36, 39);
    public static final Color landGray = new Color(77, 96, 89);
    public static final Color pastGray = new Color(127, 141, 137);
    public static final Color wrongRed = new Color(224, 161, 161);
    public static final Color rightGreen = new Color(166, 242, 165);

    public static Font mediumFont = null;
    public static Font largeFont = null;
    // Font
    static
    {
        try
        {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new File("Hubballi-Regular.ttf"));
            mediumFont = baseFont.deriveFont(Font.PLAIN, 18);
            largeFont = baseFont.deriveFont(Font.PLAIN, 36);

        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }
        catch (FontFormatException e)
        {
            System.out.println(e.getStackTrace());
        }

    }
}
