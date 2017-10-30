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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXMLFile {

    public void doSometing() {

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
            user.setAttribute("id", "1");

            // name elements
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode("My name"));
            user.appendChild(name);

            // passphrase elements
            Element passPhrase = doc.createElement("passphrase");
            passPhrase.setAttribute("phrase_id", "1");
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
}