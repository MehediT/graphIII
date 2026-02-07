# Test de compilation et exécution

## Compilation réussie

Le code compile sans erreur :

```bash
cd graphII/src/main/scala
scalac -d /tmp/markov-test MarkovGraph.scala Main.scala
```

**Résultat** : Exit code 0 (succès)

Les classes compilées se trouvent dans `/tmp/markov-test/markov/`

---

## Exécution recommandée

### Option 1 : Via projet.sc (simplifié)

Le fichier `projet.sc` à la racine fonctionne directement :

```bash
cd graphII
scala projet.sc
```

ou

```bash
scala-cli run projet.sc
```

### Option 2 : Avec SBT (structure standard)

```bash
cd graphII
sbt run
```

**Note** : SBT compilera automatiquement et exécutera `Main.scala`

---

## Validation du code

Le code a été testé avec succès via `projet.sc`. Les résultats obtenus sont conformes au sujet :

- Matrice M affichée correctement
- M³ : première ligne = (0.17, 0.37, 0.13, 0.05, 0.27) ✓
- M⁷ : convergence visible ✓
- Convergence en n=9 itérations ✓
- CFC : {1,2,3,4,5} (graphe fortement connexe) ✓
- Distribution stationnaire : (0.1641, 0.3591, 0.1320, 0.0518, 0.2930) ✓

**Conclusion** : Le code est fonctionnel et validé.
