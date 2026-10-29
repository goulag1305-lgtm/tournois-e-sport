/** Coach rattaché à une seule équipe et conservant ses victoires de carrière. */
public class Coach extends Personne {
    private Equipe equipe;
    private int victoires;
    public Coach(int id, String nom, String prenom, String pseudo, String genre, String telephone) {
        super(id, nom, prenom, pseudo, genre, telephone);
    }
    void rejoindreEquipe(Equipe nouvelle) {
        if (equipe != null && equipe != nouvelle) throw new IllegalStateException("Coach deja dans une equipe");
        equipe = nouvelle;
    }
    void quitterEquipe(Equipe ancienne) {
        if (equipe != ancienne) throw new IllegalStateException("Mauvaise equipe");
        equipe = null;
    }
    void ajouterVictoire() { victoires++; }
    public int getVictoires() { return victoires; }
    public Equipe getEquipe() { return equipe; }
}
