package pt.pa.controller;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.scene.control.Alert;
import pt.pa.command.CustomTripCommand;
import pt.pa.command.TripCommand;
import pt.pa.memento.Memento;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;
import pt.pa.model.TransportsMap;
import pt.pa.model.TransportsMap.RouteInfo;
import pt.pa.strategy.DistanceRouteStrategy;
import pt.pa.strategy.DurationRouteStrategy;
import pt.pa.strategy.SustainabilityRouteStrategy;
import pt.pa.utils.UIUtil;
import pt.pa.view.MapView;

import java.util.*;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

/**
 * Controller class for Map
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class MapController {
    private final TransportsMap model;
    private MapView view;
    private static final Logger LOGGER = Logger.getLogger(MapController.class.getName());

    private Map<String, Memento> transportMementos = new HashMap<>();
    private final Map<Route, Memento> bicycleDurationMementos = new HashMap<>();


    /**
     * MapController constructor
     * @param model TransportsMap object
     * @param view MapView object
     */
    public MapController(TransportsMap model, MapView view) {
        this.model = model;
        this.view = view;

        view.setController(this);
        view.setTriggers(this);
        this.model.addObserver(this.view);

        setupLogger();
        LOGGER.info("LOGGER initialized");
    }

    /**
     * Returns the total number of routes
     * @return String containing the number of routes
     */
    public String doGetRoutesTotal() {
        LOGGER.info("Total de rotas solicitado: " + model.numEdges());
        return String.valueOf(model.numEdges());
    }

    /**
     * Gets the stop centrality from the model
     * @return HashMap containing the stops centrality data
     */
    public Map<String, Integer> doGetCentrality(){
        LOGGER.info("Centralidade solicitada: " + model.stopsCentrality());
        return model.stopsCentrality();
    }

    /**
     * Gets the details of a clicked Stop
     * @param stopVertex Clicked Stop vertex
     * @return Alert containing stop details
     */
    public Alert doStopDetails(Vertex<Stop> stopVertex){
        Stop stopElement = stopVertex.element();
        LOGGER.info("Informação de Paragem solicitada: Paragem[" + stopElement.getCode()+"]" );
        return UIUtil.createStopInfoPopup(stopElement, model.getAdjacentVertices(stopVertex), model.getStopTransports(stopElement.getCode()));
    }

    /**
     * Gets the details of a clicked route
     * @param routeEdge Clicked Route
     * @return Alert containing route details
     */
    public Alert doRouteDetails(Edge<Route, Stop> routeEdge){
        Route route = routeEdge.element();

        Stop start = model.getStopByCode(route.getStartStopCode());
        Stop end = model.getStopByCode(route.getEndStopCode());
        LOGGER.info("Informação de Rota solicitada: Inicio["+model.getStopByCode(route.getStartStopCode())+"]"+" Fim["+model.getStopByCode(route.getEndStopCode())+"]");
        return view.createRouteInfoPopup(start.getName(), end.getName(), route);
    }

    /**
     * Toggles the state of a route
     * @param route Route to toggle
     */
    public void doToggleRoute(Edge<Route, Stop> route){
        model.toggleRouteStatus(route.element());
        LOGGER.info("Estado da rota alterado: " + route.element());
    }

    /**
     * Toggles the state of transport on a route
     * @param route Route of the transport
     * @param transport Transport to toggle
     */
    public void doToggleTransport(Edge<Route, Stop> route, Transport transport){
        if(!transportMementos.containsKey(transport.toString())){
            transportMementos.put(transport.toString(), route.element().save(transport.toString()));
            route.element().disableTransport(transport);
        } else{
            route.element().restore(transportMementos.remove(transport.toString()));
        }

        LOGGER.info("Estado do transporte da rota alterado: " + route.element());
    }

    /**
     * Returns the total number of routes by transport type
     * @param transport Transport type
     * @return String containing the number of total routes by transport type
     */
    public String doGetRoutesTotalByTransport(Transport transport) {
        LOGGER.info("Total de rotas ["+transport+"] solicitado: "+model.numEdgesWithTransport(transport));
        return String.valueOf(model.numEdgesWithTransport(transport));
    }

    /**
     * Returns the Stop names
     * @return Array containing the stop names
     */
    public String[] doGetStopNames() {
        String[] stopNames = new String[model.numVertices()];

        List<Stop> stops = model.getStops();

        for (int i = 0; i < stops.size(); i++) {
            stopNames[i] = stops.get(i).getName();
        }
        LOGGER.info("Nome Paragens solicitado");
        return stopNames;
    }

    /**
     * Executes the calculation of a custom route
     * @return List containing RouteInfo objects
     */
    public List<RouteInfo> doCalculateCustomRoute(){
        List<TripCommand> tripCommands = new ArrayList<>();

        Stack<Stop> customTripStops = view.getCustomTripStops();

        String criterion = view.getCustomTripCriterion();

        for(int i = 0; i < customTripStops.size() - 1; i++){
            Stop start = customTripStops.get(i);
            Stop end = customTripStops.get(i + 1);

            List<Transport> allTransports = new ArrayList<>(List.of(Transport.values()));

            TripCommand tripCommand = new TripCommand(start, end, criterion, allTransports, this);
            tripCommands.add(tripCommand);
        }

        CustomTripCommand customTripCommand = new CustomTripCommand(tripCommands);

        return customTripCommand.execute();
    }

    /**
     * Executes the calculation of a route between two stops
     * @return List of RouteInfo objects containing the route information
     */
    private List<RouteInfo> calculateRoute(String startStopName, String endStopName, String criterion, List<Transport> transports) {

        if(criterion == null) {
            view.showError("Selecione um critério.");
            LOGGER.severe("Critério não selecionado");
            return null;
        }

        switch (criterion) {
            case "Distância" -> model.setRouteStrategy(new DistanceRouteStrategy());
            case "Duração" -> model.setRouteStrategy(new DurationRouteStrategy());
            case "Sustentabilidade" -> model.setRouteStrategy(new SustainabilityRouteStrategy());
            default -> throw new IllegalArgumentException("Critério inválido: " + criterion);
        }

        Stop start = model.getStopByName(startStopName);
        Stop end = model.getStopByName(endStopName);

        if(start == null) {
            view.showError("Paragem de início não encontrada.");
            LOGGER.severe("Paragem não encontrada: Inicio["+startStopName+"] Fim["+endStopName+"]");
            return null;
        }

        if(end == null) {
            view.showError("Paragem de fim não encontrada.");
            LOGGER.severe("Paragem não encontrada: Inicio["+startStopName+"] Fim["+endStopName+"]");
            return null;
        }

        if(transports.isEmpty()) {
            view.showError("Selecione pelo menos um transporte.");
            LOGGER.severe("Nenhum transporte selecionado");
            return null;
        }

        Map<Stop, RouteInfo> result = model.calculateRoute(start, transports);

        RouteInfo routeInfoTemp = result.get(end);

        if (routeInfoTemp.cameFrom() == null) {
            return null;
        }

        List<RouteInfo> routeInfos = new ArrayList<>();

        while (routeInfoTemp.cameFrom() != null) {
            routeInfos.add(0, routeInfoTemp);
            routeInfoTemp = result.get(routeInfoTemp.cameFrom());
        }

        for (RouteInfo ri : routeInfos) {
            routeInfos.set(routeInfos.indexOf(ri), new RouteInfo(ri.cameFrom(), ri.arrivedAt(), ri.routeTaken(), ri.transportTaken(), ri.costToArrive() - ((routeInfos.indexOf(ri) + 1) * 5), ri.criterion()));
        }

        LOGGER.info("Cálculo de rota : De " + start.getName() + " para " + end.getName() + ", critério utilizado: " + criterion);

        return routeInfos;
    }

    /**
     * Executes the calculation of a route
     * @return List containing RouteInfo objects
     */
    public List<RouteInfo> doCalculateRoute() {
        String startStopName = view.getTripStart();
        String endStopName = view.getTripEnd();
        String criterion = view.getTripCriterion();
        List<Transport> transports = view.getTripTransports();

        return calculateRoute(startStopName, endStopName, criterion, transports);
    }

    /**
     * Executes the calculation of a route from a command
     * @param startStop Start Stop
     * @param endStop End Stop
     * @param criterion Optimization criterion
     * @param transports List of transports
     * @return List containing RouteInfo objects
     */
    public List<RouteInfo> calculateRouteFromCommand(Stop startStop, Stop endStop, String criterion, List<Transport> transports) {
        return calculateRoute(startStop.getName(), endStop.getName(), criterion, transports);
    }

    /**
     * Gets all routes from the model
     * @return List of Route objects
     */
    public List<Route> doGetAllRoutes() {
        List<Route> routes = new ArrayList<>();
        for(Edge<Route, Stop> edge : model.edges()) {
            routes.add(edge.element());
        }
        LOGGER.info("Total de Rotas Solicito : Total = "+routes.size());
        return routes;
    }

    /**
     * Returns the total number of stops
     * @return String containing the number of stops
     */
    public String doGetStopsTotal() {
        LOGGER.info("Total de paragens solicitadas: " + model.getStops().size());
        return String.valueOf(model.getStops().size());
    }

    /**
     * Returns de number of isolated or non isolated stops
     * @param isIsolated True to obtain the isolated stops, False to obtain the not-isolated stops
     * @return String containing the number of isolated stops
     */
    public String doGetStopsTotal(boolean isIsolated) {
        int counter = 0;
        for (Stop stop : model.getStops()) {
            Vertex<Stop> vertex = model.getVertexByStop(stop);
            if (isIsolated == model.incidentEdges(vertex).isEmpty()) {
                counter++;
            }
        }
        LOGGER.info("Total de paragens " + (isIsolated ? "" : "não ") + "isoladas solicitadas: " + counter);
        return String.valueOf(counter);
    }

    /**
     * Returns the list of the top 5 centrality stops
     * @return List containing the top 5 centrality stops
     */
    public List<Map.Entry<String, Integer>> doGetTop5Stops() {
        LOGGER.info("Top 5 paragens por centralidade solicitadas");

        Map<String, Integer> centrality = doGetCentrality();

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(centrality.entrySet());

        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        return entryList.subList(0, Math.min(5, entryList.size()));
    }

    /**
     * Searches for stops that are X routes away from the selected vertex
     * @param stopVertex Selected vertex
     * @param maxDistanceInput Max distance given by the user
     * @return Alert containing the names of the stops found.
     */
    public Alert doSearchNRoutesDistance(Vertex<Stop> stopVertex, String maxDistanceInput){
        try {
            int maxDistance = Integer.parseInt(maxDistanceInput);

            List<String> nRoutesDistance = new ArrayList<>();

            for(Vertex<Stop> vertex : model.searchNRoutesDistance(stopVertex, maxDistance)){
                nRoutesDistance.add(vertex.element().getName());
            }
            LOGGER.info("Busca por Paragens próximas : Paragem Inicial " + stopVertex.element().getName() + ", distância máxima: " + maxDistanceInput);
            return UIUtil.createDistancePopup(nRoutesDistance, maxDistance, stopVertex.element().getName());
        } catch (NumberFormatException e) {
            view.showError("A distância deve ser um número.");
            LOGGER.severe("Erro de conversão: Distância máxima inválida - " + maxDistanceInput);
        }

        return null;
    }

    /**
     * Sets the logger up
     */
    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("logger.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates the bicycle duration for a specific route.
     * @param route The route to update.
     * @param newDuration The new duration for bicycles.
     */
    public void doUpdateBicycleDuration(Route route, Integer newDuration) {
        if (route.getDurations().containsKey(Transport.BICYCLE)) {
            // Save the current state before updating
            bicycleDurationMementos.put(route, route.saveBicycleDuration());

            // Update the duration
            route.updateBicycleDuration(newDuration);
            LOGGER.info("Duração via bicicleta atualizada na rota " +
                    "Inicio[" + model.getStopByCode(route.getStartStopCode()) + "] " +
                    "Fim[" + model.getStopByCode(route.getEndStopCode()) + "] " +
                    "para: " + newDuration + " minutos.");
        } else {
            LOGGER.warning("A rota não suporta transporte por bicicleta.");
        }
    }

    /**
     * Undo the last bicycle duration update for a specific route.
     * @param route The route to undo the bicycle duration update.
     */
    public void doUndoBicycleDuration(Route route) {
        if (bicycleDurationMementos.containsKey(route)) {
            Memento memento = bicycleDurationMementos.get(route);

            // Restore the saved state
            route.restoreBicycleDuration(memento);
            LOGGER.info("Duração via bicicleta restaurada na rota " +
                    "Inicio[" + model.getStopByCode(route.getStartStopCode()) + "] " +
                    "Fim[" + model.getStopByCode(route.getEndStopCode()) + "]");

            bicycleDurationMementos.remove(route); // Clear memento for this route
        } else {
            LOGGER.warning("Nenhuma alteração de duração para bicicleta encontrada para desfazer nesta rota.");
        }
    }

}
