package com.moneylover.app.controllers.Pages.User;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class UserController extends PageController implements LoaderInterface {
    private com.moneylover.Modules.User.Controllers.UserController userController;

    private BooleanProperty changeUser;

    public UserController(BooleanProperty changeWallet, BooleanProperty changeUser) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.changeUser = changeUser;
        this.userController = new com.moneylover.Modules.User.Controllers.UserController();
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);
        this.textFieldName.setText(this.user.getName());
        this.textFieldEmail.setText(this.user.getEmail());
        this.textFieldPhone.setText(this.user.getPhone());
    }
    /*========================== Draw ==========================*/
    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPhone;

    @FXML
    private TextField textFieldPassword;

    @FXML
    private TextField textFieldPasswordConfirmation;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/user/user.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();
        System.out.println("test");

        return parent;
    }

    @FXML
    private void updateUser() {
        String name = this.textFieldName.getText().trim();
        String phone = this.textFieldPhone.getText().trim();
        String password = this.textFieldPassword.getText().trim();
        String passwordConfirmation = this.textFieldPasswordConfirmation.getText().trim();

        if (name.isEmpty()) {
            this.showErrorDialog("Please input all needed information!");
            return;
        }
        if ((!password.isEmpty() || !passwordConfirmation.isEmpty()) && !password.equals(passwordConfirmation)) {
            this.setFieldsNull(this.textFieldPassword);
            this.showErrorDialog("Password Confirmation is invalid!");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(password);

        try {
            this.userController.update(user, this.user.getId());
            this.setFieldsNull(this.textFieldPassword, this.textFieldPasswordConfirmation);
            this.changeUser.set(true);
        } catch (SQLException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
