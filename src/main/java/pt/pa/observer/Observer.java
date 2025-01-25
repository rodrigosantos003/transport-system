package pt.pa.observer;

/**
 * Observer interface
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public interface Observer {
    /**
     * Updates the status when notified by the observable subject
     * @param subject Subject to get notifications
     * @param arg Optional object type to notify
     */
    void update(Observable subject, Object arg);
}
