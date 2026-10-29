import java.util.*;
/** Equipe avec joueurs, coach et points ; tri par points puis nom. */
public class Equipe implements Comparable<Equipe> {
    private final String nom;
    private Coach coach;
    private final List<Joueur> joueurs = new ArrayList<>();
    private int points;
    private int tournoisActifs; // Empêche de changer l'effectif pendant un tournoi.
    public Equipe(String nom) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom de l'equipe est obligatoire");
        this.nom = nom;
    }
    public synchronized void ajouterJoueur(Joueur joueur) {
        verifierModifiable();
        Objects.requireNonNull(joueur);
        if (joueurs.contains(joueur)) return;
        // Vérifier AVANT d'ajouter pour éviter une liste incohérente en cas d'exception.
        joueur.rejoindreEquipe(this);
        joueurs.add(joueur);
    }
    public synchronized void retirerJoueur(Joueur joueur) {
        verifierModifiable();
        if (!joueurs.contains(joueur)) throw new IllegalArgumentException("Joueur absent");
        joueur.quitterEquipe(this);
        joueurs.remove(joueur);
    }
    public synchronized void setCoach(Coach nouveau) {
        verifierModifiable();
        if (nouveau == coach) return;
        if (nouveau != null) nouveau.rejoindreEquipe(this); // vérification avant modification
        if (coach != null) coach.quitterEquipe(this);
        coach = nouveau;
    }
    private void verifierModifiable() {
        if (tournoisActifs > 0) throw new IllegalStateException("Equipe en tournoi");
    }
    synchronized void verrouiller() { tournoisActifs++; }
    synchronized void deverrouiller() {
        if (tournoisActifs == 0) throw new IllegalStateException("Equipe non verrouillee");
        tournoisActifs--;
    }
    synchronized void ajouterVictoire() {
        points += 3;
        if (coach != null) coach.ajouterVictoire();
        for (Joueur j : joueurs) j.ajouterVictoire();
    }
    public String getNom() { return nom; }
    public synchronized Coach getCoach() { return coach; }
    public synchronized List<Joueur> getJoueurs() { return Collections.unmodifiableList(new ArrayList<>(joueurs)); }
    public synchronized int getPoints() { return points; }
    @Override public int compareTo(Equipe autre) {
        int c = Integer.compare(autre.getPoints(), getPoints());
        return c != 0 ? c : nom.compareToIgnoreCase(autre.nom);
    }
    @Override public String toString() { return nom + " (" + points + " pts)"; }
}
