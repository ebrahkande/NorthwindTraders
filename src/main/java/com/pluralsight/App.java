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

            // Execute the query
            String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM products";

            // Execute the query and get the result set
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("\n--- Northwind Traders Product Inventory ---");

            // Print Table Header
            // Use String.format or printf to create aligned columns.
            System.out.printf("%-4s | %-40s | %-8s | %s%n", "Id", "Name", "Price", "Stock");
            System.out.println("----------------------------------------------------------------------");

            // Loop through the results and print each product's details
            while (resultSet.next()) {

                int productId = resultSet.getInt("ProductID");
                String productName = resultSet.getString("ProductName");
                double unitPrice = resultSet.getDouble("UnitPrice");
                int unitsInStock = resultSet.getInt("UnitsInStock");

                // Print the data in a single formatted row.
                System.out.printf("%-4d | %-40s | $%-7.2f | %d%n",
                        productId,
                        productName,
                        unitPrice,
                        unitsInStock
                );
            }

            // Close the connection
            // to ensure resources close automatically, even on exceptions.
            theConnection.close();


        } catch (SQLException e) {
            System.out.println("Something went wrong with the database connection or query: " + e.getMessage());
        }

    }

}