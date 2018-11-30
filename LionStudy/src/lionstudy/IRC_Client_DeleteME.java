//package basicirc_client;
//
//import java.util.Scanner;
//
//public class BasicIRC_Client
//{
//    
//            
//    public static void main(String[] args)
//    {
//        
//        String server = "localhost";
//        int port = 6543;
//        //This will ned to be user account name
////        String nickname = "HappyBoi117";
//        //This will be the channel or private message to send the message
//        String channel = "Test1";
//        //This will be the message to send
////        String message = "Test message";
//        int quitControl=0;
//            do{
//                BasicIRC_Log chatSocket = new BasicIRC_Log(server,port,channel);
//                System.out.println("LOG FOR FILE Test1.txt");
//                System.out.println("=============================");
//                chatSocket.IRC_getLog("Test1");
//                System.out.println("=============================");
//                System.out.println();
//                System.out.println();
//                System.out.println("LOG FOR FILE Test2.txt");
//                System.out.println("=============================");
//                chatSocket.IRC_getLog("Test2");
//                System.out.println("=============================");
//                System.out.println();
//                System.out.println();
//                System.out.println("LOG FOR FILE #MagicLandOfDisney.txt");
//                System.out.println("=============================");
//                chatSocket.IRC_getLog("#MagicLandOfDisney");
//                System.out.println("=============================");
//                System.out.println();
//                System.out.println();
//                //chatSocket.IRC_nick(nickname);
//                //chatSocket.IRC_user("Brad_Naugle", "null", "null", "real name");
//                //chatSocket.IRC_channelJoin(channel);
//
//            Scanner scanner = new Scanner(System.in);
//            System.out.print("Enter a number to quit: ");
//            quitControl=scanner.nextInt();
//           
//        }while(quitControl==0);
//    
//    }
//    
//}
