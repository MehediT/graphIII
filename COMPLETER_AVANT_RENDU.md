# À compléter avant le rendu

## Actions à effectuer OBLIGATOIREMENT

### 1. Compléter votre nom

Remplacer `[Nom de l'étudiant]` par votre nom dans :

- **RAPPORT_FINAL.md** (ligne 18)
- **RAPPORT_FINAL.pdf** (page de garde) - Si nécessaire, regénérer le PDF depuis le .md
- **inventaire_notions.md** (ligne 3)

**Commande de recherche** :
```bash
cd graphII
grep -r "\[Nom de l'étudiant\]" .
```

### 2. Vérifier le nombre de pages du PDF

**IMPORTANT** : Le rapport doit faire **entre 20 et 50 pages** (consigne stricte).

**Ouvrir `RAPPORT_FINAL.pdf` et vérifier le nombre de pages** :
- macOS : Aperçu → Affichage → Vignettes (affiche le nb de pages)
- Windows : Adobe Reader → regarder en bas à droite
- Linux : `pdfinfo RAPPORT_FINAL.pdf | grep Pages`

**Si le PDF n'a pas le bon nombre de pages** :
- < 20 pages : Ajouter du contenu dans `RAPPORT_FINAL.md` puis régénérer
- > 50 pages : Condenser le contenu

### 3. Renommer l'archive avec VOTRE nom

L'archive actuelle s'appelle `markov_partie2_PROJET.zip`

**Renommer en** :
```bash
cd /Users/mehedi.toureweblib.eu/Efrei/semester_7/Scala
mv markov_partie2_PROJET.zip markov_partie2_NOM_Prenom.zip
```

**Exemple** :
```bash
mv markov_partie2_PROJET.zip markov_partie2_DUPONT_Jean.zip
```

### 4. Vérifier le contenu de l'archive

```bash
unzip -l markov_partie2_VOTRENOM.zip | head -30
```

**Doit contenir** :
- `graphII/build.sbt`
- `graphII/project/build.properties`
- `graphII/src/main/scala/MarkovGraph.scala`
- `graphII/src/main/scala/Main.scala`
- `graphII/src/main/resources/exemple_meteo.txt`
- `graphII/exemple_meteo.txt`
- `graphII/README.md`
- `graphII/inventaire_notions.md`
- `graphII/RAPPORT_FINAL.pdf`

**Ne doit PAS contenir** :
- `.bsp/`, `.idea/`, `.scala-build/`, `target/`

### 5. Tester l'archive sur un environnement propre

**Simuler la correction** :

```bash
# Extraire l'archive
unzip markov_partie2_VOTRENOM.zip -d /tmp/test-rendu

# Tester la compilation
cd /tmp/test-rendu/graphII
sbt compile

# Tester l'exécution
sbt run

# Ou avec le script legacy
scala projet.sc
```

**Résultats attendus** :
- Compilation sans erreur
- Affichage de M, M³, M⁷
- Convergence en n=9
- CFC : C1 = {1,2,3,4,5}
- Distribution stationnaire conforme

---

## Checklist finale avant soumission

- [ ] Nom complété dans RAPPORT_FINAL.pdf, README.md, inventaire_notions.md
- [ ] PDF vérifié : entre 20 et 50 pages
- [ ] Archive renommée avec VOTRE nom
- [ ] Contenu de l'archive vérifié (zip -l)
- [ ] Compilation testée (sbt compile ou scalac)
- [ ] Exécution testée (sbt run ou scala projet.sc)
- [ ] Scaladoc présente dans le code (commentaires /** ... */)
- [ ] README.md complet et lisible
- [ ] inventaire_notions.md détaillé (14 notions)
- [ ] Fichiers temporaires supprimés

---

## En cas de problème

### Si SBT ne fonctionne pas chez le correcteur

Le projet est **compatible multi-mode** :

1. **Script Scala** : `scala projet.sc` fonctionne sans SBT
2. **Compilation manuelle** : `scalac` compile les sources
3. **Structure SBT** : Documentation SBT standard présente

Le README explique les 3 méthodes.

### Si le PDF est trop court/long

**Régénérer depuis le Markdown** :

```bash
# Avec pandoc (si installé)
pandoc RAPPORT_FINAL.md -o RAPPORT_FINAL.pdf \
  --pdf-engine=xelatex \
  --number-sections \
  --toc \
  -V geometry:margin=2.5cm

# Ou utiliser un éditeur Markdown (Typora, VS Code + extension)
```

---

## Deadline et soumission

**Attention** : Pénalité en cas de retard de plus d'une heure !

Vérifier :
- Date et heure limite de soumission
- Plateforme de rendu (Moodle, email, etc.)
- Format attendu (ZIP uniquement ? Autres ?)

---

**Une fois ces étapes complétées, le projet est prêt pour la soumission.**
