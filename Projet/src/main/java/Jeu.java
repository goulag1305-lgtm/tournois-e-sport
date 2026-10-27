/**
 * Classe abstraite définissant un jeu e-sport.
 */
public abstract class Jeu {
    private String nom;

    public Jeu(String nom) { this.nom = nom; }
    
    /** Nombre de joueurs requis par équipe pour ce jeu. */
    public abstract int nombreJoueursRequis();
    
    public String getNom() { return nom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return nom.equals(((Jeu) o).nom);
    }
}

/** Jeu en mode solo (1 joueur par équipe). */
class JeuSolo extends Jeu {
    public JeuSolo(String nom) { super(nom); }
    @Override public int nombreJoueursRequis() { return 1; }
}

/** Jeu en équipe (entre 3 et 5 joueurs par équipe). */
class JeuEquipe extends Jeu {
    private int nbJoueurs;
    public JeuEquipe(String nom, int nbJoueurs) {
        super(nom);
        if (nbJoueurs < 3 || nbJoueurs > 5) throw new IllegalArgumentException("Entre 3 et 5 joueurs par equipe.");
        this.nbJoueurs = nbJoueurs;
    }
    @Override public int nombreJoueursRequis() { return nbJoueurs; }
}
