# IMT Arena

IMT Arena est une application Java qui organise et simule des tournois e-sport.
Elle met en pratique l'heritage, l'encapsulation, les collections, les tests JUnit
et l'enregistrement de rapports avec JPA et H2.

## Organisation

```text
Projet/
|-- documentation/              # Explications de conception et diagramme UML
|-- data/                       # Base H2 creee par l'application
|-- src/
|   |-- main/java/              # Classes du programme (package par defaut)
|   |-- main/resources/META-INF/ # Configuration JPA
|   |-- test/java/              # Tests JUnit
|-- pom.xml
|-- README.md
```

Les classes sont dans le package par defaut pour garder le projet accessible
aux debutants. Les sources et les tests suivent les dossiers standard de Maven.

## Compiler et tester

Depuis le dossier contenant `pom.xml`, avec Java 17 et Maven installes :

```powershell
mvn test
mvn package
```

## Lancer la demonstration

```powershell
mvn exec:java
```

La demonstration cree un tournoi, joue ses matchs, affiche le classement et
enregistre le rapport dans la base H2 locale `data/arena`.

## Regles principales

- Un tournoi exige au moins quatre equipes et un arbitre qualifie pour le jeu.
- Chaque paire d'equipes se rencontre une fois.
- Une victoire ajoute trois points a l'equipe et compte pour la promotion des
  joueurs amateurs apres trois victoires obtenues depuis leur arrivee.
- Le classement d'un tournoi depend des victoires obtenues dans ce tournoi ;
  les points conserves par une equipe ne faussent pas les resultats d'une autre
  competition.
- Le gestionnaire lance au plus trois tournois en meme temps.
- Seuls les rapports de tournoi sont persistes avec JPA dans H2.

Le diagramme des classes est [documentation/diagramme-classes.puml](documentation/diagramme-classes.puml).
