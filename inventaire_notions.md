# Inventaire des notions de programmation fonctionnelle

## Projet : Graphes de Markov - Partie 2
## Auteur : Méhédi Touré
## Date : Février 2026

---

## 1. Immutabilité

### 1.1 Variables immuables (`val`)

**Fichier** : `MarkovGraph.scala`, `Main.scala`

**Exemples** :
```scala
// MarkovGraph.scala, ligne 28
val matrix = Array.fill(n, n)(0.0)

// MarkovGraph.scala, ligne 51
val n = A.length

// Main.scala, ligne 20
val dataPath = "exemple_meteo.txt"
val (adjMeteo, nMeteo) = loadAdjacencyFromFile(dataPath)
val M = adjacencyListToMatrix(adjMeteo, nMeteo)
```

**Justification** : Utilisation systématique de `val` (immuable) plutôt que `var` (mutable) pour toutes les variables qui ne changent pas. Seules les boucles itératives (Tarjan, convergence) utilisent `var` lorsque strictement nécessaire.

### 1.2 Collections immuables

**Fichier** : `MarkovGraph.scala`

**Exemples** :
```scala
// Liste immuable pour la partition
def tarjanFromGraph(...): List[List[Int]]

// Map immuable pour la liste d'adjacence
def loadAdjacencyFromFile(...): (Map[Int, List[(Int, Double)]], Int)

// Pattern matching avec List
partition.zipWithIndex.groupBy(_._1).toList.map { case (_, pairs) =>
  pairs.map(_._2).sorted
}
```

**Justification** : Utilisation de `List` et `Map` (collections immuables par défaut en Scala) plutôt que `ListBuffer`, `HashMap` mutables. Exception : pile `ArrayBuffer` dans Tarjan pour raisons de performance (O(1) pour push/pop).

---

## 2. Fonctions d'ordre supérieur

### 2.1 `map`

**Fichier** : `MarkovGraph.scala`, lignes multiples

**Exemples** :
```scala
// Ligne 107 : Conversion matrice → successeurs
(0 until n).map { i =>
  (0 until n).filter(j => M(i)(j) > 0).toArray
}.toArray

// Ligne 84 : Construction map d'adjacence
edges.groupBy(_._1).map { case (from, list) =>
  from -> list.map { case (_, to, p) => (to, p) }
}

// Ligne 228 : Normalisation vecteur
piNext.map(_ / sumPi)

// Ligne 226 : Calcul différence
(pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum
```

**Justification** : `map` transforme chaque élément d'une collection sans mutation (création d'une nouvelle collection).

### 2.2 `flatMap`

**Fichier** : `MarkovGraph.scala`, ligne 78

**Exemple** :
```scala
val edges = lines.tail.flatMap { line =>
  val parts = line.trim.split("\\s+")
  if (parts.length >= 3) Some((parts(0).toInt, parts(1).toInt, parts(2).toDouble))
  else None
}
```

**Justification** : `flatMap` combine `map` + `flatten`. Ici, filtrage des lignes invalides (None) et extraction des triplets valides (Some).

### 2.3 `filter`

**Fichier** : `MarkovGraph.scala`, lignes 76, 107

**Exemples** :
```scala
// Ligne 76 : Filtrage lignes vides
val lines = Source.fromFile(path).getLines().filter(_.trim.nonEmpty).toList

// Ligne 107 : Filtrage successeurs (M(i,j) > 0)
(0 until n).filter(j => M(i)(j) > 0).toArray
```

**Justification** : `filter` sélectionne les éléments vérifiant un prédicat (programmation déclarative).

### 2.4 `foldLeft`

**Fichier** : `MarkovGraph.scala`, ligne 95

**Exemple** :
```scala
def matrixPower(M: Array[Array[Double]], k: Int): Array[Array[Double]] = {
  val n = M.length
  if (k == 0) {
    // Matrice identité
    val I = Array.fill(n, n)(0.0)
    for (i <- 0 until n) I(i)(i) = 1.0
    I
  } else {
    (1 until k).foldLeft(M) { (result, _) =>
      multiplyMatrices(result, M)
    }
  }
}
```

**Justification** : `foldLeft` remplace la boucle `for` avec `var result`. Version fonctionnelle : accumulation via fonction pure (pas de mutation de variable).

