import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Klasa wczytujaca pliki .xml oraz zapisujaca dane do plikow o takim formacie
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class XmlParser {
	/**
	 * Funkcja zwracajaca zparsowany plik XML w postaci obiektu {@link Document}
	 * 
	 * @param fileName - sciezka do parsowanego pliku
	 * 
	 * @return Zwraca obiekt typu {@link Document}
	 */
	public static Document parseXML(String fileName) {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			return doc;

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Funkcja zapisujaca wyniki gry do pliku
	 * 
	 * @param path - sciezka pliku
	 * @param list - lista wynikow
	 * @param nicks - lista imion
	 */
	public static void writeResultsXML(String path, List<String> list, List<String> nicks) {
		try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("Results");
				doc.appendChild(rootElement);
		 
				// staff elements
				
				for(int i = 0; i < list.size(); i++) {
					Element result = doc.createElement("Result");
					rootElement.appendChild(result);
					Element time = doc.createElement("Time");
					Element name = doc.createElement("Name");
					result.appendChild(name);
					result.appendChild(time);
					name.appendChild(doc.createTextNode(nicks.get(i)));
					time.appendChild(doc.createTextNode(list.get(i)));
				}
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(path));
		 
				transformer.transform(source, result);
		 
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
	}
	
	/**
	 * Funkcja zwracajaca {@link NodeList} obiektow o podanym tagu
	 * 
	 * @param doc - referencja na obiekt {@link Document}
	 * @param tagName - tag pod ktorym znajduja sie interesujace nas obiekty w pliku
	 * 
	 * @return Zwraca {@link NodeList} obiektow o podanym tagu
	 */
	public static NodeList parseDocument(Document doc, String tagName) {
		NodeList nl = doc.getElementsByTagName(tagName);
		return nl;
	}
	
	/**
	 * Funkcja zwracajaca podany element w postaci stringa
	 * 
	 * @param ele - referenca na {@link Element}
	 * @param tagName - tag pod ktorym znajduja sie interesujace nas obiekty
	 * 
	 * @return Zwraca dany atrybut w postaci stringa
	 */
	public static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	/**
	 * {@link #getTextValue(Element, String)} z konwersja na int
	 */
	public static int getIntValue(Element ele, String tagName) {
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	/**
	 * {@link #getTextValue(Element, String)} z konwersja na double
	 */
	public static double getDoubleValue(Element ele, String tagName) {
		return Double.parseDouble(getTextValue(ele, tagName));
	}
}
