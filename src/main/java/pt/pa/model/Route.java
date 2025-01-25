package pt.pa.model;

import pt.pa.memento.Memento;
import pt.pa.memento.Originator;

import java.util.HashMap;

/**
 * Model for the routes
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class Route implements Originator {
    private String startStopCode;
    private String endStopCode;
    private HashMap<Transport, Float> distances;
    private HashMap<Transport, Integer> durations;
    private HashMap<Transport, Float> costs;
    private boolean isActive;

    /**
     * Route constructor
     * @param startStopCode Start stop code
     * @param endStopCode End stop code
     * @param distances Distances
     * @param durations Durations
     * @param costs Costs
     */
    public Route(String startStopCode, String endStopCode, HashMap<Transport, Float> distances, HashMap<Transport, Integer> durations, HashMap<Transport, Float> costs) {
        this.startStopCode = startStopCode;
        this.endStopCode = endStopCode;
        this.distances = distances;
        this.durations = durations;
        this.costs = costs;
        this.isActive = true;
    }

    /**
     * Returns the start stop code
     * @return String containing the start stop code
     */
    public String getStartStopCode() {
        return startStopCode;
    }

    /**
     * Returns the end stop code
     * @return String containing the end stop code
     */
    public String getEndStopCode() {
        return endStopCode;
    }

    /**
     * Returns the distances hash map
     * @return Hash map containing the distance of each transport
     */
    public HashMap<Transport, Float> getDistances() {
        return distances;
    }

    /**
     * Returns de durations hash map
     * @return Hash map containing the duration of each transport
     */
    public HashMap<Transport, Integer> getDurations() {
        return durations;
    }

    /**
     * Returns the costs hash map
     * @return Hash map containing the cost of each transport
     */
    public HashMap<Transport, Float> getCosts() {
        return costs;
    }

    /**
     * Returns the route status
     * @return True if the route is active. False otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Toggles the route status
     */
    public void toggleActive() {
        this.isActive = !this.isActive;
    }

    /**
     * Disables a transport
     * @param transport Transport to disable
     */
    public void disableTransport(Transport transport) {
        distances.put(transport, null);
        durations.put(transport, null);
        costs.put(transport, null);
    }

    /**
     * Enables a transport
     * @param transport Transport to enable
     * @param distance Value of distance
     * @param duration Value of duration
     * @param cost Value of cost
     */
    public void enableTransport(Transport transport, Float distance, Integer duration, Float cost) {
        distances.put(transport, distance);
        durations.put(transport, duration);
        costs.put(transport, cost);
    }

    /**
     * Saves bicycle duration
     * @return Memento with the bicycle duration
     */
    public Memento saveBicycleDuration() {
        // Save the current duration for bicycles
        return new TransportMemento(Transport.BICYCLE, distances.get(Transport.BICYCLE), durations.get(Transport.BICYCLE), costs.get(Transport.BICYCLE));
    }

    /**
     * Restores bicycle duration
     * @param memento Memento to restore the bicycle duration
     */
    public void restoreBicycleDuration(Memento memento) {
        TransportMemento transportMemento = (TransportMemento) memento;

        if (transportMemento.transport == Transport.BICYCLE) {
            enableTransport(transportMemento.transport, transportMemento.distance, transportMemento.duration, transportMemento.cost);
        }
    }

    /**
     * Updates the bicycle duration
     * @param duration Value of duration
     */
    public void updateBicycleDuration(Integer duration) {
        if (durations.containsKey(Transport.BICYCLE)) {
            durations.put(Transport.BICYCLE, duration);
        }
    }


    /**
     * Returns the route data
     * @return String containing the route data
     */
    @Override
    public String toString() {
        return startStopCode + " - " + endStopCode;
    }

    @Override
    public Memento save(String key) {
        Transport transport = Transport.fromString(key);
        return new TransportMemento(transport, distances.get(transport), durations.get(transport), costs.get(transport));
    }

    @Override
    public void restore(Memento memento) {
        TransportMemento transportMemento = (TransportMemento) memento;

        enableTransport(transportMemento.transport, transportMemento.distance, transportMemento.duration, transportMemento.cost);
    }

    /**
     * Class to save a memento with a transport state
     */
    private class TransportMemento implements Memento{
        private Transport transport;
        private Float distance;
        private Integer duration;
        private Float cost;

        /**
         * TransportsMemento constructor
         * @param transport Transport
         * @param distanceToSave Distance
         * @param durationToSave Duration
         * @param costToSave Cost
         */
        public TransportMemento (Transport transport, Float distanceToSave, Integer durationToSave, Float costToSave){
            this.transport = transport;
            this.distance = distanceToSave;
            this.duration = durationToSave;
            this.cost = costToSave;
        }
    }
}
