import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

class Specialty implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Set<Subject> subjects = new HashSet<>();

    public Specialty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Subject> getSubjects() {
        return new HashSet<>(subjects);
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Specialty))
            return false;
        return name.equals(((Specialty) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}