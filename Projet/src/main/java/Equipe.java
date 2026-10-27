import java.util.*;

/**
 * Représente une équipe e-sport constituée de joueurs, d'un coach et de points accumulés.
 * Implémente Comparable pour trier les équipes par points décroissants.
 */
public class Equipe implements Comparable<Equipe> {
    private String nom;
    private Coach coach;
    private List<Joueur> joueurs = new ArrayList<>();
    private int points = 0;

    public Equipe(String nom) { this.nom = nom; }

    /** Ajoute un joueur à l'équipe et met à jour son appartenance. */
    public void ajouterJoueur(Joueur j) {
        if (!joueurs.contains(j)) {
            joueurs.add(j);
            j.rejoindreEquipe(this);
        }
    }

    public void setCoach(Coach c) { this.coach = c; }
    public void ajouterPoints(int p) { this.points += p; }

    public String getNom() { return nom; }
    public Coach getCoach() { return coach; }
    public List<Joueur> getJoueurs() { return Collections.unmodifiableList(joueurs); }
    public int getPoints() { return points; }

    @Override
    public int compareTo(Equipe o) {
        int res = Integer.compare(o.points, this.points);
        return res != 0 ? res : this.nom.compareToIgnoreCase(o.nom);
    }

    @Override
    public String toString() { return nom + " (" + points + " pts)"; }
}
