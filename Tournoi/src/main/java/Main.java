import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
/** Démonstration des principales fonctionnalités, sans saisie utilisateur. */
public class Main {
    public static void main(String[] args) throws Exception {
        Jeu jeu = new JeuEquipe("Valorant", 3, 3);
        Arbitre arbitre = new Arbitre(1, "Dupont", "Jean", "Arb1", "M", "0600000000");
        arbitre.ajouterQualification(jeu);
        Tournoi tournoi = new Tournoi("IMT Arena Cup", jeu, LocalDate.now(), 1200, 2);
        tournoi.ajouterArbitre(arbitre);
        for (int i = 1; i <= 4; i++) {
            Equipe equipe = new Equipe("Equipe_" + i);
            equipe.setCoach(new Coach(i + 10, "Coach", "C" + i, "Coach" + i, "M", "0600000000"));
            for (int j = 1; j <= 3; j++) {
                equipe.ajouterJoueur(new Joueur(i * 10 + j + 100, "Joueur", "J" + j,
                        "P" + i + "_" + j, "M", "0600000000"));
            }
            tournoi.ajouterEquipe(equipe);
        }
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("arenaPU");
        GestionnaireTournois gestionnaire = new GestionnaireTournois(100, 42, emf);
        try {
            gestionnaire.ajouterTournoi(tournoi);
            System.out.println("Planifies : " + gestionnaire.tournoisPlanifies().size());
            gestionnaire.lancerSuivant();
            System.out.println("Statut au lancement : " + tournoi.getStatut());
            RapportTournoi rapport = tournoi.attendreRapport(10, TimeUnit.SECONDS);
            System.out.println("Rapport : " + rapport);
            System.out.println("Classement : " + tournoi.classement());
            System.out.println("Matchs par vainqueur : " + tournoi.matchsParVainqueur().keySet());
        } finally {
            gestionnaire.fermer();
            emf.close();
        }
    }
}
