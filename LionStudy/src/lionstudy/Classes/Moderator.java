package lionstudy.Classes;


public class Moderator extends Account {
    public void changeAcctType(Account acct, int newBadge){
        acct.badgetype = newBadge;
    }
}
