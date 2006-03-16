/*
 * Main.java
 *
 */

package sdtconnector;
import com.opengear.ui.SplashWindow;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.LookUtils;
import net.roydesign.app.Application;
import net.roydesign.mac.MRJAdapter;
import org.jdesktop.swingx.util.OS;
import org.jdesktop.swingx.util.WindowUtils;


public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InvalidPreferencesFormatException {
        final SplashWindow splash = new SplashWindow("images/opengear-splash.png");
        if (!OS.isWindows()) {
            splash.setVisible(true);
        }
        // Initialise the L&F
        try {
            String lafName = System.getProperty("swing.defaultlaf");
            
            if (lafName != null) {
                // Just use whatever the user wanted
            } else if (LookUtils.IS_OS_MAC) {
                lafName = "ch.randelshofer.quaqua.QuaquaLookAndFeel";
            } else if (LookUtils.IS_OS_WINDOWS) {
                lafName = Options.getSystemLookAndFeelClassName();
            } else if (LookUtils.IS_JAVA_6_OR_LATER) {
                lafName = UIManager.getSystemLookAndFeelClassName();
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
            if (lafName != null && lafName.startsWith("com.jgoodies")) {
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
        File cwd = new File(System.getProperty("user.dir"));       
        File preferences = new File(cwd, "preferences.xml");
        
        Preferences userRoot = Preferences.userRoot();
        String prefsPath = "opengear/sdtconnector";
        try {
            if (preferences.exists() && !userRoot.nodeExists(prefsPath)) {
                Preferences.importPreferences(new FileInputStream(preferences));
                userRoot.node(prefsPath).sync();
            }
        } catch (BackingStoreException ex) {
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (InvalidPreferencesFormatException ex) {
        }

        // Close the splash window after everything is up and initialised
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splash.setVisible(false);
            }
        });
        window.setVisible(true);
    }
    
}
