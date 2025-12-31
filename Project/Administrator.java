import java.io.Serializable;

class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String email;

    public Administrator(String username, String password, String nom, String prenom, String email) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Administrator))
            return false;
        return username.equals(((Administrator) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ") - " + email;
    }
}