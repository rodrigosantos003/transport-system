package pt.pa.view;

import pt.pa.controller.MapController;
import pt.pa.observer.Observer;

/**
 * Interface for the UI
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public interface MapViewUI extends Observer {
    /**
     * Sets the view triggers for the controller
     * @param controller MapController object
     */
    void setTriggers(MapController controller);

    /**
     * Displays an error message
     * @param message Message to present
     */
    void showError(String message);
}
