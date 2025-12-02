package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {

        if (args.length != 2) {

            System.out.println("Application needs two args to run: A username and a password for the db");
            System.exit(1);
        }

        // Get the username and password from args[]
        String username = args[0];
        String password = args[1];


        try {
            //1. create the connection (kinda like opening MySQL Workbench)
            Connection theConnection = DriverManager.getConnection("jdbc:mysql://Localhost:3306/northwind", username, password);
            System.out.println("connected to the db");

            // Create a statement object to send the query
            Statement statement = theConnection.createStatement();

            // Execute the query and get the result set
            String query = "SELECT ProductName FROM products";

            ResultSet resultSet = statement.executeQuery(query);

            // Loop through the results and print each product name
            while (resultSet.next()) {
                String productName = resultSet.getString("ProductName");
                System.out.println(productName);

            }

            // Close the connection
            theConnection.close();


        } catch (SQLException e) {
            System.out.println("Something went wrong" + e);
        }

    }

}