**Avant (impératif)** :
```scala
var result = M
for (_ <- 1 until k) result = multiplyMatrices(result, M)
result
```

**Après (fonctionnel)** :
```scala
(1 until k).foldLeft(M) { (result, _) => multiplyMatrices(result, M) }
```

### 2.5 `forall`

**Fichier** : `MarkovGraph.scala`, lignes 199-201

**Exemple** :
```scala
def isPersistentClass(matrix: Array[Array[Double]], component: List[Int]): Boolean = {
  val set = component.toSet
  component.forall { i =>
    (0 until matrix.length).forall { j =>
      matrix(i)(j) <= 0 || set(j)
    }
  }
}
```

**Justification** : `forall` teste un prédicat sur tous les éléments (version fonctionnelle de la boucle avec condition && cumulée).

### 2.6 `zipWithIndex`, `zip`

**Fichier** : `MarkovGraph.scala`, lignes 170, 226, 238, 242

**Exemples** :
```scala
// Ligne 170 : Regroupement par racine CFC
partition.zipWithIndex.groupBy(_._1).toList.map { case (_, pairs) =>
  pairs.map(_._2).sorted
}

// Ligne 226 : Calcul de différence entre vecteurs
(pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum

// Ligne 238 : Affichage avec index
partition.zipWithIndex.foreach { case (comp, idx) =>
  println(f"  C${idx + 1}: {${comp.map(_ + 1).mkString(", ")}}")
}
```

**Justification** : `zip` associe éléments deux à deux, `zipWithIndex` ajoute l'index (évite compteur manuel).

---

## 3. Pattern matching et déstructuration

### 3.1 Déstructuration dans les `for`

**Fichier** : `MarkovGraph.scala`, lignes 30-34

**Exemple** :
```scala
for ((from, transitions) <- adj) {
  for ((to, probability) <- transitions) {
    matrix(from)(to) = probability
  }
}
```

**Justification** : Déstructuration automatique des tuples `(from, transitions)` et `(to, probability)`.

### 3.2 Pattern matching dans `case`

**Fichier** : `MarkovGraph.scala`, lignes multiples

**Exemples** :
```scala
// Ligne 84
edges.groupBy(_._1).view.mapValues { list =>
  list.map { case (_, to, p) => (to, p) }  // ← déstructuration triplet
}.toMap

// Ligne 170
partition.zipWithIndex.groupBy(_._1).toList.map { case (_, pairs) =>
  pairs.map(_._2).sorted
}

// Ligne 226
(pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum
```

**Justification** : Extraction des composants d'un tuple via pattern matching (syntaxe concise et sûre).

---

## 4. Fonctions pures

### 4.1 Fonctions sans effets de bord

**Fichier** : `MarkovGraph.scala`

**Exemples** :
```scala
// Fonction pure : même entrée → même sortie, pas d'effet de bord
def multiplyMatrices(A: Array[Array[Double]], B: Array[Array[Double]]): Array[Array[Double]] = {
  val n = A.length
  val result = Array.fill(n, n)(0.0)
  for (i <- 0 until n) {
    for (j <- 0 until n) {
      for (k <- 0 until n) {
        result(i)(j) += A(i)(k) * B(k)(j)
      }
    }
  }
  result  // Retourne nouvelle matrice, n'affecte pas A ou B
}

// Fonction pure malgré mutations internes (encapsulées)
def extractSubmatrix(...): Array[Array[Double]] = {
  val vertices = partition(componentIndex)
  val k = vertices.length
  Array.tabulate(k, k) { (i, j) =>
    matrix(vertices(i))(vertices(j))
  }
}
```

**Justification** : Les fonctions ne modifient pas leurs paramètres d'entrée. Les mutations (quand présentes) sont encapsulées dans le scope local. La fonction retourne une nouvelle valeur.

**Exception** : `printMatrix`, `printAllStationaryDistributions` ont des effets de bord (affichage console) par définition.

### 4.2 Pas de modification des paramètres

**Principe** : Aucune fonction ne modifie `M`, `adj`, `partition`, etc. passés en paramètre.

