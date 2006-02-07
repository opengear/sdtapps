/*
 * Main.java
 *
 */

package sdtconnector;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.UIManager;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.LookUtils;
import net.roydesign.app.Application;
import net.roydesign.mac.MRJAdapter;
import org.jdesktop.swingx.util.OS;
import org.jdesktop.swingx.util.WindowUtils;


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
        // Initialise the L&F
        try {
            String lafName = System.getProperty("swing.defaultlaf");
            
            if (lafName != null) {
                // Just use whatever the user wanted
            } else if (LookUtils.IS_OS_MAC) {
                lafName = "ch.randelshofer.quaqua.QuaquaLookAndFeel";
            } else if (LookUtils.IS_OS_WINDOWS) {
                lafName = Options.getSystemLookAndFeelClassName();
            } else {
                // Use The Looks L&F on pre-1.6 java on linux, since
                // the pre 1.6 GTK L&F did not work that well
                //lafName = "com.birosoft.liquid.LiquidLookAndFeel";
                lafName = Options.PLASTICXP_NAME;
                //lafName = "com.birosoft.liquid.LiquidLookAndFeel";
                //lafName = "org.jvnet.substance.SubstanceLookAndFeel";
                //lafName = UIManager.getCrossPlatformLookAndFeelClassName();
                //UIManager.setLookAndFeel(lafName);
                //SubstanceLookAndFeel.setCurrentTheme(new SubstanceSteelBlueTheme());
                //SubstanceLookAndFeel.setCurrentGradientPainter(new SpecularGradientPainter());
                //SubstanceLookAndFeel.setCurrentButtonShaper(new ClassicButtonShaper());
            }
            if (lafName.startsWith("com.jgoodies")) {
                Options.setUseNarrowButtons(false);
            }
            if (lafName != null) {
                System.out.println("Using " + lafName + " look & feel");
                UIManager.setLookAndFeel(lafName);
            }
            
        } catch (Exception e) {}
        Application.getInstance().setName("SDTConnector");
        MainWindow window = new MainWindow();
        if (LookUtils.IS_JAVA_5_OR_LATER) {
            window.setLocationByPlatform(true);
        }
        
        window.setVisible(true);
    }
    
}
