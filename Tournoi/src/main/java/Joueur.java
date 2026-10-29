/** Joueur amateur ou professionnel ; au plus une équipe à la fois. */
public class Joueur extends Personne {
    private Equipe equipe;
    private int victoiresDepuisArrivee;
    private boolean professionnel;

    public Joueur(int id, String nom, String prenom, String pseudo, String genre, String telephone) {
        super(id, nom, prenom, pseudo, genre, telephone);
    }
    /** Appelé par Equipe pour maintenir les deux côtés de l'association. */
    void rejoindreEquipe(Equipe nouvelle) {
        if (equipe != null && equipe != nouvelle) throw new IllegalStateException("Joueur deja dans une equipe");
        equipe = nouvelle;
    }
    void quitterEquipe(Equipe ancienne) {
        if (equipe != ancienne) throw new IllegalStateException("Mauvaise equipe");
        equipe = null;
        victoiresDepuisArrivee = 0;
    }
    /** Une victoire de l'équipe depuis l'arrivée du joueur. */
    void ajouterVictoire() {
        victoiresDepuisArrivee++;
        if (victoiresDepuisArrivee >= 3) professionnel = true;
    }
    public boolean estProfessionnel() { return professionnel; }
    public int getVictoiresDepuisArrivee() { return victoiresDepuisArrivee; }
    public Equipe getEquipe() { return equipe; }
}
