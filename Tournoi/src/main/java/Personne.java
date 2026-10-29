/** Informations communes aux joueurs, coachs et arbitres. */
public abstract class Personne {
    private final int id;
    private final String nom, prenom, pseudo, genre, telephone;

    protected Personne(int id, String nom, String prenom, String pseudo, String genre, String telephone) {
        if (id <= 0) throw new IllegalArgumentException("L'identifiant doit etre positif");
        this.id = id;
        this.nom = texteObligatoire(nom, "nom");
        this.prenom = texteObligatoire(prenom, "prenom");
        this.pseudo = texteObligatoire(pseudo, "pseudo");
        this.genre = texteObligatoire(genre, "genre");
        this.telephone = texteObligatoire(telephone, "telephone");
    }

    private static String texteObligatoire(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ " + champ + " est obligatoire");
        }
        return valeur;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPseudo() { return pseudo; }
    public String getGenre() { return genre; }
    public String getTelephone() { return telephone; }
    @Override public String toString() { return prenom + " '" + pseudo + "' " + nom; }
}