**Exemple** :
```scala
// multiplyMatrices ne modifie ni A ni B
def multiplyMatrices(A: Array[Array[Double]], B: Array[Array[Double]]): Array[Array[Double]]

// tarjan ne modifie pas M
def tarjan(M: Array[Array[Double]]): List[List[Int]]
```

---

## 5. Récursion et récursion terminale

### 5.1 Récursion dans Tarjan

**Fichier** : `MarkovGraph.scala`, lignes 188-216

**Exemple** :
```scala
def parcours(x: Int): Unit = {
  num += 1
  numEmp(x) = num
  retour(x) = num
  P += x
  dansPile(x) = true
  for (y <- succ(x)) {
    if (numEmp(y) == Int.MaxValue) {
      parcours(y)  // ← Appel récursif
      retour(x) = math.min(retour(x), retour(y))
    } else {
      if (dansPile(y))
        retour(x) = math.min(retour(x), numEmp(y))
    }
  }
  // ... suite
}
```

**Justification** : Parcours en profondeur (DFS) = récursion naturelle. Respecte le pseudo-code académique de Tarjan.

### 5.2 Récursion pour itération stationnaire (Main.scala)

**Fichier** : `Main.scala`, lignes 44-51

**Exemple** :
```scala
def findConvergence(Mn: Array[Array[Double]], n: Int, maxIter: Int): (Array[Array[Double]], Int) = {
  if (n >= maxIter) return (Mn, n)
  val MnNext = multiplyMatrices(Mn, M)
  if (diffMatrices(MnNext, Mn) < epsilon) (MnNext, n + 1)
  else findConvergence(MnNext, n + 1, maxIter)  // ← Récursion terminale
}
```

**Justification** : Remplace la boucle `while` par une récursion terminale (tail recursion). Le compilateur Scala optimise en boucle (pas de surcoût pile).

---

## 6. Expressions et composition

### 6.1 Expression `if-else` (retourne une valeur)

**Fichier** : `MarkovGraph.scala`, ligne 227

**Exemple** :
```scala
pi = if (sumPi > 0) piNext.map(_ / sumPi) else piNext
```

**Justification** : En Scala, `if-else` est une **expression** (retourne une valeur), pas une instruction. Permet d'éviter `var` et mutation.

### 6.2 Composition de fonctions

**Fichier** : `MarkovGraph.scala`, ligne 76

**Exemple** :
```scala
val lines = Source.fromFile(path)
              .getLines()
              .filter(_.trim.nonEmpty)
              .toList
```

**Justification** : Chaînage de fonctions (méthode fluent API). Transformation de données par composition : Source → Iterator → filtrage → List.

---

## 7. Fonctions anonymes (lambdas)

### 7.1 Syntaxe `x => ...`

**Fichier** : `MarkovGraph.scala`, lignes multiples

**Exemples** :
```scala
// Ligne 107
(0 until n).map { i =>
  (0 until n).filter(j => M(i)(j) > 0).toArray
}

// Ligne 96 : foldLeft avec lambda
(1 until k).foldLeft(M) { (result, _) =>
  multiplyMatrices(result, M)
}

// Ligne 228
piNext.map(_ / sumPi)  // ← Syntaxe raccourcie (placeholder _)
```

**Justification** : Fonctions anonymes pour transformation de données (évite déclaration de fonctions intermédiaires).

### 7.2 Placeholder `_`

**Exemples** :
```scala
_.trim.nonEmpty              // Ligne 76
piNext.map(_ / sumPi)        // Ligne 228
pairs.map(_._2).sorted       // Ligne 171
```

**Justification** : Syntaxe Scala pour fonction anonyme simple (équivalent à `x => x.trim.nonEmpty`).

---

## 8. Options et gestion d'erreurs fonctionnelle

### 8.1 `Option[T]` avec `Some`/`None`

**Fichier** : `MarkovGraph.scala`, lignes 78-81

**Exemple** :
```scala
val edges = lines.tail.flatMap { line =>
  val parts = line.trim.split("\\s+")
  if (parts.length >= 3) 
    Some((parts(0).toInt, parts(1).toInt, parts(2).toDouble))
  else 
    None
}
```

**Justification** : Gestion d'erreurs fonctionnelle : `flatMap` filtre automatiquement les `None` (lignes invalides). Évite exceptions ou valeurs sentinelles.

