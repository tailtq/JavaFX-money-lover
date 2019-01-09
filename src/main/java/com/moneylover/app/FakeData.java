package com.moneylover.app;

import com.github.javafaker.Faker;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Helpers.UpdatableBcrypt;
import com.moneylover.Modules.Currency.Controllers.CurrencyController;
import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Modules.User.Controllers.UserController;
import com.moneylover.Modules.User.Entities.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class FakeData {
    private static FakeData fakeData;

    private Faker faker;

    private CurrencyController currencyController;

    private UserController userController;

    public FakeData() throws SQLException, ClassNotFoundException, NotFoundException {
        this.faker = new Faker();
        this.currencyController = new CurrencyController();
        this.userController = new UserController();
    }

    public static void main(String args[]) {
        try {
            fakeData = new FakeData();
//        fakeData.createCurrencies();
            fakeData.createUser();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void createCurrencies() throws NotFoundException, SQLException {
        Currency vnd = new Currency();
        vnd.setName("Việt Nam Đồng");
        vnd.setCode("VND");
        vnd.setSymbol("₫");
        vnd.setImage("/assets/images/flags/currency_vnd.png");
        this.currencyController.create(vnd);

        Currency usd = new Currency();
        usd.setName("United States Dollar");
        usd.setCode("USD");
        usd.setSymbol("$");
        usd.setImage("/assets/images/flags/currency_usd.png");
        this.currencyController.create(usd);

        Currency jpy = new Currency();
        jpy.setName("Yen");
        jpy.setCode("JPY");
        jpy.setSymbol("¥");
        jpy.setImage("/assets/images/flags/currency_jpy.png");
        this.currencyController.create(jpy);
    }

    public void createUser() throws InvocationTargetException, SQLException, IllegalAccessException, NotFoundException, NoSuchMethodException, NoSuchFieldException {
        User user;
        UpdatableBcrypt bcrypt = new UpdatableBcrypt();

        for (int i = 0; i < 10; i++) {
            user = new User();
            user.setName(this.faker.name().fullName());
            user.setPassword(bcrypt.hash("123123"));
            user.setEmail(this.faker.internet().emailAddress());
            user.setPhone(this.faker.phoneNumber().cellPhone());
            user.setBirthday(this.faker.date().birthday());
            this.userController.create(user);
        }
    }
}
