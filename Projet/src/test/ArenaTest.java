import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires JUnit 5 pour valider l'encapsulation et les règles métier critiques.
 */
public class ArenaTest {

    @Test
    void testPromotionJoueurApresTroisVictoires() {
        Joueur j = new Joueur(1, "Nom", "Prenom", "Pseudo");
        assertFalse(j.estPro());
        j.ajouterVictoire();
        j.ajouterVictoire();
        assertFalse(j.estPro());
        j.ajouterVictoire();
        assertTrue(j.estPro()); // Promotion automatique validée
    }

    @Test
    void testEquipeUniqueParJoueur() {
        Joueur j = new Joueur(1, "Nom", "Prenom", "Pseudo");
        Equipe e1 = new Equipe("E1");
        Equipe e2 = new Equipe("E2");
        e1.ajouterJoueur(j);
        // Doit lever une exception si le joueur tente de rejoindre une seconde équipe
        assertThrows(IllegalStateException.class, () -> e2.ajouterJoueur(j));
    }
}
