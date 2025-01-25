package pt.pa.memento;

/**
 * Originator of the Memento
 */
public interface Originator {
    /**
     * Saves a state to a memento
     * @param key Key to identify the memento
     * @return Memento object
     */
    Memento save(String key);

    /**
     * Restores a state from the memento
     * @param memento Memento to restore the state from
     */
    void restore(Memento memento);
}
