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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.util.OS;

public class SDTURLHelper {

    /** 
     * sdt:// URL parser methods
     */    
    private static URI uri = null;
    
    public static boolean hasURL() {
        return (uri != null);
    }
    
    public static void setURL(String sdtUrl) throws URISyntaxException, Exception {
        String scheme;
        
        // Kludge our way around IE URL-decoding before passing the argument,
        // and the URI class choking on space characters
        uri = new URI(sdtUrl.replaceAll(" ", "%20"));
        scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase("sdt")) {
            throw new Exception("Invalid URL scheme: " + scheme);
        }
    }
    
    public static Gateway getGateway()  {
        return uriGateway();
    }
    
    public static Host getHost() {
        return uriHost(uriGateway());
    }
    public static Host getHost(Gateway gateway) {
        return uriHost(gateway);
    }
    
    public static Service getService() {
        return uriService(uriHost(uriGateway()));
    }
    public static Service getService(Host host) {
        return uriService(host);
    }
    
    private static Gateway uriGateway() {
        if (uri == null) {
            return null;
        }
        
        Gateway gw = null;
        String s = uri.getHost();
        
        String username = uri.getUserInfo();
        // Strip password
        if (username != null) {
            int pos = username.indexOf(':');
            if (pos != -1) {
                username = username.substring(0, pos);
            }
        }
        
        if (s != null && s.length() > 0) {
            gw = SDTManager.getGatewayByName(s, username);
            if (gw == null) {
                gw = SDTManager.getGatewayByAddress(s, username);
            }
        }
        return gw;
    }
    
    private static Host uriHost(Gateway gw) {
        if (uri == null || gw == null) {
            return null;
        }
        
        Host host = null;
        String s = uri.getPath();
        
        if (s != null && s.length() > 1) {
            s = s.substring(1); // Trim leading slash
            host = gw.getHostByName(s);
            if (host == null) {
                host = gw.getHostByAddress(s);
            }
            if (host == null) {
                // Create a new host
                host = new Host();
                host.setAddress(s);
                if (s.equals("127.0.0.1") || s.equals("localhost")) {
                    host.setName("Local Services");
                }
                gw.addHost(host);
            }
        }
        return host;
    }

    private static int uriRemotePort(String s) {
        if (s.toLowerCase().startsWith("tcp port ")) {
            return Integer.valueOf(s.substring("tcp port ".length()));
        }
        return 0;
    }
    
    private static int uriUdpPort(String s) {
        if (s.toLowerCase().startsWith("udp port ")) {
            return Integer.valueOf(s.substring("udp port ".length()));
        }
        return 0;
    }
    
    public static Service uriService(Host host) {
        if (uri == null || host == null) {
            return null;
        }
        
        int remotePort, udpPort;
        Service service = null;
        String s = uri.getFragment();

        if (s != null && s.length() > 0) {
            service = host.getServiceByName(s);
            
            if (service == null) {
                // Service may be generically named, e.g. "TCP Port XXXX"
                remotePort = uriRemotePort(s);
                udpPort = uriUdpPort(s);
                service = host.getServiceByPort(remotePort, udpPort);

                if (service == null) {
                    // The host isn't configured with this service, try snarfing it from the master list
                    service = SDTManager.getServiceByName(s);
                    
                    if (service == null) {
                        // Is there a matching generically named service in the master list
                        service = SDTManager.getServiceByPort(remotePort, udpPort);
                        
                        if (service == null) {
                            // If all else fails, create a new service
                            Launcher launcher = new Launcher();
                            Service svc = null;

                            service = new Service();

                            if (remotePort >= 2000 && remotePort < 2096) {
                                // Serial telnet
                                svc = SDTManager.getServiceByPort(23, 0);
                                service.setName("Serial " + (remotePort - 2000) + " Telnet");
                            } else if (remotePort >= 3000 && remotePort <= 3096) {
                                // Serial SSH
                                svc = SDTManager.getServiceByPort(22, 0);
                                service.setName("Serial " + (remotePort - 3000) + " SSH");
                            }
                            if (svc != null) {
                               service.setIcon(svc.getIcon());
                               launcher.setClient(svc.getFirstLauncher().getClient());
                            }

                            launcher.setRemotePort(remotePort);
                            launcher.setUdpPort(udpPort);
                            service.addLauncher(launcher);

                            SDTManager.addService(service);
                        }
                    }
                    host.addService(service);
                }
            }
        
            String q = uri.getQuery();
            if (q != null) {
                service.getFirstLauncher().getClient().setQuery(q);
            }    
        }
        return service;
    }

    /** 
     * Protocol handler registration methods
     */
    private static File[] getFirefoxProfiles() {
        String profilesPath;

        if (OS.isWindows()) {
            profilesPath = System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles";
        } else if (OS.isMacOSX()) {
            profilesPath = System.getProperty("user.home") + "/Library/Applications/Support/Firefox/Profiles";
        } else {
            profilesPath = System.getProperty("user.home") + "/.mozilla/firefox";
        }
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
        File dir = new File(profilesPath);
        if (dir != null) {
            return dir.listFiles(filter);
        }
        return null;
    }
    
    private static boolean isRegisteredWithFirefox() {
        int registered = 0;
        File[] profiles = getFirefoxProfiles();
                
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
                        if (line.equals(getFirefoxPrefsLine())) {
                            registered++;
                            continue profiles;
                        }
                    }
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return (profiles != null && registered == profiles.length) ? true : false;
    }
     
    private static boolean registerWithFirefox() {
        int registered = 0;
        File[] profiles = getFirefoxProfiles();

        if (profiles == null) {
            return false;
        }

    profiles:
        for (File profile : profiles) {                

            String line;
            FileReader in;
            FileWriter out;
            String prefsFilePath = profile.getPath() + "/user.js";
            File tmpfile;
            
            try {
                tmpfile = File.createTempFile("sdtcon", null);

                File f = new File(prefsFilePath);
                if (f.exists() == false) {
                    f.createNewFile();
                }
                
                in = new FileReader(prefsFilePath);
                out = new FileWriter(tmpfile, false);
            } catch (IOException ex) {
                continue;
            }

            BufferedReader br = new BufferedReader(in);
            BufferedWriter bw = new BufferedWriter(out);
            
            try {
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(firefoxPrefsPrefix)) {
                        continue;
                    }
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                }
                bw.write(getFirefoxPrefsLine());
                bw.newLine();
                bw.flush();
                registered++;
                
                bw.close();
                br.close();
                tmpfile.renameTo(new File(prefsFilePath));
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return (profiles != null && registered == profiles.length) ? true : false;
    }
    
    private static boolean isRegisteredWithWindows() {
        File tmpfile = null;
        
        try {
            tmpfile = File.createTempFile("sdt", ".reg");
            Process p = Runtime.getRuntime().exec("regedit /e " + tmpfile.getPath() + " " + registryKeyPath);
            p.waitFor();
            if (tmpfile.length() > 0) {
                return true;
            }
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        } finally {
            if (tmpfile != null) {
                tmpfile.delete();
            }
        }
        return false;
    }

    private static boolean registerWithWindows() {
        File tmpfile = null;
        FileWriter out = null;
        BufferedWriter bw = null;
        
        try {
            tmpfile = File.createTempFile("sdt", ".reg");
            out = new FileWriter(tmpfile, true);
            bw = new BufferedWriter(out);
            
            for (String line : getRegistryEntry()) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            Process p = Runtime.getRuntime().exec("regedit /s " + tmpfile.getPath());
            p.waitFor();
            return true;
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {}
            }
            if (tmpfile != null) {
                tmpfile.delete();
            }
        }
        return false;
    }
    
    public static boolean isRegistered() {
        if (OS.isWindows()) {
            return isRegisteredWithWindows();
        } else {
            return isRegisteredWithFirefox();
        }
    }
    
    public static boolean register() {
        if (OS.isWindows()) {
            return registerWithWindows();
        } else {
            return registerWithFirefox();
        }
    }
    
    private static final String firefoxPrefsPrefix = "user_pref(\"network.protocol-handler.app.sdt\", \"";
    
    private static String getFirefoxPrefsLine() {
        return firefoxPrefsPrefix + System.getProperty("user.dir") + "/SDTConnector\");";
    }
    
    public static List<String> getRegistryEntry() {
        List<String> list = new ArrayList<String>();
        
        list.addAll(REGISTRY_HEADER_ITEMS);
        list.add("@=\"\\\"" + System.getProperty("user.dir").replaceAll("\\\\", "\\\\\\\\") + "\\\\SDTConnector.exe\\\" \\\"%1\\\"\"");
        list.addAll(REGISTRY_FOOTER_ITEMS);
        return list;
    }
    
    private static final List<String> REGISTRY_HEADER_ITEMS = Collections.unmodifiableList(Arrays.asList(
            "Windows Registry Editor Version 5.00",
            "",
            "[HKEY_CURRENT_USER\\Software\\Classes\\sdt]",
            "@=\"URL:SDT Protocol\"",
            "\"URL Protocol\"=\"\"",
            "",
            "[HKEY_CURRENT_USER\\Software\\Classes\\sdt\\Default Icon]",
            "@=\"%SystemRoot%\\\\system32\\\\url.dll,0\"",
            "",
            "[HKEY_CURRENT_USER\\Software\\Classes\\sdt\\shell]",
            "",
            "[HKEY_CURRENT_USER\\Software\\Classes\\sdt\\shell\\open]",
            "",
            "[HKEY_CURRENT_USER\\Software\\Classes\\sdt\\shell\\open\\command]"
            ));
    private static final List<String> REGISTRY_FOOTER_ITEMS = Collections.unmodifiableList(Arrays.asList(
            "",
            ""
            ));
    private static final String registryKeyPath = "HKEY_CURRENT_USER\\Software\\Classes\\sdt";
}