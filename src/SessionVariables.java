/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Session;


import DB_Connect.dbconnect;
import UserInterface.MainJFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Fadi Tabet
 */
public class SessionVariables {
    
    private dbconnect db;
    
    //ACCOUNT ID & IDENTITY ID 
    public static String userid;  //Contains User Account ID
    public static String identityid; //Contains Person Id (EMP OR CUST) IDS
    private String role_id;
    //PUBLIC & PRIVATE IP
    public static String userpublicip;  //Store Public IP
    public static String userprivateip; //Store Private IP
    public static String fakeGenIP; //Store FAKE GEN IP
     private boolean isRead = false;
    //DefenseLog
    public static String oldip="";
    public static String oldusername="";
    public static String recieverNAME;
    
    
    
    //Session Start/Exit --> DATE & TIME
    public static String sessionBeginTime; //Session Start DATE & TIME
    public static String sessionKillTime;  //Session End DATE & TIME
    
    //Session clock
    public static String sessionID;
    public static int session_timeout; //set the session timeout
    public static long sessionStart; //Session Start Time in MS
    public static long sessionEnds; //Session End Time in MS
    public static String sessionDurationO; //Session Old Duration
    public static String sessionDuration; //Alive Session Duration
   
    //Valid Actions
    public static List<String> validActivities =  new ArrayList<String>();
    
    
    //Used to Generate Bank & Transaction Unique Id's
     private static final long LIMIT = 100000000000L;
     private static long last = 0;
     
     //Transaction ID
     public static String TranID;
     
     //IP REGEX
     static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
     static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
     
     
    /**
     *
     */
    public SessionVariables() throws SQLException{
         this.db = new dbconnect();
         generationSessionID();
      
    }
    
    //METHODS TO START WITH THE BEGINING OF THE APPLICATION
    public void Starts() throws UnknownHostException {
        
        getpublicip();
        getprivateip();
               
}
    //GET PRIVATE IP ADDRESS
    private static void getprivateip() throws UnknownHostException{
        userprivateip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Private IP : "+userprivateip);
       
    }

