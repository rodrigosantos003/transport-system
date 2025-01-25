package pt.pa.command;
import pt.pa.model.TransportsMap.RouteInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Command for custom trip
 * @author Rodrigo Santos - 202100722,
  * João Fernandes - 202100718,
  * Rúben Dâmaso - 202100723
  *
  * [PL2 - Prof. André Sanguinetti]
 */
public class CustomTripCommand implements Command {
    Queue<TripCommand> tripCommands;

    /**
     * CustomTripCommand constructor
     * @param tripCommands List of TripCommand
     */
    public CustomTripCommand(List<TripCommand> tripCommands) {
        this.tripCommands = new LinkedList<>(tripCommands);
    }

    @Override
    public List<RouteInfo> execute() {
        List<RouteInfo> routeInfos = new ArrayList<>();
        for(TripCommand tripCommand : tripCommands) {
            routeInfos.addAll(tripCommand.execute());
        }

        return routeInfos;
    }
}
