package pt.pa.utils;

import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pt.pa.utils.ParsingUtil.parseFloatOrDefault;
import static pt.pa.utils.ParsingUtil.parseIntOrDefault;

/**
 * Utility class to import the dataset
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class DatasetUtils {

    /**
     * Loads the routes from CSV file
     * @return List containing all routes
     */
    public static List<Route> loadRoutesFromCSV() {
        List<Route> routes = new ArrayList<>();

        try{
            // Open the file
            InputStream stream = DatasetUtils.class.getResourceAsStream("/dataset/routes.csv");
            if(stream == null){
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            // Skip header
            br.readLine();

            // Start reading CSV data
            String line = br.readLine();
            while (line != null){
                // Split values
                String[] values = line.split(",", -1);

                // Build the object
                String startStopCode = values[0];
                String endStopCode = values[1];

                HashMap<Transport, Float> distances = new HashMap<>();
                distances.put(Transport.TRAIN, parseFloatOrDefault(values[2], null));
                distances.put(Transport.BUS, parseFloatOrDefault(values[3], null));
                distances.put(Transport.BOAT, parseFloatOrDefault(values[4], null));
                distances.put(Transport.WALK, parseFloatOrDefault(values[5], null));
                distances.put(Transport.BICYCLE, parseFloatOrDefault(values[6], null));

                HashMap<Transport, Integer> durations = new HashMap<>();
                durations.put(Transport.TRAIN, parseIntOrDefault(values[7], null));
                durations.put(Transport.BUS, parseIntOrDefault(values[8], null));
                durations.put(Transport.BOAT, parseIntOrDefault(values[9], null));
                durations.put(Transport.WALK, parseIntOrDefault(values[10], null));
                durations.put(Transport.BICYCLE, parseIntOrDefault(values[11], null));

                HashMap<Transport, Float> costs = new HashMap<>();
                costs.put(Transport.TRAIN, parseFloatOrDefault(values[12], null));
                costs.put(Transport.BUS, parseFloatOrDefault(values[13], null));
                costs.put(Transport.BOAT, parseFloatOrDefault(values[14], null));
                costs.put(Transport.WALK, parseFloatOrDefault(values[15], null));
                costs.put(Transport.BICYCLE, parseFloatOrDefault(values[16], null));

                // Add object to list
                routes.add(new Route(startStopCode, endStopCode, distances, durations, costs));

                // Next line
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("ERROR: Could not read the file.");
        }

        return routes;
    }

    /**
     * Loads screen coordinates from CSV file
     * @param stops List of stops to link the coordinates
     */
    public static void loadCoordinatesFromCSV(List<Stop> stops){
        try{
            // Open the file
            InputStream stream = DatasetUtils.class.getResourceAsStream("/dataset/xy.csv");
            if(stream == null){
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            // Skip header
            br.readLine();

            // Start reading CSV data
            String line = br.readLine();
            while (line != null){
                // Split values
                String[] values = line.split(",", -1);

                String stopCode = values[0];

                for(Stop stop : stops){
                    if(stop.getCode().equals(stopCode)){
                        int posX = parseIntOrDefault(values[1], 0);
                        int posY = parseIntOrDefault(values[2], 0);

                        stop.setCoordinates(posX, posY);
                    }
                }

                line = br.readLine();
            }
        } catch (IOException e){
            System.out.println("ERROR: Could not read the file.");
        }
    }

    /**
     * Loads the stops from CSV file
     * @return List containing all stops
     */
    public static List<Stop> loadStopsFromCSV() {
        List<Stop> stops = new ArrayList<>();

        try{
            // Open the file
            InputStream stream = DatasetUtils.class.getResourceAsStream("/dataset/stops.csv");
            if(stream == null){
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            // Skip header
            br.readLine();

            // Start reading CSV data
            String line = br.readLine();
            while (line != null){
                // Split values
                String[] values = line.split(",", -1);

                // Build the object
                String code = values[0];
                String name = values[1];
                float latitude = Float.parseFloat(values[2]);
                float longitude = Float.parseFloat(values[3]);

                // Add object to list
                stops.add(new Stop(code, name, latitude, longitude));

                // Next line
                line = br.readLine();
            }
        } catch (IOException e){
            System.out.println("ERROR: Could not read the file.");
        }

        return stops;
    }
}
