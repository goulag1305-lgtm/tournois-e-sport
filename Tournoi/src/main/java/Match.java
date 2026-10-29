import java.util.*;
/** Une rencontre de deux équipes distinctes, sans match nul. */
public class Match {
    public enum Statut { PLANIFIE, TERMINE, INCIDENT }
    private final Equipe equipe1, equipe2;
    private final Jeu jeu;
    private final Arbitre arbitre;
    private final List<Joueur> alignement1, alignement2;
    private volatile Statut statut = Statut.PLANIFIE;
    private volatile Equipe vainqueur;

    public Match(Equipe equipe1, Equipe equipe2, Jeu jeu, Arbitre arbitre) {
        this(equipe1, equipe2, jeu, arbitre,
             alignementAutomatique(equipe1, jeu), alignementAutomatique(equipe2, jeu));
    }

    private static List<Joueur> alignementAutomatique(Equipe equipe, Jeu jeu) {
        Objects.requireNonNull(equipe, "equipe");
        Objects.requireNonNull(jeu, "jeu");
        List<Joueur> joueurs = equipe.getJoueurs();
        return joueurs.subList(0, Math.min(joueurs.size(), jeu.nombreJoueursRequis()));
    }
    public Match(Equipe equipe1, Equipe equipe2, Jeu jeu, Arbitre arbitre,
                 List<Joueur> alignement1, List<Joueur> alignement2) {
        this.equipe1 = Objects.requireNonNull(equipe1);
        this.equipe2 = Objects.requireNonNull(equipe2);
        this.jeu = Objects.requireNonNull(jeu);
        this.arbitre = Objects.requireNonNull(arbitre);
        if (equipe1 == equipe2) throw new IllegalArgumentException("Equipes identiques");
        if (!arbitre.estQualifie(jeu)) throw new IllegalArgumentException("Arbitre non qualifie");
        verifierAlignement(equipe1, alignement1);
        verifierAlignement(equipe2, alignement2);
        this.alignement1 = List.copyOf(alignement1);
        this.alignement2 = List.copyOf(alignement2);
    }
    private void verifierAlignement(Equipe equipe, List<Joueur> joueurs) {
        if (joueurs.size() != jeu.nombreJoueursRequis() || new HashSet<>(joueurs).size() != joueurs.size()
                || !equipe.getJoueurs().containsAll(joueurs)) {
            throw new IllegalArgumentException("Alignement invalide");
        }
    }
    /** Une chance sur dix d'incident, sinon victoire de l'une des équipes. */
    public synchronized void jouer(Random hasard) {
        Objects.requireNonNull(hasard, "hasard");
        if (statut != Statut.PLANIFIE) throw new IllegalStateException("Match deja traite");
        arbitre.ajouterMatchArbitre(); // même un match interrompu a été arbitré
        if (hasard.nextInt(100) < 10) { statut = Statut.INCIDENT; return; }
        vainqueur = hasard.nextBoolean() ? equipe1 : equipe2;
        vainqueur.ajouterVictoire();
        statut = Statut.TERMINE;
    }
    public Statut getStatut() { return statut; }
    public Equipe getVainqueur() { return vainqueur; }
    public Equipe getEquipe1() { return equipe1; }
    public Equipe getEquipe2() { return equipe2; }
    public Jeu getJeu() { return jeu; }
    public Arbitre getArbitre() { return arbitre; }
    public List<Joueur> getAlignement1() { return alignement1; }
    public List<Joueur> getAlignement2() { return alignement2; }
}
