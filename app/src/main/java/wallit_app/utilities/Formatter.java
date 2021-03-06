package wallit_app.utilities;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formats a double value into a pretty string.
 * @author skner
 */
public class Formatter {

    static public String doubleToEuroString(double value)  {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "PT")).format(value);
    }

}
