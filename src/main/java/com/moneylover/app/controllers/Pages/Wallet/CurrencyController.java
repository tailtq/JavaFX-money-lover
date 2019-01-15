package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;

public class CurrencyController implements DialogInterface {
    private ObservableList<Currency> currencies = FXCollections.observableArrayList();

    private IntegerProperty selectedCurrencyId;

    CurrencyController(IntegerProperty selectedCurrencyId) throws SQLException, ClassNotFoundException {
        this.selectedCurrencyId = selectedCurrencyId;
        com.moneylover.Modules.Currency.Controllers.CurrencyController currencyController = new com.moneylover.Modules.Currency.Controllers.CurrencyController();
        this.currencies.setAll(currencyController.list());
    }

    CurrencyController(IntegerProperty selectedCurrencyId, ObservableList<Currency> currencies) {
        this.selectedCurrencyId = selectedCurrencyId;
        this.currencies = currencies;
    }

    public ObservableList<Currency> getCurrencies() {
        return currencies;
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewCurrencies = null;

    void loadCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        this.listViewCurrencies.setItems(this.currencies);
        this.listViewCurrencies.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell<Currency> call(ListView param) {
                try {
                    return new CurrencyCell(selectedCurrencyId);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        this.createScreen(parent, "Choose Currency", 400, 300);
    }

    void handleSelectedCurrencyId(Button selectCurrency) {
        this.selectedCurrencyId.addListener((observableValue, oldValue, newValue) -> {
            ObservableList<String> classes = selectCurrency.getStyleClass();
            int i = 0;

            for (String element: classes) {
                if (element.contains("currency__")) {
                    classes.remove(i);
                    break;
                }
                i++;
            }

            if (newValue.intValue() == 0) {
                selectCurrency.setText("");
                return;
            }

            for (Currency currency: this.currencies) {
                if (currency.getId() != newValue.intValue()) {
                    continue;
                }
                selectCurrency.setText(currency.getName());
                classes.add(currency.getIcon());

                return;
            }
        });
    }
}
