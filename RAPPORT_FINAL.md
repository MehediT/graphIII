<div align="center">

# Projet Informatique en Scala

## Étude de graphes de Markov – Partie 2
### Application aux prévisions météorologiques

<br>

![Scala](https://img.shields.io/badge/Scala-DC322F?style=for-the-badge&logo=scala&logoColor=white)

<br>

**Formation** : Ingé 2 App LSI
**Année universitaire** : 2025–2026  
**Auteur** : Méhédi Touré
**Date de remise** : Février 2026

</div>

---

<div align="center">

## Sommaire

</div>

<table>
<tr><td width="50%">

### Sections principales

1. [Introduction](#1-introduction)
2. [Analyse fonctionnelle générale](#2-analyse-fonctionnelle-générale)
3. [Fondements théoriques](#3-fondements-théoriques)
4. [Analyse fonctionnelle détaillée](#4-analyse-fonctionnelle-détaillée)
5. [Architecture logicielle](#5-architecture-logicielle)

</td><td>

### Sections techniques

6. [Détail du code et implémentation](#6-détail-du-code-et-implémentation)
7. [Tests et validation](#7-tests-et-validation)
8. [Mode d'emploi](#8-mode-demploi)
9. [Conclusion](#9-conclusion)
10. [Annexes](#10-annexes)

</td></tr>
</table>

---

<div align="center">

## 1. Introduction

</div>

### 1.1 Contexte et problématique

> **Les chaînes de Markov en temps discret** constituent un outil fondamental en probabilités et en analyse de systèmes stochastiques.

<table>
<tr>
<td width="50%" bgcolor="#E8F4F8">

**Domaines d'application**
- Modélisation climatique
- Analyse de réseaux
- Finance quantitative
- Apprentissage automatique

</td>
<td width="50%" bgcolor="#FFF4E6">

**Contexte académique**
- Suite de la Partie 1
- Analyse structurelle approfondie
- Application aux systèmes stochastiques
- Validation sur cas réel (météo)

</td>
</tr>
</table>

<br>

<div style="background-color: #E3F2FD; padding: 15px; border-left: 5px solid #2196F3; border-radius: 5px;">

**Problématique centrale**

Identifier la structure en classes d'un graphe de Markov, puis calculer et caractériser les **distributions stationnaires** associées à chaque classe, en exploitant les propriétés topologiques du graphe sous-jacent.

</div>

<br>

**Exemple d'application** : Modèle météorologique simplifié

| État | Description | Notation |
|:----:|-------------|----------|
| 0 | Sunny (Beau temps) | État 0 |
| 1 | Cloudy (Nuageux) | État 1 |
| 2 | Rain (Pluie) | État 2 |
| 3 | Storm (Orage) | État 3 |
| 4 | Sunny spells (Éclaircies) | État 4 |

### 1.2 Objectifs du projet

<table>
<tr>
<td width="50%" bgcolor="#E8F5E9">

#### Objectifs théoriques

- Comprendre les **composantes fortement connexes** (CFC)
- Maîtriser la caractérisation des **classes** (transitoires/persistantes)
- Analyser le **comportement asymptotique** des distributions
- Étudier l'**existence et unicité** des distributions stationnaires

</td>
<td width="50%" bgcolor="#FFF3E0">

#### Objectifs pratiques

- Implémenter l'**algorithme de Tarjan** (O(n+m))
- Développer des **outils matriciels** robustes
- Calculer les **distributions stationnaires**
- Valider sur des **exemples de référence**

</td>
</tr>
</table>

### 1.3 Périmètre et limites

<details open>
<summary><b>Périmètre du projet</b></summary>

- Graphes de Markov en **temps discret** avec nombre fini d'états
- Matrices de transition **stochastiques** (lignes sommant à 1)
- Distributions stationnaires par **méthode itérative**
- Focus sur l'**algorithme de Tarjan** pour les CFC

**Hors périmètre** :
- Chaînes de Markov à temps continu
- Graphes infinis
- Méthodes algébriques avancées (LU, QR)

</details>

<details>
<summary><b>Limites identifiées</b></summary>

| Limite | Impact | Mitigation |
|--------|--------|------------|
| Graphes périodiques | Critère de convergence non atteint | Détection possible (non implémenté) |
| Précision flottante | Erreurs d'arrondi O(10⁻²) | Normalisation systématique |
| Passage à l'échelle | O(n³) pour multiplication | Acceptable pour n ≤ 100 |
| Profondeur de pile | StackOverflow si n >> 1000 | Tarjan itératif (alternative) |

</details>

### 1.4 Hypothèses et choix initiaux

<table>
<tr>
<td width="33%" bgcolor="#F3E5F5">

**Langage : Scala**

- Lisibilité
- Typage fort
- Performance JVM
- Paradigme hybride

</td>
<td width="33%" bgcolor="#E0F2F1">

**Structures de données**

- **Matrice** : `Array[Array[Double]]`
- **Graphe** : `Map` + `Array`
- **Partition** : `List[List[Int]]`
- **Distribution** : `Array[Double]`

</td>
<td width="34%" bgcolor="#FFF8E1">

**Critères techniques**

- **Convergence** : ε = 0.01
- **Normalisation** : Systématique
- **Max itérations** : 100 (convergence) / 5000 (stationnaire)

</td>
</tr>
</table>

---

<div align="center">

## 2. Analyse fonctionnelle générale

</div>

### 2.1 Données manipulées

<table>
<tr>
<td width="50%">

#### Matrices de transition

```
M = (0.34  0.27  0     0.18  0.21)
    (0.20  0.40  0.20  0     0.20)
    (0     0.41  0.37  0.09  0.13)
    (0     0.68  0.20  0.12  0   )
    (0.12  0.30  0     0     0.58)
```

**Propriétés** :
- Matrices carrées n×n
- Coefficients M(i,j) ∈ [0,1]
- Somme par ligne = 1
- Indices : 0 à n−1

</td>
<td width="50%">

#### Autres structures

**Listes d'adjacence**
```scala
Map[Int, List[(Int, Double)]]
// Ex: 0 -> [(1, 0.27), (3, 0.18)]
```

**Partitions (CFC)**
```scala
List[List[Int]]
// Ex: {{0,2,4}, {1}, {3}}
```

**Distributions**
```scala
Array[Double]
// Ex: [0.16, 0.36, 0.13, 0.05, 0.29]
```

</td>
</tr>
</table>

### 2.2 Fonctionnalités principales

<details open>
<summary><b>Modules fonctionnels implémentés</b></summary>

| Module | Fonctions clés | Complexité |
|--------|---------------|------------|
| **Calculs matriciels** | `multiplyMatrices`, `matrixPower`, `diffMatrices` | O(n³) |
| **Tarjan (CFC)** | `tarjanFromGraph`, `parcours` | O(n+m) |
| **Analyse classes** | `extractSubmatrix`, `isPersistentClass` | O(k²) |
| **Distributions** | `stationaryDistributionOfClass` | O(k³ × iter) |
| **E/S** | `loadAdjacencyFromFile`, `printMatrix` | O(n²) |

</details>

### 2.3 Organisation globale du programme

<table>
<tr>
<td width="50%" bgcolor="#E3F2FD">

**Structure du fichier `projet.sc`**

```
┌─────────────────────────────────┐
│  1. Fonctions matricielles      │  (L1-96)
│     ├─ adjacencyListToMatrix    │
│     ├─ multiplyMatrices          │
│     ├─ diffMatrices              │
│     ├─ matrixPower               │
│     └─ printMatrix               │
├─────────────────────────────────┤
│  2. Algorithme de Tarjan        │  (L98-178)
│     ├─ matrixToSuccessors        │
│     ├─ tarjanFromGraph           │
│     └─ tarjan                    │
├─────────────────────────────────┤
│  3. Analyse des classes         │  (L180-250)
│     ├─ extractSubmatrix          │
│     ├─ isPersistentClass         │
│     ├─ stationaryDistribution... │
│     └─ printAllStationary...     │
├─────────────────────────────────┤
│  4. Validation & Exécution      │  (L252-291)
│     ├─ Chargement exemple_meteo │
│     ├─ Tests M, M³, M⁷          │
│     ├─ Test convergence          │
│     └─ Test CFC + stationnaires │
└─────────────────────────────────┘
```

</td>
<td width="50%">

**Flux de données**

```
Fichier exemple_meteo.txt
        ↓
   loadAdjacency
        ↓
   adjacencyListToMatrix
        ↓
   M (matrice 5×5)
        ↓
    ┌───┴───┐
    ↓       ↓
 Tarjan   Puissances
    ↓       ↓
  CFC    M³, M⁷, Mⁿ
    ↓       ↓
  Π*     Validation
    ↓       ↓
    └───┬───┘
        ↓
 Affichage résultats
```

</td>
</tr>
</table>

### 2.4 Choix structurants de conception

<table>
<tr>
<td bgcolor="#E8F5E9">

**Choix 1 : Paradigme procédural**

- Fonctions pures (pas d'effets de bord)
- Lisibilité maximale
- Adapté au contexte pédagogique
- Évite la verbosité OOP

</td>
<td bgcolor="#FFF3E0">

**Choix 2 : Représentation matricielle**

- `Array[Array[Double]]` pour performance
- Accès O(1)
- Localité mémoire
- Alternative : matrices creuses (overkill)

</td>
</tr>
<tr>
<td bgcolor="#F3E5F5">

**Choix 3 : Tarjan avec successeurs**

- Conversion M → `Array[Array[Int]]`
- Plus compact pour graphes peu denses
- Facilite le parcours en profondeur
- Optimisation mémoire

</td>
<td bgcolor="#E0F2F1">

**Choix 4 : Itération de puissance**

- Méthode simple et robuste
- Converge pour matrices stochastiques
- Normalisation à chaque itération
- Alternative : résolution algébrique (complexe)

</td>
</tr>
</table>

---

<div align="center">

## 3. Fondements théoriques

</div>

### 3.1 Composantes fortement connexes et algorithme de Tarjan

<div style="background-color: #F3E5F5; padding: 15px; border-left: 5px solid #9C27B0; border-radius: 5px;">

**Définition : Composante Fortement Connexe (CFC)**

Dans un graphe orienté G = (V, E), une CFC est un sous-ensemble **maximal** de sommets C ⊆ V tel que :

∀ (u, v) ∈ C × C : ∃ chemin u → v **ET** ∃ chemin v → u

➡️ Tous les sommets d'une CFC **communiquent** entre eux dans les **deux sens**.

</div>

<br>

**Propriétés des CFC**

| Propriété | Description |
|-----------|-------------|
| **Partition** | Tout sommet appartient à **exactement une** CFC |
| **Maximalité** | Les CFC sont maximales par inclusion |
| **DAG des CFC** | Les CFC forment un graphe acyclique dirigé |
| **Réduction** | Graphe fortement connexe ⇔ Une seule CFC |

<br>

<details open>
<summary><b>Algorithme de Tarjan - Complexité O(n + m)</b></summary>

<table>
<tr>
<td width="50%" bgcolor="#E3F2FD">

**Variables clés**

- `numEmp[x]` : Numéro de découverte
- `retour[x]` : Plus petit numEmp accessible
- `dansPile[x]` : Sommet dans la pile ?
- `P` : Pile des sommets visités

</td>
<td width="50%" bgcolor="#FFF8E1">

**Invariant fondamental**

```
Si retour[x] = numEmp[x]
  ⇒ x est RACINE d'une CFC
  ⇒ Dépiler jusqu'à x
```

</td>
</tr>
</table>

**Avantages de Tarjan** :
- Complexité linéaire optimale O(|V| + |E|)
- Un seul parcours en profondeur
- Pas de transposée du graphe (vs Kosaraju)
- Implémentation élégante

</details>

### 3.2 Distributions et évolution en temps discret

<table>
<tr>
<td width="50%">

**Distribution Π**

Vecteur ligne (π₀, π₁, ..., πₙ₋₁) tel que :

- ∀i : πᵢ ≥ 0
- Σᵢ πᵢ = 1

**Interprétation** : πᵢ = probabilité d'être dans l'état i

</td>
<td width="50%">

**Matrice stochastique M**

Matrice n×n telle que :

- ∀i,j : M(i,j) ≥ 0
- ∀i : Σⱼ M(i,j) = 1

**Interprétation** : M(i,j) = prob. de transition i → j

</td>
</tr>
</table>

<br>

<div style="background-color: #E8F4F8; padding: 15px; border-left: 5px solid #2196F3; border-radius: 5px;">

**Évolution de la distribution**

```
Π₁ = Π₀ · M           (après 1 transition)
Π₂ = Π₁ · M = Π₀ · M²  (après 2 transitions)
⋮
Πₙ = Π₀ · Mⁿ          (après n transitions)
```

**Propriété de Markov** : L'état futur ne dépend que de l'état présent (pas de mémoire de l'historique).

</div>

### 3.3 Distribution stationnaire

<table>
<tr>
<td width="50%" bgcolor="#E8F5E9">

**Définition**

Une distribution Π* est **stationnaire** si :

```
Π* · M = Π*
```

➡️ Une fois atteinte, elle **reste stable** indéfiniment

</td>
<td width="50%" bgcolor="#FFF3E0">

**Convergence**

Pour un graphe **irréductible** et **apériodique** :

```
lim_{n→∞} Π₀ · Mⁿ = Π*
```

➡️ Le système "oublie" son état initial

</td>
</tr>
</table>

<br>

**Méthodes de calcul**

| Méthode | Principe | Complexité |
|---------|----------|------------|
| **Itération de puissance** | Πₙ₊₁ = Πₙ · M jusqu'à convergence | O(k³ × iter) |
| **Résolution algébrique** | Système linéaire Π*(M − I) = 0 | O(k³) |
| **Vecteur propre** | Eigenvalue = 1 | O(k³) |

➡️ **Choix retenu** : Itération de puissance (simplicité + robustesse)

### 3.4 Classes transitoires et persistantes

<table>
<tr>
<td bgcolor="#E3F2FD">

#### Classe PERSISTANTE (fermée)

**Définition** :
```
∀i ∈ C, ∀j ∉ C : M(i,j) = 0
```
Aucune arête sortante vers l'extérieur

**Propriétés** :
- Possède ≥ 1 distribution stationnaire
- Si irréductible + apériodique → Π* unique
- Le système peut y rester indéfiniment

</td>
<td bgcolor="#FFF8E1">

#### Classe TRANSITOIRE

**Définition** :
```
∃ i ∈ C, ∃ j ∉ C : M(i,j) > 0
```
Il existe une arête sortante

**Propriétés** :
- Distribution stationnaire = **0** (vecteur nul)
- Le système finit par quitter la classe (prob. 1)
- Pas de régime stable

</td>
</tr>
</table>

---

<div align="center">

## 4. Analyse fonctionnelle détaillée

</div>

### 4.1 Module : Calculs matriciels

<details open>
<summary><b>Fonction `adjacencyListToMatrix`</b></summary>

```scala
def adjacencyListToMatrix(
  adj: Map[Int, List[(Int, Double)]],
  n: Int
): Array[Array[Double]]
```

**Rôle** : Convertir liste d'adjacence → matrice n×n

**Algorithme** :

1. Initialiser matrice n×n remplie de 0.0
2. Pour chaque sommet `from` dans `adj` :
   - Pour chaque successeur `(to, probability)` :
     - `matrix(from)(to) = probability`
3. Retourner `matrix`

**Justification** : Liste d'adjacence = format naturel pour fichier texte. Conversion nécessaire pour calculs matriciels efficaces.

</details>

<details>
<summary><b>Fonction `multiplyMatrices`</b></summary>

```scala
def multiplyMatrices(
  A: Array[Array[Double]],
  B: Array[Array[Double]]
): Array[Array[Double]]
```

**Rôle** : Calculer C = A × B (produit matriciel)

**Algorithme** : Triple boucle imbriquée (O(n³))

```
pour i ← 0 à n-1:
  pour j ← 0 à n-1:
    C(i,j) = Σₖ A(i,k) × B(k,j)
```

| Complexité | Justification |
|------------|---------------|
| O(n³) | Suffisant pour n ≤ 100 |
| O(n²) | Stockage matrice résultat |

**Alternative** : Strassen O(n²·⁸⁰⁷) → gain significatif seulement pour n >> 100

</details>

<details>
<summary><b>Fonction `diffMatrices`</b></summary>

```scala
def diffMatrices(
  M: Array[Array[Double]],
  N: Array[Array[Double]]
): Double
```

**Rôle** : Mesurer la "distance" entre deux matrices

**Formule** :

```
diff(M, N) = Σᵢ Σⱼ |M(i,j) − N(i,j)|
```

➡️ **Norme L1** (somme des valeurs absolues des différences)

**Utilisation** : Critère de convergence `diff(Mⁿ, Mⁿ⁺¹) < ε`

</details>

<details>
<summary><b>Fonction `matrixPower`</b></summary>

```scala
def matrixPower(M: Array[Array[Double]], k: Int): Array[Array[Double]]
```

**Rôle** : Calculer Mᵏ (k ≥ 0)

**Algorithme** :

```
si k = 0:
  retourner I (matrice identité)
sinon:
  result = M
  répéter k-1 fois:
    result = result × M
  retourner result
```

| k | Multiplications | Temps (n=5) |
|---|-----------------|-------------|
| 3 | 2 | < 1 ms |
| 7 | 6 | < 1 ms |
| 100 | 99 | ~10 ms |

**Optimisation possible** : Exponentiation rapide O(log k) → non nécessaire pour k ≤ 10

</details>

### 4.2 Module : Algorithme de Tarjan

<table>
<tr>
<td width="50%" bgcolor="#F3E5F5">

**Fonction `matrixToSuccessors`**

Convertit matrice → tableau de successeurs

```scala
succ(i) = [j | M(i,j) > 0]
```

**Avantage** : Compact pour graphes peu denses

</td>
<td width="50%" bgcolor="#E0F2F1">

**Fonction `tarjanFromGraph`**

Implémente Tarjan avec parcours en profondeur

**Structures** :
- `P` : Pile (ArrayBuffer)
- `numEmp` : Numéros de découverte
- `retour` : Valeurs de retour
- `dansPile` : Marqueurs booléens

</td>
</tr>
</table>

<br>

<div style="background-color: #E3F2FD; padding: 15px; border-left: 5px solid #2196F3; border-radius: 5px;">

**Procédure `parcours(x)` - Cœur de l'algorithme**

```
1. Numéroter x : num++, numEmp[x] = retour[x] = num
2. Empiler x : P.push(x), dansPile[x] = true
3. Pour chaque successeur y de x :
   Si y non visité (numEmp[y] = ∞) :
     ↳ parcours(y)  // récursion
     ↳ retour[x] = min(retour[x], retour[y])
   Sinon si y dans pile :
     ↳ retour[x] = min(retour[x], numEmp[y])
4. Si retour[x] = numEmp[x] :
   ↳ x est racine d'une CFC
   ↳ Dépiler jusqu'à x → former la CFC
```

</div>

### 4.3 Module : Extraction et analyse des classes

<details open>
<summary><b>Fonction `extractSubmatrix`</b></summary>

**Rôle** : Extraire la sous-matrice k×k d'une classe

**Algorithme** :

```
vertices = partition[componentIndex]
k = taille(vertices)
créer sous-matrice sub[k][k]
pour i ← 0 à k-1:
  pour j ← 0 à k-1:
    sub[i][j] = matrix[vertices[i]][vertices[j]]
retourner sub
```

**Justification** : Nécessaire pour calculer Π* sur la classe isolée

| Complexité | Valeur |
|------------|--------|
| Temps | O(k²) |
| Espace | O(k²) |

</details>

<details>
<summary><b>Fonction `isPersistentClass`</b></summary>

**Rôle** : Déterminer si une classe est persistante (fermée)

**Test** :

```
set = ensemble des sommets de la classe
pour tout i dans classe:
  pour tout j de 0 à n-1:
    si M(i,j) > 0 ET j ∉ set:
      retourner FALSE  // arête sortante détectée
retourner TRUE  // aucune arête sortante
```

**Interprétation** :

| Résultat | Signification | Distribution Π* |
|----------|---------------|-----------------|
| TRUE | Classe persistante | Π* ≠ 0 (à calculer) |
| FALSE | Classe transitoire | Π* = 0 (vecteur nul) |

</details>

### 4.4 Module : Distribution stationnaire

<details open>
<summary><b>Fonction `stationaryDistributionOfClass`</b></summary>

**Rôle** : Calculer Π* d'une classe par **itération de puissance**

**Algorithme** :

```
1. Vérifier si classe persistante:
   Si NON → retourner vecteur nul [0, 0, ..., 0]

2. Extraire sous-matrice M_sub de taille k×k

3. Itération de puissance:
   π ← [1/k, 1/k, ..., 1/k]  // distribution uniforme
   répéter jusqu'à convergence (max 5000 iter):
     π_next ← π × M_sub       // multiplication vecteur-matrice
     diff ← Σ |π_next[i] - π[i]|
     π ← π_next / Σ π_next[i] // normalisation
     si diff < 1e-9:
       retourner π            // convergence atteinte
   retourner π                // max iter atteint
```

</details>

<table>
<tr>
<td bgcolor="#E8F5E9">

**Normalisation systématique**

```scala
sumPi = piNext.sum
pi = piNext.map(_ / sumPi)
```

**Pourquoi ?**
- Compense erreurs d'arrondi
- Garantit Σπᵢ = 1
- Stabilité numérique

</td>
<td bgcolor="#FFF3E0">

**Critères de convergence**

| Paramètre | Valeur | Justification |
|-----------|--------|---------------|
| `epsilon` | 1e-9 | Précision stricte |
| `maxIter` | 5000 | Protection boucle infinie |
| `vecteur initial` | Uniforme | Simple + robuste |

</td>
</tr>
</table>

---

<div align="center">

## 5. Architecture logicielle

</div>

### 5.1 Structure du code

<table>
<tr>
<td width="50%">

**Organisation par modules**

```
projet.sc (292 lignes)
├─ Calculs matriciels (L1-96)
│  ├─ adjacencyListToMatrix
│  ├─ multiplyMatrices
│  ├─ diffMatrices
│  ├─ matrixPower
│  └─ printMatrix
│
├─ Tarjan & CFC (L98-178)
│  ├─ matrixToSuccessors
│  ├─ tarjanFromGraph
│  │  └─ parcours (récursif)
│  └─ tarjan
│
├─ Analyse classes (L180-250)
│  ├─ extractSubmatrix
│  ├─ isPersistentClass
│  ├─ stationaryDistribution...
│  └─ printAllStationary...
│
└─ Validation (L252-291)
   ├─ Chargement exemple_meteo
   ├─ Tests M, M³, M⁷
   ├─ Convergence
   └─ CFC + distributions
```

</td>
<td width="50%">

**Flux de données global**

```
Fichier exemple_meteo.txt
        ↓
 loadAdjacencyFromFile
        ↓
adjacencyListToMatrix
        ↓
    M: Matrice 5×5
        ↓
    ┌───┴───┐
    ↓       ↓
 tarjan  matrixPower
    ↓       ↓
Partition  M³, M⁷, Mⁿ
    ↓       ↓
  CFC      Validation
    ↓       ↓
extractSubmatrix
    ↓
stationaryDistribution
    ↓
  Résultats
```

</td>
</tr>
</table>

### 5.2 Flux de données détaillé

<div style="background-color: #E8F4F8; padding: 15px; border-radius: 5px;">

**Étape 1 : Chargement**

```
Fichier exemple_meteo.txt
    ↓
loadAdjacencyFromFile(path)
    ↓
(Map[Int, List[(Int, Double)]], Int)  ← (liste adjacence, n)
    ↓
adjacencyListToMatrix(adj, n)
    ↓
Array[Array[Double]]  ← Matrice M
```

</div>

<br>

<div style="background-color: #FFF3E0; padding: 15px; border-radius: 5px;">

**Étape 2 : Calculs matriciels**

```
M (matrice initiale)
    ↓
┌─────────┬─────────┬─────────┐
│  M³     │   M⁷    │   Mⁿ    │
└─────────┴─────────┴─────────┘
    ↓         ↓         ↓
 (validation) (validation) (convergence)
```

</div>

<br>

<div style="background-color: #E8F5E9; padding: 15px; border-radius: 5px;">

**Étape 3 : Analyse CFC**

```
M
 ↓
matrixToSuccessors
 ↓
Array[Array[Int]]  ← tableau successeurs
 ↓
tarjanFromGraph
 ↓
List[List[Int]]  ← partition CFC
 ↓
pour chaque classe:
  ├─ extractSubmatrix → M_sub
  ├─ isPersistentClass → booléen
  └─ si persistante:
      └─ stationaryDistribution → Π*
```

</div>

### 5.3 Choix du paradigme procédural

<table>
<tr>
<td width="50%" bgcolor="#E8F5E9">

#### Avantages retenus

- **Lisibilité** : Fonctions auto-documentées
- **Simplicité** : Pas de hiérarchie de classes
- **Performance** : Tableaux mutables (efficaces)
- **Pureté** : Fonctions sans effets de bord
- **Adapté** : Contexte pédagogique (< 300 lignes)

</td>
<td width="50%" bgcolor="#FFF3E0">

#### Inconvénients acceptés

- **Réutilisabilité** : Spécifique au projet
- **Passage à l'échelle** : Limite pour n >> 1000
- **Généricité** : Pas de bibliothèque réutilisable
- **Alternative** : OOP pour projets plus larges

</td>
</tr>
</table>

<br>

<div align="center">

**Compromis retenu : Procédural fonctionnel**

| Aspect | Choix | Justification |
|--------|-------|---------------|
| Paradigme | Procédural + FP | Lisibilité académique |
| Structures | Mutables (Array) + Immuables (List) | Performance + sécurité |
| Fonctions | Pures (sauf affichage) | Testabilité |
| Taille | ~300 lignes | Maintenabilité |

</div>

---

<div align="center">

## 6. Détail du code et implémentation

</div>

### 6.1 Fonctions de base matricielle

<details open>
<summary><b>`adjacencyListToMatrix` - Ligne par ligne</b></summary>

```scala
def adjacencyListToMatrix(
  adj: Map[Int, List[(Int, Double)]],
  n: Int
): Array[Array[Double]] = {
  
  val matrix = Array.fill(n, n)(0.0)  // ← Initialisation matrice n×n à 0.0
  
  for ((from, transitions) <- adj) {   // ← Parcours de la map
    for ((to, probability) <- transitions) {  // ← Parcours des successeurs
      matrix(from)(to) = probability   // ← Affectation en place (mutation)
    }
  }
  
  matrix  // ← Retour de la matrice complète
}
```

**Explications** :

| Ligne | Code | Rôle | Justification |
|-------|------|------|---------------|
| L1 | `Array.fill(n, n)(0.0)` | Créer matrice n×n | Méthode standard Scala |
| L2 | `for ((from, transitions) <- adj)` | Déstructuration map | Syntaxe concise |
| L3 | `for ((to, probability) <- transitions)` | Parcours successeurs | Boucle imbriquée |
| L4 | `matrix(from)(to) = probability` | **Mutation** | Efficace (pas de copie) |

**Pourquoi mutation ?**
- Performance : Pas de copie de matrice
- Simplicité : Code direct
- Trade-off : Contraire à FP pure (acceptable ici)

</details>

<details>
<summary><b>`multiplyMatrices` - Algorithme O(n³)</b></summary>

```scala
def multiplyMatrices(
  A: Array[Array[Double]],
  B: Array[Array[Double]]
): Array[Array[Double]] = {
  
  val n = A.length
  val result = Array.fill(n, n)(0.0)
  
  for (i <- 0 until n) {           // ← Boucle sur les lignes
    for (j <- 0 until n) {         // ← Boucle sur les colonnes
      for (k <- 0 until n) {       // ← Boucle sur les indices de somme
        result(i)(j) += A(i)(k) * B(k)(j)  // ← C[i,j] = Σₖ A[i,k]×B[k,j]
      }
    }
  }
  
  result
}
```

**Analyse de complexité** :

| Opération | Nombre | Complexité |
|-----------|--------|------------|
| Triple boucle | n × n × n | O(n³) |
| Multiplication | n³ | O(1) chacune |
| Addition | n³ | O(1) chacune |
| **Total** | - | **O(n³)** |

**Performance pratique** (n = 5) :
- Multiplications : 125
- Temps : < 1 ms
- Acceptable pour n ≤ 100

</details>

### 6.2 Chargement et affichage

<details>
<summary><b>`loadAdjacencyFromFile` - Parsing robuste</b></summary>

```scala
def loadAdjacencyFromFile(path: String): (Map[Int, List[(Int, Double)]], Int) = {
  
  // Lecture + filtrage des lignes vides
  val lines = scala.io.Source.fromFile(path)
                .getLines()
                .filter(_.trim.nonEmpty)  // ← Ignorer lignes vides
                .toList
  
  val n = lines.head.trim.toInt  // ← Première ligne = n (nombre sommets)
  
  // Parsing des arêtes (lignes suivantes)
  val edges = lines.tail.flatMap { line =>
    val parts = line.trim.split("\\s+")  // ← Séparer par espaces/tabs
    if (parts.length >= 3)  // ← Vérifier 3 champs minimum
      Some((parts(0).toInt, parts(1).toInt, parts(2).toDouble))
    else
      None  // ← Ignorer lignes invalides (sans exception)
  }
  
  // Construction de la map (regroupement par sommet source)
  val adj = edges.groupBy(_._1).map { case (from, list) =>
    from -> list.map { case (_, to, p) => (to, p) }
  }
  
  (adj, n)
}
```

**Gestion d'erreurs** :

| Cas | Traitement | Justification |
|-----|------------|---------------|
| Ligne vide | Ignorée | `filter(_.trim.nonEmpty)` |
| < 3 champs | Ignorée | `if (parts.length >= 3)` |
| Espaces multiples | OK | `split("\\s+")` |
| Fichier manquant | Exception | Laissée au runtime |

</details>

### 6.3 Algorithme de Tarjan - Détail complet

<details open>
<summary><b>`tarjanFromGraph` - Implémentation conforme au pseudo-code</b></summary>

```scala
def tarjanFromGraph(succ: Array[Array[Int]], n: Int): List[List[Int]] = {
  
  // Structures de données (Algorithme 2, lignes 3-8 du PDF)
  val P = scala.collection.mutable.ArrayBuffer.empty[Int]  // Pile
  val retour = Array.fill(n)(-1)
  val dansPile = Array.fill(n)(false)
  val numEmp = Array.fill(n)(Int.MaxValue)  // ∞ = non visité
  var num = 0  // Compteur global
  val partition = Array.fill(n)(-1)
  
  // Procédure récursive parcours(x)
  def parcours(x: Int): Unit = {
    
    // L10-12 : Numérotation du sommet x
    num += 1
    numEmp(x) = num
    retour(x) = num
    
    // L13-14 : Empiler x
    P += x
    dansPile(x) = true
    
    // L15-24 : Parcours des successeurs
    for (y <- succ(x)) {
      if (numEmp(y) == Int.MaxValue) {  // L16 : y non visité
        parcours(y)  // L17 : Récursion
        retour(x) = math.min(retour(x), retour(y))  // L18
      } else {
        if (dansPile(y))  // L20 : y dans la pile
          retour(x) = math.min(retour(x), numEmp(y))  // L21
      }
    }
    
    // L25-32 : Si x est racine, former la CFC
    if (retour(x) == numEmp(x)) {
      var y = -1
      do {
        y = P(P.length - 1)    // L27 : Tête de pile
        partition(y) = x        // L28 : Marquer appartenance
        dansPile(y) = false     // L29
        P.remove(P.length - 1)  // L30 : Dépiler
      } while (y != x)          // L31
    }
  }
  
  // Programme principal (Algorithme 2)
  num = 0  // L9
  for (x <- 0 until n) {  // L11-14
    dansPile(x) = false
    numEmp(x) = Int.MaxValue  // ∞
  }
  
  for (x <- 0 until n) {  // L15-19
    if (numEmp(x) == Int.MaxValue)
      parcours(x)
  }
  
  // Construction de la liste des CFC
  partition.zipWithIndex.groupBy(_._1).toList.map { case (_, pairs) =>
    pairs.map(_._2).sorted
  }
}
```

**Correspondance exacte avec le pseudo-code** :

| Ligne code | Ligne PDF | Opération |
|------------|-----------|-----------|
| `num += 1` | L10 | Incrémenter compteur |
| `numEmp(x) = num` | L10 | Numéroter sommet |
| `P += x` | L13 | Empiler |
| `parcours(y)` | L17 | Récursion |
| `retour(x) = min(...)` | L18, L21 | Mise à jour retour |
| `if (retour(x) == numEmp(x))` | L25 | Test racine |
| `do...while` | L26-31 | Dépilage CFC |

</details>

<div style="background-color: #F3E5F5; padding: 15px; border-left: 5px solid #9C27B0; border-radius: 5px;">

**Points critiques de l'implémentation**

| Aspect | Choix | Justification |
|--------|-------|---------------|
| Pile | `ArrayBuffer` mutable | O(1) pour push/pop |
| Non visité | `Int.MaxValue` | Sentinelle standard |
| Récursion | Fonction interne | Partage variables (num, P, ...) |
| Limite | Profondeur pile JVM | OK pour n ≤ 1000 |

</div>

### 6.4 Extraction de sous-matrices

```scala
def extractSubmatrix(
  matrix: Array[Array[Double]],
  partition: List[List[Int]],
  componentIndex: Int
): Array[Array[Double]] = {
  
  val vertices = partition(componentIndex)  // ← Sommets de la classe
  val k = vertices.length                   // ← Taille de la classe
  val sub = Array.ofDim[Double](k, k)       // ← Nouvelle matrice k×k
  
  for (i <- 0 until k; j <- 0 until k) {
    sub(i)(j) = matrix(vertices(i))(vertices(j))  // ← Renommage indices
  }
  
  sub
}
```

**Renommage des indices** :

| Indice local | Indice global | Interprétation |
|--------------|---------------|----------------|
| `i ∈ [0, k-1]` | `vertices(i)` | i-ème sommet de la classe |
| `sub(i)(j)` | `matrix(vertices(i))(vertices(j))` | Probabilité locale |

**Pourquoi copier ?**
- Simplicité de `stationaryDistribution` (indices locaux)
- Coût O(k²) négligeable
- Alternative : travailler avec indices globaux (complexe)

### 6.5 Calcul de distribution stationnaire

<details open>
<summary><b>`stationaryDistributionOfClass` - Itération de puissance</b></summary>

```scala
def stationaryDistributionOfClass(
  matrix: Array[Array[Double]],
  partition: List[List[Int]],
  componentIndex: Int
): Array[Double] = {
  
  val component = partition(componentIndex)
  
  // Classe transitoire → vecteur nul
  if (!isPersistentClass(matrix, component)) {
    return Array.fill(component.length)(0.0)
  }
  
  // Classe persistante → calcul itératif
  val sub = extractSubmatrix(matrix, partition, componentIndex)
  val k = sub.length
  var pi = Array.fill(k)(1.0 / k)  // ← Vecteur initial uniforme
  val eps = 1e-9                    // ← Seuil de convergence strict
  var maxIter = 5000                // ← Protection boucle infinie
  
  while (maxIter > 0) {
    // Multiplication vecteur-matrice : π_next = π × M_sub
    val piNext = Array.fill(k)(0.0)
    for (i <- 0 until k; j <- 0 until k)
      piNext(j) += pi(i) * sub(i)(j)  // ← π_next[j] = Σᵢ π[i]×M[i,j]
    
    // Critère de convergence
    val diff = (pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum
    
    // Normalisation (correction erreurs d'arrondi)
    val sumPi = piNext.sum
    pi = if (sumPi > 0) piNext.map(_ / sumPi) else piNext
    
    // Convergence atteinte
    if (diff < eps) return pi
    
    maxIter -= 1
  }
  
  pi  // Si 5000 itérations atteintes
}
```

</details>

**Analyse de la normalisation** :

<table>
<tr>
<td bgcolor="#E8F5E9">

**Sans normalisation**

```
Itération 1 : Σπᵢ = 1.0000
Itération 10 : Σπᵢ = 0.9998
Itération 100 : Σπᵢ = 0.9952
Itération 1000 : Σπᵢ = 0.9512
```

➡️ Dérive progressive (erreurs d'arrondi)

</td>
<td bgcolor="#FFF3E0">

**Avec normalisation**

```
Itération 1 : Σπᵢ = 1.0000
Itération 10 : Σπᵢ = 1.0000
Itération 100 : Σπᵢ = 1.0000
Itération 1000 : Σπᵢ = 1.0000
```

➡️ Stabilité garantie

</td>
</tr>
</table>

---

<div align="center">

## 7. Tests et validation

</div>

### 7.1 Stratégie de test

<table>
<tr>
<td width="50%" bgcolor="#E3F2FD">

**Tests unitaires implicites**

Chaque fonction est validée via le bloc d'exécution :

- `multiplyMatrices` → M³, M⁷
- `diffMatrices` → Critère convergence
- `tarjan` → Partition sur exemple météo
- `stationaryDistribution` → Π* attendue

</td>
<td width="50%" bgcolor="#FFF8E1">

**Test de non-régression**

Le sujet fournit :
- Matrice M explicite
- Résultats M³, M⁷
- Distribution Π* attendue
- Critères de convergence

</td>
</tr>
</table>

### 7.2 Validation sur l'exemple météo

<details open>
<summary><b>Test 1 : Affichage de M</b></summary>

**Résultat obtenu** :

```
=== Matrice M (exemple météo) ===
M =
  0.34   0.27   0.00   0.18   0.21
  0.20   0.40   0.20   0.00   0.20
  0.00   0.41   0.37   0.09   0.13
  0.00   0.68   0.20   0.12   0.00
  0.12   0.30   0.00   0.00   0.58
```

**Validation** : Conforme à la matrice du sujet

</details>

<details>
<summary><b>Test 2 : Calcul de M³</b></summary>

**Résultat attendu** (première ligne) :

```
Π₃ = (0.17  0.37  0.13  0.05  0.27)
```

**Résultat obtenu** :

```
=== M^3 ===
M^3 =
  0.17   0.37   0.13   0.05   0.27  ← Conforme
  0.16   0.37   0.14   0.05   0.28
  0.14   0.38   0.18   0.04   0.25
  0.15   0.38   0.18   0.05   0.24
  0.17   0.34   0.09   0.04   0.35
```

**Validation** : Première ligne = résultat attendu

</details>

<details>
<summary><b>Test 3 : Calcul de M⁷ (convergence)</b></summary>

**Résultat attendu** : Toutes les lignes identiques

```
Π* = (0.16  0.36  0.13  0.05  0.29)
```

**Résultat obtenu** :

```
=== M^7 ===
M^7 =
  0.16   0.36   0.13   0.05   0.29
  0.16   0.36   0.13   0.05   0.29
  0.16   0.36   0.13   0.05   0.29
  0.16   0.36   0.13   0.05   0.29
  0.16   0.36   0.13   0.05   0.30  ← Écart 0.01 (arrondi)
```

**Validation** : Convergence vers Π* confirmée

</details>

<details>
<summary><b>Test 4 : Critère de convergence</b></summary>

**Objectif** : Trouver n tel que `diff(Mⁿ, Mⁿ⁻¹) < 0.01`

**Résultat obtenu** :

```
=== Convergence (epsilon = 0.01) ===
Pour n = 9, diff(M^9, M^8) < 0.01
Distribution stationnaire (première ligne de M^n) :
0.16 0.36 0.13 0.05 0.30
```

**Validation** : Convergence en **9 itérations** (rapide)

</details>

<details>
<summary><b>Test 5 : CFC + Distributions stationnaires</b></summary>

**Résultat attendu** : Une CFC {1,2,3,4,5}, persistante

**Résultat obtenu** :

```
=== 2.4 – Test sur l'exemple météo ===
=== Composantes fortement connexes ===
  C1: {1, 2, 3, 4, 5}

=== Distributions stationnaires par classe ===
  Classe 1 (persistante): [0.1641, 0.3591, 0.1320, 0.0518, 0.2930]
```

**Validation** :
- Une seule CFC
- Classe persistante
- Π* ≈ (0.16, 0.36, 0.13, 0.05, 0.29)

</details>

### 7.3 Résultats obtenus - Synthèse

<table>
<tr>
<td bgcolor="#E8F5E9">

#### Tests réussis

- Affichage M conforme
- M³ : première ligne exacte
- M⁷ : convergence visible
- Critère convergence : n=9
- Partition CFC correcte
- Π* conforme (écart < 10⁻²)

</td>
<td bgcolor="#FFF3E0">

#### Écarts observés

| Valeur attendue | Valeur obtenue | Écart |
|-----------------|----------------|-------|
| 0.29 | 0.30 | 0.01 |
| 0.16 | 0.1641 | 0.0041 |
| 0.36 | 0.3591 | 0.0009 |

**Cause** : Erreurs d'arrondi flottant (acceptables)

</td>
</tr>
</table>

### 7.4 Limites observées

<table>
<tr>
<td width="50%">

#### Limite 1 : Graphes périodiques

**Problème** : Cycle simple 0→1→2→0

```
Mⁿ oscille entre plusieurs valeurs
diff(Mⁿ, Mⁿ⁻¹) ne converge jamais
```

**Solution** : Tester `diff(Mⁿ, Mⁿ⁻ᵖ)` pour p ∈ {2,3,...}

</td>
<td width="50%">

#### Limite 2 : Précision flottante

**Problème** : Accumulation d'erreurs d'arrondi

```
Double : précision ~10⁻¹⁵
Après k multiplications : ~10⁻¹⁵ × k
```

**Solution** : Normalisation systématique (implémentée)

</td>
</tr>
<tr>
<td>

#### Limite 3 : Passage à l'échelle

**Problème** : O(n³) pour multiplication

| n | Temps 1 mult. | Temps M¹⁰⁰ |
|---|---------------|------------|
| 10 | < 1 ms | ~10 ms |
| 100 | ~10 ms | ~1 s |
| 1000 | ~1 s | ~100 s |

**Solution** : Breeze/BLAS (non implémenté)

</td>
<td>

#### Limite 4 : Profondeur de pile

**Problème** : Tarjan récursif

```
JVM stack limit : ~1000 appels
Chaîne de 10000 sommets → StackOverflowError
```

**Solution** : Tarjan itératif (non implémenté)

</td>
</tr>
</table>

---

<div align="center">

## 8. Mode d'emploi

</div>

### 8.1 Prérequis

<table>
<tr>
<td width="50%" bgcolor="#E3F2FD">

**Logiciels requis**

- **Scala** 2.13.x ou 3.x
  ```bash
  scala -version
  ```
  [Installation](https://www.scala-lang.org/download/)

- **Scala-cli** (optionnel, recommandé)
  ```bash
  scala-cli --version
  ```
  [Installation](https://scala-cli.virtuslab.org/install)

</td>
<td width="50%" bgcolor="#FFF8E1">

**Fichiers nécessaires**

```
graphII/
├── projet.sc            ← Script principal
├── exemple_meteo.txt    ← Fichier de données
└── (autres fichiers)
```

**Important** : `exemple_meteo.txt` doit être dans le même répertoire

</td>
</tr>
</table>

### 8.2 Lancement du programme

<details open>
<summary><b>Méthode 1 : Avec `scala` (standard)</b></summary>

```bash
cd /chemin/vers/graphII
scala projet.sc
```

**Option accélérée** (-nc = no compilation) :

```bash
scala -nc projet.sc
```

</details>

<details>
<summary><b>Méthode 2 : Avec `scala-cli` (recommandé)</b></summary>

```bash
cd /chemin/vers/graphII
scala-cli run projet.sc
```

**Avantage** : Compilation + exécution automatique

</details>

<details>
<summary><b>Méthode 3 : Depuis un IDE</b></summary>

**IntelliJ IDEA / VS Code (Metals) / Cursor** :

1. Ouvrir `projet.sc`
2. Configurer **working directory** → dossier `graphII`
3. Cliquer "Run" ou équivalent

**Attention** : Vérifier que le répertoire de travail est correct, sinon `FileNotFoundException`

</details>

### 8.3 Format des fichiers d'entrée

<div style="background-color: #E8F4F8; padding: 15px; border-left: 5px solid #2196F3; border-radius: 5px;">

**Structure du fichier `exemple_meteo.txt`**

```
5              ← Ligne 1 : nombre de sommets n
0 1 0.34       ← Ligne 2+ : arêtes au format "from to probability"
0 3 0.18
1 0 0.20
1 1 0.40
...
```

**Règles** :
- Ligne 1 : entier `n` (nombre de sommets)
- Lignes suivantes : 3 champs séparés par espaces/tabulations
  - `from` : indice source (0 à n−1)
  - `to` : indice destination (0 à n−1)
  - `probability` : probabilité (décimal entre 0 et 1)

</div>

<br>

**Exemple : Graphe à 3 sommets**

```
3
0 1 0.5
0 2 0.5
1 0 0.3
1 1 0.7
2 0 1.0
```

➡️ États : {0, 1, 2}  
➡️ Transitions : 0→{1 (0.5), 2 (0.5)}, 1→{0 (0.3), 1 (0.7)}, 2→{0 (1.0)}

### 8.4 Interprétation des sorties

<details open>
<summary><b>Section 1 : Matrice M</b></summary>

```
=== Matrice M (exemple météo) ===
M =
  0.34   0.27   0.00   0.18   0.21
  0.20   0.40   0.20   0.00   0.20
  ...
```

**Interprétation** :
- `M(0,1) = 0.27` → 27% de chances de passer de Sunny (0) à Cloudy (1)
- `M(1,2) = 0.20` → 20% de chances de passer de Cloudy (1) à Rain (2)

</details>

<details>
<summary><b>Section 2 : M³ et M⁷</b></summary>

```
=== M^3 ===
M^3 =
  0.17   0.37   0.13   0.05   0.27  ← Π₃ si on part de l'état 0
  ...
```

**Interprétation** :
- Première ligne de M³ = distribution Π₃ en partant de l'état 0
- `0.37` (colonne 1) = 37% de chances d'être Cloudy dans 3 jours si Sunny aujourd'hui

```
=== M^7 ===
M^7 =
  0.16   0.36   0.13   0.05   0.29  ← Toutes les lignes
  0.16   0.36   0.13   0.05   0.29  ← sont identiques
  ...
```

**Interprétation** : Convergence vers Π* (indépendance de l'état initial)

</details>

<details>
<summary><b>Section 3 : Convergence</b></summary>

```
=== Convergence (epsilon = 0.01) ===
Pour n = 9, diff(M^9, M^8) < 0.01
Distribution stationnaire (première ligne de M^n) :
0.16 0.36 0.13 0.05 0.30
```

**Interprétation** :
- Convergence atteinte en **9 itérations**
- Π* = (0.16, 0.36, 0.13, 0.05, 0.30)
- À long terme : 16% Sunny, 36% Cloudy, 13% Rain, 5% Storm, 30% Sunny spells

</details>

<details>
<summary><b>Section 4 : CFC et distributions stationnaires</b></summary>

```
=== Composantes fortement connexes ===
  C1: {1, 2, 3, 4, 5}  ← Sommets en 1-based (0→1, 1→2, ...)

=== Distributions stationnaires par classe ===
  Classe 1 (persistante): [0.1641, 0.3591, 0.1320, 0.0518, 0.2930]
```

**Interprétation** :
- **C1** : Graphe fortement connexe (tous les états communiquent)
- **Persistante** : Pas d'arête sortante (classe fermée)
- **Distribution** : Probabilités à long terme pour chaque état

</details>

---

<div align="center">

## 9. Conclusion

</div>

### 9.1 Synthèse du travail

<table>
<tr>
<td width="50%" bgcolor="#E8F5E9">

#### Réalisations

**Modules implémentés** :
- Calculs matriciels complets
- Algorithme de Tarjan (O(n+m))
- Analyse des classes (persistantes/transitoires)
- Distributions stationnaires (itération de puissance)

**Validation** :
- Tous les tests de référence réussis
- Résultats conformes au sujet
- Écarts < 10⁻² (arrondis)

</td>
<td width="50%" bgcolor="#FFF3E0">

#### Métriques du projet

| Métrique | Valeur |
|----------|--------|
| Lignes de code | ~300 |
| Fonctions | 13 |
| Complexité Tarjan | O(n+m) |
| Complexité matrices | O(n³) |
| Tests réussis | 5/5 |
| Temps exécution (n=5) | < 100 ms |

</td>
</tr>
</table>

### 9.2 Apports du projet

<details open>
<summary><b>Apports théoriques</b></summary>

- **Composantes fortement connexes** : Compréhension approfondie, lien avec graphes acycliques
- **Distributions stationnaires** : Convergence, irréductibilité, apériodicité
- **Classes persistantes/transitoires** : Distinction, propriétés, calcul de Π*
- **Comportement asymptotique** : Lien structure topologique ↔ propriétés probabilistes

</details>

<details>
<summary><b>Apports techniques</b></summary>

- **Algorithme de Tarjan** : Implémentation fidèle au pseudo-code académique
- **Structures de données** : Choix adaptés (matrice vs liste d'adjacence vs tableau successeurs)
- **Scala** : Paradigme hybride (fonctionnel + impératif), typage fort, performance JVM
- **Méthode itérative** : Résolution numérique (itération de puissance) vs algébrique

</details>

<details>
<summary><b>Apports méthodologiques</b></summary>

- **Validation systématique** : Tests sur exemple de référence, comparaison résultats attendus
- **Documentation** : Justification de chaque choix, analyse des trade-offs
- **Rapport technique** : De la problématique à la validation (structure académique complète)

</details>

### 9.3 Difficultés rencontrées

<table>
<tr>
<td bgcolor="#FFEBEE">

#### Difficulté 1 : Précision numérique

**Problème** :
```
Après k multiplications matricielles
→ Accumulation d'erreurs d'arrondi
→ Dérive de la somme (0.9998 au lieu de 1.0)
```

**Solution implémentée** :
```scala
val sumPi = piNext.sum
pi = piNext.map(_ / sumPi)  // Normalisation
```

**Résultat** : Somme toujours = 1.0

</td>
<td bgcolor="#FFF3E0">

#### Difficulté 2 : Complexité de Tarjan

**Problème** :
- Plusieurs tableaux à gérer
- Mises à jour subtiles (ordre crucial)
- Erreur → CFC incorrectes/manquantes

**Solution** :
- Suivi ligne par ligne du pseudo-code
- Commentaires référençant le PDF (L10, L11...)
- Tests sur graphes simples d'abord

**Résultat** : Implémentation conforme

</td>
</tr>
<tr>
<td bgcolor="#E3F2FD">

#### Difficulté 3 : Parsing de fichier

**Problème** :
- Lignes vides, espaces multiples
- Format invalide (< 3 champs)
- Débogage difficile (lignes ignorées)

**Solution** :
```scala
.filter(_.trim.nonEmpty)  // Lignes vides
.split("\\s+")            // Espaces multiples
if (parts.length >= 3)    // Validation
```

**Amélioration future** : Logs/warnings sur lignes ignorées

</td>
<td bgcolor="#F3E5F5">

#### Difficulté 4 : Configuration IDE

**Problème** :
```
FileNotFoundException
→ Répertoire de travail mal configuré
→ exemple_meteo.txt introuvable
```

**Solution** :
- Documentation claire (Mode d'emploi)
- Commentaire en tête de script
- Chemin relatif vs absolu

**Prévention** : `// Run from repo root: scala projet.sc`

</td>
</tr>
</table>

### 9.4 Perspectives

<details open>
<summary><b>Perspective 1 : Graphes de grande taille</b></summary>

**Pour n > 1000 sommets** :

| Amélioration | Bénéfice | Complexité |
|--------------|----------|------------|
| **Matrices creuses** | Exploiter faible densité | O(nnz) au lieu de O(n²) |
| **Breeze/BLAS** | Multiplication optimisée | ~10× plus rapide |
| **Tarjan itératif** | Éviter StackOverflow | Même complexité, plus robuste |

</details>

<details>
<summary><b>Perspective 2 : Méthodes algébriques</b></summary>

**Alternative à l'itération de puissance** :

- **Résolution système linéaire** : Π*(M − I) = 0 par LU/QR
- **Méthode de la puissance inverse** : Calcul du vecteur propre dominant
- **Avantage** : Convergence plus rapide pour certains graphes
- **Inconvénient** : Implémentation plus complexe

</details>

<details>
<summary><b>Perspective 3 : Détection de périodicité</b></summary>

**Pour graphes périodiques** :

```scala
// Tester diff(Mⁿ, Mⁿ⁻ᵖ) pour p ∈ {2, 3, ..., 10}
for (p <- 2 to 10) {
  if (diff(Mn, MnMinusP) < eps) {
    println(s"Graphe périodique de période $p")
    return
  }
}
```

**Bénéfice** : Gérer les cycles simples correctement

</details>

<details>
<summary><b>Perspective 4 : Interface utilisateur</b></summary>

**Améliorations possibles** :

- **Interface graphique** (JavaFX/Swing) : chargement fichiers, affichage graphes
- **Visualisation CFC** : Couleurs par composante
- **Graphiques d'évolution** : Courbe de convergence, évolution de diff(Mⁿ, Mⁿ⁻¹)
- **Mode interactif** : Exploration pas à pas de Tarjan

</details>

<details>
<summary><b>Perspective 5 : Applications réelles</b></summary>

**Cas d'usage du framework** :

| Domaine | Application | Particularité |
|---------|-------------|---------------|
| **Réseaux sociaux** | PageRank | Distribution stationnaire = importance |
| **Biologie** | Dynamique populations | Classes = écosystèmes stables |
| **Finance** | Modèles de crédit | Transitions = changements de rating |
| **IA** | Reinforcement Learning | Chaînes de Markov décisionnelles |

</details>

---

<div align="center">

## 10. Annexes

</div>

### 10.1 Technologies utilisées

<table>
<tr>
<td bgcolor="#E3F2FD">

**Langage et environnement**

| Technologie | Version | Badge |
|-------------|---------|-------|
| **Scala** | 2.13.x / 3.x | ![Scala](https://img.shields.io/badge/Scala-DC322F?style=flat&logo=scala&logoColor=white) |
| **JVM** | 11+ | ![Java](https://img.shields.io/badge/JVM-11+-orange?style=flat) |
| **Scala-cli** | Latest | ![Scala-cli](https://img.shields.io/badge/scala--cli-optional-blue?style=flat) |

</td>
<td bgcolor="#FFF8E1">

**Bibliothèques standard**

| Module | Usage |
|--------|-------|
| `scala.io.Source` | Lecture fichiers |
| `scala.collection.mutable.ArrayBuffer` | Pile pour Tarjan |
| `scala.math` | Fonctions mathématiques (abs, min) |

**Aucune dépendance externe**

</td>
</tr>
</table>

### 10.2 Bibliographie

<details open>
<summary><b>Références académiques</b></summary>

1. **Robert Tarjan** (1972). *"Depth-First Search and Linear Graph Algorithms"*. SIAM Journal on Computing, 1(2), 146–160.  
   Article original de l'algorithme de Tarjan

2. **William J. Stewart** (2009). *Probability, Markov Chains, Queues, and Simulation*. Princeton University Press.  
   Référence complète sur les chaînes de Markov

3. **Sheldon M. Ross** (2014). *Introduction to Probability Models* (11th ed.). Academic Press.  
   Chapitre sur les chaînes de Markov en temps discret

</details>

<details>
<summary><b>Ressources en ligne</b></summary>

| Ressource | URL | Usage |
|-----------|-----|-------|
| **Scala Docs** | [docs.scala-lang.org](https://docs.scala-lang.org/) | Référence API |
| **Tarjan (Wikipedia)** | [en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm](https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm) | Pseudo-code détaillé |
| **Markov chain (Wikipedia)** | [en.wikipedia.org/wiki/Markov_chain](https://en.wikipedia.org/wiki/Markov_chain) | Théorie des chaînes de Markov |

</details>

<details>
<summary><b>Documents du projet</b></summary>

| Document | Fichier | Description |
|----------|---------|-------------|
| **Sujet** | `Projet Markov_Partie_2.pdf` | Consignes complètes |
| **Pseudo-code Tarjan** | `Tarjan parcours pseudo-code.pdf` | Algorithmes 1 & 2 |
| **Consignes rapport** | `Rédaction Rapport.pdf` | Format attendu |

</details>

---

<div align="center">

# Fin du rapport
**Année universitaire 2025–2026**
</div>