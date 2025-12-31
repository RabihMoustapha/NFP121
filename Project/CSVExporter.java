import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class CSVExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Title,Author,Year,Type,AccessCount,Subjects");
            for (Media media : mediaList) {
                String subjects = "";
                for (Subject subject : media.getSubjects()) {
                    subjects += subject.getCode() + ";";
                }
                if (!subjects.isEmpty())
                    subjects = subjects.substring(0, subjects.length() - 1);

                writer.printf("\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,\"%s\"%n",
                        media.getId(),
                        media.getTitle(),
                        media.getAuthor(),
                        media.getPublicationYear(),
                        media.getType(),
                        media.getAccessCount(),
                        subjects);
            }
        }
    }
}