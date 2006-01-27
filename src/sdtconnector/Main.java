/*
 * Main.java
 *
 */

package sdtconnector;
import javax.swing.JFrame;
import javax.swing.UIManager;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.LookUtils;
import org.jdesktop.swingx.util.OS;


/**
 *
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            String lafName = System.getProperty("swing.defaultlaf");
            System.out.println("Default laf = " + lafName);
            if (lafName != null) {
                // Just use whatever the user wanted
            } else if (LookUtils.IS_JAVA_6_OR_LATER ||LookUtils.IS_OS_MAC) {
                lafName = UIManager.getSystemLookAndFeelClassName();
            } else if (LookUtils.IS_OS_WINDOWS) {
                lafName = Options.getSystemLookAndFeelClassName();            
            } else {
                // Use The Looks L&F on pre-1.6 java on linux, since
                // the pre 1.6 GTK L&F did not work that well
                //lafName = "com.birosoft.liquid.LiquidLookAndFeel";
                lafName = Options.PLASTICXP_NAME;
                //lafName = UIManager.getCrossPlatformLookAndFeelClassName();
            }
            
            System.out.println("Using " + lafName + " look & feel");
            UIManager.setLookAndFeel(lafName);
        } catch (Exception e) {}
        if (OS.isMacOSX()) {
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    "Opengear SDT Connector");
        }
        new MainWindow().setVisible(true);
    }
    
}
