import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

/** Un tournoi : équipes, arbitres, matchs et rapport final. */
public class Tournoi implements Comparable<Tournoi> {
    public enum Statut { PLANIFIE, EN_COURS, TERMINE, ECHEC }

    private final String nom;
    private final Jeu jeu;
    private final LocalDate date;
    private final double budget;
    private final int nombreArenes;
    private final List<Equipe> equipes = new ArrayList<>();
    private final List<Arbitre> arbitres = new ArrayList<>();
    private final List<Match> matchs = new ArrayList<>();
    private volatile Statut statut = Statut.PLANIFIE;
    private volatile RapportTournoi rapport;
    // Future permet d'attendre un résultat avec un délai maximum.
    private Future<RapportTournoi> tache;

    public Tournoi(String nom, Jeu jeu, LocalDate date, double budget, int nombreArenes) {
        if (nom == null || nom.isBlank() || jeu == null || date == null)
            throw new IllegalArgumentException("Informations du tournoi manquantes");
        if (!Double.isFinite(budget) || budget < 0) throw new IllegalArgumentException("Budget invalide");
        if (nombreArenes < 1 || nombreArenes > 4) throw new IllegalArgumentException("1 a 4 arenes");
        this.nom = nom;
        this.jeu = jeu;
        this.date = date;
        this.budget = budget;
        this.nombreArenes = nombreArenes;
    }

    public synchronized void ajouterEquipe(Equipe equipe) {
        if (statut != Statut.PLANIFIE) throw new IllegalStateException("Tournoi deja lance");
        if (equipe == null || equipes.contains(equipe)) throw new IllegalArgumentException("Equipe invalide ou deja inscrite");
        for (Equipe e : equipes) if (e.getNom().equalsIgnoreCase(equipe.getNom()))
            throw new IllegalArgumentException("Nom d'equipe deja inscrit");
        equipes.add(equipe);
    }

    public synchronized void ajouterArbitre(Arbitre arbitre) {
        if (statut != Statut.PLANIFIE) throw new IllegalStateException("Tournoi deja lance");
        if (arbitre == null || !arbitre.estQualifie(jeu)) throw new IllegalArgumentException("Arbitre non qualifie");
        if (!arbitres.contains(arbitre)) arbitres.add(arbitre);
    }

    /** Contrôles demandés avant le lancement. */
    synchronized void verifierDemarrage() {
        if (statut != Statut.PLANIFIE) throw new IllegalStateException("Tournoi deja lance");
        if (equipes.size() < 4) throw new IllegalStateException("Il faut au moins 4 equipes");
        if (arbitres.isEmpty()) throw new IllegalStateException("Il faut un arbitre qualifie");
        Set<Joueur> joueurs = new HashSet<>();
        Set<Coach> coachs = new HashSet<>();
        for (Equipe e : equipes) {
            if (e.getCoach() == null || e.getJoueurs().size() < jeu.nombreJoueursRequis())
                throw new IllegalStateException("Equipe incomplete : " + e.getNom());
            if (!coachs.add(e.getCoach())) throw new IllegalStateException("Coach utilise deux fois");
            for (Joueur j : e.getJoueurs())
                if (!joueurs.add(j)) throw new IllegalStateException("Joueur utilise deux fois");
        }
    }

    /** Une confrontation par paire : pour 4 équipes, 6 matchs. */
    synchronized void demarrer() {
        verifierDemarrage();
        int numero = 0;
        for (int i = 0; i < equipes.size(); i++) {
            for (int j = i + 1; j < equipes.size(); j++) {
                matchs.add(new Match(equipes.get(i), equipes.get(j), jeu,
                        arbitres.get(numero % arbitres.size())));
                numero++;
            }
        }
        statut = Statut.EN_COURS;
    }

    /** La simulation s'exécute dans le thread fourni par ExecutorService. */
    RapportTournoi simuler(Random hasard, long dureeMatchMs) throws InterruptedException {
        // Formule du sujet : matchs * durée d'un match / nombre d'arènes.
        long duree = (long) Math.ceil((double) matchs.size() * dureeMatchMs / nombreArenes);
        Thread.sleep(duree);
        for (Match match : matchs) match.jouer(hasard);
        int joues = 0;
        for (Match match : matchs) if (match.getStatut() == Match.Statut.TERMINE) joues++;
        Equipe gagnante = classement().get(0);
        return new RapportTournoi(nom, date, jeu.getNom(), matchs.size(), joues,
                gagnante.getNom(), joues >= 0.7 * matchs.size());
    }

    // Appelées uniquement par le gestionnaire de tournois.
    synchronized void definirTache(Future<RapportTournoi> nouvelleTache) { tache = nouvelleTache; }
    synchronized void terminer(RapportTournoi nouveauRapport) {
        if (statut != Statut.EN_COURS) throw new IllegalStateException("Tournoi non en cours");
        rapport = Objects.requireNonNull(nouveauRapport);
        statut = Statut.TERMINE;
    }

    synchronized void echouer() {
        if (statut == Statut.EN_COURS) statut = Statut.ECHEC;
    }

    /** Attend la fin, sans bloquer indéfiniment le programme principal. */
    public RapportTournoi attendreRapport(long delai, TimeUnit unite)
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<RapportTournoi> f;
        synchronized (this) { f = tache; }
        if (f == null) throw new IllegalStateException("Tournoi pas encore lance");
        return f.get(delai, unite);
    }

    public Statut getStatut() { return statut; }
    public RapportTournoi getRapport() { return rapport; }
    public String getNom() { return nom; }
    public Jeu getJeu() { return jeu; }
    public LocalDate getDate() { return date; }
    public double getBudget() { return budget; }
    public int getNombreArenes() { return nombreArenes; }
    public synchronized List<Equipe> getEquipes() { return Collections.unmodifiableList(new ArrayList<>(equipes)); }
    public synchronized List<Arbitre> getArbitres() { return Collections.unmodifiableList(new ArrayList<>(arbitres)); }
    public synchronized List<Match> getMatchs() { return Collections.unmodifiableList(new ArrayList<>(matchs)); }

    public List<Equipe> classement() {
        List<Equipe> resultat = new ArrayList<>(getEquipes());
        Map<Equipe, Integer> victoires = new HashMap<>();
        for (Equipe equipe : resultat) victoires.put(equipe, 0);
        for (Match match : getMatchs()) {
            if (match.getStatut() == Match.Statut.TERMINE) {
                Equipe vainqueur = match.getVainqueur();
                victoires.put(vainqueur, victoires.get(vainqueur) + 1);
            }
        }
        resultat.sort((a, b) -> {
            int comparaison = Integer.compare(victoires.get(b), victoires.get(a));
            return comparaison != 0 ? comparaison : a.getNom().compareToIgnoreCase(b.getNom());
        });
        return resultat;
    }

    /** Classe les matchs selon le nom de leur équipe gagnante. */
    public Map<String, List<Match>> matchsParVainqueur() {
        Map<String, List<Match>> groupes = new TreeMap<>();
        for (Match match : getMatchs()) {
            String nomGagnant = match.getVainqueur() == null ? "Sans vainqueur" : match.getVainqueur().getNom();
            if (!groupes.containsKey(nomGagnant)) groupes.put(nomGagnant, new ArrayList<>());
            groupes.get(nomGagnant).add(match);
        }
        return groupes;
    }

    @Override public int compareTo(Tournoi autre) {
        int resultat = date.compareTo(autre.date);
        return resultat != 0 ? resultat : nom.compareToIgnoreCase(autre.nom);
    }
}
