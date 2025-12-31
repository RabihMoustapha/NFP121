import java.util.List;

interface Exporter {
    void export(List<Media> mediaList, String filePath) throws Exception;
}