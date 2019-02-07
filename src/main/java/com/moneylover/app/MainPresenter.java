package com.moneylover.app;

import com.moneylover.Modules.Category.Controllers.CategoryController;
import com.moneylover.Modules.Currency.Controllers.CurrencyController;
import com.moneylover.Modules.Friend.Controllers.FriendController;
import com.moneylover.Modules.SubCategory.Controllers.SubCategoryController;
import com.moneylover.Modules.Type.Controllers.TypeController;
import com.moneylover.Modules.Wallet.Controllers.WalletController;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.Currency.CurrencyPresenter;
import com.moneylover.app.Friend.FriendDialogPresenter;
import com.moneylover.app.User.UserPresenter;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import com.moneylover.Infrastructure.Contracts.LoaderInterface;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPresenter extends BaseViewPresenter implements Initializable {
    private ObservableList<Wallet> wallets = FXCollections.observableArrayList();

    private LoaderInterface controller;

    private StringProperty changeMainScene;

    private BooleanProperty changeScene = new SimpleBooleanProperty(false);

    private String fxmlFile = "";

    private IntegerProperty walletIndex = new SimpleIntegerProperty(0);

    private VBox mainView;

    private long time;

    private ArrayList<Pair<String, Pair<PagePresenter, VBox>>> views = new ArrayList<>();

    @FXML
    private Button buttonTransaction,
            buttonReport,
            buttonBudget,
            buttonWallet,
            buttonFriend,
            buttonUser;

    void setChangeMainScene(StringProperty changeMainScene) {
        this.changeMainScene = changeMainScene;
    }

    BooleanProperty getChangeScene() {
        return changeScene;
    }

    void setChangeScene(boolean changeScene) {
        this.changeScene.setValue(changeScene);
    }

    VBox getMainView() {
        return this.mainView;
    }

    private void setWallets() throws IOException, SQLException, InterruptedException {
        this.controller.setWallets(this.wallets);
        this.walletIndex.addListener((observableValue, oldValue, newValue) -> {
            try {
                if (!this.fxmlFile.contains("wallets.fxml")) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.fxmlFile));
                    this.initView(fxmlLoader);
                }
            } catch (IOException | SQLException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void pressTransaction(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transactions.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressDebt(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/debt/debts.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressReport(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/report/report.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressBudget(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budgets.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressWallet(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallets.fxml"));
            this.initView(fxmlLoader);
            CurrencyPresenter.setCurrencies((new CurrencyController()).list());
        }
    }

    @FXML
    private void pressFriend(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/friend/friends.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressUser(Event e) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if (this.activeButton((Node) e.getSource())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/user/user.fxml"));
            this.initView(fxmlLoader);
        }
    }

    @FXML
    private void pressSignOut(Event e) {
        this.changeMainScene.set("signin");
    }

    private void initView(FXMLLoader viewLoader) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        String file = viewLoader.getLocation().getFile();
        this.fxmlFile = file.substring(file.indexOf("/com/moneylover"));
        time = System.nanoTime();
        this.changeViewLoader(viewLoader);
        this.setChangeScene(true);
        System.out.println((System.nanoTime() - time) / 1000000);
        this.controller.loadPresenter();
        this.controller.setWalletIndex(this.walletIndex);
        this.setWallets();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.wallets.addAll((new WalletController()).list(UserPresenter.getUser().getId()));
            this.wallets.addListener(new ListChangeListener<Wallet>() {
                @Override
                public void onChanged(Change<? extends Wallet> change) {
                    boolean disable = wallets.size() == 0;
                    disableSidebarButtons(disable);
                }
            });
            FXMLLoader fxmlLoader;

            if (this.wallets.size() == 0) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallets.fxml"));
                CurrencyPresenter.setCurrencies((new CurrencyController()).list());
            } else {
                fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transactions.fxml"));
            }

            this.initView(fxmlLoader);

            if (this.wallets.size() == 0) {
                this.buttonWallet.getStyleClass().add("active");
                this.disableSidebarButtons(true);
            } else {
                this.buttonTransaction.getStyleClass().add("active");
            }

            this.changeScene.set(false);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        CategoryPresenter.setTypes((new TypeController()).list());
                        CategoryPresenter.setCategories((new CategoryController()).list());
                        CategoryPresenter.setSubCategories((new SubCategoryController()).list());
                        CategoryPresenter.combineCategories();
                        FriendDialogPresenter.setFriends((new FriendController()).list(UserPresenter.getUser().getId()));
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void changeViewLoader(FXMLLoader viewLoader) throws IOException {
        for (Pair<String, Pair<PagePresenter, VBox>> view: this.views) {
            if (view.getKey().equals(this.fxmlFile)) {
                this.mainView = view.getValue().getValue();
                this.controller = view.getValue().getKey();
                this.changeScene.set(true);
                return;
            }
        }

        this.mainView = viewLoader.load();
        this.controller = viewLoader.getController();
        this.changeScene.set(true);
        this.views.add(new Pair(this.fxmlFile, new Pair(this.controller, this.mainView)));
    }

    private void disableSidebarButtons(boolean disable) {
        this.buttonTransaction.setDisable(disable);
        this.buttonReport.setDisable(disable);
        this.buttonBudget.setDisable(disable);
        this.buttonFriend.setDisable(disable);
        this.buttonUser.setDisable(disable);
    }
}
