/*
 * SDTURLHelper.java
 *
 */

package sdtconnector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.jdesktop.swingx.util.OS;

public class SDTURLHelper {
    
    public static Gateway getGateway() {
        return gw;
    }
    
    public static Host getHost() {
        return host;
    }
    
    public static Service getService() {
        return service;
    }

    public static boolean parseSDTURL(String arg) {
        int index, end;
        URI uri;
        
        try {
            uri = new URI(arg);
        } catch (URISyntaxException ex) {
            return false;
        }
        
        if (uri.getScheme().equalsIgnoreCase("sdt")) {
            String s = uri.getHost();
            if (s != null && s.length() > 0) {
                gw = SDTManager.getGatewayByName(s);
                if (gw == null) {
                    gw = SDTManager.getGatewayByAddress(s);
                }
            }
        } else {
            return false;
        }
        
        if (gw != null) {
            String s = uri.getPath().substring(1); // Trim leading slash
            if (s != null && s.length() > 0) {
                host = gw.getHostByName(s);
                if (host == null) {
                    host = gw.getHostByAddress(s);
                }
            }
        } else {
            return false;
        }
        
        if (host != null) {
            String s = uri.getFragment();
            if (s != null && s.length() > 0) {
                service = host.getServiceByName(s);
                if (service == null) {
                    if (s.startsWith("TCP ")) {
                        int port = Integer.valueOf(s.substring("TCP ".length()));
                        service = host.getServiceByPort(port, 0);
                    } else if (s.startsWith("UDP ")) {
                        int port = Integer.valueOf(s.substring("UDP ".length()));
                        service = host.getServiceByPort(0, port);
                    }
                }
            }
        } else {
            return false;
        }

        return service != null ? true : false;
    }
    
    public static boolean isRegisteredWithFirefox() {
        int registered = 0;
        File[] profiles = null;
                
        if (OS.isWindows()) {

        } else if (OS.isMacOSX()) {
            
        } else {
            File dir = new File(System.getProperty("user.home") + "/.mozilla/firefox");
            FileFilter filter = new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            };
            profiles = dir.listFiles(filter);
            if (profiles == null) {
                return false;
            }
            
        profiles:
            for (File profile : profiles) {
                String line;
                FileWriter out;
                FileReader in = null;
                String prefsFilePath = profile.getPath() + "/user.js";

                try {
                    in = new FileReader(prefsFilePath);
                } catch (FileNotFoundException ex) {
                    continue;
                }
                if (in != null) {
                    BufferedReader br = new BufferedReader(in);
                    try {
                        while ((line = br.readLine()) != null) {
                            if (line.equals(firefoxPrefsLine)) {
                                registered++;
                                continue profiles;
                            }
                        }
                    } catch (IOException ex) {
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }
                        } catch (IOException ex) {
                        } try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }
        return (profiles != null && registered == profiles.length) ? true : false;
    }
     
    public static boolean registerWithFirefox() {
        int registered = 0;
        File[] profiles = null;
         
        if (OS.isWindows()) {

        } else if (OS.isMacOSX()) {
            
        } else {
            File dir = new File(System.getProperty("user.home") + "/.mozilla/firefox");
            FileFilter filter = new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            };
            profiles = dir.listFiles(filter);
            if (profiles == null) {
                return false;
            }
            
        profiles:
            for (File profile : profiles) {
                String line;
                FileWriter out;
                String prefsFilePath = profile.getPath() + "/user.js";
                        
                try {
                    out = new FileWriter(prefsFilePath, true);
                } catch (IOException ex) {
                    continue;
                }
                
                BufferedWriter bw = new BufferedWriter(out);
                try {
                    bw.write(firefoxPrefsLine);
                    bw.newLine();
                    registered++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException ex) {
                    } try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ex) {
                    }
                }
            }
        }
        return (profiles != null && registered == profiles.length) ? true : false;
    }
    
    private static final String firefoxPrefsLine = "user_pref(\"network.protocol-handler.app.sdt\", \"/usr/local/bin/sdtconnector\");";

    public static Gateway gw = null;
    public static Host host = null;
    public static Service service = null;
}