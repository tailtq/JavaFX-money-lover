package com.moneylover.Modules.Transaction.Services;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Budget.Services.BudgetService;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.Category.Services.CategoryService;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Services.WalletService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionService extends BaseService {
    private WalletService walletService;

    private CategoryService categoryService;

    private BudgetService budgetService;

    public TransactionService() throws SQLException, ClassNotFoundException {
        super();
        this.walletService = new WalletService();
        this.categoryService = new CategoryService();
    }
    
    private BudgetService _getBudgetService() throws SQLException, ClassNotFoundException {
        if (this.budgetService == null) {
            this.budgetService = new BudgetService();
        }
        
        return this.budgetService;
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

    public ArrayList<Transaction> listNotReportedByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this._listNotReported(walletId, startDate, endDate);

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

        Transaction transaction = this._toObject(resultSet);
        this.closeStatement();

        return transaction;
    }

    public float getAmountByBudget(Budget budget) throws SQLException {
        String condition = "category_id = ";

        if (budget.getBudgetableType().equals(CommonConstants.APP_SUB_CATEGORY)) {
            condition = "sub_category_id = ";
        }

        condition += budget.getBudgetableId();
        float amount = this._calculate(
                "SUM(amount) as totalAmount",
                "totalAmount",
                "wallet_id = " + budget.getWalletId(),
                condition,
                "transacted_at >= CAST('" + budget.getStartedAt().toString() + "' AS DATE)",
                "transacted_at <= CAST('" + budget.getEndedAt().toString() + "' AS DATE)"
        );

        return amount;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
        int id = this._create(transaction, category.getMoneyType()),
                walletId = transaction.getWalletId();
        Transaction newTransaction = this.getDetail(id);
        Thread rollbackUpdatingAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    walletService.setAmount(
                            newTransaction.getAmount(),
                            walletId,
                            false
                    );

                    if (TransactionService.isSpent(category.getMoneyType()) && !newTransaction.getIsNotReported()) {
                        _increaseBudgetAmount(newTransaction, false);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        rollbackUpdatingAmount.start();

        return newTransaction;
    }

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this._create(transactions);

        return true;
    }

    public void update(Transaction transaction, int id) throws SQLException, NotFoundException, ClassNotFoundException {
        Transaction selectedTransaction = _getTransactionById(id);
        Category oldCategory = categoryService.getDetail(selectedTransaction.getCategoryId());
        this.walletService.setAmount(selectedTransaction.getAmount(), selectedTransaction.getWalletId(), true);

        if (TransactionService.isSpent(oldCategory.getMoneyType()) && !selectedTransaction.getIsNotReported()) {
            this._increaseBudgetAmount(selectedTransaction, true);
        }
        Category newCategory = categoryService.getDetail(transaction.getCategoryId());
        this._update(transaction, id, newCategory.getMoneyType());

        Thread updateAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Transaction selectedTransaction = _getTransactionById(id);
                    walletService.setAmount(selectedTransaction.getAmount(), selectedTransaction.getWalletId(), false);

                    if (TransactionService.isSpent(newCategory.getMoneyType()) && !selectedTransaction.getIsNotReported()) {
                        _increaseBudgetAmount(selectedTransaction, false);
                    }
                } catch (SQLException | ClassNotFoundException | NotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        updateAmount.start();
    }

    private void _increaseBudgetAmount(Transaction transaction, boolean isRevert) throws SQLException, ClassNotFoundException {
        float amount = Math.abs(transaction.getAmount());
        amount = (isRevert) ? -amount : amount;

        if (transaction.getSubCategoryId() != 0) {
            this._getBudgetService().increaseSpentAmount(
                    amount,
                    transaction.getSubCategoryId(),
                    CommonConstants.APP_SUB_CATEGORY,
                    transaction.getTransactedAt()
            );
        }

        this._getBudgetService().increaseSpentAmount(
                amount,
                transaction.getCategoryId(),
                CommonConstants.APP_CATEGORY,
                transaction.getTransactedAt()
        );
    }

    public void setNullFriendId(int friendId) throws SQLException {
        this._setNullFriendId(friendId);
    }

    public void delete(int id) throws SQLException, NotFoundException {
        Transaction transaction = this._getTransactionById(id);
        Thread updateAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    walletService.setAmount(transaction.getAmount(), transaction.getWalletId(), true);
                    _increaseBudgetAmount(transaction, true);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        updateAmount.start();
        this.deleteById(id);
    }

    /*====================================================================================*/
    private ArrayList<Transaction> _list(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String yearCondition = "";

        if (operator == '=') {
            yearCondition = "year(transacted_at) = " + date.getYear();
        } else {
            yearCondition = "year(transacted_at) >= " + date.getYear();
        }

        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                     "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "month(transacted_at) " + operator + " " + date.getMonthValue(),
                yearCondition
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
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
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _listNotReported(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "is_not_reported = 0",
                "transacted_at >= CAST('" + startDate.toString() + "' AS DATE) AND transacted_at <= CAST('" + endDate.toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
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
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private int _create(Transaction transaction, String moneyType) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_not_reported, friend_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setTimestamp(12, this.getCurrentTime());
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
        statement.setByte(10, (byte) (transaction.getIsNotReported() ? 1 : 0));

        if (subcategoryId == 0) {
            statement.setNull(4, Types.INTEGER);
        } else {
            statement.setInt(4, subcategoryId);
        }

        if (!moneyType.equals(CommonConstants.EXPENSE) && !moneyType.equals(CommonConstants.INCOME) && transaction.getFriendId() != 0) {
            statement.setInt(11, transaction.getFriendId());
        } else {
            statement.setNull(11, Types.INTEGER);
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
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_not_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,)";
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
            statement.setByte(10, (byte) (transaction.getIsNotReported() ? 1 : 0));
            statement.setTimestamp(11, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == transactions.size()) {
                statement.executeBatch(); // Execute every 1000 items.
                statement.clearBatch();
            }
        }

        return statement.executeUpdate();
    }

    private void _update(Transaction transaction, int id, String moneyType) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET wallet_id = ?, type_id = ?, category_id = ?, sub_category_id = ?, transacted_at = ?, amount = ?, location = ?, note = ?, image = ?, is_not_reported = ?, friend_id = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setTimestamp(12, this.getCurrentTime());
        statement.setInt(13, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private void _setNullFriendId(int friendId) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET friend_id = null WHERE friend_id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setInt(1, friendId);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    @Override
    protected Transaction _toObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setTypeId(resultSet.getInt("type_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at").toLocalDate());
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setLocation(resultSet.getString("location"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setImage(resultSet.getString("image"));
        transaction.setIsNotReported(resultSet.getByte("is_not_reported") == 1);
        transaction.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        transaction.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));
        transaction.setFriendId(resultSet.getInt("friend_id"));
        transaction.setCategoryName(resultSet.getString("category_name"));
        transaction.setCategoryIcon(resultSet.getString("category_icon"));
        transaction.setCategoryMoneyType(resultSet.getString("category_money_type"));
        transaction.setSubCategoryName(resultSet.getString("sub_category_name"));
        transaction.setSubCategoryIcon(resultSet.getString("sub_category_icon"));

        return transaction;
    }

    protected Transaction _getTransactionById(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = this._toNormalObject(resultSet);
        this.closeStatement();

        return transaction;
    }

    protected Transaction _toNormalObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setTypeId(resultSet.getInt("type_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at").toLocalDate());
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setLocation(resultSet.getString("location"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setImage(resultSet.getString("image"));
        transaction.setIsNotReported(resultSet.getByte("is_not_reported") == 1);
        transaction.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        transaction.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));
        transaction.setFriendId(resultSet.getInt("friend_id"));

        return transaction;
    }

    @Override
    protected ResultSet getByJoin(String select, String join, String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT " + select + " FROM " + getTable() + " " + join + condition + " ORDER BY transacted_at DESC, id DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    public static boolean isSpent(String moneyType) {
        if (moneyType.equals(CommonConstants.EXPENSE)
                || moneyType.equals(CommonConstants.DEBT_COLLECTION)
                || moneyType.equals(CommonConstants.LOAN)) {
            return true;
        }

        return false;
    }
}