---

## 9. Méthodes fonctionnelles avancées

### 9.1 `groupBy`

**Fichier** : `MarkovGraph.scala`, lignes 83, 170

**Exemple** :
```scala
// Regrouper arêtes par sommet source
val adj = edges.groupBy(_._1).view.mapValues { list =>
  list.map { case (_, to, p) => (to, p) }
}.toMap

// Regrouper sommets par racine de CFC
partition.zipWithIndex.groupBy(_._1).toList.map { case (_, pairs) =>
  pairs.map(_._2).sorted
}
```

**Justification** : `groupBy` regroupe les éléments selon une clé (équivalent fonctionnel de boucles imbriquées + map mutable).

### 9.2 `zipWithIndex`, `zip`

**Exemples** :
```scala
// Associer éléments avec leur index
partition.zipWithIndex.foreach { case (comp, idx) => ... }

// Associer deux vecteurs élément par élément
(pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum
```

**Justification** : `zip` crée des paires sans boucle manuelle avec compteur.

### 9.3 `view` (évaluation paresseuse)

**Fichier** : `MarkovGraph.scala`, ligne 83

**Exemple** :
```scala
edges.groupBy(_._1).view.mapValues { list =>
  list.map { case (_, to, p) => (to, p) }
}.toMap
```

**Justification** : `.view` crée une vue paresseuse (évaluation retardée). `mapValues` sur une map crée une vue par défaut en Scala 2.13 ; `.view` explicite pour clarté.

---

## 10. Fonctions de création de structures

### 10.1 `Array.fill`

**Fichier** : `MarkovGraph.scala`, lignes multiples

**Exemples** :
```scala
// Créer matrice n×n initialisée à 0.0
val matrix = Array.fill(n, n)(0.0)

// Créer vecteur de taille k initialisé uniformément
Array.fill(k)(1.0 / k)
```

**Justification** : Fonction d'ordre supérieur pour créer des structures (passe une expression évaluée pour chaque élément).

### 10.2 `Array.tabulate`

**Fichier** : `MarkovGraph.scala`, ligne 263

**Exemple** :
```scala
def extractSubmatrix(...): Array[Array[Double]] = {
  val vertices = partition(componentIndex)
  val k = vertices.length
  Array.tabulate(k, k) { (i, j) =>
    matrix(vertices(i))(vertices(j))
  }
}
```

**Justification** : `tabulate` crée un tableau en appliquant une fonction aux indices (i, j). Plus fonctionnel que double boucle avec mutation.

---

## 11. Immutabilité vs Performance (compromis)

### 11.1 Tableaux mutables pour performance

**Fichier** : `MarkovGraph.scala`

**Exemple** :
```scala
def multiplyMatrices(A: Array[Array[Double]], B: Array[Array[Double]]): Array[Array[Double]] = {
  val n = A.length
  val result = Array.fill(n, n)(0.0)
  for (i <- 0 until n) {
    for (j <- 0 until n) {
      for (k <- 0 until n) {
        result(i)(j) += A(i)(k) * B(k)(j)  // ← Mutation locale
      }
    }
  }
  result  // API publique reste pure (pas de modification de A ou B)
}
```

**Justification** : Mutation **locale** (dans le scope de la fonction) pour performance. L'API publique reste pure (A et B non modifiés, retourne nouvelle matrice). Compromis pragmatique pour calculs intensifs.

### 11.2 Tarjan : nécessité de mutabilité

**Fichier** : `MarkovGraph.scala`, lignes 165-232

**Structures mutables** :
```scala
val P = ArrayBuffer.empty[Int]              // Pile mutable
val retour = Array.fill(n)(-1)              // Tableau mutable
val dansPile = Array.fill(n)(false)         // Tableau mutable
var num = 0                                  // Compteur mutable
```

**Justification** : L'algorithme de Tarjan nécessite :
- Une **pile** avec push/pop en O(1) → `ArrayBuffer` (structure mutable)
- Des **tableaux** mis à jour en place → `Array` (mutable)
- Un **compteur global** partagé → `var num`

