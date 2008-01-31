/*
 * SDTURLHelper.java
 *
 */

package sdtconnector;

import java.net.URI;
import java.net.URISyntaxException;

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
            String nameOrAddress = uri.getHost();
            if (nameOrAddress != null && nameOrAddress.length() > 0) {
                gw = SDTManager.getGatewayByNameOrAddress(nameOrAddress);
            }
        } else {
            return false;
        }
        
        if (gw != null) {
            String nameOrAddress = uri.getPath().substring(1); // Trim leading slash
            if (nameOrAddress != null && nameOrAddress.length() > 0) {
                host = gw.getHostByNameOrAddress(nameOrAddress);
            }
        } else {
            return false;
        }
        
        if (host != null) {
            String name = uri.getFragment();
            if (name != null && name.length() > 0) {
                service = host.getServiceByName(name);
            }
        } else {
            return false;
        }

        return service != null ? true : false;
    }

    public static Gateway gw = null;
    public static Host host = null;
    public static Service service = null;
}