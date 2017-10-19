package Session;



import DB_Connect.dbconnect;
import Hashing.PasswordBcrypt;
import UserInterface.MainJFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This listener reacts to timeouts and forces the user to login again
 *
 * The timer is being reset at every AWT event.
 */
public class SessionListener implements AWTEventListener, ActionListener {

    
    private dbconnect db;
    private PasswordBcrypt hashing;
    private SessionVariables global;
    
    public static Timer timer;
    public static int timeoutMinutes; // *60000 to minutes
    public static String getusername;
    /**
     * Creates a new timer and registers itself as action listener.
     */
    public SessionListener(int timeoutMinutex) throws SQLException{
        timeoutMinutes = timeoutMinutex;
        timer = new Timer(timeoutMinutes * 1000, this); 
        this.db = new dbconnect();
        this.hashing = new PasswordBcrypt();
        this.global = new SessionVariables();
        
    }

    /**
     * Invoked when an event is dispatched in the AWT. Simply
     * resets the timer.
     */
    public void eventDispatched(AWTEvent event) {
        timer.restart();
    }

    /**
     * Invoked when an action occurs (i.e. the timer triggers).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
       
        try {
            if(MainJFrame.isLogin==true){
            
                stopTimer();//STOP THE SESSION TIMER
        
        JLabel jPassword = new JLabel("<html><h3><strong><font color=\"red\">ONE TIME PASSWORD ATTEMPT</font></strong></h3></html>");
        JTextField password = new JPasswordField();
        Object[] ob = {jPassword, password};
        //int result = JOptionPane.showConfirmDialog(null, ob, "Please input password for JOptionPane showConfirmDialog", JOptionPane.OK_OPTION);
          String logoutcode = "ACT200";
          String desc = "*SESSION TIME OUT* User "+SessionVariables.userid+" Logged Out";
       
         Object[] options = {"Re-login"};
         int n = JOptionPane.showOptionDialog(null,
                   ob,"Session Time Out",
                   JOptionPane.PLAIN_MESSAGE,
                   JOptionPane.WARNING_MESSAGE,
                   null,
                   options,
                   options[0]);
        
        
        if (n == JOptionPane.OK_OPTION) {
             String plain = password.getText();
            
            
             
             //if Login button is pressed with Empty textfield
             if(plain.isEmpty()){ 
               
               if(global.validActivities.contains(logoutcode)){global.setRealSessionActivities(logoutcode, desc , "YES");}
               else{global.setRealSessionActivities(logoutcode, desc , "NO");}   
               System.out.println("RELOGIN CLICKED: FIELD IS EMPTY");
               System.exit(0);    
               
            }
            
            //if Login button is pressed with Password inside textfield
            else if(!plain.isEmpty()){
                 //check if password correct restart the timer
                if(valid_username(getusername, plain)){
                    System.out.println("RELOGIN CLICKED: FILED NOT EMPTY AND CORRECT PASSWORD");
                    String login = "ACT100";
                    if(global.validActivities.contains(logoutcode)){global.setRealSessionActivities(logoutcode, desc , "YES");}
                    else{global.setRealSessionActivities(logoutcode, desc , "NO");}   
                    startTimer();
                    global.generationSessionID();
                    if(global.validActivities.contains(login)){global.setRealSessionActivities(login, "*RELOGIN SESSION* User "+SessionVariables.userid+" Logged In" , "YES");}
                    else{global.setRealSessionActivities(login, "*RELOGIN SESSION* User "+SessionVariables.userid+" Logged In" , "NO");} 
                    
                }
                //if password incorrect exit
                else{  
                   
                    if(global.validActivities.contains(logoutcode)){global.setRealSessionActivities(logoutcode, desc , "YES");}
                    else{global.setRealSessionActivities(logoutcode, desc , "NO");}   
                    System.out.println("RELOGIN CLICKED: FIELD NOT EMPTY BUT WRONG PASSWORD");
                    System.exit(0);  
                    
                }
            }
           
         
       //if X button is clicked exit 
        }
        else{
           
            if(global.validActivities.contains(logoutcode)){global.setRealSessionActivities(logoutcode, desc , "YES");}
            else{global.setRealSessionActivities(logoutcode, desc , "NO");}    
            System.out.println("CLOSE (X) BUTTON CLICKED");
            System.exit(0);
        }
        
        
            }
        } catch (SQLException ex) {
            Logger.getLogger(SessionListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }

    /**
     * stopps the timer.
     */
    public void stopTimer() throws SQLException {
        timer.removeActionListener(this);
        timer.stop();  
        global.sessionenddatetime(); //GET THE DATE & TIME WHEN SESSION ENDS
        global.sessionEndTime(); //GET STOP SESSION TIME
      }

    /**
     * starts the timer.
     */
    public void startTimer() throws SQLException {
        timer = new Timer(timeoutMinutes * 1000, this);
        timer.start();
        global.sessionStartTime(); //Start Session Time
        global.sessionstartdatetime();
        
    }
    
    private boolean valid_username(String username, String plain) {
     try{           
       PreparedStatement pst = db.connect().prepareStatement("SELECT * FROM Useraccounts WHERE username=?");
       pst.setString(1, username); 
      
       ResultSet validcredential = pst.executeQuery();  
        
       if(validcredential.next()) {  
           String storedhash = validcredential.getString("hpassword");
            if(hashing.checkPassword(plain, storedhash)){
           
           return true; 
           
            }
            
            return false;
           
       }
       else
           return false;   
             
   }
   catch(Exception e){
       e.printStackTrace();
       return false;
   }       
}

    public static Timer getTimer() {
        return timer;
    }
}