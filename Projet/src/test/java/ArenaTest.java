import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ArenaTest {
    private Joueur joueur(int id) {
        return new Joueur(id, "Nom", "Prenom", "J" + id, "X", "0600");
    }

    private Coach coach(int id) {
        return new Coach(id, "Nom", "Prenom", "C" + id, "X", "0600");
    }

    private Arbitre arbitre(int id, Jeu jeu) {
        Arbitre arbitre = new Arbitre(id, "Nom", "Prenom", "A" + id, "X", "0600");
        arbitre.ajouterQualification(jeu);
        return arbitre;
    }

    private Tournoi tournoi(String nom, Jeu jeu, int jour, int id, int nombreEquipes) {
        Tournoi tournoi = new Tournoi(nom, jeu, LocalDate.now().plusDays(jour), 100, 1);
        tournoi.ajouterArbitre(arbitre(id, jeu));
        for (int i = 0; i < nombreEquipes; i++) {
            Equipe equipe = new Equipe(nom + "_E" + i);
            equipe.setCoach(coach(id + 100 + i));
            for (int j = 0; j < jeu.nombreJoueursRequis(); j++) {
                equipe.ajouterJoueur(joueur(id + 200 + i * 10 + j));
            }
            tournoi.ajouterEquipe(equipe);
        }
        return tournoi;
    }

    @Test
    void joueurEstPromuApresTroisVictoiresEtNePeutChangerDEquipeSansLaQuitter() {
        Joueur joueur = joueur(1);
        Equipe equipe = new Equipe("A");
        Equipe autreEquipe = new Equipe("B");
        equipe.ajouterJoueur(joueur);

        assertThrows(IllegalStateException.class, () -> autreEquipe.ajouterJoueur(joueur));
        equipe.ajouterVictoire();
        equipe.ajouterVictoire();
        assertFalse(joueur.estProfessionnel());
        equipe.ajouterVictoire();
        assertTrue(joueur.estProfessionnel());
    }

    @Test
    void coachEtArbitreRespectentLeursAffectations() {
        Jeu jeu = new JeuSolo("Combat", 1);
        Coach coach = coach(1);
        new Equipe("A").setCoach(coach);
        assertThrows(IllegalStateException.class, () -> new Equipe("B").setCoach(coach));

        Arbitre nonQualifie = new Arbitre(2, "Nom", "Prenom", "A2", "X", "0600");
        assertThrows(IllegalArgumentException.class,
                () -> new Match(new Equipe("C"), new Equipe("D"), jeu, nonQualifie));
    }

    @Test
    void tournoiJoueUneRencontreParPaireEtEnregistreSonRapportDansH2() throws Exception {
        Jeu jeu = new JeuSolo("Combat", 1);
        Tournoi tournoi = tournoi("Coupe", jeu, 0, 1, 4);
        EntityManagerFactory base = Persistence.createEntityManagerFactory("arenaPU", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:arena-test;DB_CLOSE_DELAY=-1",
                "hibernate.hbm2ddl.auto", "create-drop"));
        GestionnaireTournois gestionnaire = new GestionnaireTournois(0, 42, base);
        try {
            gestionnaire.ajouterTournoi(tournoi);
            gestionnaire.lancerSuivant();
            assertNotNull(tournoi.attendreRapport(5, TimeUnit.SECONDS));
            assertEquals(6, tournoi.getMatchs().size());
            assertEquals(Tournoi.Statut.TERMINE, tournoi.getStatut());
            assertEquals(1, gestionnaire.tousLesRapports().size());
        } finally {
            gestionnaire.fermer();
            base.close();
        }
    }

    @Test
    void tournoiRefuseMoinsDeQuatreEquipes() throws Exception {
        Jeu jeu = new JeuSolo("Combat", 1);
        Tournoi tournoi = tournoi("Petit", jeu, 0, 1, 3);
        GestionnaireTournois gestionnaire = new GestionnaireTournois(0, 42, null);
        try {
            gestionnaire.ajouterTournoi(tournoi);
            assertThrows(IllegalStateException.class, gestionnaire::lancerSuivant);
        } finally {
            gestionnaire.fermer();
        }
    }

    @Test
    void classementDependDesVictoiresDuTournoiEtNonDesPointsAnterieurs() {
        Jeu jeu = new JeuSolo("Combat", 1);
        Tournoi tournoi = tournoi("Coupe", jeu, 0, 1, 4);
        Equipe equipeAvecAnciensPoints = tournoi.getEquipes().get(1);
        for (int i = 0; i < 5; i++) equipeAvecAnciensPoints.ajouterVictoire();

        tournoi.demarrer();
        Random sansIncident = new Random(42) {
            @Override
            public int nextInt(int borne) {
                return borne - 1;
            }

            @Override
            public boolean nextBoolean() {
                return true;
            }
        };
        for (Match match : tournoi.getMatchs()) match.jouer(sansIncident);

        assertTrue(equipeAvecAnciensPoints.getPoints() > tournoi.classement().get(0).getPoints());
        assertNotSame(equipeAvecAnciensPoints, tournoi.classement().get(0));
    }

    @Test
    void gestionnaireLimiteLesTournoisSimultanesATrois() throws Exception {
        Jeu jeu = new JeuSolo("Combat", 1);
        GestionnaireTournois gestionnaire = new GestionnaireTournois(1000, 42, null);
        List<Tournoi> tournois = List.of(
                tournoi("Coupe1", jeu, 0, 1, 4),
                tournoi("Coupe2", jeu, 1, 2, 4),
                tournoi("Coupe3", jeu, 2, 3, 4),
                tournoi("Coupe4", jeu, 3, 4, 4));
        try {
            tournois.forEach(gestionnaire::ajouterTournoi);
            for (int i = 0; i < 3; i++) gestionnaire.lancerSuivant();
            assertThrows(IllegalStateException.class, gestionnaire::lancerSuivant);
        } finally {
            gestionnaire.fermer();
        }
    }
}
