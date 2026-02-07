# Projet informatique en Scala – Étude de graphes de Markov (Partie 2)
## Application aux prévisions météorologiques

Ce rapport présente la synthèse des notions théoriques nécessaires à l’étude des graphes de Markov en temps discret, puis leur application au modèle météorologique proposé. La partie implémentation fera l’objet d’un développement ultérieur.

---

## I. Regroupement de sommets en classes

### I.1 Définition des classes

Des sommets sont regroupés dans une **classe** s’ils **communiquent tous entre eux** : à partir d’un sommet de la classe, on peut atteindre tous les autres sommets de cette classe (et revenir au point de départ). Ces regroupements sont appelés **composantes fortement connexes** (ou simplement *classes*).

### I.2 Exemples

**Exemple 1 – Graphe à 4 sommets**

Matrice de transition :

```
M = (0.5  0.5  0    0   )
    (0    0.5  0.5  0   )
    (0    0    0.5  0.5 )
    (0.5  0    0    0.5 )
```

Tous les sommets communiquent entre eux : on peut aller de n’importe quel sommet à n’importe quel autre et revenir. Il n’y a qu’**une seule classe** : {1, 2, 3, 4}.

**Exemple 2 – Graphe à 10 sommets (validation partie 1)**

- Le sommet 2 ne communique qu’avec lui-même → classe {2}.
- Le sommet 4 ne communique qu’avec lui-même → classe {4}.
- Le sommet 10 : on peut en partir vers d’autres sommets mais pas y revenir → classe {10}.
- Le sommet 9 : même situation → classe {9}.
- Les sommets 1, 5 et 7 communiquent tous entre eux → classe {1, 5, 7}.
- Les sommets 3, 6 et 8 communiquent tous entre eux → classe {3, 6, 8}.

L’ensemble des classes est donc : **{{1, 5, 7}, {2}, {3, 6, 8}, {4}, {9}, {10}}**.

### I.3 Propriétés des classes

- Tout sommet appartient à **une et une seule** classe.
- Toute classe contient **au moins un** sommet.
- Un graphe possède **au moins une** classe.
- L’ensemble des classes forme une **partition** de l’ensemble des sommets du graphe.

### I.4 Détermination des classes : algorithme de Tarjan

Pour obtenir cette partition, on utilise l’**algorithme de Tarjan**, qui permet de calculer les composantes fortement connexes en une complexité linéaire en la taille du graphe. Le pseudo-code est fourni dans le sujet ; son implémentation sera détaillée dans la partie III (Implémentation).

**Entrée** : graphe (ou matrice d’adjacence).  
**Sortie** : liste des composantes fortement connexes (ex. pour le graphe à 10 sommets : C1 = {1, 7, 5}, C2 = {2}, C3 = {3, 8, 6}, C4 = {4}, C5 = {9}, C6 = {10}). L’ordre d’affichage des composantes n’a pas d’importance.

---

## II. Propriétés des graphes de Markov pour les probabilités

### II.1 Notion de distribution

Sur un graphe de Markov, les arêtes sont pondérées par des **probabilités de transition** d’un état à un autre. À un instant donné, le système se trouve dans un état ; comme les transitions sont probabilistes, on ne peut pas prédire avec certitude l’état futur, mais on peut donner les **probabilités** d’être dans chaque état.

Une **distribution** (notée Π) est un **vecteur ligne** dont chaque composante est la probabilité que le système soit dans l’état correspondant. La somme des composantes est donc égale à 1.

**Exemple** : si aujourd’hui il fait beau (*Sunny*), le système est dans l’état 1 avec probabilité 1. La distribution associée est :

Π = (1  0  0  0  0)

### II.2 Graphe de Markov « Météo »

Le graphe étudié modélise l’évolution de la météo jour après jour : chaque état correspond à un type de temps, chaque transition à la probabilité de passer d’un état à l’autre en une journée.

| Numéro d’état | 1 | 2 | 3 | 4 | 5 |
|---------------|---|---|---|---|---|
| Météo         | Sunny | Cloudy | Rain | Storm | Sunny spells |

La **matrice de transition** M (probabilités de passage d’un état à l’autre en une journée) est :

```
M = (0.34  0.27  0     0.18  0.21)
    (0.20  0.40  0.20  0     0.20)
    (0     0.41  0.37  0.09  0.13)
    (0     0.68  0.20  0.12  0   )
    (0.12  0.30  0     0     0.58)
```

### II.3 Calcul de l’évolution en temps discret

Connaissant une distribution initiale Π₀, l’évolution après une transition est :

Π₁ = Π₀ · M

En répétant le processus :

Π₂ = Π₁ · M = (Π₀ · M) · M = Π₀ · M²

et plus généralement, après *n* étapes (*n* jours) :

**Πₙ = Π₀ · Mⁿ**

Cette relation est la base des calculs qui suivent.

---

### II.4 Application aux questions météo

#### Question 1 – Probabilité « nuageux » dans 3 jours sachant « beau » aujourd’hui

- État initial : *Sunny* (état 1).  
- Distribution initiale : Π₀ = (1  0  0  0  0).  
- On calcule Π₃ = Π₀ · M³.

Le calcul donne :

Π₃ = (0.17  0.37  0.13  0.05  0.27)

La probabilité que le temps soit nuageux (*Cloudy*, état 2) dans 3 jours est la deuxième composante : **0,37**, soit **37 %**.

#### Question 2 – Distribution dans une semaine sachant « pluie » aujourd’hui

- État initial : *Rain* (état 3).  
- Distribution initiale : Π₀ = (0  0  1  0  0).  
- On calcule Π₇ = Π₀ · M⁷.

La matrice M⁷ a toutes ses lignes identiques (à l’arrondi près), ce qui donne :

Π₇ = (0.16  0.36  0.13  0.05  0.29)

Sachant qu’il pleut aujourd’hui, dans une semaine on obtient donc :
- 16 % de chances qu’il fasse beau (Sunny),
- 36 % de chances que le temps soit nuageux (Cloudy),
- 13 % de chances qu’il pleuve (Rain),
- 5 % de chances qu’il y ait un orage (Storm),
- 29 % de chances qu’il y ait des éclaircies (Sunny spells).

On constate que l’influence de l’état initial s’atténue après plusieurs transitions.

#### Question 3 – Existence d’une distribution stationnaire

On cherche à savoir si les probabilités finissent par devenir **indépendantes** de la distribution initiale, c’est-à-dire s’il existe une **distribution stationnaire** Π* telle que :

**Π* · M = Π***

Les résultats de la question 2 montrent que M⁷ a des lignes égales : quelle que soit la distribution initiale, Π₇ est (à l’arrondi près) le même vecteur. Le système converge donc vers un **régime stable**.

La distribution stationnaire du graphe météo est :

Π* = (0.16  0.36  0.13  0.05  0.29)

Elle donne les probabilités à long terme de chaque état météorologique. Cette convergence est liée au fait que le graphe est fortement connexe et sans état absorbant ; dans ce modèle simplifié, la distribution stationnaire est atteinte après un nombre relativement faible de transitions (déjà visible à 7 jours).

---

## III. Implémentation *(à rédiger ultérieurement)*

La partie implémentation couvrira notamment :

1. **Calculs matriciels** : construction de la matrice à partir d’une liste d’adjacence, multiplication de matrices, fonction de différence entre deux matrices, et validations (M, M³, M⁷, convergence).
2. **Propriétés des graphes** : extraction de sous-matrice pour une classe, calcul et affichage des distributions stationnaires pour toutes les classes, tests sur les fichiers exemples.

Cette section sera complétée une fois l’implémentation réalisée.
