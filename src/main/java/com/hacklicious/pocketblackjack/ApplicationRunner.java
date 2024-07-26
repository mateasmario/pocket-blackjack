package com.hacklicious.pocketblackjack;

import com.hacklicious.pocketblackjack.switcher.ViewSwitcher;
import com.hacklicious.pocketblackjack.util.ResourceUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ApplicationRunner extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image(ResourceUtil.getResourceAsUrl("icon.png").toExternalForm()));
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceUtil.getResourceAsUrl("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        ViewSwitcher.setScene(scene);
        scene.getStylesheets().add(ResourceUtil.getResourceAsUrl("styles.css").toExternalForm());
        stage.setTitle("Pocket Blackjack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}