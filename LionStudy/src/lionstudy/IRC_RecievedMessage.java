package lionstudy;

public class IRC_RecievedMessage
{
    public String source;
    public String nick;
    public String command;
    public String target;
    public String content;
    public int line;
    
    IRC_RecievedMessage()
    {
        source ="NULL";
        nick="NULL";
        command="NULL";
        target="NULL";
        content="NULL";
        line=0;
    }
}
