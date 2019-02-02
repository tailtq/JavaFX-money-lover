package com.moneylover.app.Friend;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Friend.Controllers.FriendController;
import com.moneylover.Modules.Friend.Entities.Friend;
import com.moneylover.app.Friend.View.FriendCell;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.User.UserPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    @FXML
    private TextField textFieldFriendName;

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
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private void _addNewFriend(Friend friend) {
        this.friends.add(0, friend);
        FriendDialogPresenter.addFriends(this.friends);
    }

    @FXML
    private void createFriend() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/friend/friend-save.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Add Friend", 300, 120);
    }

    @FXML
    private void saveFriend(Event event) {
        String name = this.textFieldFriendName.getText();
        String validation = FriendPresenter.validateFriend(name);

        if (validation != null) {
            this.showErrorDialog(validation);
            return;
        }

        if (name.isEmpty()) {
            this.showErrorDialog("Please input all needed information!");
            return;
        }

        Friend friend = new Friend(UserPresenter.getUser().getId(), name);
        try {
            friend = this.friendController.create(friend);
            this._addNewFriend(friend);
            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    public static String validateFriend(String name) {
        if (name.isEmpty()) {
            return "Please input all needed information!";
        }

        if (name.length() > 80) {
            return "Name is not valid";
        }

        return null;
    }

    private void _handleFriendId() {
        this.handledFriendId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            int id = Integer.parseInt(newValue.substring(7)), i = 0;

            for (Friend friend: this.friends) {
                if (friend.getId() == id) {
                    break;
                }
                i++;
            }


            if (newValue.contains("UPDATE")) {
                try {
                    Friend friend = this.friendController.getDetail(id);
                    this.friends.set(i, friend);
                } catch (SQLException | NotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                this.friends.remove(i);
            }

            FriendDialogPresenter.addFriends(this.friends);
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
