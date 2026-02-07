# Projet Graphes de Markov - Partie 2

Mon projet est aussi disponible sur Github via "https://github.com/MehediT/graphIII"

## Description

Projet universitaire d'étude de graphes de Markov en temps discret, avec application aux prévisions météorologiques. Ce projet implémente l'algorithme de Tarjan pour le calcul de composantes fortement connexes et le calcul de distributions stationnaires.

## Structure du projet

```
graphII/
├── build.sbt                    # Configuration SBT
├── project/
│   └── build.properties         # Version SBT
├── src/
│   └── main/
│       ├── scala/
│       │   ├── MarkovGraph.scala   # Fonctions principales
│       │   └── Main.scala          # Point d'entrée et validation
│       └── resources/
│           └── exemple_meteo.txt   # Données météo (5 états)
├── RAPPORT_FINAL.pdf            # Rapport académique
├── RAPPORT_FINAL.md             # Meme Rapport que le pdf
├── inventaire_notions.md        # Inventaire des notions FP
└── README.md                    # Ce fichier
```

## Fonctionnalités

### 1. Calculs matriciels

- `adjacencyListToMatrix` : Conversion liste d'adjacence → matrice
- `multiplyMatrices` : Multiplication de matrices (O(n³))
- `diffMatrices` : Norme L1 entre deux matrices
- `matrixPower` : Calcul de M^k (programmation fonctionnelle avec `foldLeft`)

### 2. Algorithme de Tarjan

- `tarjanFromGraph` : Calcul des composantes fortement connexes (CFC)
- Complexité optimale : O(n + m)
- Implémentation fidèle au pseudo-code académique

### 3. Analyse des classes

- `extractSubmatrix` : Extraction de sous-matrice pour une classe
- `isPersistentClass` : Détection des classes persistantes/transitoires
- `stationaryDistributionOfClass` : Calcul de la distribution stationnaire

## Prérequis

- **Scala** 2.13.x
- **SBT** 1.9.x (ou supérieur)
- **JVM** 11 ou supérieur

### Installation

**macOS/Linux** :

```bash
# Installer SBT via SDKMAN
sdk install sbt

# Ou via Homebrew (macOS)
brew install sbt
```

**Windows** :

Télécharger depuis [https://www.scala-sbt.org/download.html](https://www.scala-sbt.org/download.html)

## Compilation et exécution

### Méthode 1 : Avec SBT (recommandé)

```bash
# Compilation
sbt compile

# Exécution
sbt run

# Compilation + exécution
sbt "run"
```


Pour compatibilité, le fichier `projet.sc` est conservé :

```bash
cd graphII
scala projet.sc
```

## Génération de la documentation

```bash
# Générer la Scaladoc
scaladoc -d scaladoc src/main/scala/MarkovGraph.scala src/main/scala/Main.scala

# La documentation est visible en ouvrant le index.hmlt qui est dans scaladoc/
```

Ouvrir `target/scala-2.13/api/index.html` dans un navigateur.

## Résultats attendus

L'exécution du programme affiche :

1. **Matrice M** (5×5) : matrice de transition météo
2. **M³** : distribution après 3 jours
3. **M⁷** : convergence vers la distribution stationnaire
4. **Convergence** : n tel que diff(Mⁿ, Mⁿ⁻¹) < 0.01
5. **Composantes fortement connexes** : partition du graphe
6. **Distributions stationnaires** : pour chaque classe (persistante ou transitoire)

### Exemple de sortie

```
=== Matrice M (exemple météo) ===
M =
  0.34   0.27   0.00   0.18   0.21
  0.20   0.40   0.20   0.00   0.20
  ...

=== Convergence (epsilon = 0.01) ===
Pour n = 9, diff(M^9, M^8) < 0.01
Distribution stationnaire (première ligne de M^n) :
0.16 0.36 0.13 0.05 0.30

=== Composantes fortement connexes ===
  C1: {1, 2, 3, 4, 5}

=== Distributions stationnaires par classe ===
  Classe 1 (persistante): [0.1641, 0.3591, 0.1320, 0.0518, 0.2930]
```

## Format du fichier de données

Le fichier `exemple_meteo.txt` suit le format :

```
5              ← Nombre de sommets
0 1 0.34       ← Arête : from to probability
0 3 0.18
1 0 0.20
...
```

## Notions de programmation fonctionnelle utilisées

- **Immutabilité** : Utilisation de `val` (variables immuables), `List`, `Map`
- **Fonctions d'ordre supérieur** : `map`, `flatMap`, `filter`, `foldLeft`, `forall`
- **Pattern matching** : Déstructuration dans les `case`
- **Récursion** : `matrixPower` avec `foldLeft`, `stationaryDistribution` avec récursion terminale
- **Collections immuables** : `List`, `Map` (sauf pour Tarjan qui nécessite mutabilité pour performance)

Voir `inventaire_notions.md` pour l'inventaire complet.

## Auteur

Master 1 Informatique - EFREI  
Année universitaire 2025-2026

## Références

- **Sujet** : `Projet Markov_Partie_2.pdf`
- **Pseudo-code Tarjan** : `Tarjan parcours pseudo-code.pdf`
- **Rapport** : `RAPPORT_FINAL.pdf`
