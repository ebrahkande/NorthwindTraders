package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // Needed for parameterized queries
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class App {

    // Define the JDBC URL as a constant
    private static final String JDBC_URL = "jdbc:mysql://Localhost:3306/northwind";

    // Global variables to hold credentials obtained from command line
    private static String username;
    private static String password;

    public static void main(String[] args) {

        // Input Validation for Credentials
        if (args.length != 2) {
            System.out.println("Application needs two args to run: A username and a password for the db");
            System.exit(1);
        }

        // Argument Extraction
        username = args[0];
        password = args[1];

        // Main Loop & Menu Setup
        // Use try-with-resources for the Scanner
        try (Scanner scanner = new Scanner(System.in)) {

            boolean running = true;
            while (running) {

                // Display Menu
                System.out.println("\nWhat do you want to do?");
                System.out.println("1) Display all products");
                System.out.println("2) Display all customers");
                System.out.println("3) Display all categories");
                System.out.println("0) Exit");
                System.out.print("Select an option: ");

                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    // Process Choice
                    switch (choice) {
                        case 1:
                            displayAllProducts();
                            break;
                        case 2:
                            displayAllCustomers();
                            break;
                        case 3:
                            displayAllCategories(scanner);
                            break;
                        case 0:
                            running = false;
                            System.out.println("Exiting application. Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid option. Please select 0, 1, 2, or 3.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            }
        }
    }


    private static void displayAllCategories(Scanner scanner) {
        // Query to list category ID and name, ordered by ID
        String query = "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID";
        boolean success = false;

        try (
                Connection theConnection = DriverManager.getConnection(JDBC_URL, username, password);
                Statement statement = theConnection.createStatement();
                ResultSet results = statement.executeQuery(query)
        ) {
            System.out.println("\n--- Available Categories ---");
            System.out.printf("%-4s | %s%n", "ID", "Category Name");
            System.out.println("-------------------------------------");

            // Display all categories
            while (results.next()) {
                success = true;
                int categoryId = results.getInt("CategoryID");
                String categoryName = results.getString("CategoryName");
                System.out.printf("%-4d | %s%n", categoryId, categoryName);
            }

            if (!success) {
                System.out.println("No categories found in the database.");
                return;
            }

            // Prompt the user for a categoryId
            System.out.println("-------------------------------------");
            System.out.print("Enter the Category ID to view products: ");
            if (scanner.hasNextInt()) {
                int selectedId = scanner.nextInt();
                scanner.nextLine();

                // Call method to display products for the selected category
                displayProductsByCategory(selectedId);
            } else {
                System.out.println("Invalid input. Please enter a valid Category ID number.");
                scanner.nextLine();
            }

        } catch (SQLException e) {
            System.err.println("Database Error during category retrieval: " + e.getMessage());
        }
    }


    private static void displayProductsByCategory(int categoryId) {

        // Query to list product details for a specific CategoryID
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE CategoryID = ?";
        boolean success = false;

        try (
                Connection theConnection = DriverManager.getConnection(JDBC_URL, username, password);
                PreparedStatement preparedStatement = theConnection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, categoryId);

            // Execute the query
            try (ResultSet results = preparedStatement.executeQuery()) {

                System.out.println("\n--- Products in Category ID " + categoryId + " ---");
                // Print Table Header
                System.out.printf("%-4s | %-40s | %-8s | %s%n", "Id", "Name", "Price", "Stock");
                System.out.println("----------------------------------------------------------------------");

                // Loop through the results
                while (results.next()) {
                    success = true;
                    // Retrieve the four required product fields
                    int productId = results.getInt("ProductID");
                    String productName = results.getString("ProductName");
                    double unitPrice = results.getDouble("UnitPrice");
                    int unitsInStock = results.getInt("UnitsInStock");

                    // Display Product in Row Format
                    System.out.printf("%-4d | %-40s | $%-7.2f | %d%n",
                            productId,
                            productName,
                            unitPrice,
                            unitsInStock
                    );
                }
                System.out.println("----------------------------------------------------------------------");
            }

            if (!success) {
                System.out.println("No products found for Category ID " + categoryId + ".");
            }

        } catch (SQLException e) {
            System.err.println("Database Error during product display by category: " + e.getMessage());
        }
    }


    private static void displayAllProducts() {
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM products";

        try (
                Connection theConnection = DriverManager.getConnection(JDBC_URL, username, password);
                Statement statement = theConnection.createStatement();
                ResultSet results = statement.executeQuery(query)
        ) {
            System.out.println("\n--- Northwind Products ---");
            System.out.printf("%-4s | %-40s | %-8s | %s%n", "Id", "Name", "Price", "Stock");
            System.out.println("----------------------------------------------------------------------");

            while (results.next()) {
                int productId = results.getInt("ProductID");
                String productName = results.getString("ProductName");
                double unitPrice = results.getDouble("UnitPrice");
                int unitsInStock = results.getInt("UnitsInStock");

                System.out.printf("%-4d | %-40s | $%-7.2f | %d%n",
                        productId,
                        productName,
                        unitPrice,
                        unitsInStock
                );
            }
        } catch (SQLException e) {
            System.err.println("Database Error during product retrieval: " + e.getMessage());
        }
    }

    private static void displayAllCustomers() {
        String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country;";

        Connection theConnection = null;
        Statement statement = null;
        ResultSet results = null;

        try {
            theConnection = DriverManager.getConnection(JDBC_URL, username, password);
            statement = theConnection.createStatement();
            results = statement.executeQuery(query);

            System.out.println("\n--- Northwind Customers (Ordered by Country) ---");
            System.out.printf("%-30s | %-40s | %-15s | %-15s | %-20s%n",
                    "Contact Name", "Company Name", "City", "Country", "Phone");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");

            while (results.next()) {
                String contactName = results.getString("ContactName");
                String companyName = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.printf("%-30s | %-40s | %-15s | %-15s | %-20s%n",
                        contactName, companyName, city, country, phone
                );
            }
        } catch (SQLException e) {
            System.err.println("Database Error during customer retrieval: " + e.getMessage());

        } finally {
            // Close resources in the reverse order of creation
            try {
                if (results != null) results.close();
                if (statement != null) statement.close();
                if (theConnection != null) {
                    theConnection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}