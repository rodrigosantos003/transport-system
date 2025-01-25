package pt.pa.strategy;

import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;
import pt.pa.model.TransportsMap;

import java.util.List;
import java.util.Map;

/**
 * Route Strategy interface
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public interface RouteStrategy {
    /**
     * Calculates a route
     * @param map TransportsMap object
     * @param start Start stop
     * @param transports List of transports
     * @return HashMap containing the possible route for the stop
     */
    Map<Stop, TransportsMap.RouteInfo> calculateRoute(TransportsMap map, Stop start, List<Transport> transports);

    /**
     * Gets the value based on the strategy criterion
     * @param route Route to get the value
     * @param transport Transport type
     * @return Float value
     */
    Float getValueByCriterion(Route route, Transport transport);
}
