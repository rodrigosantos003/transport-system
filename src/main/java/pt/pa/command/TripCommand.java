package pt.pa.command;

import pt.pa.controller.MapController;
import pt.pa.model.Stop;
import pt.pa.model.Transport;
import pt.pa.model.TransportsMap.RouteInfo;

import java.util.List;

/**
 * Command for trip
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class TripCommand implements Command {
    private final MapController controller;
    private final Stop start;
    private final Stop end;
    private final String criterion;
    private final List<Transport> transports;

    /**
     * TripCommand constructor
     * @param start Start Stop
     * @param end End Stop
     * @param criterion Criterion
     * @param transports List of transports
     * @param controller MapController
     */
    public TripCommand(Stop start, Stop end, String criterion, List<Transport> transports, MapController controller) {
        this.controller = controller;
        this.start = start;
        this.end = end;
        this.criterion = criterion;
        this.transports = transports;
    }

    @Override
    public List<RouteInfo> execute() {
        return controller.calculateRouteFromCommand(
                start,
                end,
                criterion,
                transports
        );
    }
}
