package lionstudy;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import lionstudy.Classes.*;

// Pi address: 76.180.26.194
// Pi port: 3306
//Before Using database please ask Corey to turn on portforwarding!!
public class ServiceDispatcher {

    private static final String USERNAME = "Lion";
    private static final String PASSWORD = "LionStudy!?";
    private static final String CONN_STRING = "jdbc:mysql://76.180.26.194:3306/LionStudy";
    ResultSet rs = null;
    Statement stmt = null;

    //Note that this function should be run at the beginning of the application for TESTING purposes, not actually needed to run the application but should be used to test the database is responding
    public void TestConnect() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement myStmt = myConn.createStatement();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to login server.\n Returned error: " + e, "Could Not Connect", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    //returns true if login is successful, takes username and password as parameters
    public boolean Login(String username, String password) {
        boolean login = false;
        int ID = 0,  badge;      
        String firstname, lastname;
        username = username.toLowerCase();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ID = rs.getInt("ID");
                firstname = rs.getString("first");
                lastname = rs.getString("last");
                badge = rs.getInt("badge");
                login = true;
                pstmt = myConn.prepareStatement("UPDATE Users SET online = '1' WHERE ID = ?");
                pstmt.setInt(1, ID);
                pstmt.executeUpdate();
                CurrentUser.setID(ID);
                CurrentUser.setUsername(username);
                CurrentUser.setFirstname(firstname);
                CurrentUser.setLastname(lastname);
                CurrentUser.setBadgetype(badge);
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return login;
    }
    
    public void logout(){
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("UPDATE Users SET online = '0' WHERE ID = ?");
            pstmt.setInt(1, CurrentUser.getID());
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Returns an ArrayList of All Users in the form of Accounts, note that passwords are set to nothing as we do not need them once the application has been logged into
    public ArrayList<Account> GetAllUsers() {
        ArrayList<Account> AccountList = new ArrayList<Account>();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Users");
            while (rs.next()) {
                String username = rs.getString("username");
                String password = "";
                String firstName = rs.getString("first");
                String lastName = rs.getString("last");
                int badgetype = rs.getInt("badge");
                int ID = rs.getInt("ID");
                int online = rs.getInt("online");
                Account temp = new Account(username, password, firstName, lastName, badgetype, ID);
                temp.setOnline(online);
                AccountList.add(temp);
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return AccountList;
    }

    //Creates a user on the User database, must pass an Account as Parameter
    public void CreateUser(Account account) {
        account.username = account.username.toLowerCase();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("INSERT INTO `Users`(username,password,first,last,badge) VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getFirstName());
            pstmt.setString(4, account.getLastName());
            pstmt.setInt(5, account.getBadgetype());
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "That username already exists, please login", "Sign Up Failure", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Creates a class on the class database, takes a classname in string form as a parameter. Database auto-generates ID for it.
    public void CreateClass(String className) {
        className = className.toUpperCase();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("INSERT INTO `Classes`(class) VALUES (?)");
            pstmt.setString(1, className);
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Returns an ArrayList of All class names in the form of Strings
    public ArrayList<String> GetAllClasses() {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Classes");
            while (rs.next()) {
                String classname = rs.getString("class");
                classes.add(classname);
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return classes;
    }
    
    //Adds a class to the current user, taking the classname as a parameter
    public void AddClasstoUser(String classname) {
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("INSERT INTO `UserClasses`(Username,ClassName) VALUES (?, ?)");
            pstmt.setString(1, CurrentUser.getUsername());
            pstmt.setString(2, classname);
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void DeleteClassfromUser(String classname) {
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("DELETE FROM `UserClasses` WHERE Username = ? AND ClassName = ?");
            pstmt.setString(1, CurrentUser.getUsername());
            pstmt.setString(2, classname);
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void AddUserContact(String ContactUsername) {
        try {
            ContactUsername = ContactUsername.toLowerCase();
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("INSERT INTO `Contacts`(username, ContactUsername) VALUES (?, ?)");
            pstmt.setString(1, CurrentUser.getUsername());
            pstmt.setString(2, ContactUsername);
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //Deletes a users contact
    public void DeleteUserContact(String ContactUsername) {
        try {
            ContactUsername = ContactUsername.toLowerCase();
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("DELETE FROM `Contacts` WHERE username = ? AND ContactUsername = ?");
            pstmt.setString(1, CurrentUser.getUsername());
            pstmt.setString(2, ContactUsername);
            pstmt.executeUpdate();
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Get all of users current contacts in the form of an arraylist of accounts
    public ArrayList<Account> GetAllUsersContacts() {
        ArrayList<Account> Contacts = new ArrayList<Account>();
        try {
            String usernamec = "";
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("SELECT * FROM Contacts WHERE username = ?");
            pstmt.setString(1, CurrentUser.getUsername());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                usernamec = rs.getString("ContactUsername");
                System.out.println(usernamec);
                pstmt = myConn.prepareStatement("SELECT * FROM Users WHERE username = ?");
                pstmt.setString(1, usernamec);
                ResultSet rst = pstmt.executeQuery();
                while (rst.next()) {
                    String username = rst.getString("username");
                    String password = "";
                    String firstName = rst.getString("first");
                    String lastName = rst.getString("last");
                    int badgetype = rst.getInt("badge");
                    int ID = rst.getInt("ID");
                    int online = rst.getInt("online");
                    Account temp = new Account(username, password, firstName, lastName, badgetype, ID);
                    temp.setOnline(online);
                    Contacts.add(temp);
                }
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return Contacts;
    }
    
    //Get all of users currently enrolled classes
    public ArrayList<String> GetAllUsersClasses() {
        ArrayList<String> Classes = new ArrayList<String>();
        try {
            Connection myConn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            stmt = myConn.createStatement();
            PreparedStatement pstmt = myConn.prepareStatement("SELECT * FROM UserClasses WHERE Username = ?");
            pstmt.setString(1, CurrentUser.getUsername());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String classname = rs.getString("ClassName");
                Classes.add(classname);
                }
            myConn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return Classes;
    }
    
}
