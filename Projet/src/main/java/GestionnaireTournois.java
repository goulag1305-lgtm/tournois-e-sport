import jakarta.persistence.*;
import java.util.*;
import java.util.concurrent.*;

/** Planifie les tournois et limite à trois les simulations simultanées. */
public class GestionnaireTournois {
    private final List<Tournoi> tournois = new ArrayList<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(3);
    private final long dureeMatchMs;
    private final Random hasard;
    private final EntityManagerFactory base; // null uniquement pour les tests sans H2.

    public GestionnaireTournois(long dureeMatchMs, long graine, EntityManagerFactory base) {
        if (dureeMatchMs < 0) throw new IllegalArgumentException("Duree negative");
        this.dureeMatchMs = dureeMatchMs;
        this.hasard = new Random(graine);
        this.base = base;
    }

    public synchronized void ajouterTournoi(Tournoi tournoi) {
        if (tournoi == null || tournois.contains(tournoi)) throw new IllegalArgumentException("Tournoi invalide");
        tournois.add(tournoi);
    }

    /** Cherche dans les inscriptions actuelles pour éviter un index devenu obsolete. */
    public synchronized Equipe trouverEquipe(String nom) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom d'equipe obligatoire");
        for (Tournoi tournoi : tournois) {
            for (Equipe equipe : tournoi.getEquipes()) {
                if (equipe.getNom().equalsIgnoreCase(nom)) return equipe;
            }
        }
        return null;
    }

    public synchronized List<Tournoi> tournoisPlanifies() {
        List<Tournoi> resultat = new ArrayList<>();
        for (Tournoi t : tournois) if (t.getStatut() == Tournoi.Statut.PLANIFIE) resultat.add(t);
        Collections.sort(resultat);
        return resultat;
    }

    public synchronized List<Tournoi> tournoisTermines() {
        List<Tournoi> resultat = new ArrayList<>();
        for (Tournoi t : tournois) if (t.getStatut() == Tournoi.Statut.TERMINE) resultat.add(t);
        resultat.sort((a, b) -> Double.compare(b.getBudget(), a.getBudget()));
        return resultat;
    }

    public synchronized List<Match> matchsEquipe(Equipe equipe) {
        List<Match> resultat = new ArrayList<>();
        for (Tournoi t : tournois) for (Match m : t.getMatchs())
            if (m.getEquipe1() == equipe || m.getEquipe2() == equipe) resultat.add(m);
        return resultat;
    }

    public synchronized List<Arbitre> arbitresOccupes() {
        Set<Arbitre> occupes = new HashSet<>();
        for (Tournoi t : tournois) if (t.getStatut() == Tournoi.Statut.EN_COURS)
            occupes.addAll(t.getArbitres());
        return new ArrayList<>(occupes);
    }

    public synchronized List<Arbitre> arbitresDisponibles(List<Arbitre> tous) {
        List<Arbitre> resultat = new ArrayList<>(tous);
        resultat.removeAll(arbitresOccupes());
        return resultat;
    }

    /** synchronized empêche deux lancements de dépasser ensemble la limite de 3. */
    public synchronized Tournoi lancerSuivant() {
        int enCours = 0;
        for (Tournoi t : tournois) if (t.getStatut() == Tournoi.Statut.EN_COURS) enCours++;
        if (enCours >= 3) throw new IllegalStateException("Trois tournois deja en cours");

        List<Tournoi> planifies = tournoisPlanifies();
        if (planifies.isEmpty()) throw new IllegalStateException("Aucun tournoi planifie");
        Tournoi prochain = planifies.get(0); // Le plus proche dans le temps.
        prochain.verifierDemarrage();

        // Un joueur, un coach ou une équipe ne participe pas à deux tournois actifs.
        for (Tournoi t : tournois) {
            if (t.getStatut() != Tournoi.Statut.EN_COURS) continue;
            for (Arbitre arbitre : prochain.getArbitres()) {
                if (t.getArbitres().contains(arbitre)) {
                    throw new IllegalStateException("Arbitre deja occupe");
                }
            }
            for (Equipe equipe : prochain.getEquipes()) {
                for (Equipe active : t.getEquipes()) {
                    if (equipe == active || equipe.getCoach() == active.getCoach())
                        throw new IllegalStateException("Equipe ou coach deja occupe");
                    for (Joueur joueur : equipe.getJoueurs())
                        if (active.getJoueurs().contains(joueur))
                            throw new IllegalStateException("Joueur deja occupe");
                }
            }
        }

        List<Equipe> equipesVerrouillees = new ArrayList<>();
        try {
            for (Equipe equipe : prochain.getEquipes()) {
                equipe.verrouiller();
                equipesVerrouillees.add(equipe);
            }
            prochain.demarrer();
        } catch (RuntimeException | Error erreur) {
            deverrouiller(equipesVerrouillees);
            throw erreur;
        }
        Random tirage = new Random(hasard.nextLong());

        Future<RapportTournoi> tache;
        try {
            tache = pool.submit(() -> {
                RapportTournoi rapport;
                try {
                    rapport = prochain.simuler(tirage, dureeMatchMs);
                    if (base != null) sauvegarder(rapport);
                } catch (InterruptedException erreur) {
                    prochain.echouer();
                    Thread.currentThread().interrupt();
                    throw erreur;
                } catch (RuntimeException | Error erreur) {
                    prochain.echouer();
                    throw erreur;
                } finally {
                    deverrouiller(equipesVerrouillees);
                }
                prochain.terminer(rapport);
                return rapport;
            });
        } catch (RuntimeException | Error erreur) {
            prochain.echouer();
            deverrouiller(equipesVerrouillees);
            throw erreur;
        }
        prochain.definirTache(tache);
        return prochain;
    }

    private void deverrouiller(List<Equipe> equipes) {
        for (Equipe equipe : equipes) equipe.deverrouiller();
    }

    /** Seuls les rapports sont enregistrés dans la base H2. */
    private void sauvegarder(RapportTournoi rapport) {
        EntityManager em = base.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rapport);
            em.getTransaction().commit();
        } catch (RuntimeException erreur) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw erreur;
        } finally { em.close(); }
    }

    public List<RapportTournoi> tousLesRapports() {
        if (base == null) throw new IllegalStateException("Base absente");
        EntityManager em = base.createEntityManager();
        try { return em.createQuery("SELECT r FROM RapportTournoi r", RapportTournoi.class).getResultList(); }
        finally { em.close(); }
    }

    public List<RapportTournoi> rapportsParJeu(String jeu) {
        if (base == null) throw new IllegalStateException("Base absente");
        EntityManager em = base.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM RapportTournoi r WHERE r.nomJeu = :jeu", RapportTournoi.class)
                    .setParameter("jeu", jeu).getResultList();
        } finally { em.close(); }
    }

    /** Arrêt propre des threads, à appeler à la fin du Main. */
    public void fermer() throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) pool.shutdownNow();
    }
}
