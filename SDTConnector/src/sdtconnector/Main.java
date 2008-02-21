/*
 * Main.java
 *
 */

package sdtconnector;
import com.opengear.ui.SplashWindow;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
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
import sdtconnector.SDTURLHelper;


public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    private static void launchURL(String url) {
       /* 
        * Parse command line argument in the form of: sdt://gateway/host#service
        * and launch the corresponding service.
        */
        URI uri;
        Gateway gw;
        Host host;
        Service service;

        uri = SDTURLHelper.getURI(url);
        if (uri == null) {
            /*
            JOptionPane.showMessageDialog(window,
                "The SDT URL " + url + " is malformed.\n" +
                "The correct form is: sdt://gateway/host#service",
                "Malformed URL",
                JOptionPane.ERROR_MESSAGE);
             */
        } else {
            gw = SDTURLHelper.gatewayFromURI(uri);
            if (gw == null) {
                JOptionPane.showMessageDialog(window,
                    "The gateway " + uri.getHost() + " is unknown.\n" +
                    "Click File -> New Gateway to add this gateway and click the sdt:// link again.",
                    "Unknown gateway",
                    JOptionPane.ERROR_MESSAGE);                    
            } else {
                host = SDTURLHelper.hostFromURI(uri, gw);
                service = SDTURLHelper.serviceFromURI(uri, host);
                window.launchService(gw, host, service);
            }
        }
    }
    
    private static void registerProtocolHandler() {
        String skip = Settings.getProperty("skipHandlerCheck");

        if (skip.equals("true")) {
            return;
        }

        if (SDTURLHelper.isRegistered() == false) {
            String registerSDTMessage;
            String yesText = "Yes";
            String noText = "No";
            String neverText = "No, don't ask me again";
            Object[] options = { yesText, noText, neverText };

            if (OS.isWindows()) {
                registerSDTMessage = "Use SDTConnector to open sdt:// links?";
            } else {
                registerSDTMessage = "Use SDTConnector to open sdt:// links in Mozilla Firefox?";
            }

            int n = JOptionPane.showOptionDialog(window,
                registerSDTMessage,
                "Enable sdt:// links",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (n == -1) {
                return;
            }
            
            if (options[n].equals(yesText)) {
                SDTURLHelper.register();
            } else if (options[n].equals(neverText)) {
                Settings.setProperty("skipHandlerCheck", "true");
            }
        }
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
        window = new MainWindow();
        if (LookUtils.IS_JAVA_5_OR_LATER) {
            window.setLocationByPlatform(true);
        }
        File cwd = new File(System.getProperty("user.dir"));       
        File preferences = new File(cwd, "preferences.xml");
        
        Preferences userRoot = Preferences.userRoot();
        try {
            if (preferences.exists() && !userRoot.nodeExists(SDTManager.prefsPath)) {
                Preferences.importPreferences(new FileInputStream(preferences));
                userRoot.node(SDTManager.prefsPath).sync();
            }
        } catch (BackingStoreException ex) {
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (InvalidPreferencesFormatException ex) {
        }            
        window.setVisible(true);
        
        // Close the splash window after everything is up and initialised
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splash.setVisible(false);
            }
        });

        registerProtocolHandler();
        if (args.length > 0) {
            launchURL(args[0]);
        }
    }
    
    public static MainWindow getMainWindow() {
        return window;
    }
    
    private static MainWindow window;
}
