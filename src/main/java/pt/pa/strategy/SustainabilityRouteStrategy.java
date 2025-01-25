package pt.pa.strategy;

import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;
import pt.pa.model.TransportsMap;

import java.util.List;
import java.util.Map;

/**
 * Concrete strategy for sustainability routes
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class SustainabilityRouteStrategy implements RouteStrategy{
    @Override
    public Map<Stop, TransportsMap.RouteInfo> calculateRoute(TransportsMap map, Stop start, List<Transport> transports) {
        return map.BellmanFord(start, transports, "Sustentabilidade");
    }

    @Override
    public Float getValueByCriterion(Route route, Transport transport) {
        return route.getCosts().get(transport) == null ? null : route.getCosts().get(transport).floatValue();
    }
}
