import java.util.Objects;
/** Jeu extensible : un nouveau type peut hériter de cette classe. */
public abstract class Jeu {
    private final String nom;
    private final int nombreManches;
    protected Jeu(String nom, int nombreManches) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom du jeu est obligatoire");
        this.nom = nom;
        if (nombreManches < 1) throw new IllegalArgumentException("Nombre de manches invalide");
        this.nombreManches = nombreManches;
    }
    public String getNom() { return nom; }
    public int getNombreManches() { return nombreManches; }
    public abstract int nombreJoueursRequis();
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jeu autre = (Jeu) obj;
        return nombreManches == autre.nombreManches && nom.equals(autre.nom);
    }
    @Override public int hashCode() { return Objects.hash(getClass(), nom, nombreManches); }
}
