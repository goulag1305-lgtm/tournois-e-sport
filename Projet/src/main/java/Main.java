import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.*;

/**
 * Classe principale exécutant la démonstration complète de l'application IMT Arena.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== DEMO IMT ARENA (VERSION ULTIMATE FLAT & COMMENTEE) ===");

        // 1. Initialisation d'un jeu d'équipe et d'un arbitre qualifié
        Jeu valorant = new JeuEquipe("Valorant", 3);
        Arbitre arbitre = new Arbitre(1, "Dupont", "Jean", "Arb1", valorant);

        // 2. Création de 3 équipes avec leurs coachs et joueurs respectifs
        List<Equipe> equipes = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Equipe eq = new Equipe("Equipe_" + i);
            eq.setCoach(new Coach(i, "Coach", "A", "C" + i));
            for (int j = 1; j <= 3; j++) {
                eq.ajouterJoueur(new Joueur(i * 10 + j, "Joueur", "B", "J" + i + "_" + j));
            }
            equipes.add(eq);
        }

        // 3. Planification et génération des matchs du tournoi
        Tournoi tournoi = new Tournoi("IMT Arena Cup", LocalDate.now(), valorant);
        equipes.forEach(tournoi::inscrireEquipe);
        tournoi.genererMatchs(arbitre);

        // 4. Simulation du tournoi
        tournoi.simuler(new Random(42));
        System.out.println("Tournoi réussi (>=70% matchs joués) : " + tournoi.estReussi());

        // 5. Persistance du rapport de tournoi via JPA (Base H2)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("arenaPU");
        EntityManager em = emf.createEntityManager();
        RapportRepository repo = new RapportRepository(em);

        Equipe vainqueur = equipes.stream().max(Comparator.comparingInt(Equipe::getPoints)).orElse(equipes.get(0));
        long nbJoues = tournoi.getMatchs().stream().filter(m -> m.getStatut() == StatutMatch.TERMINE).count();

        RapportTournoi rapport = new RapportTournoi(tournoi.getNom(), valorant.getNom(), (int) nbJoues, vainqueur.getNom(), tournoi.estReussi());
        repo.sauvegarder(rapport);

        System.out.println("Rapport sauvegardé en base H2 : " + repo.trouverTous());

        em.close();
        emf.close();
    }
}
