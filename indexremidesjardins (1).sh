#!/bin/bash

  # Script de génération du tableau d'inventaire des notions Scala
  # Projet : Graphes de Markov - EFREI LSI 1

  PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
  SRC_DIR="$PROJECT_ROOT/src/main/scala" # A changer en fonction de votre architecture
  OUTPUT_FILE="$PROJECT_ROOT/inventaire_notions.md"

  echo "=== Génération de l'inventaire des notions Scala ==="
  echo ""

  # Fonction de recherche exhaustive
  search() {
      local pattern="$1"
      grep -rn -E "$pattern" "$SRC_DIR" --include="*.scala" 2>/dev/null
  }

  # Fonction pour ajouter une notion au tableau
  add_notion() {
      local notion="$1"
      local files="$2"

      if [ -n "$files" ]; then
          # Première ligne avec la notion
          echo "$files" | head -1 | awk -F':' -v notion="$notion" '{
              gsub(/^.*\/src\//,"src/",$1);
              gsub(/^.*\/markov\//,"src/main/scala/markov/",$1);
              print "| **" notion "** | " $1 " | " $2 " |"
          }' >> "$OUTPUT_FILE"
          
          # Lignes supplémentaires sans répéter la notion
          echo "$files" | tail -n +2 | awk -F':' '{
              gsub(/^.*\/src\//,"src/",$1);
              gsub(/^.*\/markov\//,"src/main/scala/markov/",$1);
              print "| | " $1 " | " $2 " |"
          }' >> "$OUTPUT_FILE"
      else
          # Si pas de fichiers trouvés, juste la notion avec cellules vides
          echo "| **$notion** | - | - |" >> "$OUTPUT_FILE"
      fi
  }

  # Début du markdown
  cat > "$OUTPUT_FILE" << 'HEADER'

---

## Tableau 1 : Notions de base

| Notion | Chemin du fichier | Ligne(s) |
|--------|-------------------|----------|
HEADER

  echo "📋 Tableau 1 : Notions de base..."

  # 1. Fonction anonyme / lambda
  add_notion "Fonction anonyme / lambda" "$(search '\.map.*=>|\.filter.*=>|\.fold.*=>' | grep -v '//' | head -5)"

  # 2. Fonction personnelle d'ordre supérieur
  add_notion "Fonction personnelle d'ordre supérieur" "$(search 'def.*\(.*: => ' | head -3)"

  # 3. lazy
  add_notion "lazy" "$(search '^[[:space:]]*lazy ')"

  # 4. andThen
  add_notion "andThen" "$(search '\.andThen')"

  # 5. compose
  add_notion "compose" "$(search '\.compose')"

  # 6. Object Companion
  add_notion "Object Companion" "$(search '^object [A-Z]')"

  # 7. Classe abstraite
  add_notion "Classe abstraite" "$(search '^abstract class')"

  # 8. Héritage de classes
  add_notion "Héritage de classes" "$(search '^[[:space:]]*class [A-Z].*extends' | grep -v 'case class')"

  # 9. override dans une classe
  add_notion "override dans une classe" "$(search '^[[:space:]]*override ' | head -10)"

  # 10. Trait
  add_notion "Trait" "$(search '^trait [A-Z]')"

  # 11. Héritage simple de trait
  add_notion "Héritage simple de trait" "$(search 'extends [A-Z][a-zA-Z]*' | grep -v ' with ' | head -10)"

  # 12. override dans un trait
  add_notion "override dans un trait" ""

  # 13. Héritage multiple de trait
  add_notion "Héritage multiple de trait" "$(search ' with [A-Z]' | head -5)"

  # 14. Traits comme Stackable Modifications
  add_notion "Traits comme Stackable Modifications" ""

  # 15. Trait mixé à une instance
  add_notion "Trait mixé à une instance" "$(search 'new [A-Z].*with')"

  # 16. Récursivité
  recursion=$(grep -rn "def parcours" "$SRC_DIR" --include="*.scala" 2>/dev/null | head -1)
  add_notion "Récursivité" "$recursion"

  # 17. Récursivité terminale
  add_notion "Récursivité terminale" "$(search '@tailrec')"

  # Section Collections
  cat >> "$OUTPUT_FILE" << 'COLLECTIONS'

---

## Tableau 2 : Collections

| Notion | Chemin du fichier | Ligne(s) |
|--------|-------------------|----------|
COLLECTIONS

  echo "📋 Tableau 2 : Collections..."

  # Collections immuables (en-tête seulement)
  echo "| **Collections immuables** | | |" >> "$OUTPUT_FILE"

  # Seq
  add_notion "Seq" "$(search ': Seq\[|Seq\[' | head -5)"

  # List
  add_notion "List" "$(search ': List\[|List\[|List\(' | head -5)"

  # Vector
  add_notion "Vector" "$(search ': Vector\[|Vector\[|Vector\(' | head -5)"

  # Range
  add_notion "Range" "$(search '1 to |\.to\(|\.until\(' | head -5)"

  # Map
  add_notion "Map" "$(search ': Map\[|Map\[' | head -5)"

  # Set
  add_notion "Set" "$(search ': Set\[|Set\[' | head -5)"

  # Tuple
  add_notion "Tuple" "$(search '\([a-zA-Z]+, [a-zA-Z]+\)|\(Int, Double\)' | head -5)"

  # LazyList
  add_notion "LazyList" "$(search 'LazyList')"

  # Séquence infinie
  add_notion "Séquence infinie" ""

  # Lazy view
  add_notion "Lazy view" "$(search '\.view')"

  # Collections mutables - import
  add_notion "Collections mutables (import scala.collection.mutable.*)" "$(search 'import scala\.collection\.mutable')"

  # Collections mutables spécifiques
  add_notion "Seq (mutable)" "$(search 'mutable\.Seq')"
  add_notion "Array" "$(search 'Array\[')"
  add_notion "ArrayBuffer" "$(search 'ArrayBuffer')"
  add_notion "ListBuffer" "$(search 'ListBuffer')"
  add_notion "StringBuilder" "$(search 'StringBuilder')"
  add_notion "ArrayQueue" "$(search 'ArrayQueue')"
  add_notion "Queue" "$(search 'mutable\.Queue')"
  add_notion "Stack" "$(search 'mutable\.Stack')"
  add_notion "HashMap" "$(search 'mutable\.HashMap')"
  add_notion "LinkedHashMap" "$(search 'LinkedHashMap')"
  add_notion "HashSet" "$(search 'mutable\.HashSet')"
  add_notion "LinkedHashSet" "$(search 'LinkedHashSet')"
  add_notion "BitSet" "$(search 'BitSet')"

  # Section Notions avancées
  cat >> "$OUTPUT_FILE" << 'ADVANCED'

---

## Tableau 3 : Notions avancées

| Notion | Chemin du fichier | Ligne(s) |
|--------|-------------------|----------|
ADVANCED

  echo "📋 Tableau 3 : Notions avancées..."

  # Ordered
  add_notion "Ordered" "$(search 'extends Ordered\[')"

  # Ordering
  add_notion "Ordering" "$(search 'Ordering\[')"

  # For-comprehension
  add_notion "For-comprehension" "$(search '^[[:space:]]*for [({]' | head -5)"

  # Pattern matching sur une collection
  pattern_coll=$(search 'case.*::.*=>|case Nil =>|case.*List\(' | head -5)
  add_notion "Pattern matching sur une collection" "$pattern_coll"

  # Pattern matching autre
  add_notion "Pattern matching autre" "$(search ' match \{' | head -10)"

  # Collections parallèles
  add_notion "Collections parallèles" "$(search '\.par\b')"

  # Option
  add_notion "Option" "$(search 'Option\[|Some\(|case None' | head -5)"

  # Opaque type
  add_notion "Opaque type" "$(search 'opaque type')"

  # Monoïd
  add_notion "Monoïd" "$(search 'Monoid')"

  # given with
  add_notion "given with" "$(search 'given.*with')"

  # Invariant[A]
  add_notion "Invariant[A]" "$(search 'type.*\[A\]|def.*\[A\]' | grep -v '\[\+A\]' | grep -v '\[-A\]' | grep -v '//' | head -3)"

  # Covariant[+A]
  add_notion "Covariant[+A]" "$(search '\[\+[A-Z]\]')"

  # Contravariant[-A]
  add_notion "Contravariant[-A]" "$(search '\[-[A-Z]\]')"

  # Upper bounds
  add_notion "Upper bounds" "$(search '<:' | head -5)"

  # Lower bounds
  add_notion "Lower bounds" "$(search '>:' | head -3)"

  # Curryfication
  add_notion "Curryfication" "$(search 'def.*\)\(' | grep -v '//' | head -5)"

  # Exceptions
  exceptions=$(search 'try \{|catch \{|throw new' | head -10)
  add_notion "Exceptions avec try catch throw throws" "$exceptions"

  # Either - Try
  either_try=$(search 'Either\[|Try\[|Left\(|Right\(' | head -10)
  add_notion "Either - Try" "$either_try"

  echo ""
  echo "✅ Inventaire généré avec succès !"
  echo "Fichier : $OUTPUT_FILE"
  echo ""
  wc -l "$OUTPUT_FILE" | awk '{print "Lignes générées: " $1}'
  echo ""