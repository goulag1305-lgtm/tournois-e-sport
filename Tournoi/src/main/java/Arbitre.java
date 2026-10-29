import java.util.*;
/** Arbitre qualifié pour un ou plusieurs jeux. */
public class Arbitre extends Personne {
    private final Set<Jeu> qualifications = new HashSet<>();
    private int matchsArbitres;
    public Arbitre(int id, String nom, String prenom, String pseudo, String genre, String telephone) {
        super(id, nom, prenom, pseudo, genre, telephone);
    }
    public void ajouterQualification(Jeu jeu) { qualifications.add(Objects.requireNonNull(jeu)); }
    public boolean estQualifie(Jeu jeu) { return qualifications.contains(jeu); }
    public Set<Jeu> getQualifications() { return Collections.unmodifiableSet(qualifications); }
    synchronized void ajouterMatchArbitre() { matchsArbitres++; }
    public synchronized int getMatchsArbitres() { return matchsArbitres; }
}
