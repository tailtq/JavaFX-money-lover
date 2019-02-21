package com.moneylover.app.Currency.View;

import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class CurrencyCell extends ListCell<Currency> implements DialogInterface {
    private Button currencyCell;

    private Currency currency;

    private IntegerProperty selectedCurrencyId;

    public CurrencyCell(IntegerProperty selectedCurrencyId) throws IOException {
        this.selectedCurrencyId = selectedCurrencyId;
        this.loadCell();
    }

    private void loadCell() throws IOException {
        FXMLLoader currencyCellLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency-cell.fxml")
        );
        currencyCellLoader.setController(this);
        this.currencyCell = currencyCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private Text currencyName;

    @FXML
    private Label currencyCode;

    @Override
    protected void updateItem(Currency item, boolean empty) {
        super.updateItem(item, empty);
        this.currency = item;

        if (empty) {
            setGraphic(null);
        } else {
            if (item.getId() == this.selectedCurrencyId.get()) {
                this.currencyCell.getStyleClass().add("active");
            }

            this.currencyCell.getStyleClass().add(item.getIcon());
            this.currencyName.setText(item.getName());
            this.currencyCode.setText(item.getCode() + " - " + item.getSymbol());
            setGraphic(this.currencyCell);
        }
    }

    @FXML
    private void selectCurrency(Event e) {
        this.selectedCurrencyId.set(this.currency.getId());

        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
