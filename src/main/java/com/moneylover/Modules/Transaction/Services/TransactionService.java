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

    public ArrayList<Transaction> listDebts(int walletId) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId);

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

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException, ClassNotFoundException {
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
        int id = this._create(transaction, category.getMoneyType()),
                walletId = transaction.getWalletId();
        this.walletService.setAmount(
                this._getTotalAmount(walletId, '>'),
                this._getTotalAmount(walletId, '<'),
                walletId
        );
        Transaction newTransaction = this.getDetail(id);

        if (TransactionService.isSpent(category.getMoneyType()) && !newTransaction.getIsNotReported()) {
            this._increaseBudgetAmount(newTransaction, false);
        }

        return newTransaction;
    }

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this._create(transactions);

        return true;
    }

    public void update(Transaction transaction, int id) throws SQLException, NotFoundException, ClassNotFoundException {
        Transaction selectedTransaction = this._getTransactionById(id);
        Category oldCategory = this.categoryService.getDetail(selectedTransaction.getCategoryId());

        if (TransactionService.isSpent(oldCategory.getMoneyType()) && !selectedTransaction.getIsNotReported()) {
            this._increaseBudgetAmount(selectedTransaction, true);
        }

        Category newCategory = this.categoryService.getDetail(transaction.getCategoryId());
        this._update(transaction, id, newCategory.getMoneyType());
        int walletId = transaction.getWalletId();
        this.walletService.setAmount(
                this._getTotalAmount(walletId, '>'),
                this._getTotalAmount(walletId, '<'),
                walletId
        );

        if (TransactionService.isSpent(newCategory.getMoneyType())) {
            selectedTransaction = this._getTransactionById(id);

            if (!selectedTransaction.getIsNotReported()) {
                this._increaseBudgetAmount(selectedTransaction, false);
            }
        }
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

    public void delete(int id) throws SQLException, NotFoundException, ClassNotFoundException {
        Transaction transaction = this._getTransactionById(id);
        this.walletService.setAmount(
                this._getTotalAmount(transaction.getWalletId(), '>'),
                this._getTotalAmount(transaction.getWalletId(), '<'),
                transaction.getWalletId()
        );
        this._increaseBudgetAmount(transaction, true);
        this.deleteById(id);
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

    private ArrayList<Transaction> _list(int walletId) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();

        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "friend_id IS NOT NULL"
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    /**
     * @param walletId      int
     * @param operator      represent for expense or income
     * @return total amount
     * @throws SQLException ...
     */
    private float _getTotalAmount(int walletId, char operator) throws SQLException {
        float totalAmount = this._calculate(
                "SUM(amount) AS totalAmount",
                "totalAmount",
                "wallet_id = " + walletId,
                "amount " + operator + " 0"
        );

        return totalAmount;
    }

    private int _create(Transaction transaction, String moneyType) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_not_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        statement.setByte(10, (byte) (transaction.getIsNotReported() ? 1 : 0));

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
            }
        }

        return statement.executeUpdate();
    }

    private void _update(Transaction transaction, int id, String moneyType) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET wallet_id = ?, type_id = ?, category_id = ?, sub_category_id = ?, transacted_at = ?, amount = ?, location = ?, note = ?, image = ?, is_not_reported = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setTimestamp(11, this.getCurrentTime());
        statement.setInt(12, id);
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