**Alternative fonctionnelle pure** : Passer tous les états en paramètres (explosion du nombre de paramètres, perte de lisibilité, pas de gain réel). Pour Tarjan, la mutabilité **locale** (encapsulée dans la fonction) est un compromis acceptable.

---

## 12. Synthèse des notions utilisées

| Notion | Occurrences | Fichiers |
|--------|-------------|----------|
| **Immutabilité (`val`)** | ~50 | MarkovGraph, Main |
| **Collections immuables (`List`, `Map`)** | ~15 | MarkovGraph, Main |
| **`map`** | ~10 | MarkovGraph |
| **`flatMap`** | 2 | MarkovGraph |
| **`filter`** | 3 | MarkovGraph |
| **`foldLeft`** | 1 | MarkovGraph |
| **`forall`** | 2 | MarkovGraph |
| **`zip`, `zipWithIndex`** | 4 | MarkovGraph |
| **Pattern matching** | ~8 | MarkovGraph |
| **Fonctions anonymes** | ~12 | MarkovGraph |
| **`Option` (`Some`/`None`)** | 1 | MarkovGraph |
| **`Array.tabulate`** | 1 | MarkovGraph |
| **Récursion** | 3 | MarkovGraph, Main |

---

## 13. Justification des choix fonctionnels

| Aspect | Choix | Justification |
|--------|-------|---------------|
| **Variables** | `val` (immuable) | Par défaut ; `var` uniquement si nécessaire (boucles, compteurs) |
| **Collections** | `List`, `Map` (immuables) | Sécurité, pas de mutation accidentelle |
| **Transformations** | `map`, `flatMap`, `filter` | Déclaratif, évite boucles manuelles |
| **Accumulation** | `foldLeft` | Remplace `var` + boucle `for` |
| **Création structures** | `Array.tabulate`, `Array.fill` | Fonctions d'ordre supérieur |
| **Gestion erreurs** | `Option` | Évite exceptions / valeurs sentinelles |
| **Tarjan** | Mutabilité locale (pile, tableaux) | Nécessaire pour O(n+m), encapsulé |

---

## 14. Comparaison : Avant (impératif) / Après (fonctionnel)

### Exemple 1 : `matrixPower`

**Avant (impératif)** :
```scala
var result = M
for (_ <- 1 until k) {
  result = multiplyMatrices(result, M)
}
result
```

**Après (fonctionnel)** :
```scala
(1 until k).foldLeft(M) { (result, _) =>
  multiplyMatrices(result, M)
}
```

### Exemple 2 : `extractSubmatrix`

**Avant (impératif)** :
```scala
val sub = Array.ofDim[Double](k, k)
for (i <- 0 until k; j <- 0 until k) {
  sub(i)(j) = matrix(vertices(i))(vertices(j))
}
sub
```

**Après (fonctionnel)** :
```scala
Array.tabulate(k, k) { (i, j) =>
  matrix(vertices(i))(vertices(j))
}
```

### Exemple 3 : `diffMatrices`

**Avant (impératif)** :
```scala
var sum = 0.0
for (i <- 0 until n) {
  for (j <- 0 until n) {
    sum += math.abs(M(i)(j) - N(i)(j))
  }
}
sum
```

**Après (fonctionnel)** :
```scala
(0 until n).flatMap { i =>
  (0 until n).map { j =>
    math.abs(M(i)(j) - N(i)(j))
  }
}.sum
```

---

## Conclusion

Le projet utilise de manière **intensive** les concepts de programmation fonctionnelle :

- **Immutabilité** : Préférence systématique pour `val` et collections immuables
- **Fonctions d'ordre supérieur** : `map`, `flatMap`, `filter`, `foldLeft`, `forall`
- **Pureté** : Fonctions sans effets de bord (sauf affichage)
- **Pattern matching** : Déstructuration élégante des structures
- **Récursion** : Utilisée pour Tarjan et itération

**Compromis pragmatiques** :
- Tarjan utilise des structures mutables (pile, tableaux) pour des raisons de **performance** et de **conformité au pseudo-code académique**
- Les mutations sont **encapsulées** (locales aux fonctions) et n'affectent pas l'API publique
- Les fonctions restent **pures** : mêmes entrées → mêmes sorties

Ce compromis est typique en Scala : **FP par défaut, mutabilité locale quand justifiée**.
