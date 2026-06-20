package com.isae.medialibrary.persistence.exporter;

import com.isae.medialibrary.model.Media;
import java.util.List;

public interface Exporter {
    void export(List<Media> mediaList, String filePath) throws Exception;
}