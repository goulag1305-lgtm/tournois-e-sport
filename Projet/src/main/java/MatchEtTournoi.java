import java.time.LocalDate;
import java.util.*;

enum StatutMatch { EN_ATTENTE, TERMINE, ANNULE_INCIDENT }
enum StatutTournoi { PLANIFIE, EN_COURS, TERMINE }

/**
 * Représente un match opposant deux équipes sous la supervision d'un arbitre.
 */
class Match {
    private Equipe e1, e2, vainqueur;
    private Jeu jeu;
    private Arbitre arbitre;
    private StatutMatch statut = StatutMatch.EN_ATTENTE;

    public Match(Equipe e1, Equipe e2, Jeu jeu, Arbitre arbitre) {
        if (e1.equals(e2)) throw new IllegalArgumentException("Une equipe ne peut pas jouer contre elle-meme.");
        if (!arbitre.estQualifie(jeu)) throw new IllegalArgumentException("Arbitre non qualifie pour ce jeu.");
        this.e1 = e1;
        this.e2 = e2;
        this.jeu = jeu;
        this.arbitre = arbitre;
    }

    /** Simule le déroulement d'un match (gère un aléas de 10% d'incident technique). */
    public synchronized void jouer(Random random) {
        if (statut != StatutMatch.EN_ATTENTE) return;
        if (random.nextInt(100) < 10) { // 10% de chance d'incident
            statut = StatutMatch.ANNULE_INCIDENT;
        } else {
            vainqueur = random.nextBoolean() ? e1 : e2;
            vainqueur.ajouterPoints(3); // 3 points pour le vainqueur
            if (vainqueur.getCoach() != null) vainqueur.getCoach().incVictoire();
            for (Joueur j : vainqueur.getJoueurs()) j.ajouterVictoire();
            arbitre.incMatchs();
            statut = StatutMatch.TERMINE;
        }
    }

    public StatutMatch getStatut() { return statut; }
    public Equipe getVainqueur() { return vainqueur; }
    public Equipe getE1() { return e1; }
    public Equipe getE2() { return e2; }
}

/**
 * Représente un tournoi regroupant entre 3 et 5 équipes selon un format de type Round-Robin.
 */
public class Tournoi implements Comparable<Tournoi> {
    private String nom;
    private LocalDate date;
    private Jeu jeu;
    private List<Equipe> equipes = new ArrayList<>();
    private List<Match> matchs = new ArrayList<>();
    private StatutTournoi statut = StatutTournoi.PLANIFIE;

    public Tournoi(String nom, LocalDate date, Jeu jeu) {
        this.nom = nom;
        this.date = date;
        this.jeu = jeu;
    }

    public void inscrireEquipe(Equipe e) {
        if (e.getJoueurs().size() < jeu.nombreJoueursRequis()) {
            throw new IllegalArgumentException("L'equipe n'a pas assez de joueurs.");
        }
        equipes.add(e);
    }

    /** Génère toutes les confrontations possibles entre les équipes inscrites. */
    public void genererMatchs(Arbitre arbitre) {
        matchs.clear();
        for (int i = 0; i < equipes.size(); i++) {
            for (int j = i + 1; j < equipes.size(); j++) {
                matchs.add(new Match(equipes.get(i), equipes.get(j), jeu, arbitre));
            }
        }
    }

    /** Lance la simulation de tous les matchs du tournoi. */
    public void simuler(Random random) {
        statut = StatutTournoi.EN_COURS;
        for (Match m : matchs) {
            m.jouer(random);
        }
        statut = StatutTournoi.TERMINE;
    }

    /** Vérifie la règle des 70% de réussite (ratio de matchs joués/terminés). */
    public boolean estReussi() {
        long joues = matchs.stream().filter(m -> m.getStatut() == StatutMatch.TERMINE).count();
        return matchs.isEmpty() ? false : ((double) joues / matchs.size()) >= 0.7;
    }

    public String getNom() { return nom; }
    public LocalDate getDate() { return date; }
    public Jeu getJeu() { return jeu; }
    public List<Match> getMatchs() { return matchs; }
    public List<Equipe> getEquipes() { return equipes; }

    @Override
    public int compareTo(Tournoi o) { return this.date.compareTo(o.date); }
}
