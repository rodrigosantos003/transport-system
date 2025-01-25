package pt.pa.utils;

/**
 * Utility class to parse values, if possible, or the default values otherwise
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class ParsingUtil {
    /**
     * Returns a parsed float value or a default one
     * @param value String value to parse
     * @param defaultValue Default value
     * @return Parsed value if it's a valid one. Default value otherwise
     */
    public static Float parseFloatOrDefault(String value, Float defaultValue) {
        try {
            if(!value.isEmpty())
                return Float.parseFloat(value);

            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Returns a parsed integer value or a default one
     * @param value String value to parse
     * @param defaultValue Default value
     * @return Parsed value if it's a valid one. Default value otherwise
     */
    public static Integer parseIntOrDefault(String value, Integer defaultValue) {
        try {
            if(!value.isEmpty())
                return Integer.parseInt(value);

            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
