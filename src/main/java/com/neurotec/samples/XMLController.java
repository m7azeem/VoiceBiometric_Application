package com.neurotec.samples;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
            Element audio = doc.createElement("audio");
            audio.appendChild(doc.createTextNode("[path to mp3 file]"));
            user.appendChild(audio);

            // passphrase elements
            Element template = doc.createElement("template");
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
    public void readXMLData(){
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

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("User id : " + eElement.getAttribute("id"));
                    System.out.println("Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                    // get passphrase as well.
                    System.out.println("SoundFile path: " + eElement.getElementsByTagName("audio").item(0).getTextContent());
                    System.out.println("Template path: " + eElement.getElementsByTagName("template").item(0).getTextContent());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}