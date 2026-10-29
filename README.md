## Installation et utilisation avec Maven

### Prérequis

Pour exécuter le projet, installer :

- **Java JDK 17 ou supérieur** (le projet compile en Java 17).
- **Apache Maven**.
- **Git** pour récupérer le projet.
- **VS Code** avec les extensions Java (recommandé).

### 1. Vérifier les installations

Ouvrir un terminal PowerShell et exécuter :

```powershell
java -version
javac -version
mvn -version
```

Si `mvn` n'est pas reconnu, télécharger Apache Maven depuis https://maven.apache.org/download.cgi, extraire l'archive et ajouter le dossier `bin` de Maven à la variable d'environnement `Path` de Windows.

Redémarrer ensuite le terminal.

### 2. Ouvrir le projet

Ouvrir dans VS Code le dossier contenant `pom.xml`.

Toutes les commandes suivantes doivent être exécutées depuis ce dossier.

### 3. Compiler et lancer les tests

```powershell
mvn clean test
```

Cette commande télécharge automatiquement les dépendances nécessaires, compile les classes Java et exécute les tests JUnit.

Le résultat attendu est :

```text
Tests run: 7, Failures: 0, Errors: 0
BUILD SUCCESS
```

### 4. Exécuter le programme

```powershell
mvn exec:java
```

Le programme lance une démonstration du tournoi e-sport et affiche notamment les résultats, le classement et le rapport.

### 5. Base de données H2

La base de données H2 est utilisée pour enregistrer les rapports de tournoi avec JPA/Hibernate.

Le fichier de configuration se trouve ici :

```text
src/main/resources/META-INF/persistence.xml
```

La base est créée automatiquement dans le dossier `data/` lors de l'exécution.

Il n'est pas nécessaire d'installer un serveur de base de données.

### 6. Structure des fichiers

```text
Projet/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── META-INF/
│   │           └── persistence.xml
│   └── test/
│       └── java/
│           └── ArenaTest.java
└── conception/
```

### 7. Problèmes fréquents

**`mvn` n'est pas reconnu :** vérifier l'installation de Maven et la variable `Path`.

**`No tests to run` :** vérifier que `ArenaTest.java` se trouve dans `src/test/java/`.

**`No Persistence provider for EntityManager named arenaPU` :** vérifier que `persistence.xml` est bien dans `src/main/resources/META-INF/`.

**`BUILD FAILURE` :** consulter les lignes `[ERROR]` dans le terminal pour identifier le problème.

### 8. Fichiers à ne pas envoyer sur GitHub

Ne pas ajouter les dossiers `target/` et `data/` au dépôt : ils contiennent des fichiers générés localement.

Chaque membre de l'équipe peut reconstruire le projet avec Maven à partir des sources et du fichier `pom.xml`.