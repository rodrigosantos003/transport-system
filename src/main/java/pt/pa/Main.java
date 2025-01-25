package pt.pa;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.pa.controller.MapController;
import pt.pa.model.TransportsMap;
import pt.pa.view.MapView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Main class
 *
 * @author amfs
 */
public class Main extends Application {

    /**
     * The default entry point of the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TransportsMap graph = new TransportsMap();
        MapView view = new MapView(graph);
        MapController controller = new MapController(graph, view);

        Scene scene = new Scene(view, 1500, 760);
        scene.getStylesheets().add("styles/ui.css");

        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setTitle("Projeto PA 2024/25 - Maps");
        stage.setScene(scene);
        stage.show();

        view.initGraphView();
    }
}
