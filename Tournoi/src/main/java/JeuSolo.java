/** Un seul joueur désigné pour chaque équipe dans un match. */
public class JeuSolo extends Jeu {
    public JeuSolo(String nom, int nombreManches) { super(nom, nombreManches); }
    @Override public int nombreJoueursRequis() { return 1; }
}
