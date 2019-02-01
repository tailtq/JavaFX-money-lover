package com.moneylover.app.User;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Infrastructure.Contracts.LoaderInterface;
import com.moneylover.app.PagePresenter;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;

public class UserPresenter extends PagePresenter implements LoaderInterface {
    private com.moneylover.Modules.User.Controllers.UserController userController;

    private static User user;

    public UserPresenter() throws SQLException, ClassNotFoundException {
        this.userController = new com.moneylover.Modules.User.Controllers.UserController();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserPresenter.user = user;
    }

    /*========================== Draw ==========================*/
    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPhone;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private PasswordField textFieldPasswordConfirmation;

    public void loadPresenter() {
        User user = UserPresenter.getUser();
        this.textFieldName.setText(user.getName());
        this.textFieldEmail.setText(user.getEmail());
        this.textFieldPhone.setText(user.getPhone());
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

        if (name.length() > 80) {
            this.showErrorDialog("Name is not valid");
            return;
        }

        if (phone.length() > 20) {
            this.showErrorDialog("Telephone is not exit");
            return;
        }

        if ((!password.isEmpty() || !passwordConfirmation.isEmpty()) && !password.equals(passwordConfirmation)) {
            this.setFieldsNull(this.textFieldPassword);
            this.showErrorDialog("Password Confirmation is invalid!");
            return;
        }

        User user = new User();
        user.setId(UserPresenter.getUser().getId());
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(password);

        try {
            this.userController.update(user, user.getId());
            UserPresenter.setUser(this.userController.getDetail(user.getId()));
            this.setFieldsNull(this.textFieldPassword, this.textFieldPasswordConfirmation);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
