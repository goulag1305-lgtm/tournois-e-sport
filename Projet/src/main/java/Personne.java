/**
 * Classe abstraite mère représentant une personne de la plateforme.
 * Contient les attributs communs partagés par les joueurs, coachs et arbitres.
 */
public abstract class Personne {
    private int id;
    private String nom, prenom, pseudo;

    public Personne(int id, String nom, String prenom, String pseudo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPseudo() { return pseudo; }

    @Override
    public String toString() { return prenom + " '" + pseudo + "' " + nom; }
}

/**
 * Représente un Joueur participant aux équipes.
 * Règle métier : Un joueur ne peut appartenir qu'à une seule équipe et passe pro après 3 victoires.
 */
class Joueur extends Personne {
    private Equipe equipe;
    private boolean pro = false;
    private int victoires = 0;

    public Joueur(int id, String nom, String prenom, String pseudo) {
        super(id, nom, prenom, pseudo);
    }

    /** Associe le joueur à une équipe de manière exclusive. */
    public void rejoindreEquipe(Equipe e) {
        if (this.equipe != null && !this.equipe.equals(e)) {
            throw new IllegalStateException("Un joueur ne peut avoir qu'une seule equipe.");
        }
        this.equipe = e;
    }

    /** Incrémente les victoires et gère la promotion automatique au statut PRO. */
    public void ajouterVictoire() {
        this.victoires++;
        if (this.victoires >= 3) this.pro = true;
    }

    public boolean estPro() { return pro; }
    public Equipe getEquipe() { return equipe; }
}

/**
 * Représente un Coach encadrant une équipe.
 */
class Coach extends Personne {
    private int victoiresCarriere = 0;

    public Coach(int id, String nom, String prenom, String pseudo) {
        super(id, nom, prenom, pseudo);
    }

    public void incVictoire() { this.victoiresCarriere++; }
}

/**
 * Représente un Arbitre qualifié pour un jeu spécifique.
 */
class Arbitre extends Personne {
    private Jeu jeuQualifie;
    private int matchsArbitres = 0;

    public Arbitre(int id, String nom, String prenom, String pseudo, Jeu jeu) {
        super(id, nom, prenom, pseudo);
        this.jeuQualifie = jeu;
    }

    public boolean estQualifie(Jeu j) { return jeuQualifie != null && jeuQualifie.equals(j); }
    public void incMatchs() { this.matchsArbitres++; }
}
