package com.moneylover.app.Friend;

import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.Friend.Entities.Friend;
import com.moneylover.app.BaseViewPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendDialogPresenter extends BaseViewPresenter implements DialogInterface {
    private static ArrayList<Friend> friends;

    public static void setFriends(ArrayList<Friend> friends) {
        FriendDialogPresenter.friends = friends;
    }

    private IntegerProperty selectedFriend;

    public FriendDialogPresenter(IntegerProperty selectedFriend) {
        this.selectedFriend = selectedFriend;
    }

    @FXML
    private VBox VBoxFriends;

    public void showFriendDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/friend/choose-friend.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this._loadVBoxFriends();

        this.createScreen(parent, "Choose Friend", 330, 500);
    }

    private void _loadVBoxFriends() {
        List<Button> buttons = new ArrayList<>();

        for (Friend friend: FriendDialogPresenter.friends) {
            Button button = new Button();
            button.getStyleClass().addAll("currency__choose-button", "image-button", "boy", "white-bg-color");
            button.setGraphic(new Text(friend.getName()));
            button.setAlignment(Pos.CENTER_LEFT);
            buttons.add(button);
            button.setMaxHeight(Double.MAX_VALUE);
            button.setOnAction(actionEvent -> {
                this.selectedFriend.set(friend.getId());
                this.closeScene(actionEvent);
            });
        }

        this.VBoxFriends.getChildren().addAll(buttons);
    }

    public void handleSelectedFriendId(Button selectFriend) {
        this.selectedFriend.addListener((observableValue, oldValue, newValue) -> {
            for (Friend friend : FriendDialogPresenter.friends) {
                if (friend.getId() != newValue.intValue()) {
                    continue;
                }

                selectFriend.setText(friend.getName());
                selectFriend.getStyleClass().add("boy");

                return;
            }
        });
    }

    @FXML
    public void closeScene(Event e) {
        DialogInterface.closeScene(e);
    }
}
