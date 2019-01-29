package com.moneylover.app.Friend;

import com.moneylover.Modules.Friend.Controllers.FriendController;
import com.moneylover.Modules.Friend.Entities.Friend;
import com.moneylover.app.Friend.View.FriendCell;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.User.UserPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;

public class FriendPresenter extends PagePresenter {
    private FriendController friendController;

    private StringProperty handledFriendId = new SimpleStringProperty();

    private ObservableList<Friend> friends;

    public FriendPresenter() throws SQLException, ClassNotFoundException {
        this.friendController = new FriendController();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewFriends;

    private void _setListViewFriends() {
        this._handleFriendId();

        if (this.friends.size() == 0) {
            this.listViewFriends.setPlaceholder(new Label("No Friend In List"));
        }

        this.listViewFriends.setItems(this.friends);
        this.listViewFriends.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView listView) {
                try {
                    return new FriendCell(handledFriendId);
                } catch (SQLException | ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void createFriend() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/friend/friend-create.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Add Friend", 300, 230);
    }

    @FXML
    private void storeFriend() {
        System.out.println("Test");
    }

    private void _handleFriendId() {
        this.handledFriendId.addListener((observableValue, oldValue, newValue) -> {

        });
    }

    @Override
    public void loadPresenter() throws SQLException {
        this.friends = FXCollections.observableArrayList(
                this.friendController.list(UserPresenter.getUser().getId())
        );
        this._setListViewFriends();
    }
}
