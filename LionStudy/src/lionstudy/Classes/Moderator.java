package lionstudy.Classes;


public class Moderator extends Account {
    public void changeAcctType(Account acct, int newBadge){
        acct.setBadgetype(newBadge);
    }
}
