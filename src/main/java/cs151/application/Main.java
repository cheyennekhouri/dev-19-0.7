package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    //home setup
    @Override
    public void start(Stage stage) throws Exception {
        DataStore.load();
        DataStore.loadProfiles();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 320, 240);
        stage.setTitle("KnowledgeTrack Home");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DataStore.save();
        DataStore.saveProfiles();
    }

    public static void main(String[] args) {
        launch();
    }
}
