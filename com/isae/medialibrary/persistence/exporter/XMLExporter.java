package com.isae.medialibrary.persistence.exporter;

import com.isae.medialibrary.model.Media;
import com.isae.medialibrary.model.Subject;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.util.List;

public class XMLExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("medialibrary");
        doc.appendChild(root);

        for (Media media : mediaList) {
            Element mediaElem = doc.createElement("media");
            mediaElem.setAttribute("id", media.getId());
            mediaElem.setAttribute("type", media.getType());

            addElement(doc, mediaElem, "title", media.getTitle());
            addElement(doc, mediaElem, "author", media.getAuthor());
            addElement(doc, mediaElem, "year", String.valueOf(media.getPublicationYear()));
            addElement(doc, mediaElem, "description", media.getDescription());
            addElement(doc, mediaElem, "accessCount", String.valueOf(media.getAccessCount()));

            Element subjectsElem = doc.createElement("subjects");
            for (Subject s : media.getSubjects()) {
                Element subjElem = doc.createElement("subject");
                subjElem.setTextContent(s.getCode());
                subjectsElem.appendChild(subjElem);
            }
            mediaElem.appendChild(subjectsElem);
            root.appendChild(mediaElem);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        if (value != null && !value.trim().isEmpty()) {
            Element elem = doc.createElement(name);
            elem.setTextContent(value);
            parent.appendChild(elem);
        }
    }
}