package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
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

        FXMLLoader currencyCellLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency-cell.fxml")
        );
        currencyCellLoader.setController(this);
        currencyCell = currencyCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private Text currencyName;

    @FXML
    private Label currencyCode;

    @Override
    protected void updateItem(Currency item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            this.currencyCell.getStyleClass().add("currency__" + item.getCode().toLowerCase());
            this.currencyName.setText(item.getName());
            this.currencyCode.setText(item.getCode() + " - " + item.getSymbol());
            setGraphic(currencyCell);
        }
        this.currency = item;
    }

    @FXML
    private void selectCurrency(Event e) {
        this.selectedCurrencyId.set(this.currency.getId());

        this.closeScene(e);
    }
}
