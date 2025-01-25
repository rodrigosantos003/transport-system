package pt.pa.observer;

/**
 * Observable interface
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public interface Observable {
    /**
     * Adds an observer to the subject
     * @param observer Oberver to add
     */
    void addObserver(Observer observer);

    /**
     * Removes an oberver from the subject
     * @param observer Observer to remove
     */
    void removeObserver(Observer observer);

    /**
     * Notifies the observers
     * @param arg Optional object type to notify
     */
    void notifyObservers(Object arg);
}
