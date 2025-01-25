package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.pa.controller.MapController;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;
import pt.pa.model.TransportsMap;
import pt.pa.model.TransportsMap.RouteInfo;
import pt.pa.observer.Observable;
import pt.pa.utils.PropertiesUtil;
import pt.pa.utils.UIUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Map view
 *
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class MapView extends BorderPane implements MapViewUI {
    // Attributes
    private SmartGraphPanel<Stop, Route> graphView;
    private TransportsMap graph;
    private MapController controller;

    // Components
    private VBox sideMenu;
    private Button btnCentrality;
    private Button btnCalculateRoute;
    private Button btnCalculateCustomTrip;
    private Label lblTitle;
    private ScrollPane scrollPane;

    private boolean isCustomTrip = false;
    private final Stack<Stop> customTripStops = new Stack<>();
    private VBox customTripStopsList;

    //Trip
    private ComboBox<String> cbTripStart;
    private ComboBox<String> cbTripEnd;
    private VBox vbTripTransports;
    private VBox vbTripCriteria;

    //Custom trip
    private VBox vbCustomTripCriteria;

    /**
     * MapView constructor
     *
     * @param graph Graph to visualize
     */
    public MapView(TransportsMap graph) {
        try {
            initializeGraph(graph);
            doLayout();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Sets the MapController
     * @param controller MapController object
     */
    public void setController(MapController controller) {
        this.controller = controller;
    }

    // region Initialization
    /**
     * Initializes the graph properties and view
     * @param graph TransportsMap Object (i.e. the graph)
     * @throws Exception If there's an error initializing the graph view.
     */
    private void initializeGraph(TransportsMap graph) throws Exception{
        InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
        URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");
        URL uiCSS = MapView.class.getClassLoader().getResource("styles/ui.css");

        if (css != null && uiCSS != null) {
            this.graph = graph;
            this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());
            graphView.setMaxHeight(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.height")));
            graphView.setMaxWidth(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.width")));

            this.getStylesheets().add(uiCSS.toExternalForm());
        }
    }

    /**
     * Initializes the graph view
     */
    public void initGraphView(){
        graphView.init();

        populateGraph();

        update(graph, null);

        graphView.getModel().vertices().forEach((Vertex<Stop> vertex)-> {
            Stop stop = vertex.element();
            graphView.setVertexPosition(vertex, stop.getPosX(), stop.getPosY());
        });

        update(graph, null);
    }

    /**
     * Populates the graph with vertices and edges
     */
    private void populateGraph(){
        graph.getStops().forEach((Stop stop)->{
            graph.insertVertex(stop);
        });

        graph.getRoutes().forEach((Route route)->{
            Stop start = graph.getStopByCode(route.getStartStopCode());
            Stop end = graph.getStopByCode(route.getEndStopCode());

            graph.insertEdge(start, end, route);
        });
    }

    /**
     * Builds the page layout
     */
    private void doLayout() {
        initComponents();
        renderSideMenu();
        setCenter(this.graphView);
    }

    /**
     * Initializes the "global" components
     */
    private void initComponents() {
        sideMenu = new VBox();
        sideMenu.setAlignment(Pos.BASELINE_CENTER);

        btnCentrality = UIUtil.createStyledButton("Ver mais", 2);
        btnCalculateRoute = UIUtil.createStyledButton("Calcular", 1);
        btnCalculateCustomTrip = UIUtil.createStyledButton("Calcular", 1);

        sideMenu.getStyleClass().add("menu");

        lblTitle = UIUtil.createStyledLabel("Título", 1);

        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scroll-pane");
    }
    // endregion

    // region UI rendering
    /**
     * Resets the side menu
     * @param title Title for the next page
     */
    private void resetSideMenu(String title){
        sideMenu.getChildren().clear();
        sideMenu.getChildren().add(UIUtil.logoImage());
        lblTitle.setText(title);
    }

    /**
     * Creates a back button
     * @return Button to go back to home page
     */
    private Button createBackButton(){
        Button btnBack = UIUtil.createStyledButton("Voltar", 2);
        btnBack.setOnAction(event -> {
            renderSideMenu();
        });
        return btnBack;
    }

    /**
     * Renders the side menu
     */
    private void renderSideMenu() {
        resetSideMenu("");

        Button btnViewMetrics = UIUtil.createStyledButton("Visualizar Métricas", 1);
        Button btnStartTrip = UIUtil.createStyledButton("Começar Viagem", 1);
        Button btnCustomTrip = UIUtil.createStyledButton("Viagem Personalizada", 1);

        btnViewMetrics.setOnAction((ActionEvent event) -> {
            renderMetrics();
        });

        btnStartTrip.setOnAction((ActionEvent event) -> {
            renderStartTrip();
        });

        btnCustomTrip.setOnAction((ActionEvent event) -> {
            renderCustomTrip();
        });

        sideMenu.getChildren().addAll(btnViewMetrics, btnStartTrip, btnCustomTrip);
        setLeft(sideMenu);
    }

    /**
     * Renders the metrics view
     */
    private void renderMetrics() {
        resetSideMenu("Informações");

        // Paragens
        Label lblParagens = UIUtil.createStyledLabel("Paragens", 3);
        //Total
        Label lblParagensTotal = UIUtil.createStyledLabel("Total", 4);
        Label lblRParagensTotalValue = UIUtil.createStyledLabel(controller.doGetStopsTotal(), 6);
        //Isoladas
        Label lblParagensIsoladasTotal = UIUtil.createStyledLabel("Isoladas", 4);
        Label lblRParagensIsoladasTotalValue = UIUtil.createStyledLabel(controller.doGetStopsTotal(true), 6);
        //Não isoladas
        Label lblParagensNaoIsoladasTotal = UIUtil.createStyledLabel("Não-Isoladas", 4);
        Label lblRParagensNaoIsoladasTotalValue = UIUtil.createStyledLabel(controller.doGetStopsTotal(false), 6);

        HBox rowParagens = new HBox();

        rowParagens.setSpacing(10);

        rowParagens.getChildren().addAll(lblParagens, lblParagensTotal, lblRParagensTotalValue,lblParagensIsoladasTotal,lblRParagensIsoladasTotalValue,lblParagensNaoIsoladasTotal,lblRParagensNaoIsoladasTotalValue);

        // Rotas
        Label lblRotas = UIUtil.createStyledLabel("Rotas", 3);
        Label lblRotasTotal = UIUtil.createStyledLabel("Total", 4);

        Label lblRotasTotalValue = UIUtil.createStyledLabel(controller.doGetRoutesTotal(), 6);

        Label lblRotasPossiveis = UIUtil.createStyledLabel("Possíveis Rotas de: ", 4);

        ComboBox<String> cbTransportType = new ComboBox<>();
        for(Transport transport : Transport.values()){
            cbTransportType.getItems().add(transport.toString());
        }

        cbTransportType.setValue(Transport.values()[0].toString());

        Label lblRotasPossiveisValue = UIUtil.createStyledLabel(controller.doGetRoutesTotalByTransport(Transport.values()[0]), 6);

        cbTransportType.setOnAction((ActionEvent event) -> {
            lblRotasPossiveisValue.setText(controller.doGetRoutesTotalByTransport(Transport.fromString(cbTransportType.getValue())));
        });

        HBox rowRotas = new HBox();

        rowRotas.setSpacing(10);

        rowRotas.getChildren().addAll(lblRotasTotal, lblRotasTotalValue, lblRotasPossiveis, cbTransportType, lblRotasPossiveisValue);

        // Centralidade
        Label lblCentralidade = UIUtil.createStyledLabel("Centralidade", 3);

        Button btnBack = UIUtil.createStyledButton("Voltar", 2);
        btnBack.setOnAction(event -> {
            renderSideMenu();
        });
        Region spacer = new Region();
        spacer.setPrefHeight(50);


        //Top 5 centralidade
        Label lblTop5Centralidade = UIUtil.createStyledLabel("Top 5 paragens mais centrais", 2);

        Button btnViewCentralityChart = UIUtil.createStyledButton("Ver mais", 2);
        btnViewCentralityChart.setOnAction(event -> openCentralityChartPopup());

        sideMenu.getChildren().addAll(  lblTitle, //Title
                lblParagens,
                rowParagens,
                lblRotas, //Rotas
                rowRotas,
                lblCentralidade, btnCentrality, //Centrality
                lblTop5Centralidade,btnViewCentralityChart,//Top 5 centrality
                spacer,
                btnBack //Voltar
        );
    }

    /**
     * Renders the start trip view
     */
    private void renderStartTrip() {
        resetSideMenu("Iniciar Viagem");

        cbTripStart = new ComboBox<>();
        cbTripStart.getItems().addAll(controller.doGetStopNames());
        HBox hbOrigem = UIUtil.createField("Origem", cbTripStart);

        cbTripEnd = new ComboBox<>();
        cbTripEnd.getItems().addAll(controller.doGetStopNames());
        HBox hbDestino = UIUtil.createField("Destino", cbTripEnd);

        vbTripTransports = UIUtil.createTransportGroup();

        vbTripCriteria = UIUtil.createCriteriaGroup();

        Button btnBack = createBackButton();

        sideMenu.getChildren().addAll(lblTitle, hbOrigem, hbDestino, vbTripTransports, vbTripCriteria, btnCalculateRoute, btnBack);

        // Event handler for the calculate route button
        btnCalculateRoute.setOnAction((ActionEvent event) -> {
                    resetEdgeStyles();

                    List<RouteInfo> routeInfos = controller.doCalculateRoute();

                    if (routeInfos != null) renderTripResults(routeInfos);
                }
        );
    }

    /**
     *
     * Renders the custom trip view
     */
    private void renderCustomTrip() {
        resetSideMenu("Viagem Personalizada");
        isCustomTrip = true;

        vbCustomTripCriteria = UIUtil.createCriteriaGroup();

        Label lblParagens = UIUtil.createStyledLabel("Paragens", 3);
        ScrollPane scrollPane = createStopsScrollPane();

        Button btnBack = createBackButton();

        sideMenu.getChildren().addAll(lblTitle, vbCustomTripCriteria, lblParagens, scrollPane, btnCalculateCustomTrip, btnBack);

        setupCalculateCustomTripButton();
    }

    private void renderTripResults(List<RouteInfo> routeInfos){
        try{
            if (routeInfos != null) {
                for (RouteInfo routeInfo : routeInfos) {
                    graphView.getStylableEdge(routeInfo.routeTaken()).setStyleClass(routeInfo.transportTaken().getStyleClass());
                }
            }

            resetSideMenu("Resumo da Viagem");

            Label lblResumo = UIUtil.createStyledLabel(routeInfos.get(0).cameFrom() + " -> " + routeInfos.get(routeInfos.size() - 1).arrivedAt(), 3);

            float custoTotal = Float.parseFloat(String.format("%.2f", routeInfos.get(routeInfos.size()-1).costToArrive()).
                    replace(",", "."));

            Label custo = new Label();
            switch (routeInfos.get(0).criterion()) {
                case "Distância":
                    custo.setText("Distância: " + custoTotal + " km");
                    break;
                case "Sustentabilidade":
                    custo.setText("Sustentabilidade: " + custoTotal);
                    break;
                case "Duração":
                    custo.setText("Duração: " + custoTotal + " minutos");
                    break;
            }

            custo.getStyleClass().add("small-label");

            Label lblMeiosUtilizados = UIUtil.createStyledLabel("Meios de Transporte Utilizados", 3);

            Set<Transport> transports = new HashSet<>();

            for (RouteInfo routeInfo : routeInfos) {
                transports.add(routeInfo.transportTaken());
            }

            HBox meiosUtilizados = new HBox();

            meiosUtilizados.setAlignment(Pos.CENTER);

            meiosUtilizados.setSpacing(10);

            for (Transport transport : transports) {
                Label lblTransport = UIUtil.createStyledLabel(transport.toEmoji(), 5);
                meiosUtilizados.getChildren().add(lblTransport);
            }

            VBox routeStops = new VBox();

            routeStops.getStyleClass().add("stop-list");

            ScrollPane scrollPane = UIUtil.createScrollPane(routeStops);
            scrollPane.setPrefHeight(280);

            for (RouteInfo routeInfo : routeInfos) {
                Label lblStop = UIUtil.createStyledLabel((routeInfos.indexOf(routeInfo) + 1) + ". " + routeInfo.cameFrom().getName(), 7);
                routeStops.getChildren().add(lblStop);
            }

            RouteInfo destino = routeInfos.get(routeInfos.size() - 1);
            Label lblStop = UIUtil.createStyledLabel((routeInfos.size() + 1) + ". " + destino.arrivedAt().getName(), 7);

            routeStops.getChildren().add(lblStop);

            Label lblRota = UIUtil.createStyledLabel("Rota", 3);


            Button btnBack = UIUtil.createStyledButton("Voltar", 2);
            btnBack.setOnAction(event -> {
                resetEdgeStyles();
                customTripStops.clear();
                renderSideMenu();
            });

            sideMenu.getChildren().addAll(lblTitle, lblResumo, custo, lblMeiosUtilizados, meiosUtilizados, lblRota, scrollPane, btnBack);

        } catch (RuntimeException e){
            showError("Erro ao calcular a rota!");
        }
    }

    /**
     * Resets the edge styles
     */
    private void resetEdgeStyles() {
        for(Route route : controller.doGetAllRoutes()){
            if(route.isActive()){
                graphView.getStylableEdge(route).setStyleClass("edge");
            }
        }
    }

    /**
     * Creates the route details popup
     * @param startStopName Start stop name
     * @param endStopName End stop name
     * @param route Route object
     * @return Alert with the route details
     */
    public Alert createRouteInfoPopup(String startStopName, String endStopName, Route route) {
        // Alert object
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Informação da Rota");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().getStylesheets().add("/styles/ui.css");

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        // Title section
        VBox titleBox = new VBox(UIUtil.logoImage());
        titleBox.setAlignment(Pos.CENTER);

        // Route Section
        Label routeLabel = UIUtil.createStyledLabel(startStopName + " → " + endStopName, 3);
        Label routeCodes = UIUtil.createStyledLabel(route.getStartStopCode() + " → " + route.getEndStopCode(), 4);

        VBox routeBox = new VBox(5, routeLabel, routeCodes);
        routeBox.setAlignment(Pos.CENTER);

        // Transport Type Table Section
        VBox tableContainer = new VBox();
        tableContainer.getChildren().add(UIUtil.buildTransportTable(route)); // Add the initial table

        Label bicycleDurationLabel = UIUtil.createStyledLabel(
                route.getDurations().get(Transport.BICYCLE) != null
                        ? route.getDurations().get(Transport.BICYCLE).toString()
                        : "N/A",
                4
        );

        // Editable Bicycle Duration Section
        VBox editableBox = new VBox(10);
        editableBox.getStyleClass().add("editable-box");

        Label editableInfo = UIUtil.createStyledLabel(
                "Pode alterar livremente o tempo que demora a efetuar esta rota no campo abaixo!",
                4
        );

        HBox bikeDurationBox = new HBox(10);
        bikeDurationBox.getStyleClass().add("bike-duration-box");
        Label bikeLabel = UIUtil.createStyledLabel("Duração via bicicleta: ", 4);
        TextField bikeDurationField = new TextField();
        bikeDurationField.setPromptText("Digite a duração...");
        bikeDurationField.getStyleClass().add("bike-duration-field");

        //Action Buttons
        Button updateButton = new Button("Alterar Duração");
        updateButton.getStyleClass().add("action-button");
        updateButton.setOnAction(event -> {
            try {
                Integer newDuration = Integer.parseInt(bikeDurationField.getText());
                controller.doUpdateBicycleDuration(route, newDuration);

                // Update the bicycle duration label with the new value from the user
                bicycleDurationLabel.setText(newDuration.toString());

                // Refresh the table
                VBox updatedTableContainer = new VBox(); // Create a new VBox to hold the updated table
                updatedTableContainer.getChildren().add(UIUtil.buildTransportTable(route));

                // Replace the old table with the updated one while keeping its position
                tableContainer.getChildren().set(0, updatedTableContainer.getChildren().get(0));
            } catch (NumberFormatException e) {
                showError("Insira um valor numérico válido para a duração.");
            }
        });

        Button undoButton = new Button("Desfazer");
        undoButton.getStyleClass().add("action-button");
        undoButton.setOnAction(event -> {
            controller.doUndoBicycleDuration(route);

            Integer originalDuration = route.getDurations().get(Transport.BICYCLE);
            bicycleDurationLabel.setText(originalDuration != null ? originalDuration.toString() : "N/A");

            // Refresh the table
            VBox updatedTableContainer = new VBox();
            updatedTableContainer.getChildren().add(UIUtil.buildTransportTable(route));


            tableContainer.getChildren().set(0, updatedTableContainer.getChildren().get(0));
        });

        //Add buttons to the layout
        HBox buttonBox = new HBox(10, updateButton, undoButton);
        buttonBox.setAlignment(Pos.CENTER);

        bikeDurationBox.getChildren().addAll(bikeLabel, bikeDurationField);
        editableBox.getChildren().addAll(editableInfo, bikeDurationBox, buttonBox);

        // Add buttons for the dialog footer
        ButtonType backButton = new ButtonType("Voltar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType deactivateButton = new ButtonType("Alterar Estado", ButtonBar.ButtonData.APPLY);
        alert.getButtonTypes().addAll(backButton, deactivateButton);

        // Set the content
        content.getChildren().addAll(titleBox, routeBox, tableContainer, editableBox);
        alert.getDialogPane().setContent(content);

        return alert;
    }
    // endregion

    // region Centrality chart
    private VBox createTopStopsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Paragens");
        yAxis.setLabel("Nº de rotas incidentes");
        //Ajustar a medida para ser um inteiro
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 5 Paragens com mais rotas");
        barChart.getStylesheets().add("styles/barChart.css");

        List<Map.Entry<String, Integer>> topStops = controller.doGetTop5Stops();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Paragens");

        for (Map.Entry<String, Integer> entry : topStops) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);


        return new VBox(barChart);
    }

    private void openCentralityChartPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Top 5 Paragens - Centralidade");

        VBox chartContainer = this.createTopStopsChart();

        Scene scene = new Scene(chartContainer, 600, 400);
        popupStage.setScene(scene);
        popupStage.show();
    }
    // endregion

    //region Trip
    /**
     * Gets the start of a trip
     * @return Name of the start stop
     */
    public String getTripStart(){
        return cbTripStart.getValue();
    }

    /**
     * Gets the end of a trip
     * @return Name of the end stop
     */
    public String getTripEnd(){
        return cbTripEnd.getValue();
    }

    /**
     * Gets the trip transports
     * @return List of transports
     */
    public List<Transport> getTripTransports(){
        return UIUtil.getSelectedTransports(vbTripTransports);
    }

    /**
     * Gets the trip criterion
     * @return Criterion name
     */
    public String getTripCriterion(){
        RadioButton selectedCriterion = UIUtil.getSelectedCriterion(vbTripCriteria);
        return selectedCriterion == null ? null : selectedCriterion.getText();
    }

    //endregion

    // region Custom trip

    /**
     * Gets the custom trip stops
     * @return Stack of Stop elements containing the custom trip stops
     */
    public Stack<Stop> getCustomTripStops() {
        return customTripStops;
    }

    /**
     * Gets the selected custom trip criterion
     * @return String containing the selected custom trip criterion
     */
    public String getCustomTripCriterion(){
        RadioButton selectedCriterion = UIUtil.getSelectedCriterion(vbCustomTripCriteria);
        return selectedCriterion == null ? null : selectedCriterion.getText();
    }

    /**
     * Creates the stops scroll pane
     * @return Scroll pane containing the stops list
     */
    private ScrollPane createStopsScrollPane() {
        customTripStopsList = new VBox();
        customTripStopsList.getStyleClass().add("stop-list");
        updateCustomTripStops();
        return UIUtil.createScrollPane(customTripStopsList);
    }

    /**
     * Sets the action for the custom trip button
     */
    private void setupCalculateCustomTripButton() {
        btnCalculateCustomTrip.setOnAction((ActionEvent event) -> {
            resetEdgeStyles();

            List<RouteInfo> routeInfos = controller.doCalculateCustomRoute();

            if (routeInfos != null) renderTripResults(routeInfos);
        });
    }

    /**
     * Adds the clicked vertex to the custom trip stops
     * @param vertex Clicked vertex
     */
    private void addClickedVertex(SmartGraphVertex<Stop> vertex){
        Stop stop = vertex.getUnderlyingVertex().element();

        if(customTripStops.isEmpty()){
            customTripStops.push(stop);
        }
        else{
            Stop lastStop = customTripStops.peek();
            if(!lastStop.equals(stop)){
                customTripStops.push(stop);
            }
            else{
                customTripStops.pop();
            }
        }

        updateCustomTripStops();
    }

    /**
     * Updates the stops of custom trip
     */
    private void updateCustomTripStops(){
        customTripStopsList.getChildren().clear();

        if(customTripStops.isEmpty()){
            Label lblSemParagens = UIUtil.createStyledLabel("Selecione uma paragem do mapa para iniciar!", 7);
            customTripStopsList.getChildren().add(lblSemParagens);
        }
        else{
            int i = 1;
            for(Stop stop : customTripStops){
                Label lblStop = UIUtil.createStyledLabel(i++ + ". " + stop.getName(), 7);
                customTripStopsList.getChildren().add(lblStop);
            }
        }
    }
    // endregion

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro:");
        alert.setContentText(message);

        alert.showAndWait();
    }

    // region Design patterns methods
    @Override
    public void setTriggers(MapController controller) {
        // Stop details
        graphView.setVertexDoubleClickAction((SmartGraphVertex<Stop> vertex)->{
            if(isCustomTrip){
               addClickedVertex(vertex);
            }
            else {
                Vertex<Stop> stopVertex = vertex.getUnderlyingVertex();

                Alert details = controller.doStopDetails(stopVertex);
                details.showAndWait().ifPresent(buttonType -> {
                    // Calculate N routes
                    if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
                        UIUtil.createInputDialog("Introduza o número de rotas a calcular:")
                                .showAndWait().ifPresent(distanceInput -> {
                                    controller.doSearchNRoutesDistance(stopVertex, distanceInput).showAndWait();
                                });
                    }
                });
            }
        });

        // Route details
        graphView.setEdgeDoubleClickAction((SmartGraphEdge<Route, Stop> smartGraphEdge)->{
            Edge<Route, Stop> edge = smartGraphEdge.getUnderlyingEdge();
            Alert popup = controller.doRouteDetails(edge);

            popup.showAndWait().ifPresent(buttonType -> {
                // Deactivate/Activate route
                if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
                    ComboBox<String> cbOptions = new ComboBox<>();
                    cbOptions.getItems().add("Rota completa");
                    cbOptions.getItems().addAll(Transport.valuesToStringArray());

                    UIUtil.createRouteStatusPopup(cbOptions).showAndWait().ifPresent(confirmButton->{
                        if(confirmButton.getButtonData() == ButtonBar.ButtonData.APPLY){
                            if(cbOptions.getValue().equals("Rota completa")) controller.doToggleRoute(edge);
                            else controller.doToggleTransport(edge, Transport.fromString(cbOptions.getValue()));
                        }
                    });
                }
            });
        });

        // Stop centrality
        btnCentrality.setOnAction((ActionEvent event) -> {
            resetSideMenu("Centralidade");

            Map<String, Integer> centralityMap = controller.doGetCentrality();

            scrollPane.setContent(UIUtil.createCentralityGrid(centralityMap));

            Button btnBack = UIUtil.createStyledButton("Voltar", 2);
            btnBack.setOnAction((ActionEvent backEvent) ->{
                renderMetrics();
            });

            sideMenu.getChildren().addAll(lblTitle, scrollPane, btnBack);
        });
    }

    /**
     * Checks a route status to update its style
     */
    private void checkRoutesStatus(){
        for(Edge<Route, Stop> edge : graph.edges()){
            SmartStylableNode stylableEdge = graphView.getStylableEdge(edge);

            if(stylableEdge != null){
                if(!edge.element().isActive()){
                    graphView.getStylableEdge(edge).setStyleClass("edge-disabled");
                } else{
                    graphView.getStylableEdge(edge).setStyleClass("edge");
                }
            }
        }
    }

    @Override
    public void update(Observable subject, Object arg) {
        if(subject == graph){
            checkRoutesStatus();
            graphView.updateAndWait();
        }
    }
    // endregion
}