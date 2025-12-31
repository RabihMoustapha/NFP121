import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

class Student implements Observer, Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private Specialty specialty;
    private Set<Subject> enrolledSubjects = new HashSet<>();

    public Student(String username, String password, String nom, String prenom, Specialty specialty) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.specialty = specialty;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public Set<Subject> getEnrolledSubjects() {
        return new HashSet<>(enrolledSubjects);
    }

    public void enrollInSubject(Subject s) {
        enrolledSubjects.add(s);
    }

    public boolean isInterestedInMedia(Media media) {
        for (Subject subject : enrolledSubjects) {
            if (media.getSubjects().contains(subject))
                return true;
        }
        return false;
    }

    @Override
    public void update(Object info) {
        if (info instanceof Media) {
            Media newMedia = (Media) info;
            if (isInterestedInMedia(newMedia)) {
                System.out.println("EMAIL to " + username + ": New media '" + newMedia.getTitle() + "' available");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Student))
            return false;
        return username.equals(((Student) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ") - " + specialty.getName();
    }
}