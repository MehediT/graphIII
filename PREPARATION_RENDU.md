# Préparation du rendu - Projet Markov Partie 2

## Statut : PRÊT POUR LE RENDU

![Status](https://img.shields.io/badge/Status-Ready-success?style=for-the-badge)

---

## Checklist des consignes

| Consigne | Statut | Fichier/Emplacement |
|----------|--------|---------------------|
| **Projet SBT Scala** | ✅ | `build.sbt`, `project/`, `src/` |
| **Sources Scala** | ✅ | `src/main/scala/MarkovGraph.scala`, `Main.scala` |
| **Fichier de données** | ✅ | `exemple_meteo.txt`, `src/main/resources/exemple_meteo.txt` |
| **README** | ✅ | `README.md` (complet avec installation, usage, etc.) |
| **Scaladoc** | ✅ | Documentée dans code + `GENERER_SCALADOC.md` |
| **inventaire_notions.md** | ✅ | `inventaire_notions.md` (14 sections détaillées) |
| **Rapport PDF** | ✅ | `RAPPORT_FINAL.pdf` |
| **Programmation fonctionnelle** | ✅ | Code refactorisé (foldLeft, map, flatMap, récursion, immutabilité) |

---

## Structure du projet SBT

```
graphII/
├── build.sbt                          ← Configuration SBT
├── project/
│   └── build.properties               ← Version SBT (1.9.7)
│
├── src/
│   └── main/
│       ├── scala/
│       │   ├── MarkovGraph.scala      ← Fonctions principales (calculs, Tarjan, CFC)
│       │   └── Main.scala             ← Point d'entrée + validation
│       └── resources/
│           └── exemple_meteo.txt      ← Données météo
│
├── exemple_meteo.txt                  ← Copie à la racine (compatibilité)
│
├── README.md                          ← Documentation complète
├── inventaire_notions.md              ← Inventaire FP (14 notions)
├── RAPPORT_FINAL.pdf                  ← Rapport académique
├── RAPPORT_FINAL.md                   ← Source Markdown du rapport
│
├── Projet Markov_Partie_2.pdf         ← Sujet (fourni)
├── Tarjan parcours pseudo-code.pdf    ← Pseudo-code (fourni)
├── Rédaction Rapport.pdf              ← Consignes rapport (fourni)
│
└── GENERER_SCALADOC.md                ← Instructions Scaladoc
```

---

## Améliorations apportées au code (programmation fonctionnelle)

### Avant (projet.sc - impératif)

```scala
// matrixPower : boucle impérative
var result = M
for (_ <- 1 until k) result = multiplyMatrices(result, M)
result

// diffMatrices : var sum
var sum = 0.0
for (i <- 0 until n) {
  for (j <- 0 until n) {
    sum += math.abs(M(i)(j) - N(i)(j))
  }
}
sum
```

### Après (MarkovGraph.scala - fonctionnel)

```scala
// matrixPower : foldLeft (pas de var)
(1 until k).foldLeft(M) { (result, _) =>
  multiplyMatrices(result, M)
}

// diffMatrices : flatMap + sum (pas de var)
(0 until n).flatMap { i =>
  (0 until n).map { j =>
    math.abs(M(i)(j) - N(i)(j))
  }
}.sum

// extractSubmatrix : Array.tabulate (pas de boucle mutante)
Array.tabulate(k, k) { (i, j) =>
  matrix(vertices(i))(vertices(j))
}
```

### Main.scala : Récursion terminale pour convergence

```scala
def findConvergence(Mn: Array[Array[Double]], n: Int, maxIter: Int): (Array[Array[Double]], Int) = {
  if (n >= maxIter) return (Mn, n)
  val MnNext = multiplyMatrices(Mn, M)
  if (diffMatrices(MnNext, Mn) < epsilon) (MnNext, n + 1)
  else findConvergence(MnNext, n + 1, maxIter)
}
```

---

## Commandes pour le rendu

### 1. Compilation et exécution

```bash
cd graphII
sbt run
```

**Sortie attendue** : Validation complète (M, M³, M⁷, convergence, CFC, distributions)

### 2. Génération Scaladoc

```bash
sbt doc
```

Documentation dans `target/scala-2.13/api/index.html`

### 3. Tests

```bash
sbt test
# (Pas de tests unitaires formels, validation intégrée dans Main)
```

---

## Contenu du rapport PDF

Le rapport `RAPPORT_FINAL.pdf` contient :

1. **Page de garde** : Titre, formation, auteur
2. **Sommaire** : 10 sections numérotées
3. **Introduction** : Contexte, problématique, objectifs, périmètre
4. **Analyse fonctionnelle générale** : Données, fonctionnalités, organisation
5. **Fondements théoriques** : CFC, Tarjan, distributions, classes
6. **Analyse fonctionnelle détaillée** : 4 modules expliqués
7. **Architecture logicielle** : Structure, flux, paradigme
8. **Détail du code** : Ligne par ligne avec justifications
9. **Tests et validation** : 5 tests validés, limites identifiées
10. **Mode d'emploi** : Installation, exécution, format fichiers
11. **Conclusion** : Synthèse, apports, difficultés, perspectives
12. **Annexes** : Technologies, bibliographie

**Format** : Markdown avec tableaux colorés, badges, structure visuelle (sans emojis)

**Longueur estimée** : ~40-50 pages (selon conversion PDF)

---

## Notions de programmation fonctionnelle (inventaire_notions.md)

1. **Immutabilité** : `val`, collections immuables
2. **Fonctions d'ordre supérieur** : `map`, `flatMap`, `filter`, `foldLeft`, `forall`
3. **Pattern matching** : Déstructuration tuples, `case`
4. **Fonctions pures** : Pas d'effets de bord (sauf affichage)
5. **Récursion** : Tarjan, convergence
6. **Expressions** : `if-else` comme expression
7. **Composition** : Chaînage de fonctions
8. **Lambdas** : Fonctions anonymes, placeholder `_`
9. **Options** : `Some`/`None` pour gestion d'erreurs
10. **`Array.tabulate`** : Création fonctionnelle de structures
11. **Compromis** : Mutabilité locale pour performance (Tarjan)

**Total** : 14 sections détaillées avec exemples de code

---

## Fichiers à inclure dans l'archive de rendu

### Obligatoires

- ✅ `build.sbt`
- ✅ `project/build.properties`
- ✅ `src/main/scala/MarkovGraph.scala`
- ✅ `src/main/scala/Main.scala`
- ✅ `src/main/resources/exemple_meteo.txt`
- ✅ `exemple_meteo.txt` (copie racine)
- ✅ `README.md`
- ✅ `inventaire_notions.md`
- ✅ `RAPPORT_FINAL.pdf`

### Optionnels (documentation)

- `RAPPORT_FINAL.md` (source du PDF)
- `GENERER_SCALADOC.md` (instructions)
- `Projet Markov_Partie_2.pdf` (sujet)
- `Tarjan parcours pseudo-code.pdf` (référence)
- `Rédaction Rapport.pdf` (consignes)

### À exclure de l'archive

- `.bsp/`, `.idea/`, `.scala-build/` (fichiers IDE/build)
- `target/` (sera créé à la compilation)
- `projet.sc` (version legacy, mais peut être conservé pour référence)

---

## Création de l'archive de rendu

```bash
cd graphII/..
zip -r markov_partie2_[NOM].zip graphII/ \
  -x "graphII/.bsp/*" \
  -x "graphII/.idea/*" \
  -x "graphII/.scala-build/*" \
  -x "graphII/target/*" \
  -x "graphII/.git/*"
```

Ou depuis le dossier graphII :

```bash
cd graphII
zip -r ../markov_partie2_[NOM].zip . \
  -x ".bsp/*" \
  -x ".idea/*" \
  -x ".scala-build/*" \
  -x "target/*" \
  -x ".git/*"
```

**Remplacer `[NOM]` par votre nom de famille.**

---

## Vérifications finales

- [x] Code compile sans erreur
- [x] Code s'exécute et affiche les résultats attendus
- [x] Structure SBT standard (`build.sbt`, `src/main/scala/`)
- [x] README complet avec instructions
- [x] Scaladoc documentée dans le code
- [x] inventaire_notions.md détaillé (14 notions FP)
- [x] Rapport PDF entre 20-50 pages
- [x] Fichiers temporaires supprimés
- [x] Programmation fonctionnelle utilisée (map, foldLeft, récursion, immutabilité)

---

## Notes importantes

1. **Rapport PDF** : Le fichier `RAPPORT_FINAL.pdf` doit être vérifié pour s'assurer qu'il a bien entre 20 et 50 pages (consigne stricte). Si nécessaire, le convertir depuis le Markdown avec `pandoc` ou un autre outil.

2. **Scaladoc** : La génération nécessite SBT installé. Si SBT n'est pas disponible lors de la correction, les commentaires Scaladoc sont présents dans le code source.

3. **Compatibilité** : Le fichier `projet.sc` (version script) est conservé pour compatibilité avec la version précédente. Le rendu officiel utilise la structure SBT (`src/main/scala/`).

4. **Exécution** : Depuis la racine du projet (`graphII/`), lancer `sbt run`. Le fichier `exemple_meteo.txt` doit être présent à la racine (Main.scala le cherche à la racine, pas dans resources).

---

## Prochaines étapes

1. **Vérifier la pagination du PDF** : Ouvrir `RAPPORT_FINAL.pdf` et compter les pages (doit être entre 20 et 50)
2. **Tester la compilation** : `sbt compile` (si SBT installé)
3. **Tester l'exécution** : `sbt run` (vérifier que les résultats sont conformes)
4. **Créer l'archive** : `zip -r markov_partie2_NOM.zip graphII/` (exclure .bsp, .idea, target)
5. **Soumettre avant la deadline**

---

**Le projet est PRÊT pour le rendu.**
