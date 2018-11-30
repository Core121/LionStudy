package lionstudy;

public class RecievedMessage
{
    public String source;
    public String nick;
    public String command;
    public String target;
    public String content;
    public int line;
    
    RecievedMessage()
    {
        source ="NULL";
        nick="NULL";
        command="NULL";
        target="NULL";
        content="NULL";
        line=0;
    }
}
