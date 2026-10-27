import jakarta.persistence.*;
import java.util.List;

/**
 * Entité JPA stockant le bilan et le rapport d'un tournoi dans la base H2.
 */
@Entity
@Table(name = "rapports")
public class RapportTournoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomTournoi;
    private String nomJeu;
    private int matchsJoues;
    private String vainqueur;
    private boolean reussite;

    protected RapportTournoi() {}

    public RapportTournoi(String nomTournoi, String nomJeu, int matchsJoues, String vainqueur, boolean reussite) {
        this.nomTournoi = nomTournoi;
        this.nomJeu = nomJeu;
        this.matchsJoues = matchsJoues;
        this.vainqueur = vainqueur;
        this.reussite = reussite;
    }

    public Long getId() { return id; }
    @Override
    public String toString() {
        return "Rapport[Tournoi=" + nomTournoi + ", Jeu=" + nomJeu + ", Vainqueur=" + vainqueur + ", Reussi=" + reussite + "]";
    }
}

/**
 * DAO / Repository épuré pour gérer les opérations JPA sur l'entité RapportTournoi.
 */
class RapportRepository {
    private EntityManager em;

    public RapportRepository(EntityManager em) { this.em = em; }

    public void sauvegarder(RapportTournoi r) {
        em.getTransaction().begin();
        em.persist(r);
        em.getTransaction().commit();
    }

    public List<RapportTournoi> trouverTous() {
        return em.createQuery("SELECT r FROM RapportTournoi r", RapportTournoi.class).getResultList();
    }
}
