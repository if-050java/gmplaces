package com.gmplaces.models;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gmplaces.controlers.Utils.parseExceptions;

/**
 * Created by roman vintonyak on 21.10.14.
 */

public class DataServiceImpl implements IDataService {

    final Logger logger = Logger.getLogger(DataServiceImpl.class);
    private final File file = new File("./log/addresses.xml"); //xml file location
    private static final String ERROR = "ERROR"; //Unsuccessful operation
    private static final String SUCCESS = "SUCCESS"; //Successful operation

    //return all data from xml
    public List<Address> getData() {

        List<Address> list = new ArrayList<>();
        Document doc;
        try {
            doc = getDocument(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("address");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    list.add(new Address(Double.valueOf(eElement.getAttribute("latitude")), Double.valueOf(eElement.getAttribute("longitude")), eElement.getAttribute("description")));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(parseExceptions(e));
        }
        return list;
    }

    //put new address into xml file
    //returns status of operation
    public String putData(Address address) {
        Document doc = null;
        try {
            doc = getDocument(file);
            addNewElement(address, doc, doc.getDocumentElement());
            writeIntoFile(doc);
            return SUCCESS;
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            logger.error(parseExceptions(e));
            return ERROR;
        }
    }

    //clear all data from xml file
    public String clearData() {
        Document doc;
        try {
            doc = getDocument(file);
            Element rootElement = doc.getDocumentElement();
            clearChildNodes(rootElement);
            writeIntoFile(doc);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            logger.error(parseExceptions(e));
            return ERROR;
        }
        return SUCCESS;
    }

    //remove specific address from xml file
    //returns status of operation
    public String removeData(Address address) {
        Document doc = null;
        try {
            doc = getDocument(file);
            NodeList nodes = doc.getElementsByTagName("address");
            for (int index = 0; index < nodes.getLength(); index++) {
                Element addr = (Element) nodes.item(index);
                if (address.getLatitude() == Double.parseDouble(addr.getAttribute("latitude")) &&
                        address.getLongitude() == Double.parseDouble(addr.getAttribute("longitude"))) {
                    addr.getParentNode().removeChild(addr);
                }
            }
            writeIntoFile(doc);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            logger.error(parseExceptions(e));
            return ERROR;
        }
        return SUCCESS;
    }

    //get Document object
    private Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        if (!file.exists()) {
            doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("addresses");
            doc.appendChild(rootElement);
            return doc;

        }
        return dBuilder.parse(file);
    }

    //add new element into xml file
    private void addNewElement(Address address, Document doc, Element rootElement) throws TransformerException {
        Element addressElement = doc.createElement("address");
        rootElement.appendChild(addressElement);

        // set attributes to address element

        Attr latitude = doc.createAttribute("latitude");
        latitude.setValue(String.valueOf(address.getLatitude()));
        addressElement.setAttributeNode(latitude);

        Attr longitude = doc.createAttribute("longitude");
        longitude.setValue(String.valueOf(address.getLongitude()));
        addressElement.setAttributeNode(longitude);

        Attr description = doc.createAttribute("description");
        description.setValue(address.getDescription());
        addressElement.setAttributeNode(description);

    }

    //update xml file
    private void writeIntoFile(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    //clear all child nodes recursively
    private void clearChildNodes(Node node) {
        while (node.hasChildNodes()) {
            NodeList nList = node.getChildNodes();
            int index = node.getChildNodes().getLength() - 1;
            Node n = nList.item(index);
            clearChildNodes(n);
            node.removeChild(n);
        }

    }

}
