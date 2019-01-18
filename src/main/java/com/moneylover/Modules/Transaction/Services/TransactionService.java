package com.moneylover.Modules.Transaction.Services;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.Category.Services.CategoryService;
import com.moneylover.Modules.Time.Entities.Time;
import com.moneylover.Modules.Time.Services.TimeService;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Services.WalletService;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class TransactionService extends BaseService {
    private TimeService timeService;

    private WalletService walletService;

    private CategoryService categoryService;

    public TransactionService() throws SQLException, ClassNotFoundException {
        super();
        this.timeService = new TimeService();
        this.walletService = new WalletService();
        this.categoryService = new CategoryService();
    }

    protected String getTable() {
        return Transaction.getTable();
    }

    public ArrayList<Transaction> list(int walletId, int month, int year, char operator) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId, month, year, operator);

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN times ON transactions.time_id = times.id " +
                        "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "transactions.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = this.toObject(resultSet);
        this.closeStatement();

        return transaction;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        LocalDate day = LocalDate.parse(transaction.getTransactedAt().toString());
        Time time = this.timeService.getDetail(day.getMonth().getValue(), day.getYear());
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
        transaction.setTimeId(time.getId());

        int id = this._create(transaction, category.getMoneyType());
        transaction = this.getDetail(id);
        this.walletService.calculateAmount(transaction.getAmount(), transaction.getWalletId());
        //TODO: find budget and update

        return transaction;
    }

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this._create(transactions);

        return true;
    }

    public Transaction update(Transaction transaction, int id) throws SQLException, NotFoundException {
        this._update(transaction, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Transaction> _list(int walledId, int month, int year, char operator) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN times ON transactions.time_id = times.id " +
                        "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walledId,
                "month " + operator + " " + month,
                "year " + operator + " " + year
        );

        while (resultSet.next()) {
            transactions.add(this.toObject(resultSet));
        }

        return transactions;
    }

    private int _create(Transaction transaction, String moneyType) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, time_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int subcategoryId = transaction.getSubCategoryId();
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        LocalDate currentDate = LocalDate.now();
        statement.setInt(1, transaction.getWalletId());
        statement.setInt(2, transaction.getTimeId());
        statement.setInt(3, transaction.getTypeId());
        statement.setInt(4, transaction.getCategoryId());
        statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
        statement.setString(8, transaction.getLocation());
        statement.setNString(9, transaction.getNote());
        statement.setString(10, transaction.getImage());
        statement.setByte(11, transaction.getIsReported());
        statement.setDate(12, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));

        if (subcategoryId == 0) {
            statement.setNull(5, Types.INTEGER);
        } else {
            statement.setInt(5, subcategoryId);
        }

        if (moneyType.equals(CommonConstants.EXPENSE)
                || moneyType.equals(CommonConstants.DEBT_COLLECTION)
                || moneyType.equals(CommonConstants.LOAN)) {
            statement.setFloat(7, -transaction.getAmount());
        } else {
            statement.setFloat(7, transaction.getAmount());
        }

        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private int _create(ArrayList<Transaction> transactions) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, time_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Transaction transaction: transactions) {
            LocalDate currentDate = LocalDate.now();
            statement.setInt(1, transaction.getWalletId());
            statement.setInt(2, transaction.getTimeId());
            statement.setInt(3, transaction.getTypeId());
            statement.setInt(4, transaction.getCategoryId());
            statement.setInt(5, transaction.getSubCategoryId());
            statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
            statement.setFloat(7, transaction.getAmount());
            statement.setString(8, transaction.getLocation());
            statement.setNString(9, transaction.getNote());
            statement.setString(10, transaction.getImage());
            statement.setByte(11, transaction.getIsReported());
            statement.setDate(12, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == transactions.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return statement.executeUpdate();
    }

    private int _update(Transaction transaction, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, transaction.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Transaction toObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setTimeId(resultSet.getInt("time_id"));
        transaction.setTypeId(resultSet.getInt("type_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at"));
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setLocation(resultSet.getString("location"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setImage(resultSet.getString("image"));
        transaction.setIsReported(resultSet.getByte("is_reported"));
        transaction.setCreatedAt(resultSet.getDate("created_at"));
        transaction.setUpdatedAt(resultSet.getDate("updated_at"));
        transaction.setCategoryName(resultSet.getString("category_name"));
        transaction.setCategoryIcon(resultSet.getString("category_icon"));
        transaction.setCategoryMoneyType(resultSet.getString("category_money_type"));
        transaction.setSubCategoryName(resultSet.getString("sub_category_name"));
        transaction.setSubCategoryIcon(resultSet.getString("sub_category_icon"));

        return transaction;
    }
}
