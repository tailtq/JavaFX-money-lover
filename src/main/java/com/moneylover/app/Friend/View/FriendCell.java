package com.moneylover.app.Friend.View;

import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Modules.Friend.Controllers.FriendController;
import com.moneylover.Modules.Friend.Entities.Friend;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class FriendCell extends ListCell<Friend> implements DialogInterface {
    private HBox friendCell;

    private Friend friend;

    private FriendController friendController;

    private StringProperty handledFriendId;

    public FriendCell(StringProperty handledFriendId) throws SQLException, ClassNotFoundException, IOException {
        this.handledFriendId = handledFriendId;
        this.friendController = new FriendController();
        this._loadCell();
    }

    private void _loadCell() throws IOException {
        FXMLLoader friendCellLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/pages/friend/friend-cell.fxml")
        );
        friendCellLoader.setController(this);
        this.friendCell = friendCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelFriendName;

    @Override
    protected void updateItem(Friend item, boolean empty) {
        super.updateItem(item, empty);
        this.friend = item;

        if (empty) {
            setGraphic(null);
            return;
        }

        this.labelFriendName.setText(item.getName());
        setGraphic(this.friendCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addEditPopup((Node) e.getSource());
    }

    @FXML
    private void edit() {

    }

    @FXML
    private void delete() {

    }
}
