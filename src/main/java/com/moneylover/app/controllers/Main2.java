package com.moneylover.app.controllers;

import com.github.javafaker.Faker;


import java.sql.*;
import java.util.ArrayList;

public class Main2 {
    static Faker faker = new Faker();
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        createCountry();
        createBudgets();
        createCategories();
        createSubCategories();
        createTimes();
        createTransactions();
        createTypes();
        createUser();
        createWallets();
    }

    public static void createCountry() throws SQLException, ClassNotFoundException {


        String sql = "INSERT INTO countries(id,name,currency_name,image,created_at,updated_at) VALUES(01,VIETNAMESE,VND,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "name");
        preparedStatement.setString(3, "currencyName");
        preparedStatement.setString(4, "image");
        preparedStatement.setString(5, "createdAt");
        preparedStatement.setString(6, "updatedAt");
        preparedStatement.executeUpdate();
        }

    }

    public static void createUser() throws SQLException, ClassNotFoundException {


        String sqlCountrys = "SELECT id FROM countries";
        String sql = "INSERT INTO wallets(id,country_id ,name,email ,password ,birthday ,phone ,created_at ,updated_at ) VALUES(?,?,?,?,?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "countryId");
        preparedStatement.setString(3, "name");
        preparedStatement.setString(4, "email");
        preparedStatement.setString(5, "password");
        preparedStatement.setString(6, "birthday");
        preparedStatement.setString(7, "phone");
        preparedStatement.setString(8, "createdAt");
        preparedStatement.setString(9, "updatedAt");
        Statement statement = connection.createStatement();
        ResultSet countries = statement.executeQuery(sql);
        // Lay id tu countries de tao user
        }
    }
    public static void createWallets() throws SQLException, ClassNotFoundException {

        String sqlCurrency = "SELECT id FROM countries";
        String sql = "INSERT INTO wallets(id,currency_id,name,inflow,outflow,created_at,updated_at) VALUES(?,?,?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "currencyId");
        preparedStatement.setString(3, "name");
        preparedStatement.setString(4, "inflow");
        preparedStatement.setString(5, "outflow");
        preparedStatement.setString(6, "createdAt");
        preparedStatement.setString(7, "updatedAt");
        Statement statement = connection.createStatement();
        preparedStatement.executeUpdate();
        ResultSet countries = statement.executeQuery(sql);
        }
    }
    public static void createTypes() throws SQLException, ClassNotFoundException {

        String sql = "INSERT INTO types(id,money_type,name,created_at,updated_at) VALUES(?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "moneyType");
        preparedStatement.setString(3, "name");
        preparedStatement.setString(4, "createdAt");
        preparedStatement.setString(5, "updatedAt");
        preparedStatement.executeUpdate();
        }
    }
    public static void createCategories() throws SQLException, ClassNotFoundException {
        String sqlType = "SELECT id FROM types";
        String sql = "INSERT INTO categories(id,type_id,money_type,name,image,created_at,updated_at) VALUES(?,?,?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "typeId");
        preparedStatement.setString(3, "moneyType");
        preparedStatement.setString(4, "name");
        preparedStatement.setString(5, "image");
        preparedStatement.setString(6, "createdAt");
        preparedStatement.setString(7, "updatedAt");
        Statement statement = connection.createStatement();
        preparedStatement.executeUpdate();
        ResultSet types = statement.executeQuery(sql);
        }
    }
    public static void createSubCategories() throws SQLException, ClassNotFoundException {
        String sqlType = "SELECT id FROM types";
        String sqlCategory = "SELECT id FROM categories";
        String sql = "INSERT INTO sub_categories(id,type_id,category_id,money_type,name,image,created_at,updated_at) VALUES(?,?,?,?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "typeId");
        preparedStatement.setString(3, "categoryId");
        preparedStatement.setString(4, "moneyType");
        preparedStatement.setString(5, "name");
        preparedStatement.setString(6, "image");
        preparedStatement.setString(7, "createdAt");
        preparedStatement.setString(8, "updatedAt");
        Statement statement = connection.createStatement();
        preparedStatement.executeUpdate();
        ResultSet categories = statement.executeQuery(sql);
        ResultSet types = statement.executeQuery(sql);
        }
    }
    public static void createTimes() throws SQLException, ClassNotFoundException {

        String sql = "INSERT INTO times(id,month,year,created_at,updated_at) VALUES(?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "month");
        preparedStatement.setString(3, "year");
        preparedStatement.setString(4, "createdAt");
        preparedStatement.setString(5, "updatedAt");
        preparedStatement.executeUpdate();
        }
    }
    public static void createTransactions() throws SQLException, ClassNotFoundException {
        String sqlWallet = "SELECT id FROM wallets";
        String sqlUser = "SELECT id FROM users";
        String sqlType = "SELECT id FROM types";
        String sqlCategory = "SELECT id FROM categories";
        String sqlSubCategory = "SELECT id FROM sub_categories";
        String sqlTime = "SELECT id FROM times";
        String sql = "INSERT INTO transactions(id,wallet_id,user_id,type_id,category_id,sub_category_id,time_id,transacted_at,amount,image,location,note,is_reported,created_at,updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for ( int i=1 ; i<=20 ; i++){
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "id");
        preparedStatement.setString(2, "walletId");
        preparedStatement.setString(3, "userId");
        preparedStatement.setString(4, "typeId");
        preparedStatement.setString(5, "categoryId");
        preparedStatement.setString(6, "timeId");
        preparedStatement.setString(7, "transactedAt");
        preparedStatement.setString(8, "amount");
        preparedStatement.setString(9, "image");
        preparedStatement.setString(10, "location");
        preparedStatement.setString(11, "note");
        preparedStatement.setString(12, "reported");
        preparedStatement.setString(13, "createdAt");
        preparedStatement.setString(14, "updatedAt");
        Statement statement = connection.createStatement();
        preparedStatement.executeUpdate();
        ResultSet wallets = statement.executeQuery(sql);
        ResultSet users = statement.executeQuery(sql);
        ResultSet types = statement.executeQuery(sql);
        ResultSet categories = statement.executeQuery(sql);
        ResultSet sub_categories = statement.executeQuery(sql);
        ResultSet times = statement.executeQuery(sql);
        }
    }
    public static void createBudgets() throws SQLException, ClassNotFoundException {
       String sqlWallet = "SELECT id FROM wallets";
       String sqlBudgetable = "SELECT id FROM budgets";


       String sql = "INSERT INTO budgets(id,wallet_id,budgetable_id,budgetable_type,started_at,ended_at,amount,spent_amount,created_at,updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
       for ( int i=1 ; i<=20 ; i++){
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "id");
            preparedStatement.setString(2, "walletId");
            preparedStatement.setString(3, "budgetableId");
            preparedStatement.setString(4, "budgetableType");
            preparedStatement.setString(5, "startedAt");
            preparedStatement.setString(6, "endedAt");
            preparedStatement.setString(7, "amount");
            preparedStatement.setString(8, "spentAmount");
            preparedStatement.setString(9, "createdAt");
            preparedStatement.setString(10, "updatedAt");
           Statement statement = connection.createStatement();
           preparedStatement.executeUpdate();
           ResultSet wallets = statement.executeQuery(sql);
           ResultSet budgets = statement.executeQuery(sql);
        }


    }



    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String username = "as";
        String password = "chien123456";
        String url = "jdbc:sqlserver://A-PC:1433;databaseName=moneylover";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        return DriverManager.getConnection(url, username, password);
    }
}


