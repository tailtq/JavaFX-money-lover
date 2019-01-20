package com.moneylover.app.Currency;

import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.app.Currency.View.CurrencyCell;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CurrencyPresenter implements DialogInterface, Initializable {
    private static ObservableList<Currency> currencies = FXCollections.observableArrayList();

    private IntegerProperty selectedCurrencyId;

    public CurrencyPresenter() {}

    public CurrencyPresenter(IntegerProperty selectedCurrencyId) {
        this.selectedCurrencyId = selectedCurrencyId;
    }

    public static void setCurrencies(ArrayList<Currency> currencies) {
        CurrencyPresenter.currencies.addAll(currencies);
    }

    public void setSelectedCurrencyId(IntegerProperty selectedCurrencyId) {
        this.selectedCurrencyId = selectedCurrencyId;
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewCurrencies;

    public void loadCurrencies() {
        this.listViewCurrencies.setItems(CurrencyPresenter.currencies);
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
    }

    public void handleSelectedCurrencyId(Button selectCurrency) {
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

            for (Currency currency: CurrencyPresenter.currencies) {
                if (currency.getId() != newValue.intValue()) {
                    continue;
                }
                selectCurrency.setText(currency.getName());
                classes.add(currency.getIcon());

                return;
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loadCurrencies();
    }
}
