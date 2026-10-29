import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
/** Seule entité persistée dans la base H2. */
@Entity
@Table(name = "rapports")
public class RapportTournoi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomTournoi;
    private LocalDate date;
    private String nomJeu;
    private int matchsPrevus;
    private int matchsJoues;
    private String equipeGagnante;
    private boolean reussite;
    protected RapportTournoi() { } // requis par JPA
    public RapportTournoi(String nomTournoi, LocalDate date, String nomJeu, int matchsPrevus,
                          int matchsJoues, String equipeGagnante, boolean reussite) {
        this.nomTournoi = texteObligatoire(nomTournoi, "nom tournoi");
        this.date = Objects.requireNonNull(date, "date");
        this.nomJeu = texteObligatoire(nomJeu, "nom jeu");
        if (matchsPrevus < 1 || matchsJoues < 0 || matchsJoues > matchsPrevus) {
            throw new IllegalArgumentException("Nombre de matchs invalide");
        }
        this.matchsPrevus = matchsPrevus;
        this.matchsJoues = matchsJoues;
        this.equipeGagnante = texteObligatoire(equipeGagnante, "equipe gagnante");
        this.reussite = reussite;
    }

    private static String texteObligatoire(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ " + champ + " est obligatoire");
        }
        return valeur;
    }
    public Long getId() { return id; }
    public String getNomTournoi() { return nomTournoi; }
    public LocalDate getDate() { return date; }
    public String getNomJeu() { return nomJeu; }
    public int getMatchsPrevus() { return matchsPrevus; }
    public int getMatchsJoues() { return matchsJoues; }
    public String getEquipeGagnante() { return equipeGagnante; }
    public boolean isReussite() { return reussite; }
    @Override public String toString() {
        return nomTournoi + " / " + nomJeu + " / " + matchsJoues + "/" + matchsPrevus
                + " / gagnant=" + equipeGagnante + " / reussi=" + reussite;
    }
}
