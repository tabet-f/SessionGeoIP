package Session;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class CloseOptionPane {

   @SuppressWarnings("serial")
   public static void createAndShowGui() {
      final JLabel label = new JLabel();
      int timerDelay = 1000;
      new Timer(timerDelay , new ActionListener() {
         int timeLeft = 5;

         @Override
         public void actionPerformed(ActionEvent e) {
            if (timeLeft > 0) {
               String s= "<html><center><h2><font color=\"red\">Your Account is Blocked for Security Purposes!</font><br><font color=\"blue\">Closing in " + timeLeft + " seconds</font></h2></center></html>";
               label.setText(s);
               timeLeft--;
            } else {
               ((Timer)e.getSource()).stop();
               Window win = SwingUtilities.getWindowAncestor(label);
               win.setVisible(false);
               System.exit(0);
            }
         }
      }){{setInitialDelay(0);}}.start();
      
      String okText ="Exit Application";
      UIManager.put("OptionPane.okButtonText", okText);
      UIManager.put("OptionPane.minimumSize",new Dimension(500,180)); 
      JOptionPane.showMessageDialog(null, label,"Security Warning",JOptionPane.WARNING_MESSAGE);
      System.exit(0);
     
   }

   
}