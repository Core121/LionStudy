package lionstudy;

class IRC_MessageParser
{
    static IRC_RecievedMessage recieved(String ircMessage)
    {
        IRC_RecievedMessage msg = new IRC_RecievedMessage();
        int spaceIndex;
        
        
        if(ircMessage.startsWith(":"))
        {
            spaceIndex = ircMessage.indexOf(' ');
            if(spaceIndex >-1)
            {
                //Grabs the source of the message (:IP exampletext or :NICKNAME! exampletext 
                msg.source=ircMessage.substring(1, spaceIndex);
                ircMessage = ircMessage.substring(spaceIndex+1);
                
                //Sees if the source has a nickname associated
                int nickIndex = msg.source.indexOf('!');
                if(nickIndex>-1)
                {
                    msg.nick = msg.source.substring(0,nickIndex);
                }
            }
        }
        //Gets index of next space in string
        spaceIndex = ircMessage.indexOf(' ');
        //If there aren't any, there are no commands in this message
        if(spaceIndex ==-1)
        {
            msg.command ="CLOSE";
            return msg;
        }
        
        
        //Grabs the command line (should be between index 0 and the next space)
        msg.command=ircMessage.substring(0,spaceIndex).toUpperCase();
        //Makes ircMessage the rest of the message after the command
        ircMessage = ircMessage.substring(spaceIndex+1);
       
        //A private message command
        if(msg.command.equalsIgnoreCase("PRIVMSG")||msg.command.equalsIgnoreCase("LOGMSG"))
        {

                spaceIndex = ircMessage.indexOf(' ');
                //The next field should be the target of the message
                msg.source= ircMessage.substring(0, spaceIndex);

                //Makes ircMessage the rest of the message after the target
                ircMessage=ircMessage.substring(spaceIndex+1);

                //If the ircMessage content's begin with a :, set content equal to the message without the :
                if(ircMessage.startsWith(":"))
                {
                    msg.content=ircMessage.substring(1);
                }
                else
                {
                    msg.content=ircMessage;
                }

        }
        
        
        //A quit or join command
        if(msg.command.equals("QUIT")||msg.command.equals("JOIN"))
        {
            if(ircMessage.startsWith(":"))
            {
                msg.content=ircMessage.substring(1);
            }
            else
            {
                msg.content=ircMessage;
            }
        }
        
        
        
        
        
        
        return msg;
    }
}
