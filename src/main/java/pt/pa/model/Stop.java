package pt.pa.model;

/**
 * Model for the stops
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class Stop {
    private final String code;
    private final String name;
    private final float latitude;
    private final float longitude;
    private int posX;
    private int posY;

    /**
     * Stop constructor
     * @param code Stop code
     * @param name Stop name
     * @param latitude Stop latitude
     * @param longitude Stop longitude
     */
    public Stop(String code, String name, float latitude, float longitude) {
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Modifies the values of a ScreenCoordinate object
     * @param posX X position
     * @param posY Y position
     */
    public void setCoordinates(int posX , int posY){
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Returns the stop code
     * @return String containing the stop code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the stop name
     * @return String containing the stop name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the stop latitude
     * @return Float containing the stop latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Returns the stop longitude
     * @return Float containing the stop longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Returns the X position of a ScreenCoordinate object
     * @return X position
     */
    public int getPosX() { return posX;
    }

    /**
     * Returns the Y position of a ScreenCoordinate object
     * @return Y position
     */
    public int getPosY() { return posY;
    }

    /**
     * Returns the data of the stop
     * @return String containing the data of the stop
     */
    @Override
    public String toString() {
        return name;
    }
}
