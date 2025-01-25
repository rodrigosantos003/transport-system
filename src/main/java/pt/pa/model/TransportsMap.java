package pt.pa.model;

import com.brunomnsilva.smartgraph.graph.*;
import pt.pa.observer.Observable;
import pt.pa.observer.Observer;
import pt.pa.strategy.DistanceRouteStrategy;
import pt.pa.strategy.RouteStrategy;
import pt.pa.utils.HashMapUtil;

import java.util.*;

import static pt.pa.utils.DatasetUtils.*;

/**
 * Model for the transports map
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class TransportsMap extends GraphEdgeList<Stop, Route> implements Observable {
    private List<Stop> stops;
    private List<Route> routes;
    private List<Observer> observers;
    private RouteStrategy routeStrategy;

    /**
     * TransportsMap constructor
     */
    public TransportsMap() {
        super();
        stops = loadStopsFromCSV();
        routes = loadRoutesFromCSV();
        loadCoordinatesFromCSV(stops);
        this.observers = new ArrayList<>();
        this.routeStrategy = new DistanceRouteStrategy();
    }

    /**
     * Returns the list of Stops
     * @return List containing the Stops
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * Returns a Stop given its name
     * @param name Stop name
     * @return Stop object with the given name if it's found. Null otherwise
     */
    public Stop getStopByName(String name){
        for(Vertex<Stop> v : vertices()){
            if(v.element().getName().equals(name)){
                return v.element();
            }
        }
        return null;
    }

    /**
     * Returns the list of Routes
     * @return List containing the Routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * Returns a Stop given its code
     * @param code Stop code
     * @return Stop object with the given code if it's found. Null otherwise
     */
    public Stop getStopByCode(String code){
        for(Vertex<Stop> v : vertices()){
            if(v.element().getCode().equals(code)){
                return v.element();
            }
        }
        return null;
    }

    /**
     * Sets the route strategy
     * @param strategy RouteStrategy object
     */
    public void setRouteStrategy(RouteStrategy strategy){
        this.routeStrategy = strategy;
    }

    /**
     * Returns the list of adjacent vertices
     * @param stopVertex Vertex to get adjacency
     * @return List containing the adjacent vertices
     */
    public List<Vertex<Stop>> getAdjacentVertices(Vertex<Stop> stopVertex){
        List<Vertex<Stop>> adjacentVertices = new ArrayList<>();

        for(Edge<Route, Stop> edge : this.incidentEdges(stopVertex)){
            adjacentVertices.add(this.opposite(stopVertex, edge));
        }

        return adjacentVertices;
    }

    /**
     * Calculates the centrality of the Stops (i.e. number of incident edges)
     * @return Hashmap containing the centrality of all stop
     */
    public Map<String, Integer> stopsCentrality(){
        Map<String, Integer> centralityMap = new HashMap<>();

        for(Vertex<Stop> stop : this.vertices()){
            int numAdjacentStops = this.incidentEdges(stop).size();

            centralityMap.put(stop.element().getName(), numAdjacentStops);
        }

        centralityMap = HashMapUtil.sortByValueDescending(centralityMap);

        return centralityMap;
    }

    /**
     * Returns the number of edges with a transport type
     * @param transport Transport type
     * @return Number of edges with the given transport type
     */
    public int numEdgesWithTransport(Transport transport) {
        int count = 0;
        for(Edge<Route, Stop> edge : edges()) {
            if(edge.element().getDistances().get(transport) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the Vertex associated with a given Stop.
     * @param stop The Stop object.
     * @return The Vertex containing the Stop if found. Null otherwise.
     */
    public Vertex<Stop> getVertexByStop(Stop stop) {
        for (Vertex<Stop> v : vertices()) {
            if (v.element().equals(stop)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Searches for stops that are X routes away from the root vertex, using a breadth-first traversal
     * @param rootVertex Root vertex
     * @param maxDistance Max distance to search
     * @return List containing the vertices found
     */
    public List<Vertex<Stop>> searchNRoutesDistance(Vertex<Stop> rootVertex, int maxDistance){
        List<Vertex<Stop>> visited = new ArrayList<>();
        Queue<Vertex<Stop>> vertexQueue = new LinkedList<>();
        Map<Vertex<Stop>, Integer> vertexDistances = new HashMap<>();

        List<Vertex<Stop>> result = new ArrayList<>();

        visited.add(rootVertex);
        vertexDistances.put(rootVertex, 0);
        vertexQueue.offer(rootVertex);

        while (!vertexQueue.isEmpty()){
            Vertex<Stop> v = vertexQueue.remove();

            int currentDistance = vertexDistances.get(v);
            if(currentDistance > 0 && currentDistance <= maxDistance){
                result.add(v);
            }

            for(Vertex<Stop> w : getAdjacentVertices(v)){
                if(!visited.contains(w)){
                    visited.add(w);
                    vertexQueue.offer(w);
                    vertexDistances.put(w, currentDistance +1);
                }
            }
        }

        return result;
    }

    /**
     * Calculates a route between two stops based on the Strategy
     * @param start Start stop
     * @param transports List of transports
     * @return Map containing the route data
     */
    public Map<Stop, RouteInfo> calculateRoute(Stop start, List<Transport> transports) {
        return routeStrategy.calculateRoute(this, start, transports);
    }

    /**
     * Calculates the shortest path between two Stops using the Bellman-Ford algorithm
     * @param start Start Stop
     * @param transports List of Transports to be considered
     * @param criterion Optimization criterion
     * @return Map containing the shortest path between the Stops
     */
    public Map<Stop, RouteInfo> BellmanFord(Stop start, List<Transport> transports, String criterion) {
        Map<Stop, RouteInfo> results = new HashMap<>();

        for (Vertex<Stop> vertex : vertices()) {
            results.put(vertex.element(), new RouteInfo(null, null, null, null, Float.POSITIVE_INFINITY, criterion));
        }

        results.put(start, new RouteInfo(null, start, null, null, 0, criterion));

        for (int i = 0; i < vertices().size() - 1; i++) {
            for (Edge<Route, Stop> edge : edges()) {
                if(edge.element().isActive()){
                    relaxEdge(results, edge, transports);
                }
            }
        }

        for (Edge<Route, Stop> edge : edges()) {
            if (!edge.element().isActive()) {
                continue;
            }
            if (relaxEdge(results, edge, transports)) {
                throw new IllegalArgumentException("O grafo contém um ciclo negativo");
            }
        }

        return results;
    }

    /**
     * Relaxes an Edge
     * @param results Map containing the results
     * @param edge Edge to be relaxed
     * @param transports List of Transports to be considered
     * @return True if the Edge was relaxed. False otherwise
     */
    private boolean relaxEdge(Map<Stop, RouteInfo> results, Edge<Route, Stop> edge, List<Transport> transports) {
        Stop u = edge.vertices()[0].element();
        Stop v = edge.vertices()[1].element();
        boolean relaxed = false;


        for (Transport transport : transports) {
            Float value = routeStrategy.getValueByCriterion(edge.element(), transport);

            if (value != null) {
                // Adiciona-se 10000 ao valor para evitar ciclos negativos
                value = value + 5;

                if (results.get(v).costToArrive > results.get(u).costToArrive + value) {
                    results.put(v, new RouteInfo(u, v, edge.element(), transport, results.get(u).costToArrive + value, results.get(u).criterion));
                    relaxed = true;
                }
                if (results.get(u).costToArrive > results.get(v).costToArrive + value) {
                    results.put(u, new RouteInfo(v, u, edge.element(), transport, results.get(v).costToArrive + value, results.get(v).criterion));
                    relaxed = true;
                }
            }
        }
        return relaxed;
    }

    /**
     * Toggles the status of a Route
     * @param route Route object
     */
    public void toggleRouteStatus(Route route){
        route.toggleActive();
        notifyObservers(null);
    }

    /**
     * Gets the transports of a stop
     * @param stopCode Stop code
     * @return Set containing the transports of the given stop
     */
    public Set<Transport> getStopTransports(String stopCode){
        Set<Transport> transports = new HashSet<>();
        for(Route route : routes){
            if(route.getStartStopCode().equals(stopCode) || route.getEndStopCode().equals(stopCode)){
                HashMap<Transport, Float> distances = route.getDistances();

                for(Transport transport : distances.keySet()){
                    if(distances.get(transport) != null) transports.add(transport);
                }
            }
        }

        return transports;
    }

    @Override
    public void addObserver(Observer observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        for(Observer o : observers) {
            o.update(this, arg);
        }
    }

    /**
     * RouteInfo class
     */
    public record RouteInfo(Stop cameFrom, Stop arrivedAt, Route routeTaken, Transport transportTaken, float costToArrive, String criterion) {}
}