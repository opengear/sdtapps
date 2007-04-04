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
import javax.xml.parsers.FactoryConfigurationError;
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
                host = new Host();
                host.setAddress(atts.getValue("address"));
            }
        }
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (element.equals("port")) {
                port.portNumber = Integer.parseInt(new String(ch, start, length));
            }
        }
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("port")) {
                if (port.protocol.equalsIgnoreCase("udp")) {
                    Service service = SDTManager.getServiceByPort(0, port.portNumber);
                    host.addService(service);
                } else if (port.protocol.equalsIgnoreCase("tcp")) {
                    Service service = SDTManager.getServiceByPort(port.portNumber, 0);
                    host.addService(service);
                }
                element = "host";
            } else if (localName.equals("host")) {
                hosts.add(host);
                element = "";
            }
        }
    }
    
    private class Port {
        public String protocol = "";
        public int portNumber = 0;
    }
    
    private SAXParser parser;
    private Host host;
    
    private String element = "";
    private AutohostsHandler handler = new AutohostsHandler();
    private EventList hosts = new BasicEventList();
    private Port port;
}