    //GET PUBLIC IP ADDRESS
    private static void getpublicip(){
     URL connection = null;
        try {
            connection = new URL("http://checkip.amazonaws.com/");
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
         URLConnection conx = null;
        try {
            conx = connection.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
         String publicip = null;
         BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(conx.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            publicip = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
       userpublicip = publicip;
       System.out.println("Public IP : "+publicip);
        //JOptionPane.showMessageDialog(null, "You Public IP Address: "+publicip+" IS NOT ON Remote Database Access Hosts List", "Warning",JOptionPane.WARNING_MESSAGE);
       
}
    //SESSION BEIGN DATE & TIME 
    public void sessionstartdatetime(){
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        sessionBeginTime = datetimeFormat.format(date); //2014/08/06 15:59:48
        
        
    }
    
    //SESSION ENDS DATE & TIME 
    public void sessionenddatetime(){
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        sessionKillTime = datetimeFormat.format(date); //2014/08/06 15:59:48
             
    }
    
    //GET IN MS SESSION START TIME
    public  void sessionStartTime(){
        sessionStart = System.currentTimeMillis();
   
    }
    //GET IN MS SESSION END TIME
    public void sessionEndTime() throws SQLException{
        sessionEnds = System.currentTimeMillis();
        sessionDuration(); //Call Session Duration to get Session Total Duration
        insertSession(); 
        
        
    }
    //CALCULATE THE SESSION DURATION TIME ELAPSED IN SECONDS 
    public void sessionDuration(){
        sessionDuration = convertSecondsToHMmSs((sessionEnds - sessionStart)/1000);
        System.out.println("*********SESSION CAPTURED DURATION: "+sessionDuration+" *********");
        sessionDurationO = sessionDuration;
        int s = 0;
        sessionDuration = String.valueOf(s);
    }
    
    //Convert millisecond to hh:mm:ss
    public static String convertSecondsToHMmSs(long seconds) {
    long s = seconds % 60;
    long m = (seconds / 60) % 60;
    long h = (seconds / (60 * 60)) % 24;
    return String.format("%02d:%02d:%02d", h,m,s);
}
    
    //GET THE CURRENT TIME hh:mm:ss
      public String getcurrentime(){
          DateFormat datetimeFormat = new SimpleDateFormat("HH:mm:ss");
          Date date = new Date();
          String currentime= datetimeFormat.format(date); //set real action time in SessionActivities class
          return currentime;
    }
      
    //GENERATE ID FOR THE SESSION
    public String generationSessionID() throws SQLException{
            
            PreparedStatement pst = db.connect().prepareStatement("SELECT count(*) FROM Sessions");
            ResultSet previd = pst.executeQuery();
            previd.next();
            int idb = previd.getInt(1);
            int idnumb = idb + 1;
            String sessionid ="SES-"+idnumb; 
            setSessionID(sessionid);
             System.out.println("SESSION ID GENERATED: "+getSessionID());
            return sessionid;
           
                    
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    //Defense log to protect the outer part
    //SAME IP ATTACKING SAME USER
   public void insertDefenseLog(String date, String attackerIP , int attempt){
       
       try {
            
            //Insert Query
            String insertquery = "insert into DefenseLog (time, date, username_attacked ,attacker_ip, attacking_attempts)"
            + "values(?, ?, ?, ?, ?)";
            PreparedStatement inst = db.connect().prepareStatement(insertquery);
            
            inst.setString(1, getcurrentime());
            inst.setString(2, date);
            inst.setString(3, oldusername);
            inst.setString(4, attackerIP);
            inst.setInt(5, attempt);
            
            inst.executeUpdate();
            
            System.out.println("*********DEFENSE LOG CAPTURED*********");
            
            //SENDING ALERT
            sendAlert(oldusername, date, attackerIP);
            
        } catch (SQLException ex) {
            Logger.getLogger(SessionVariables.class.getName()).log(Level.SEVERE, null, ex);
        }
      
       
       
   }
    
    
    //INSERT SESSION INFO INTO SESSION TABLE IN DB
    public void insertSession() throws SQLException{
            
            
            try {
            
            //Insert Query
            String insertquery = "insert into Sessions (session_id, account_id, owner_id ,start_datentime, end_datentime, "
            + "session_duration, fake_gen_IP , public_ip, private_ip) "
            + "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement inst = db.connect().prepareStatement(insertquery);
            
            inst.setString(1, getSessionID());
            inst.setString(2, userid);
            inst.setString(3, identityid);
            inst.setString(4, sessionBeginTime);
            inst.setString(5, sessionKillTime);
            inst.setString(6, sessionDurationO);
            inst.setString(7, fakeGenIP);
            inst.setString(8, userpublicip);
            inst.setString(9, userprivateip);
            inst.executeUpdate();
            
            
           
            
        } catch (SQLException ex) {
            Logger.getLogger(SessionVariables.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    //Populate the ArrayList with Activity Info for EACH activity
    public void setRealSessionActivities(String btncode, String desc, String isAuthorized) throws SQLException{
           
             //Insert Query
            String insertquery = "insert into Session_Activities (session_id, activity_code, activity_time ,"
            + "activity_description, doneby, "
            + "identity_id, isAuthorized) "
            + "values(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement inst = db.connect().prepareStatement(insertquery);
            
            
            inst.setString(1, getSessionID());
            inst.setString(2, btncode);
            inst.setString(3, getcurrentime());
            inst.setString(4, desc);
            inst.setString(5, userid);
            inst.setString(6, identityid);
            inst.setString(7, isAuthorized);
           
            inst.executeUpdate();
            
             if(isAuthorized.equalsIgnoreCase("YES")){
             System.out.println(btncode+" Captured @AUTH: YES || DESC: "+desc);
             return;
             }
             else {  System.out.println(btncode+" Captured @AUTH: NO || DESC: "+desc); autoBlock(userid); }
    
    }
    
    
    
    //Block User Account
    public void autoBlock (String userid){
        try {
             String updatequery = "UPDATE Useraccounts SET account_status = ?"
                                 + " WHERE account_id =?";
             PreparedStatement inst;
             inst = db.connect().prepareStatement(updatequery);
             inst.setString(1, "Disabled");
             inst.setString(2, userid);
             inst.executeUpdate(); 
             setRealSessionActivities("ACT200", "***SELF DEFENSE AUTO BLOCK*** Account "+SessionVariables.userid+" Blocked & Logged Out", "YES");
             sessionenddatetime(); //GET THE DATE & TIME WHEN SESSION ENDS
             sessionEndTime(); //GET STOP SESSION TIME
             CloseOptionPane.createAndShowGui();
             
            
        } catch (SQLException ex) {
            Logger.getLogger(SessionVariables.class.getName()).log(Level.SEVERE, null, ex);
        }
             
    }
    
    public void getPermittedActivities(String rolename) throws SQLException{
         
       PreparedStatement x = db.connect().prepareStatement("SELECT role_id FROM Roles WHERE role_name =?");
       x.setString(1, rolename); 
       ResultSet rid = x.executeQuery(); 
       if (rid.next()){
       role_id = rid.getString("role_id");
       }
        
       PreparedStatement st = db.connect().prepareStatement("SELECT activity_code FROM Permitted_Activities WHERE belongs_to_role=?");
       st.setString(1, role_id); 
       ResultSet validrole = st.executeQuery(); 
       while (validrole.next()){
               
           String permittedcodes = validrole.getString("activity_code");
           validActivities.add(permittedcodes);
          
           
       }
        
    }
    /**TRANSACTION & BANK ACCOUNT VARIABLES METHODS **/
    
  //Number Unique No Generator
  public static long RandomGenerator(){
  // 11 digits.
  long id = System.currentTimeMillis() % LIMIT;
  if ( id <= last ) {
    id = (last + 1) % LIMIT;
  }
  return last = id;
}
  
public void TransactionNumberGen() throws SQLException{
                PreparedStatement pst = db.connect().prepareStatement("SELECT count(*) FROM Transactions");
                ResultSet previd = pst.executeQuery();
                previd.next();
                long idb = previd.getInt(1);
                long three = Math.round(Math.random()*(999 - 100) + 100);
                long comb = (idb+three);
                String tran_id = ("TAN"+(comb+RandomGenerator()));
                TranID = tran_id;
} 
    
//Switch Inbox Color ICon

         public boolean InboxColor() throws SQLException{
               
                PreparedStatement pst = db.connect().prepareStatement("SELECT message_state from Messages where message_reciever_id=?");
                pst.setString(1,SessionVariables.userid);    
                ResultSet previd = pst.executeQuery();
                while(previd.next()){
                    String state = previd.getString("message_state");
                    while(state.equals("UNREAD")){
                       
                        return isRead=false;
                        
                    }
                    
                }
                
                return isRead=true;
              } 

           
         //Generate Unique Random Ip
         public String GenerateIP() throws SQLException{
             
            //Creating Account ID
            PreparedStatement pst = db.connect().prepareStatement("SELECT count(*) FROM Useraccounts");
            ResultSet previd = pst.executeQuery();
            previd.next();
            int idb = previd.getInt(1);
            int idnumb = idb +1;
            
                Random r = new Random();
                String ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + (r.nextInt(256)+idnumb);
                
                 return ip;
            }
         
         
         //Validate IP
         public boolean CheckIP(String ip){
             
             return IPV4_PATTERN.matcher(ip).matches() ;
         }
         
           
         public void sendAlert(String useraccount, String date, String ip) throws SQLException{
             
            //Creating Account ID
            PreparedStatement pst = db.connect().prepareStatement("SELECT * FROM Useraccounts where username=?");
            pst.setString(1, useraccount);
            ResultSet previd = pst.executeQuery();
            if(previd.next()){
            String owner_id = previd.getString("owner_id");
            String userid = previd.getString("account_id");
            char input = String.valueOf(owner_id).charAt(0);
            
            if ( input=='E'){
               
            PreparedStatement pste = db.connect().prepareStatement("SELECT * FROM Employees where emp_id=?");
            pste.setString(1, owner_id);
            ResultSet previde = pste.executeQuery();
            if(previde.next()){
                String fn = previde.getString("first_name");
                String ln = previde.getString("last_name");
                recieverNAME = fn+" "+ln; 
                        
            }
            }//end employee
            
            if ( input=='C'){
               
            PreparedStatement pste = db.connect().prepareStatement("SELECT * FROM Customers where cust_id=?");
            pste.setString(1, owner_id);
            ResultSet previde = pste.executeQuery();
            if(previde.next()){
                String fn = previde.getString("first_name");
                String ln = previde.getString("last_name");
                recieverNAME = fn+" "+ln; 
                        
            }
            }
            
             
             String senderid = "ADB";
             String sendername = "Defense Bot";
             String messagesbj = "SECURITY ALERT: YOUR ACCOUNT IS IN DANGER!";
             String messagebody = "Someone is Trying to Access Your Account and Has Your Username & Password - Attacker IP Address is "+ip;
             //Insert Query
                String custquery = "insert into Messages (message_sender_id, message_reciever_id, message_sender_name ,message_reciever_name ,message_subject, "
                        + "message_body, message_state, date, time) "
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement inst = db.connect().prepareStatement(custquery);
               
                inst.setString(1, senderid);
                inst.setString(2, userid);
                inst.setString(3, sendername);
                inst.setString(4, recieverNAME);
                inst.setString(5, messagesbj);
                inst.setString(6, messagebody);
                inst.setString(7, "UNREAD");
                inst.setString(8, date);
                inst.setString(9, getcurrentime());
                
                inst.executeUpdate(); 
                 System.out.println("*********DEFENSE BOT: ALERT SENT*********");
         }
             }
}


  
               
 





