import java.util.Objects;
/** Un jeu collectif nécessite de 3 à 5 joueurs par équipe. */
public class JeuEquipe extends Jeu {
    private final int joueursParEquipe;
    public JeuEquipe(String nom, int nombreManches, int joueursParEquipe) {
        super(nom, nombreManches);
        if (joueursParEquipe < 3 || joueursParEquipe > 5) throw new IllegalArgumentException("3 a 5 joueurs requis");
        this.joueursParEquipe = joueursParEquipe;
    }
    @Override public int nombreJoueursRequis() { return joueursParEquipe; }
    @Override public boolean equals(Object obj) {
        return super.equals(obj) && joueursParEquipe == ((JeuEquipe) obj).joueursParEquipe;
    }
    @Override public int hashCode() { return Objects.hash(super.hashCode(), joueursParEquipe); }
}
