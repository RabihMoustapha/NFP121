import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String author;
    private int publicationYear;
    private String description;
    private int accessCount;
    private Set<Subject> subjects;

    public Media(String id, String title, String author, int year, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = year;
        this.description = description;
        this.accessCount = 0;
        this.subjects = new HashSet<>();
    }

    public abstract String getType();

    public abstract String getSpecificDetails();

    // Getters et setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String a) {
        author = a;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int y) {
        publicationYear = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void incrementAccessCount() {
        accessCount++;
    }

    public Set<Subject> getSubjects() {
        return new HashSet<>(subjects);
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    public void removeSubject(Subject s) {
        subjects.remove(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Media))
            return false;
        return id.equals(((Media) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}