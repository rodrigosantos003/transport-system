package pt.pa.model;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;

import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.strategy.DistanceRouteStrategy;
import pt.pa.strategy.DurationRouteStrategy;
import pt.pa.strategy.SustainabilityRouteStrategy;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static pt.pa.utils.ParsingUtil.parseFloatOrDefault;
import static pt.pa.utils.ParsingUtil.parseIntOrDefault;

/**
 * Battery of tests for TransportsMap
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
class TransportsMapTest {
    private TransportsMap graph;
    private HashMap<Transport, Float> costs;
    private HashMap<Transport, Integer> durations;
    private HashMap<Transport, Float> distances ;

    /**
     * Sets the tests up
     */
    @BeforeEach
    void setUp() {
        this.graph = new TransportsMap();

        this.distances = new HashMap<>();
        distances.put(Transport.TRAIN, parseFloatOrDefault("1.0", 0f));
        distances.put(Transport.BUS, parseFloatOrDefault("1.0", 0f));
        distances.put(Transport.BOAT, parseFloatOrDefault("1.0", 0f));
        distances.put(Transport.WALK, parseFloatOrDefault("1.0", 0f));
        distances.put(Transport.BICYCLE, parseFloatOrDefault("1.0", 0f));

        this.durations = new HashMap<>();
        durations.put(Transport.TRAIN, parseIntOrDefault("1", 0));
        durations.put(Transport.BUS, parseIntOrDefault("1", 0));
        durations.put(Transport.BOAT, parseIntOrDefault("1", 0));
        durations.put(Transport.WALK, parseIntOrDefault("1", 0));
        durations.put(Transport.BICYCLE, parseIntOrDefault("1", 0));

        this.costs = new HashMap<>();
        costs.put(Transport.TRAIN, parseFloatOrDefault("1.0", 0f));
        costs.put(Transport.BUS, parseFloatOrDefault("1.0", 0f));
        costs.put(Transport.BOAT, parseFloatOrDefault("1.0", 0f));
        costs.put(Transport.WALK, parseFloatOrDefault("1.0", 0f));
        costs.put(Transport.BICYCLE, parseFloatOrDefault("1.0", 0f));
    }

    /**
     * Tests the insertion of a vertex
     */
    @Test
     void testInsertStop(){
        Vertex<Stop> stop_test = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        assertNotNull(stop_test, "Stop should not be null");
        assertEquals(1, graph.numVertices(), "Number of Stops should be 1 ");
    }

    /**
     * Tests the adjacency between two vertices
     */
    @Test
     void testIncidentStops(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));

        assertFalse(graph.areAdjacent(s2, s1));

        Route route = new Route("S001" , "S002" , distances,durations,costs);

        graph.insertEdge(s1, s2, route);

        assertTrue(graph.areAdjacent(s1, s2), "Stops should be adjacent");
        assertTrue(graph.areAdjacent(s2, s1), "Stops should be adjacent in the reverse direction");
    }

    /**
     * Tests the insertion of an edge
     */
    @Test
     void testInsertRoute(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop1", 3.0f, -5.5f));

        Route route = new Route("S001" , "S002" , distances,durations,costs);

        Edge<Route, Stop> e = graph.insertEdge(s1, s2, route);
        assertNotNull(e, "Edge should not be null");

        assertTrue(graph.areAdjacent(s1, s2), "Stops should be adjacent");
        assertTrue(graph.areAdjacent(s2, s1), "Stops should be adjacent in reverse direction");
    }

    /**
     * Tests the opposition between two vertices
     */
    @Test
     void testOppositeStops(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 4.0f, -80.5f));

        Route route = new Route("S001" , "S002" , distances,durations,costs);

        Edge<Route, Stop> e = graph.insertEdge(s1, s2, route);

        assertEquals(s2,this.graph.opposite(s1,e));
        assertNotEquals(s3,this.graph.opposite(s1,e));
    }

    /**
     * Tests the removal of an edge
     */
    @Test
     void testRemoveRoute(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Route route = new Route("S001" , "S002" , distances,durations,costs);

        Edge<Route, Stop> e = graph.insertEdge(s1, s2, route);

        assertEquals(1,graph.numEdges());
        assertTrue(this.graph.areAdjacent(s1,s2));

        this.graph.removeEdge(e);

        assertEquals(0,graph.numEdges());
        assertFalse(this.graph.areAdjacent(s1,s2));
    }

    /**
     * Tests the removal of a vertex
     */
    @Test
     void testRemoveStop(){
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 4.0f, -80.5f));

        assertEquals(1,this.graph.numVertices());

        this.graph.removeVertex(s3);

        assertEquals(0,this.graph.numVertices());
    }

    /**
     * Tests the replacement of a vertex
     */
    @Test
    void testReplaceStop() {
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 4.0f, -80.5f));

        Stop oldStop = graph.replace(s1,new Stop("S002", "Stop2", 4.0f, -180.5f));

        assertEquals("S001", oldStop.getCode(), "The old stop code should be 'S001'.");
    }

    /**
     * Tests the replacement of an edge
     */
    @Test
    void testReplaceEdge() {
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));

        Route initialRoute = new Route("S001", "S002", distances, durations, costs);

        Edge<Route, Stop> e = graph.insertEdge(s1, s2, initialRoute);
        assertNotNull(e, "Edge should not be null after insertion");

        assertEquals(initialRoute, e.element(), "Initial route should be set correctly");

        Route newRoute = new Route("S002", "S001", distances, durations, costs);

        Route oldRoute = graph.replace(e, newRoute);

        assertEquals(initialRoute, oldRoute, "The old route data should match the initial route");
        assertEquals(newRoute, e.element(), "Edge should now hold the new route data");

        assertTrue(graph.areAdjacent(s1, s2), "Stops should remain adjacent after edge replacement");
    }

    /**
     * Tests the method to get a stop by its code
     */
    @Test
     void testGetStopByCode() {
        Stop stop = new Stop("S001", "Stop1", 4.0f, -80.5f);
        graph.insertVertex(stop);
        assertEquals(stop, graph.getStopByCode("S001"), "The Stop should match inserted stop");
        assertNull(graph.getStopByCode("ABC"), "Should return null for non-existent code");
    }

    /**
     * Tests the error when inserting a duplicate vertex
     */
    @Test
     void testDuplicateStop() {
        Stop newStop = new Stop("SP001", "Stop1", 2.0f, -2.5f);
        graph.insertVertex(newStop);
        assertThrows(InvalidVertexException.class, () ->
                        graph.insertVertex(newStop),
                "Inserting a duplicated Stop should throw an InvalidVertexException");
    }

    /**
     * Tests de error when inserting a duplicate edge
     */
    @Test
     void testDuplicateRoute() {
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);
        assertThrows(InvalidEdgeException.class, () -> graph.insertEdge(s1, s2, route),
                "Inserting a duplicate Route should throw an InvalidEdgeException");
    }

    @Test
     void testStopsCentrality(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        Route route1 = new Route("S001", "S003", distances, durations, costs);
        graph.insertEdge(s1, s2, route);
        graph.insertEdge(s1, s3, route1);

        Map<String, Integer> centralityMap = graph.stopsCentrality();

        assertNotNull(centralityMap, "The centrality map should not be null.");
        assertFalse(centralityMap.isEmpty(), "The centrality map should not be empty.");

        assertEquals(2, centralityMap.get("Stop1"));
        assertEquals(1, centralityMap.get("Stop2"));
    }

    @Test
     void testGetAdjacentVertices(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        List<Vertex<Stop>> adjacentVertices = graph.getAdjacentVertices(s1);

        assertEquals(adjacentVertices.get(0), s2);
    }

    @Test
     void testNumEdgesWithTransport() {
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        assertEquals(1, graph.numEdgesWithTransport(Transport.BUS));
        assertEquals(1, graph.numEdgesWithTransport(Transport.TRAIN));
        assertEquals(1, graph.numEdgesWithTransport(Transport.BOAT));
        assertEquals(1, graph.numEdgesWithTransport(Transport.WALK));
        assertEquals(1, graph.numEdgesWithTransport(Transport.BICYCLE));
    }

    @Test
     void testGetVertexByStop(){
        Stop stop = new Stop("S001", "Stop1", 2.0f, -2.5f);

        Vertex<Stop> s1 = graph.insertVertex(stop);

        assertEquals(graph.getVertexByStop(stop), s1);
    }

    @Test
     void testSearchNRoutesDistance() {
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 4.0f, -6.5f));
        Vertex<Stop> s4 = graph.insertVertex(new Stop("S004", "Stop4", 5.0f, -7.5f));

        Route route12 = new Route("S001", "S002", distances, durations, costs);
        Route route23 = new Route("S002", "S003", distances, durations, costs);
        Route route34 = new Route("S003", "S004", distances, durations, costs);

        graph.insertEdge(s1, s2, route12);
        graph.insertEdge(s2, s3, route23);
        graph.insertEdge(s3, s4, route34);

        List<Vertex<Stop>> result = graph.searchNRoutesDistance(s1, 2);

        assertEquals(2, result.size(), "Should return 2 stops within 2 steps");

        assertTrue(result.contains(s2), "Result should contain Stop2");
        assertTrue(result.contains(s3), "Result should contain Stop3");
        assertFalse(result.contains(s4), "Result should not contain Stop4");

        result = graph.searchNRoutesDistance(s1, 3);

        assertEquals(3, result.size(), "Should return 3 stops within 3 steps");

        assertTrue(result.contains(s2), "Result should contain Stop2");
        assertTrue(result.contains(s3), "Result should contain Stop3");
        assertTrue(result.contains(s4), "Result should contain Stop4");
    }
    
    @Test
    void testCalculateRouteWithDistanceStrategy(){
        graph.setRouteStrategy(new DistanceRouteStrategy());

        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        List<Transport> transports = new ArrayList<>(Arrays.stream(Transport.values()).toList());

        int count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s1.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertNotEquals(0, count);

        count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s3.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertEquals(0, count);
    }

    @Test
    void testCalculateRouteWithDurationStrategy(){
        graph.setRouteStrategy(new DurationRouteStrategy());

        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        List<Transport> transports = new ArrayList<>(Arrays.stream(Transport.values()).toList());

        int count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s1.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertNotEquals(0, count);

        count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s3.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertEquals(0, count);
    }

    @Test
    void testCalculateRouteWithSustainabilityStrategy(){
        graph.setRouteStrategy(new SustainabilityRouteStrategy());

        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        List<Transport> transports = new ArrayList<>(Arrays.stream(Transport.values()).toList());

        int count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s1.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertNotEquals(0, count);

        count = 0;
        for(TransportsMap.RouteInfo info : graph.calculateRoute(s3.element(), transports).values()){
            if(info.cameFrom() != null) count++;
        }

        assertEquals(0, count);
    }

    @Test
    void testToggleRouteStatus(){
        Vertex<Stop> s1 = graph.insertVertex(new Stop("S001", "Stop1", 2.0f, -2.5f));
        Vertex<Stop> s2 = graph.insertVertex(new Stop("S002", "Stop2", 3.0f, -5.5f));
        Vertex<Stop> s3 = graph.insertVertex(new Stop("S003", "Stop3", 3.0f, -5.5f));
        Route route = new Route("S001", "S002", distances, durations, costs);
        graph.insertEdge(s1, s2, route);

        graph.toggleRouteStatus(route);
        assertFalse(route.isActive());
        graph.toggleRouteStatus(route);
        assertTrue(route.isActive());
    }
}