package lionstudy;

public class ChatDispatcher {

    private String IP = "halcyon.il.us.dal.net";// To implement when static IP addressed
    private int livePort = 6666;
    private int logPort = 6667;

    //This needs to be the user's username
    private String username;
    //This will need to be user's first name
    private String nickname;
    //This will be the channel or private message to send the message
    private String channel;
    //This will be the message to send
    private String message;

    IRC_LiveSocket liveListen;

    public ChatDispatcher(String fName, String uName, String channel) {
        nickname = fName;
        username = uName;
        this.channel = channel;
    }

    public void getLog() {
        IRC_LogSocket logListen = new IRC_LogSocket(IP, logPort, channel);
        //logListen.IRC_getLog(channel);
    }

    public void getLive() {
//        liveListen = new IRC_LiveSocket(IP,logPort);
        liveListen.IRC_nick(nickname);
        liveListen.IRC_user(username, "null", "null", "real name");
        liveListen.IRC_channelJoin(channel);

        liveListen.IRC_privMSG(channel, "Hello world, this is lionstudy test");

    }

    public void sendMsg(String message) {
        liveListen.IRC_privMSG(channel, message);
    }

}
