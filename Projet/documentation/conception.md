# Dossier de conception — IMT Arena

**Diagramme des classes :** `diagramme-classes.puml` (meme dossier).

## 1. Classes et héritage

`Personne` regroupe id, nom, prénom, pseudo, genre et téléphone. `Joueur`, `Coach` et `Arbitre` héritent de cette classe abstraite. `Jeu` est abstraite : `JeuSolo` demande un joueur par équipe et `JeuEquipe` en demande de trois à cinq. On peut créer une autre sous-classe de `Jeu` sans modifier `Match`.

Une `Equipe` contient une liste de joueurs, un coach et des points. Un joueur et un coach ne peuvent appartenir qu'à une équipe à la fois. Un joueur devient professionnel après trois victoires de son équipe depuis son arrivée. Un coach conserve ses victoires de carrière. Un arbitre possède plusieurs qualifications.

Un `Match` oppose deux équipes différentes, sur un jeu, avec un arbitre qualifié et des alignements valides. Un `Tournoi` contient au moins quatre équipes au lancement, au moins un arbitre, un budget et une à quatre arènes. Chaque paire d'équipes joue une seule fois. Le classement du tournoi compte ses propres matchs et ne réutilise pas les points accumulés par les équipes dans d'autres compétitions.

## 2. Collections choisies

- `ArrayList` : équipes, joueurs, arbitres, matchs, tournois. Facile à parcourir avec `for`.
- `HashSet` : qualifications d'arbitre et vérification de l'absence de joueurs/coachs en double.
- `TreeMap` : regrouper les matchs par nom d'équipe gagnante, trié alphabétiquement.
- `Comparable<Equipe>` : comparaison des équipes par points cumulés, puis nom. Le classement d'un tournoi se base sur les victoires de ce tournoi. `Comparator` : tournois terminés par budget décroissant.
- Les listes sont renvoyées sous forme de copies non modifiables pour proteger les collections internes.

## 3. Simulation et synchronisation

Le gestionnaire utilise `ExecutorService` avec un pool fixe de **trois threads**. La méthode `lancerSuivant()` est `synchronized` : la vérification de la limite et le passage du tournoi à `EN_COURS` sont effectués ensemble. Elle choisit le tournoi planifié dont la date est la plus proche et refuse les équipes, coachs, joueurs et arbitres déjà engagés dans un tournoi en cours. En cas d'échec de simulation ou de persistance, le tournoi passe au statut `ECHEC` et les effectifs sont déverrouillés.

La durée simulée est `ceil(nombreMatchs × duréeMatch / nombreArènes)`. Après cette attente, chaque match a 10 % de risque d'incident ; sinon son gagnant est tiré au sort avec `Random`. Les points et victoires sont mis à jour. Les effectifs ne peuvent pas être modifiés pendant le tournoi. Le statut et le rapport sont visibles entre threads grâce à `volatile`. `Future.get` attend le résultat avec un délai maximum. `fermer()` arrête proprement le pool.

## 4. Persistance et tests

Seul `RapportTournoi` est annoté `@Entity` et enregistré dans **H2** via **JPA/Hibernate** : id, nom, date, jeu, matchs prévus/joués, équipe gagnante, réussite (au moins 70 % des matchs joués). Le gestionnaire propose la lecture de tous les rapports ou par jeu. Les tests JUnit 5 couvrent notamment promotion, exclusivité d'équipe, arbitre qualifié, nombre de matchs, nombre minimal d'équipes, classement d'un tournoi et persistance H2.

**Patterns :** aucun design pattern ajouté artificiellement. L'héritage et le polymorphisme de `Jeu` répondent au besoin d'extension.
