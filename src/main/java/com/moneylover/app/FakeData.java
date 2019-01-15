package com.moneylover.app;

import com.github.javafaker.Faker;
import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Helpers.UpdatableBcrypt;
import com.moneylover.Modules.Currency.Controllers.CurrencyController;
import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Modules.Time.Controllers.TimeController;
import com.moneylover.Modules.Time.Entities.Time;
import com.moneylover.Modules.Type.Controllers.TypeController;
import com.moneylover.Modules.Type.Entities.Type;
import com.moneylover.Modules.User.Controllers.UserController;
import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Modules.Wallet.Controllers.WalletController;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;

import java.sql.SQLException;
import java.util.ArrayList;

public class FakeData {
    private static FakeData fakeData;

    private Faker faker;

    private CurrencyController currencyController;

    private UserController userController;

    private WalletController walletController;

    private TimeController timeController;

    private TypeController typeController;

    public FakeData() throws SQLException, ClassNotFoundException {
        this.faker = new Faker();
        this.currencyController = new CurrencyController();
        this.userController = new UserController();
        this.walletController = new WalletController();
        this.timeController = new TimeController();
        this.typeController = new TypeController();
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException, NotFoundException {
        try {
            fakeData = new FakeData();
//            fakeData.createCurrencies();
//            fakeData.createUser();
//            fakeData.createWallets();
//            fakeData.createTimes();
//            fakeData.createTypes();
            fakeData.createCategories();
//            fakeData.createSubCategories();
        } catch (Exception e) {
            throw e;
        }
    }

    public void createCurrencies() throws NotFoundException, SQLException {
        Currency vnd = new Currency();
        vnd.setName("Việt Nam Đồng");
        vnd.setCode("VND");
        vnd.setSymbol("₫");
        vnd.setIcon("currency__vnd");
        this.currencyController.create(vnd);

        Currency usd = new Currency();
        usd.setName("United States Dollar");
        usd.setCode("USD");
        usd.setSymbol("$");
        usd.setIcon("currency__usd");
        this.currencyController.create(usd);

        Currency jpy = new Currency();
        jpy.setName("Yen");
        jpy.setCode("JPY");
        jpy.setSymbol("¥");
        jpy.setIcon("currency__jpy");
        this.currencyController.create(jpy);
    }

    public void createUser() throws SQLException, NotFoundException {
        User user;

        for (int i = 0; i < 10; i++) {
            user = new User();
            user.setName(this.faker.name().fullName());
            user.setPassword(UpdatableBcrypt.hash("123123"));
            user.setEmail(this.faker.internet().emailAddress());
            user.setPhone(this.faker.phoneNumber().cellPhone());
            user.setBirthday(this.faker.date().birthday());
            this.userController.create(user);
        }
    }

    public void createWallets() throws SQLException, NotFoundException {
        Wallet wallet;
        ArrayList<Currency> currencies = this.currencyController.list();
        int currenciesQuantity = currencies.size();

        for (int i = 0; i < 10; i++) {
            int number = this.faker.number().numberBetween(0, currenciesQuantity - 1);

            wallet = new Wallet();
            wallet.setCurrencyId(currencies.get(number).getId());
            wallet.setName("Wallet " + i);
            wallet.setInflow((float) this.faker.number().randomDouble(1, 1000000, 10000000));
            wallet.setOutflow((float) this.faker.number().randomDouble(1, 1000000, 10000000));
            this.walletController.create(wallet);
        }

        ArrayList<UserWallet> userWallets = new ArrayList<>();
        ArrayList<User> users = this.userController.list();
        ArrayList<Wallet> wallets = this.walletController.list();
        int usersQuantity = users.size();
        int walletsQuantity = wallets.size();

        for (int i = 0; i < 20; i++) {
            int number1 = (int)(Math.random() * (usersQuantity - 1));
            int number2 = (int)(Math.random() * (walletsQuantity - 1));
            int userId = users.get(number1).getId();
            int walletId = wallets.get(number2).getId();

            UserWallet userWallet = new UserWallet(userId, walletId);
            userWallets.add(userWallet);
        }
        this.walletController.attachUsers(userWallets);
    }

    public void createTimes() throws NotFoundException, SQLException {
        ArrayList<Time> times = new ArrayList<>();
        for (int y = 2018; y <= 2019; y++) {
            for (int m = 1; m <= 12; m++) {
                times.add(new Time(m, y));
            }
        }

        this.timeController.create(times);
    }

    public void createTypes() throws NotFoundException, SQLException {
        ArrayList<Type> types = new ArrayList<>();
        types.add(new Type(CommonConstants.DEBT_LOAN,"Debt/Loan"));
        types.add(new Type(CommonConstants.EXPENSE, "Expense"));
        types.add(new Type(CommonConstants.INCOME, "Income"));

        this.typeController.create(types);
    }

    public void createCategories() throws SQLException {
        ArrayList<Type> types = this.typeController.list();

    }

    public void createSubCategories() {

    }
}
