package com.isae.medialibrary.persistence.exporter;

import com.isae.medialibrary.model.Media;
import com.isae.medialibrary.model.Subject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class CSVExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Title,Author,Year,Type,AccessCount,Subjects");
            for (Media media : mediaList) {
                StringBuilder subjects = new StringBuilder();
                for (Subject s : media.getSubjects()) {
                    if (subjects.length() > 0) subjects.append(";");
                    subjects.append(s.getCode());
                }
                writer.printf("\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,\"%s\"%n",
                        media.getId(), media.getTitle(), media.getAuthor(),
                        media.getPublicationYear(), media.getType(),
                        media.getAccessCount(), subjects.toString());
            }
        }
    }
}