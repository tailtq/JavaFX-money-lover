package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.BaseViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import java.io.IOException;

public class BudgetController extends BaseViewController implements LoaderInterface {
    @FXML
    private TabPane tabPane;

    @FXML
    private VBox runningTab;

    @FXML
    private VBox finishedTab;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @FXML
    private void changeTab(Event e) {
        Button button = (Button) e.getSource();
        boolean notActive = this.activeButton(button);

        if (notActive) {
            int value = Integer.parseInt(button.getUserData().toString());
            tabPane.getSelectionModel().select(value);
        }
    }

    @FXML
    private void deleteBudget(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            // Delete Budget
            System.out.println("Yes");
        }
    }
}
