/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lionstudy.Classes;

/**
 *
 * @author Corey
 */
public class CurrentUser {
    public static int ID;
    public static int badgetype;
    public static String username;
    public static String password;
    public static String firstname;
    public static String lastname;
    
    public CurrentUser(String username, String password, String firstname, String lastname, int badgetype, int ID) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.badgetype = badgetype;
        this.ID = ID;
    }

    
    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        CurrentUser.ID = ID;
    }

    public static int getBadgetype() {
        return badgetype;
    }

    public static void setBadgetype(int badgetype) {
        CurrentUser.badgetype = badgetype;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CurrentUser.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        CurrentUser.password = password;
    }

    public static String getFirstname() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        CurrentUser.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        CurrentUser.lastname = lastname;
    }
    
}

