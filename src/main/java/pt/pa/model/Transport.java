package pt.pa.model;

/**
 * Enumerator for the transport types
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public enum Transport {
    /**
     * Train transport
     */
    TRAIN,
    /**
     * Bus transport
     */
    BUS,
    /**
     * Boat transport
     */
    BOAT,
    /**
     * Walk transport
     */
    WALK,

    /**
     * Bicycle transport
     */
    BICYCLE;

    /**
     * Returns the string representation of the transport
     * @return String containing the transport
     */
    @Override
    public String toString() {
        return switch (this) {
            case TRAIN -> "Comboio";
            case BUS -> "Autocarro";
            case BOAT -> "Barco";
            case WALK -> "A pé";
            case BICYCLE -> "Bicicleta";
        };
    }

    /**
     * Returns the transport from a string
     * @param s String containing the transport
     * @return Transport object
     */
    public static Transport fromString(String s) {
        return switch (s) {
            case "Comboio" -> TRAIN;
            case "Autocarro" -> BUS;
            case "Barco" -> BOAT;
            case "A pé" -> WALK;
            case "Bicicleta" -> BICYCLE;
            default -> null;
        };
    }

    /**
     * Returns the style class for the transport
     * @return String containing the style class
     */
    public String getStyleClass() {
        return switch (this) {
            case TRAIN -> "edge-train";
            case BUS -> "edge-bus";
            case BOAT -> "edge-boat";
            case WALK -> "edge-walk";
            case BICYCLE -> "edge-bicycle";
        };
    }

    /**
     * Returns the corresponding emoji of the transport
     * @return Emoji string
     */
    public String toEmoji() {
        return switch (this) {
            case TRAIN -> "🚆";
            case BUS -> "🚌";
            case BOAT -> "🚢";
            case WALK -> "🚶";
            case BICYCLE -> "🚲";
        };
    }

    /**
     * Converts the values to a string array
     * @return String array containing the values
     */
    public static String[] valuesToStringArray(){
        Transport[] values = Transport.values();
        String[] stringArray = new String[values.length];

        for(int i = 0; i < values.length; i ++){
            stringArray[i] = values[i].toString();
        }

        return stringArray;
    }
}
