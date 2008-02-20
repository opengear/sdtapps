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
    
    public static URI getURI(String arg) {
        String scheme;
        URI uri;
        try {
            // Kludge our way around IE URL-decoding before passing the argument,
            // and the URI class choking on space characters
            uri = new URI(arg.replaceAll(" ", "%20"));
            scheme = uri.getScheme();
            if (scheme != null && scheme.equalsIgnoreCase("sdt")) {
                return uri;
            }
        } catch (URISyntaxException ex) {}
        
        return null;
    }
    
    public static Gateway gatewayFromURI(URI uri) {
        Gateway gw = null;
        String s = uri.getHost();
        
        if (s != null && s.length() > 0) {
            gw = SDTManager.getGatewayByName(s);
            if (gw == null) {
                gw = SDTManager.getGatewayByAddress(s);
            }
        }
        return gw;
    }
    
    public static Host hostFromURI(URI uri, Gateway gw) {
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
    
    public static Service serviceFromURI(URI uri, Host host) {
        int remotePort = 0, udpPort = 0;
        Service service = null;
        String s = uri.getFragment();
        
        if (s != null && s.length() > 0) {
            service = host.getServiceByName(s);
            if (service == null) {
                if (s.toLowerCase().startsWith("tcp port ")) {
                    remotePort = Integer.valueOf(s.substring("tcp port ".length()));
                    service = host.getServiceByPort(remotePort, 0);
                } else if (s.toLowerCase().startsWith("udp port ")) {
                    udpPort = Integer.valueOf(s.substring("udp port ".length()));
                    service = host.getServiceByPort(0, udpPort);
                }
                if (service == null) {
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
                    host.addService(service);
                }
            }
        }
        return service;
    }

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
            FileWriter out;
            String prefsFilePath = profile.getPath() + "/user.js";

            try {
                out = new FileWriter(prefsFilePath, true);
            } catch (IOException ex) {
                continue;
            }

            BufferedWriter bw = new BufferedWriter(out);
            try {
                bw.write(getFirefoxPrefsLine());
                bw.newLine();
                bw.flush();
                registered++;
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ex) {} 
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {}
            }
        }
        return (profiles != null && registered == profiles.length) ? true : false;
    }
    
    private static boolean isRegisteredWithWindows() {
        File tmpfile = null;
        
        try {
            tmpfile = File.createTempFile("sdt", ".reg");
            Runtime.getRuntime().exec("regedit /e " + tmpfile.getPath() + " " + registryKeyPath);
            if (tmpfile.length() > 0) {
                return true;
            }
        } catch (IOException ex) {
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
            return true;
        } catch (IOException ex) {
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
                //tmpfile.delete();
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
    
    private static String getFirefoxPrefsLine() {
        return "user_pref(\"network.protocol-handler.app.sdt\", \"" + System.getProperty("user.dir") + "/SDTConnector\");";
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