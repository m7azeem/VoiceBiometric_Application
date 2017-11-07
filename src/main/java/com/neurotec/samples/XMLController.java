package com.neurotec.samples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.*;

public class XMLController {

    //initialize.
    public void makeNewXMLDocument() {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("voices");
            doc.appendChild(rootElement);

            // user elements
            Element user = doc.createElement("user");
            rootElement.appendChild(user);


            // set attribute to user element
            user.setAttribute("id", "-1");

            // name elements
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode("init-user"));
            user.appendChild(name);

            // passphrase elements
            Element passPhrase = doc.createElement("passphrase");
            passPhrase.setAttribute("phrase_id", "-1");
            passPhrase.appendChild(doc.createTextNode("Hello I am [name] [name]"));
            user.appendChild(passPhrase);

            // passphrase elements
            Element audio = doc.createElement("soundFile");
            audio.appendChild(doc.createTextNode("[path to mp3 file]"));
            user.appendChild(audio);

            // passphrase elements
            Element template = doc.createElement("templateFile");
            template.appendChild(doc.createTextNode("[path to template file]"));
            user.appendChild(template);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("userTemplates.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    //read all XML document data and load it.
    public ArrayList<User> readXMLData() {
        try {
            File fXmlFile = new File("userTemplates.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("user");

            System.out.println("----------------------------");
            ArrayList<User> users = new ArrayList<User>();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                User user = new User();

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    user.id = Integer.parseInt(eElement.getAttribute("id"));
                    user.name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    //get passphrase as well.
                    user.passphrase = eElement.getElementsByTagName("passphrase").item(0).getTextContent();
                    user.phraseId = Integer.parseInt(((Element) eElement.getElementsByTagName("passphrase").item(0)).getAttribute("phrase_id"));
                    user.soundFile = eElement.getElementsByTagName("soundFile").item(0).getTextContent();
                    user.templateFile = eElement.getElementsByTagName("templateFile").item(0).getTextContent();
                    users.add(user);
                }
            }
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean addUser(User person) {
        //load stuff
        String filepath = "userTemplates.xml";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            // Get the root element
            Node voices = doc.getFirstChild();
            //call another funciton to prepare new node

            //add the enw node
            voices.appendChild(createXMLNode(doc, person));

            // write the updated content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
            return true;
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }

        return false;
    }


    public Node createXMLNode(Document doc , User person) throws ParserConfigurationException {
        // user elements
        Element user = doc.createElement("user");
        // set attribute to user element
        user.setAttribute("id", String.valueOf(person.id));

        // name elements
        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(person.name));
        user.appendChild(name);

        // passphrase elements
        Element passPhrase = doc.createElement("passphrase");
        passPhrase.setAttribute("phrase_id", String.valueOf(person.phraseId));
        passPhrase.appendChild(doc.createTextNode(person.passphrase));
        user.appendChild(passPhrase);

        // passphrase elements
        Element audio = doc.createElement("soundFile");
        audio.appendChild(doc.createTextNode(person.soundFile));
        user.appendChild(audio);

        // passphrase elements
        Element template = doc.createElement("templateFile");
        template.appendChild(doc.createTextNode(person.templateFile));
        user.appendChild(template);
        return user;
    }
}