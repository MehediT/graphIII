# Génération de la Scaladoc

## Avec SBT

```bash
cd graphII
sbt doc
```

La documentation sera générée dans : `target/scala-2.13/api/`

Ouvrir `target/scala-2.13/api/index.html` dans un navigateur.

## Avec scaladoc directement

Si SBT n'est pas disponible :

```bash
cd graphII/src/main/scala
scaladoc -d ../../../scaladoc MarkovGraph.scala Main.scala
```

La documentation sera dans le dossier `scaladoc/`.

## Structure de la Scaladoc

La documentation générée contient :

- **Package `markov`**
  - **Object `MarkovGraph`** : Toutes les fonctions de calculs matriciels, Tarjan, distributions
    - `adjacencyListToMatrix`
    - `multiplyMatrices`
    - `diffMatrices`
    - `matrixPower`
    - `tarjanFromGraph`
    - `tarjan`
    - `extractSubmatrix`
    - `isPersistentClass`
    - `stationaryDistributionOfClass`
    - `printAllStationaryDistributions`
  - **Object `Main`** : Point d'entrée avec validation

Chaque fonction est documentée avec :
- Description du rôle
- Paramètres (`@param`)
- Valeur de retour (`@return`)
- Complexité si pertinente
