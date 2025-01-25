package pt.pa.command;

import pt.pa.model.TransportsMap.RouteInfo;

import java.util.List;

/**
 * Command design pattern interface
 * @author Rodrigo Santos - 202100722,
  * João Fernandes - 202100718,
  * Rúben Dâmaso - 202100723
  *
  * [PL2 - Prof. André Sanguinetti]
 */
public interface Command {
    /**
     * Executes a command
     * @return List of RouteInfo
     */
    List<RouteInfo> execute();
}
