import java.io.Serializable;

class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nom;
    private String prenom;

    public Administrator(String username, String password, String nom, String prenom) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Administrator)) return false;
        return username.equals(((Administrator) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ")";
    }
}