import java.io.*;
import java.util.List;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.Element;

class XMLExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();

        Element root = doc.createElement("mediaLibrary");
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
            for (Subject subject : media.getSubjects()) {
                Element subjElem = doc.createElement("subject");
                subjElem.setTextContent(subject.getCode());
                subjectsElem.appendChild(subjElem);
            }
            mediaElem.appendChild(subjectsElem);
            root.appendChild(mediaElem);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    private void addElement(org.w3c.dom.Document doc, Element parent, String name, String value) {
        Element elem = doc.createElement(name);
        elem.setTextContent(value);
        parent.appendChild(elem);
    }
}