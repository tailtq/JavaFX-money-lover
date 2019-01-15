package com.moneylover.Infrastructure.Services;

import java.sql.*;

abstract public class BaseService {
    protected Connection connection;

    protected Statement statement;

    protected PreparedStatement preparedStatement;

    public BaseService() throws SQLException, ClassNotFoundException {
        connection = connectToDB();
    }

    abstract protected Object toObject(ResultSet resultSet) throws SQLException;

    abstract protected String getTable();

    private Connection connectToDB() throws ClassNotFoundException, SQLException {
        String username = "sa";
        String password = "Abcd@1234";
        String url = "jdbc:sqlserver://localhost:1433;databaseName=moneylover";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection = DriverManager.getConnection(url, username, password);

        return connection;
    }

    protected Statement getStatement() throws SQLException {
        statement = connection.createStatement();

        return statement;
    }

    protected PreparedStatement getPreparedStatement(String statementString) throws SQLException {
        preparedStatement = connection.prepareStatement(statementString);

        return preparedStatement;
    }

    protected PreparedStatement getPreparedStatement(String statementString, int option) throws SQLException {
        preparedStatement = connection.prepareStatement(statementString, option);

        return preparedStatement;
    }

    protected void closeStatement() throws SQLException {
        statement.close();
    }

    protected void closePreparedStatement() throws SQLException {
        preparedStatement.close();
    }

    protected ResultSet get(String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT * FROM " + getTable() + " " + condition + " ORDER BY created_at DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    protected ResultSet getByJoin(String join, String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT * FROM " + getTable() + " " + join + " " + condition + " ORDER BY created_at DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    protected ResultSet _getById(int id) throws SQLException {
        String query = "SELECT * FROM " + getTable() + " WHERE id = " + id;
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    protected ResultSet _getDetailBy(String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT TOP 1 * FROM " + getTable() + condition;
        statement = getStatement();

        return statement.executeQuery(query);
    }

    protected boolean deleteBy(String table, String conditions) throws SQLException {
        String statementString = "DELETE FROM " + table + " WHERE " + conditions;
        statement = getStatement();
        boolean result = statement.execute(statementString);

        closeStatement();

        return result;
    }

    public boolean deleteById(int id) throws SQLException {
        String statementString = "DELETE FROM " + getTable() + " WHERE id = " + id;
        statement = getStatement();
        boolean result = statement.execute(statementString);

        closeStatement();

        return result;
    }

    private String handleConditions(String[] conditions) {
        String condition = "";

        if (conditions.length > 0) {
            condition = " WHERE ";
            int quantity = conditions.length;

            for (int i = 0; i < quantity; i++) {
                condition += conditions[i];

                if (i != quantity - 1) {
                    condition += " AND ";
                }
            }
        }

        return condition;
    }
}
