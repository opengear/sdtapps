/*
 * AutoHostsParser.java
 *
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AutohostsParser {
    
    public AutohostsParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        try {
            parser = factory.newSAXParser();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }
    
    public EventList parse(InputStream is) {
        try {
            parser.parse(is, handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            // FIXME -- "content is not allowed in trailing section"
            //ex.printStackTrace();
        }
        return hosts;
    }
    
    public EventList parse(File f) {
        try {
            parser.parse(f, handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        return hosts;
    }
    
    public EventList parse(String uri) {
        try {
            parser.parse(uri, handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        return hosts;
    }

    private final class AutohostsHandler extends DefaultHandler {
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            element = localName;
            if (element.equals("port")) {
                port = new Port();
                port.protocol = atts.getValue("type");
            } else if (element.equals("host")) {
                String value;
                host = new Host();
                if ((value = atts.getValue("address")) != null) {
                    host.setAddress(value);
                }
                if ((value = atts.getValue("description")) != null) {
                    host.setDescription(value);
                }
                if ((value = atts.getValue("name")) != null) {
                    host.setName(value);
                }
            }
        }
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (element.equals("port")) {
                port.portNumber = Integer.parseInt(new String(ch, start, length));
            }
        }
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("port")) {
                //Service service = configureService(port);
                //host.addService(service);
                ports.add(port);
                element = "host";
            } else if (localName.equals("host")) {
                if (host.getName().isEmpty() && (host.getAddress().equals("127.0.0.1") || host.getAddress().equalsIgnoreCase("localhost"))) {
                    host.setName("Local Services");
                }
                host.setServiceList(portsToServiceList(ports));
                hosts.add(host);
                element = "";
            }
        }
        
        private EventList portsToServiceList(List<AutohostsParser.Port> ports) {
            Service service = null;
            EventList services = new BasicEventList();
            
            // Find for the configured service that matches the most number
            // of ports in the list and assume this is the service that's been
            // configured - this is currently a poor substitute for service
            // groups in the GUI
            while (true) {
                List<Port> matchedPorts = new ArrayList<Port>();
                List<Port> bestMatchPorts = new ArrayList<Port>();
                Service bestMatchService = null;

                for (Object s : SDTManager.getServiceList()) {
                    service = (Service) s;
                    List<Port> servicePorts = new ArrayList<Port>();

                    for (Object l : service.getLauncherList()) {
                        Launcher launcher = (Launcher) l;
                        Port servicePort = new Port();

                        if (launcher.getUdpPort() != 0) {
                            servicePort.protocol = "udp";
                            servicePort.portNumber = launcher.getUdpPort();
                        } else {
                            servicePort.protocol = "tcp";
                            servicePort.portNumber = launcher.getRemotePort();
                        }
                        servicePorts.add(servicePort);
                    }

                    // Services with ports not in the list are disqualified
                    if (ports.containsAll(servicePorts) == false) {
                        continue;
                    }

                    for (Port port : ports) {
                        if (servicePorts.contains(port) && (matchedPorts.contains(port) == false)) {
                            matchedPorts.add(port);
                        }
                    }

                    if (matchedPorts.size() > bestMatchPorts.size()) {
                        bestMatchService = service;
                        bestMatchPorts.clear();
                        bestMatchPorts.addAll(matchedPorts);
                    }
                    matchedPorts.clear();
                }
                if (bestMatchService == null) {
                    break;
                }
                services.add(bestMatchService);
                ports.removeAll(bestMatchPorts);
                if (ports.size() == 0) {
                    break;
                }
            }

            // Create services for ports that didn't match
            for (AutohostsParser.Port port : ports) {
                Launcher launcher = new Launcher();
                service = new Service();

                if (port.protocol.equalsIgnoreCase("tcp")) {
                    Service s = null;
                    if (port.portNumber >= 2000 && port.portNumber <= 2096) {
                        // Serial telnet
                        s = SDTManager.getServiceByPort(23, 0);
                        service.setName("Serial " + (port.portNumber - 2000) + " Telnet");
                    } else if (port.portNumber >= 3000 && port.portNumber <= 3096) {
                        // Serial SSH
                        s = SDTManager.getServiceByPort(22, 0);
                        service.setName("Serial " + (port.portNumber - 3000) + " SSH");
                    }
                    if (s != null) {
                       service.setIcon(s.getIcon());
                       launcher.setClient(s.getFirstLauncher().getClient());
                    }
                }

                if (port.protocol.equalsIgnoreCase("tcp")) {
                    launcher.setRemotePort(port.portNumber);
                } else if (port.protocol.equalsIgnoreCase("udp")) {
                    launcher.setUdpPort(port.portNumber);
                }
                service.addLauncher(launcher);
                SDTManager.addService(service);
                services.add(service);
            }
            return services;
        }
    }
    
    private class Port {
        public String protocol = "";
        public int portNumber = 0;

        public boolean equals(Object o) {
            if (o instanceof Port) {
                Port p = (Port) o;
                if (this.portNumber == p.portNumber && this.protocol.equals(p.protocol)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private SAXParser parser;
    private Host host;
    
    private String element = "";
    private AutohostsHandler handler = new AutohostsHandler();
    private EventList hosts = new BasicEventList();
    private Port port;
    
    private List<Port> ports = new ArrayList<Port>();
}
