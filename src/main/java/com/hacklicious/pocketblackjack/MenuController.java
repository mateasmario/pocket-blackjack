package com.hacklicious.pocketblackjack;

import com.hacklicious.pocketblackjack.switcher.ViewSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class MenuController {
    @FXML
    protected void onPlayButtonClick() {
        ViewSwitcher.switchFXML("round-view.fxml");
    }

    @FXML
    protected void onWebsiteButtonClick() {

    }

    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
}
