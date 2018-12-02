package lionstudy.Classes;


public class Account{
    private int ID;
    private int badgetype;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private int online;

  
    
    public Account(){
        username = null;
        badgetype = 1;
        password = null;
        firstname = null;
        lastname = null;
        ID = 0;
        online = 0;
    }
    public Account(String username, String password, String firstname, String lastname, int badgetype, int ID){
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.badgetype = badgetype;
        this.ID = ID;
        online = 0;
    }
    
    public int getID(){
        return ID;
    }
            
            
    public void setBadgetype(int badgetype) {
        this.badgetype = badgetype;
    }
     public void setfirstname(String firstname) {
        this.firstname = firstname;
    }
      public void setlastname(String lastname) {
        this.lastname = lastname;
    }
    public int getBadgetype() {
        return badgetype;
    }
    public String getFirstName(){
        return firstname;
    }
    public String getLastName(){
        return lastname;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
      public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
    @Override
    public String toString() { 
        if(online == 1){
        return String.format(username + "\t" + firstname + "\t" +  lastname + "\t" + "Online"); 
        }
        else{
          return String.format(username + "\t" + firstname + "\t" +  lastname + "\t" + "Offline");  
        }
    } 
}
