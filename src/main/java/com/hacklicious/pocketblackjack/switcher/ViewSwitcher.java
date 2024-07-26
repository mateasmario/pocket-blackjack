package com.hacklicious.pocketblackjack.switcher;

import com.hacklicious.pocketblackjack.util.ResourceUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class ViewSwitcher {
    private static Scene scene;

    public static void setScene(Scene aScene) {
        scene = aScene;
    }

    public static void switchFXML(String fileName) {
        try {
            URL url = ResourceUtil.getResourceAsUrl(fileName);
            Parent root = FXMLLoader.load(url);
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
