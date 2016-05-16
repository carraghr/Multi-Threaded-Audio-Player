import com.sun.javaws.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class Player extends Application {

    public static void main(String[] args){
        launch(args);
    }

    Controller controller;

    final String controllerLocation = "/Controller.fxml";

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setOnCloseRequest(e->{
            if (controller != null){
                controller.stopEvent();
            }
        });

        FXMLLoader loader = new FXMLLoader(Main.class.getResource(controllerLocation));

        VBox layout = null;

        try {
            layout =  loader.load(Main.class.getResourceAsStream(controllerLocation));
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(layout,601,164);

        primaryStage.setScene(scene);

        primaryStage.show();

    }
}
