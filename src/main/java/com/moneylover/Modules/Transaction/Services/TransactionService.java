package com.moneylover.Modules.Transaction.Services;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Budget.Entities.Budget;
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
    private WalletService walletService;

    private CategoryService categoryService;

    public TransactionService() throws SQLException, ClassNotFoundException {
        super();
        this.walletService = new WalletService();
        this.categoryService = new CategoryService();
    }

    protected String getTable() {
        return Transaction.getTable();
    }

    public ArrayList<Transaction> listByMonth(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId, date, operator);

        return transactions;
    }

    public ArrayList<Transaction> listByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId, startDate, endDate);

        return transactions;
    }

    public ArrayList<Transaction> listByBudget(Budget budget) throws SQLException {
        ArrayList<Transaction> transactions = this._list(budget);

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
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
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
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

    public boolean update(Transaction transaction, int id) throws SQLException, NotFoundException {
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
        this._update(transaction, id, category.getMoneyType());
        transaction = this.getDetail(id);
        this.walletService.calculateAmount(transaction.getAmount(), transaction.getWalletId());

        return true;
    }

    /*====================================================================================*/
    private ArrayList<Transaction> _list(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String yearCondition = "";

        if (operator == '=') {
            yearCondition = "year(transacted_at) = " + date.getYear();
        }

        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                     "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "month(transacted_at)  " + operator + " " + date.getMonthValue(),
                yearCondition
        );

        while (resultSet.next()) {
            transactions.add(this.toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _list(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                     "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "transacted_at >= CAST('" + startDate.toString() + "' AS DATE) AND transacted_at <= CAST('" + endDate.toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this.toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _list(Budget budget) throws SQLException {
        String categoryCondition;

        if (budget.getBudgetableType().equals(CommonConstants.APP_SUB_CATEGORY)) {
            categoryCondition = "transactions.sub_category_id = " + budget.getBudgetableId();
        } else {
            categoryCondition = "transactions.category_id = " + budget.getBudgetableId();
        }

        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                     "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + budget.getWalletId(),
                categoryCondition,
                "transacted_at >= CAST('" + budget.getStartedAt().toString() + "' AS DATE) AND transacted_at <= CAST('" + budget.getEndedAt().toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this.toObject(resultSet));
        }

        return transactions;
    }

    private int _create(Transaction transaction, String moneyType) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setTimestamp(11, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private PreparedStatement handleCreateProcess(Transaction transaction, String moneyType, String statementString) throws SQLException {
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        int subcategoryId = transaction.getSubCategoryId();
        statement.setInt(1, transaction.getWalletId());
        statement.setInt(2, transaction.getTypeId());
        statement.setInt(3, transaction.getCategoryId());
        statement.setDate(5, Date.valueOf(transaction.getTransactedAt().toString()));
        statement.setString(7, transaction.getLocation());
        statement.setNString(8, transaction.getNote());
        statement.setString(9, transaction.getImage());
        statement.setByte(10, transaction.getIsReported());

        if (subcategoryId == 0) {
            statement.setNull(4, Types.INTEGER);
        } else {
            statement.setInt(4, subcategoryId);
        }

        if (moneyType.equals(CommonConstants.EXPENSE)
                || moneyType.equals(CommonConstants.DEBT_COLLECTION)
                || moneyType.equals(CommonConstants.LOAN)) {
            statement.setFloat(6, -transaction.getAmount());
        } else {
            statement.setFloat(6, transaction.getAmount());
        }

        return statement;
    }

    private int _create(ArrayList<Transaction> transactions) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Transaction transaction: transactions) {
            statement.setInt(1, transaction.getWalletId());
            statement.setInt(2, transaction.getTypeId());
            statement.setInt(3, transaction.getCategoryId());
            statement.setInt(4, transaction.getSubCategoryId());
            statement.setDate(5, Date.valueOf(transaction.getTransactedAt().toString()));
            statement.setFloat(6, transaction.getAmount());
            statement.setString(7, transaction.getLocation());
            statement.setNString(8, transaction.getNote());
            statement.setString(9, transaction.getImage());
            statement.setByte(10, transaction.getIsReported());
            statement.setTimestamp(11, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == transactions.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return statement.executeUpdate();
    }

    private boolean _update(Transaction transaction, int id, String moneyType) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET wallet_id = ?, type_id = ?, category_id = ?, sub_category_id = ?, transacted_at = ?, amount = ?, location = ?, note = ?, image = ?, is_reported = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setTimestamp(11, this.getCurrentTime());
        statement.setInt(12, id);
        statement.executeUpdate();
        this.closePreparedStatement();

        return true;
    }

    @Override
    protected Transaction toObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setTypeId(resultSet.getInt("type_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at"));
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setLocation(resultSet.getString("location"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setImage(resultSet.getString("image"));
        transaction.setIsReported(resultSet.getByte("is_reported"));
        transaction.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        transaction.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));
        transaction.setCategoryName(resultSet.getString("category_name"));
        transaction.setCategoryIcon(resultSet.getString("category_icon"));
        transaction.setCategoryMoneyType(resultSet.getString("category_money_type"));
        transaction.setSubCategoryName(resultSet.getString("sub_category_name"));
        transaction.setSubCategoryIcon(resultSet.getString("sub_category_icon"));

        return transaction;
    }

    @Override
    protected ResultSet getByJoin(String select, String join, String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT " + select + " FROM " + getTable() + " " + join + condition + " ORDER BY transacted_at DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }
}
