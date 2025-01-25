package pt.pa.utils;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.Transport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to create UI components
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class UIUtil {
    private static final String LOGO_IMAGE_PATH = "/images/sit_logo.png";
    private static final String UI_STYLE_PATH = "/styles/ui.css";

    /**
     * Creates the logo Image View
     * @return ImageView object containing the logo image
     */
    public static ImageView logoImage(){
        ImageView logoImage = null;
        try{
            InputStream logoPath = UIUtil.class.getResourceAsStream(LOGO_IMAGE_PATH);

            if(logoPath != null){
                logoImage = new ImageView(new Image(logoPath));
                logoImage.setFitWidth(300);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return logoImage;
    }

    /**
     * Creates a styled button
     * @param text Button text
     * @param type Button type [1-big; 2-small]
     * @return Button object
     */
    public static Button createStyledButton(String text, int type) {
        Button button = new Button(text);

        switch (type){
            case 1 -> button.getStyleClass().add("big-button");
            case 2 -> button.getStyleClass().add("small-button");
            default -> button.getStyleClass().add("button");
        }

        return button;
    }

    /**
     * Builds a table with the transports info of a Route
     * @param route Route to get the info
     * @return GridPane containing transports info
     */
    public static GridPane buildTransportTable(Route route) {
        GridPane table = new GridPane();
        table.getStyleClass().add("info-grid");

        // Add table headers
        Label headerTransport = UIUtil.createStyledLabel("Tipo de Transporte: ", 4);
        Label headerDistance = UIUtil.createStyledLabel("Distância (km): ", 4);
        Label headerDuration = UIUtil.createStyledLabel("Duração (min): ", 4);
        Label headerCost = UIUtil.createStyledLabel("Sustentabilidade: ", 4);
        table.add(headerTransport, 0, 0);
        table.add(headerDistance, 0, 1);
        table.add(headerDuration, 0, 2);
        table.add(headerCost, 0, 3);

        // Populate the columns
        int column = 1;
        for (Transport transport : Transport.values()) {
            Float distanceValue = route.getDistances().get(transport);
            Integer durationValue = route.getDurations().get(transport);
            Float costValue = route.getCosts().get(transport);

            Label distance = UIUtil.createStyledLabel(distanceValue != null ? distanceValue.toString() : "N/A", 4);
            Label duration = UIUtil.createStyledLabel(durationValue != null ? durationValue.toString() : "N/A", 4);
            Label cost = UIUtil.createStyledLabel(costValue != null ? costValue.toString() : "N/A", 4);
            Label icon = UIUtil.createStyledLabel(transport.toEmoji(), 5);

            table.add(icon, column, 0);
            table.add(distance, column, 1);
            table.add(duration, column, 2);
            table.add(cost, column, 3);

            column++;
        }

        return table;
    }

    /**
     * Creates a styled label
     * @param text Label text
     * @param type Lable type [1-header; 2-sub header; 3-big label; 4-small label; 5-transport label; 6-info; 7-white]
     * @return Label object
     */
    public static Label createStyledLabel(String text, int type) {
        Label label = new Label(text);

        switch (type){
            case 1 -> label.getStyleClass().add("header");
            case 2 -> label.getStyleClass().add("sub-header");
            case 3 -> label.getStyleClass().add("big-label");
            case 4 -> label.getStyleClass().add("small-label");
            case 5 -> label.getStyleClass().add("transport-label");
            case 6 -> label.getStyleClass().add("info-label");
            case 7 -> label.getStyleClass().add("white-label");
            default -> label.getStyleClass().add("label");
        }

        return label;
    }

    /**
     * Creates a labeled field
     * @param label Text for the label
     * @param value Input element
     * @return HBox containing the label and the input element
     */
    public static HBox createField(String label, Node value){
        HBox field = new HBox(10);
        field.setAlignment(Pos.CENTER);

        Label lblLabel = createStyledLabel(label, 4);

        field.getChildren().addAll(lblLabel, value);

        return field;
    }

    /**
     * Creates a grid for stops centrality data
     * @param centralityData Centrality data
     * @return GridPane object containing the centrality data
     */
    public static GridPane createCentralityGrid(Map<String, Integer> centralityData) {
        GridPane centralityGrid = new GridPane();
        centralityGrid.getStyleClass().add("centrality-grid");

        int row = 0;
        for (Map.Entry<String, Integer> entry : centralityData.entrySet()) {
            Label lblStop = createStyledLabel(entry.getKey(), 7);

            Label lblCentralityValue = createStyledLabel(entry.getValue().toString(), 6);

            centralityGrid.add(lblStop, 0, row);
            centralityGrid.add(lblCentralityValue, 1, row);
            row++;
        }

        return centralityGrid;
    }

    /**
     * Creates the stop details popup
     * @param stop Stop
     * @param connections Stop connections
     * @param transports Stop transports
     * @return Alert with the stop details
     */
    public static Alert createStopInfoPopup(Stop stop, List<Vertex<Stop>> connections, Set<Transport> transports) {
        // Alert object
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Informação Paragem");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().getStylesheets().add(UI_STYLE_PATH);

        // Custom Content for the Alert
        VBox content = new VBox(15);

        // Title Section
        VBox titleBox = new VBox(logoImage());
        titleBox.setAlignment(Pos.CENTER);

        // Location Section
        Label locationLabel = createStyledLabel(stop.getName(), 2);

        Label codeLabel = createStyledLabel(stop.getCode(), 4);

        VBox locationBox = new VBox(5, locationLabel, codeLabel);
        locationBox.setAlignment(Pos.CENTER);

        // Icons Section (Transportation Icons)
        HBox iconBox = new HBox(10);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPadding(new Insets(10));

        // Icons
        for(Transport transport : transports){
            iconBox.getChildren().add(createStyledLabel(transport.toEmoji(), 5));
        }

        // Coordinates
        Label coordLabel = createStyledLabel("Coordenadas", 3);

        Label coordValue = createStyledLabel(String.format("%f, %f", stop.getLatitude(), stop.getLongitude()), 6);

        // Connections
        Label connectionsLabel = createStyledLabel("Conexões", 3);

        VBox connectionsBox = new VBox(10);
        connectionsBox.setAlignment(Pos.CENTER);

        if(connections.isEmpty()){
            connectionsBox.getChildren().add(createStyledLabel("Sem conexões.", 4));
        } else {
            for (Vertex<Stop> connection : connections) {
                Label lblConnection = createStyledLabel(connection.element().getName(), 6);
                connectionsBox.getChildren().add(lblConnection);
            }
        }

        GridPane infoGrid = new GridPane();
        infoGrid.getStyleClass().add("info-grid");

        infoGrid.add(coordLabel, 0, 0);
        infoGrid.add(coordValue, 0, 1);
        infoGrid.add(connectionsLabel, 1, 0);
        infoGrid.add(connectionsBox, 1, 1);

        // Set the content
        content.getChildren().addAll(titleBox, locationBox, iconBox, infoGrid);
        alert.getDialogPane().setContent(content);

        // Back button
        ButtonType backButtonType = new ButtonType("Voltar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().add(backButtonType);

        // Distance button
        ButtonType distanceButtonType = new ButtonType("Paragens que distam n rotas", ButtonBar.ButtonData.APPLY);
        alert.getButtonTypes().add(distanceButtonType);

        return alert;
    }


    /**
     * Creates the distance popup
     * @param data List with the data
     * @param distance Distance calculated
     * @param sourceName Name of the source stop
     * @return Alert with the info
     */
    public static Alert createDistancePopup(List<String> data, int distance, String sourceName){
        // Alert object
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Informação Distância");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().getStylesheets().add(UI_STYLE_PATH);

        // Custom Content for the Alert
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = createStyledLabel(String.format("Paragens que distam até %d rotas de %s", distance, sourceName), 1);
        content.getChildren().add(titleLabel);

        if(!data.isEmpty()){
            for(String stopName : data){
                Label lblStop = createStyledLabel(stopName, 3);
                content.getChildren().add(lblStop);
            }
        } else{
            content.getChildren().add(createStyledLabel(String.format("Não existem paragens que distam até %d rotas de %s", distance, sourceName), 2));
        }

        ButtonType backButton = new ButtonType("Voltar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().addAll(backButton);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    /**
     * Creates a TextInputDialog
     * @param text Header text
     * @return TextInputDialog
     */
    public static TextInputDialog createInputDialog(String text){
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setHeaderText(text);

        return textInputDialog;
    }

    /**
     * Creates a popup to change the status of a Route
     * @param cbOptions Combobox with the options to change the status
     * @return Alert containing the elements to change route status
     */
    public static Alert createRouteStatusPopup(ComboBox<String> cbOptions){
        // Alert object
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Alterar estado rota");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().getStylesheets().add(UI_STYLE_PATH);

        // Custom Content for the Alert
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = createStyledLabel("Selecione a rota ou tipo de transporte para alterar o estado", 4);

        content.getChildren().add(titleLabel);
        content.getChildren().add(cbOptions);

        ButtonType backButton = new ButtonType("Voltar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButton = new ButtonType("Confirmar", ButtonBar.ButtonData.APPLY);

        alert.getButtonTypes().addAll(backButton, confirmButton);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    /**
     * Creates a group with route optimization criteria
     * @return VBox containing the optimization criteria
     */
    public static VBox createCriteriaGroup(){
        VBox criterio = new VBox();
        criterio.setAlignment(Pos.CENTER);
        criterio.setSpacing(10);

        Label lblCriterio = UIUtil.createStyledLabel("Critério", 4);

        GridPane gridCriterio = new GridPane();
        gridCriterio.setHgap(10);
        gridCriterio.setVgap(10);
        gridCriterio.setAlignment(Pos.CENTER);

        ToggleGroup criterioGroup = new ToggleGroup();

        RadioButton rbCriterioDistancia = new RadioButton("Distância");
        rbCriterioDistancia.setToggleGroup(criterioGroup);
        RadioButton rbCriterioSustentabilidade = new RadioButton("Sustentabilidade");
        rbCriterioSustentabilidade.setToggleGroup(criterioGroup);
        RadioButton rbCriterioDuracao = new RadioButton("Duração");
        rbCriterioDuracao.setToggleGroup(criterioGroup);

        gridCriterio.add(rbCriterioDistancia, 0, 0);
        gridCriterio.add(rbCriterioSustentabilidade, 1, 0);
        gridCriterio.add(rbCriterioDuracao, 0, 1);

        criterio.getChildren().addAll(lblCriterio, gridCriterio);

        return criterio;
    }

    /**
     * Gets the selected criterion from a criteria group
     * @param criteria VBox with criteria group
     * @return RadioButton containing the selected criterion
     */
    public static RadioButton getSelectedCriterion(VBox criteria){
        ToggleGroup group = ((RadioButton)((GridPane)criteria.getChildren().get(1)).getChildren().get(0)).getToggleGroup();

        if(group != null){
            return (RadioButton) group.getSelectedToggle();
        }
        else return null;
    }

    /**
     * Creates a group with transports
     * @return VBox containing the transports
     */
    public static VBox createTransportGroup(){
        VBox transportes = new VBox();
        transportes.setAlignment(Pos.CENTER);
        transportes.setSpacing(10);

        Label lblTiposTransporte = UIUtil.createStyledLabel("Tipos de Transporte", 4);

        GridPane gridTransportes = new GridPane();
        gridTransportes.setHgap(10);
        gridTransportes.setVgap(10);
        gridTransportes.setAlignment(Pos.CENTER);

        int index = 0;

        for (Transport transport : Transport.values()) {
            CheckBox cb = new CheckBox(transport.toString());
            gridTransportes.add(cb, index % 2, index / 2);
            index++;
        }

        transportes.getChildren().addAll(lblTiposTransporte, gridTransportes);

        return transportes;
    }

    /**
     * Gets the selected transports from a transports group
     * @param transportsView Transports group
     * @return List containing the selected transports
     */
    public static List<Transport> getSelectedTransports(VBox transportsView){
        GridPane gridTransportes = (GridPane)transportsView.getChildren().get(1);
        List<Transport> transports = new ArrayList<>();

        for (Node node : gridTransportes.getChildren()) {
            if (node instanceof CheckBox cb) {
                if (cb.isSelected()) {
                    transports.add(Transport.fromString(cb.getText()));
                }
            }
        }

        return transports;
    }

    /**
     * Creates a scroll pane
     * @param content Content for the scroll pane
     * @return Scroll pane with the given content
     */
    public static ScrollPane createScrollPane(Node content){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.getStyleClass().add("scroll-pane");

        return scrollPane;
    }
}
