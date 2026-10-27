# IMT Arena - Version Documentée & Structurée à Plat

Ce projet implémente un système minimaliste de gestion et de simulation de tournois e-sport en Java, respectant une structure à plat sans sous-dossiers de packages pour une simplicité maximale.

## Structure du Projet

```text
tp-esport-arena-documented/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── Personne.java
    │   │   ├── Jeu.java
    │   │   ├── Equipe.java
    │   │   ├── MatchEtTournoi.java
    │   │   ├── Rapport.java
    │   │   └── Main.java
    │   └── resources/META-INF/
    │       └── persistence.xml
    └── test/
        └── ArenaTest.java
```

## Compilation et Exécution

```bash
mvn clean compile exec:java
mvn test
```
