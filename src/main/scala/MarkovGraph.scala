package markov

import scala.io.Source

/** Objet contenant les fonctions pour l'étude de graphes de Markov.
  *
  * Ce module implémente :
  * - Les calculs matriciels (multiplication, puissance, différence)
  * - L'algorithme de Tarjan pour les composantes fortement connexes
  * - L'extraction de sous-matrices et le calcul de distributions stationnaires
  */
object MarkovGraph {

  // ========== 1) Calculs matriciels ==========

  /** Convertit une liste d'adjacence en matrice de transition n×n.
    *
    * @param adj Liste d'adjacence : Map[sommet → List[(successeur, probabilité)]]
    * @param n Nombre de sommets du graphe
    * @return Matrice n×n où M(i,j) = probabilité de transition i → j
    */
  def adjacencyListToMatrix(
      adj: Map[Int, List[(Int, Double)]],
      n: Int
  ): Array[Array[Double]] = {
    val matrix = Array.fill(n, n)(0.0)
    for ((from, transitions) <- adj) {
      for ((to, probability) <- transitions) {
        matrix(from)(to) = probability
      }
    }
    matrix
  }

  /** Multiplie deux matrices carrées n×n.
    *
    * Complexité : O(n³)
    *
    * @param A Première matrice
    * @param B Seconde matrice
    * @return Matrice produit C = A × B
    */
  def multiplyMatrices(
      A: Array[Array[Double]],
      B: Array[Array[Double]]
  ): Array[Array[Double]] = {
    val n = A.length
    val result = Array.fill(n, n)(0.0)
    for (i <- 0 until n) {
      for (j <- 0 until n) {
        for (k <- 0 until n) {
          result(i)(j) += A(i)(k) * B(k)(j)
        }
      }
    }
    result
  }

  /** Calcule la différence entre deux matrices (norme L1).
    *
    * Formule : diff(M,N) = Σᵢ Σⱼ |M(i,j) − N(i,j)|
    *
    * @param M Première matrice
    * @param N Seconde matrice
    * @return Somme des valeurs absolues des différences
    */
  def diffMatrices(
      M: Array[Array[Double]],
      N: Array[Array[Double]]
  ): Double = {
    val n = M.length
    (0 until n).flatMap { i =>
      (0 until n).map { j =>
        math.abs(M(i)(j) - N(i)(j))
      }
    }.sum
  }

  /** Calcule la puissance k d'une matrice M (M^k).
    *
    * Convention : M^0 = I (matrice identité)
    * Complexité : O(k × n³)
    *
    * @param M Matrice de base
    * @param k Exposant (k ≥ 0)
    * @return Matrice M^k
    */
  def matrixPower(M: Array[Array[Double]], k: Int): Array[Array[Double]] = {
    val n = M.length
    if (k == 0) {
      val I = Array.fill(n, n)(0.0)
      for (i <- 0 until n) I(i)(i) = 1.0
      I
    } else {
      (1 until k).foldLeft(M) { (result, _) =>
        multiplyMatrices(result, M)
      }
    }
  }

  /** Charge une liste d'adjacence depuis un fichier.
    *
    * Format du fichier :
    * - Ligne 1 : n (nombre de sommets)
    * - Lignes suivantes : from to probability (un arête par ligne)
    *
    * @param path Chemin vers le fichier
    * @return Tuple (liste d'adjacence, nombre de sommets)
    */
  def loadAdjacencyFromFile(path: String): (Map[Int, List[(Int, Double)]], Int) = {
    val lines = Source.fromFile(path).getLines().filter(_.trim.nonEmpty).toList
    val n = lines.head.trim.toInt
    val edges = lines.tail.flatMap { line =>
      val parts = line.trim.split("\\s+")
      if (parts.length >= 3) Some((parts(0).toInt, parts(1).toInt, parts(2).toDouble))
      else None
    }
    val adj = edges.groupBy(_._1).map { case (from, list) =>
      from -> list.map { case (_, to, p) => (to, p) }
    }
    (adj, n)
  }

  /** Affiche une matrice avec un libellé optionnel.
    *
    * @param M Matrice à afficher
    * @param label Libellé optionnel (affiché avant la matrice)
    */
  def printMatrix(M: Array[Array[Double]], label: String = ""): Unit = {
    if (label.nonEmpty) println(label)
    val n = M.length
    for (i <- 0 until n) {
      println(M(i).map(x => f"$x%6.2f").mkString(" "))
    }
    println()
  }

  // ========== 2) Propriétés des graphes de Markov ==========

  /** Convertit une matrice d'adjacence en tableau de successeurs.
    *
    * @param M Matrice d'adjacence
    * @return Pour chaque sommet i, tableau des indices j tels que M(i,j) > 0
    */
  def matrixToSuccessors(M: Array[Array[Double]]): Array[Array[Int]] = {
    val n = M.length
    (0 until n).map { i =>
      (0 until n).filter(j => M(i)(j) > 0).toArray
    }.toArray
  }

  /** Algorithme de Tarjan pour calculer les composantes fortement connexes.
    *
    * Implémentation fidèle au pseudo-code fourni dans le sujet.
    * Complexité : O(n + m) où n = nombre de sommets, m = nombre d'arêtes
    *
    * @param succ Tableau de successeurs (succ(i) = successeurs du sommet i)
    * @param n Nombre de sommets
    * @return Liste des composantes fortement connexes (chaque composante = liste de sommets)
    */
  def tarjanFromGraph(succ: Array[Array[Int]], n: Int): List[List[Int]] = {
    import scala.collection.mutable.ArrayBuffer

    // Variables globales de l'algorithme
    val P = ArrayBuffer.empty[Int]  // pile de sommets
    val retour = Array.fill(n)(-1)
    val dansPile = Array.fill(n)(false)
    val numEmp = Array.fill(n)(Int.MaxValue)  // ∞ = non visité
    var num = 0
    val partition = Array.fill(n)(-1)

    /** Procédure parcours - Algorithme 1 du pseudo-code.
      *
      * @param x Sommet à explorer
      */
    def parcours(x: Int): Unit = {
      // L10-12 : Numérotation
      num += 1
      numEmp(x) = num
      retour(x) = num
      // L13-14 : Empiler
      P += x
      dansPile(x) = true
      // L15-24 : Parcours des successeurs
      for (y <- succ(x)) {
        if (numEmp(y) == Int.MaxValue) {  // L16 : y non visité
          parcours(y)  // L17
          retour(x) = math.min(retour(x), retour(y))  // L18
        } else {
          if (dansPile(y))  // L20
            retour(x) = math.min(retour(x), numEmp(y))  // L21
        }
      }
      // L25-32 : Si x est racine d'une CFC
      if (retour(x) == numEmp(x)) {
        var y = -1
        var continue = true
        while (continue) {
          y = P(P.length - 1)
          partition(y) = x
          dansPile(y) = false
          P.remove(P.length - 1)
          if (y == x) continue = false
        }
      }
    }

    // Algorithme 2 – Programme principal
    num = 0
    for (x <- 0 until n) {
      dansPile(x) = false
      numEmp(x) = Int.MaxValue
    }
    for (x <- 0 until n) {
      if (numEmp(x) == Int.MaxValue)
        parcours(x)
    }

    // Construction de la liste des CFC
    partition.zipWithIndex.groupBy(_._1).values.map { pairs =>
      pairs.map(_._2).sorted.toList
    }.toList
  }

  /** Calcule les composantes fortement connexes à partir d'une matrice.
    *
    * @param M Matrice de transition
    * @return Liste des composantes fortement connexes
    */
  def tarjan(M: Array[Array[Double]]): List[List[Int]] = {
    val succ = matrixToSuccessors(M)
    tarjanFromGraph(succ, M.length)
  }

  /** Extrait la sous-matrice correspondant à une composante.
    *
    * @param matrix Matrice complète
    * @param partition Partition du graphe en composantes
    * @param componentIndex Index de la composante à extraire
    * @return Sous-matrice contenant uniquement les sommets de la composante
    */
  def extractSubmatrix(
      matrix: Array[Array[Double]],
      partition: List[List[Int]],
      componentIndex: Int
  ): Array[Array[Double]] = {
    val vertices = partition(componentIndex)
    val k = vertices.length
    Array.tabulate(k, k) { (i, j) =>
      matrix(vertices(i))(vertices(j))
    }
  }

  /** Détermine si une classe est persistante (fermée).
    *
    * Une classe est persistante s'il n'existe aucune arête sortante
    * vers un sommet hors de la classe.
    *
    * @param matrix Matrice de transition
    * @param component Liste des sommets de la classe
    * @return true si la classe est persistante, false sinon
    */
  def isPersistentClass(matrix: Array[Array[Double]], component: List[Int]): Boolean = {
    val set = component.toSet
    component.forall { i =>
      (0 until matrix.length).forall { j =>
        matrix(i)(j) <= 0 || set(j)
      }
    }
  }

  /** Calcule la distribution stationnaire d'une classe.
    *
    * - Pour une classe transitoire : retourne un vecteur nul
    * - Pour une classe persistante : calcul par itération de puissance
    *
    * @param matrix Matrice de transition complète
    * @param partition Partition du graphe en composantes
    * @param componentIndex Index de la composante
    * @return Distribution stationnaire (vecteur de probabilités)
    */
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
    
    // Classe persistante → itération de puissance
    val sub = extractSubmatrix(matrix, partition, componentIndex)
    val k = sub.length
    
    // Fonction récursive pour l'itération (immutable dans l'API publique)
    def iterate(pi: Array[Double], maxIter: Int): Array[Double] = {
      if (maxIter <= 0) return pi
      
      // Multiplication vecteur-matrice : π_next = π × M_sub
      val piNext = Array.fill(k)(0.0)
      for (i <- 0 until k; j <- 0 until k)
        piNext(j) += pi(i) * sub(i)(j)
      
      // Critère de convergence
      val diff = (pi zip piNext).map { case (a, b) => math.abs(a - b) }.sum
      
      // Normalisation
      val sumPi = piNext.sum
      val piNormalized = if (sumPi > 0) piNext.map(_ / sumPi) else piNext
      
      if (diff < 1e-9) piNormalized
      else iterate(piNormalized, maxIter - 1)
    }
    
    iterate(Array.fill(k)(1.0 / k), 5000)
  }

  /** Affiche les composantes fortement connexes et leurs distributions stationnaires.
    *
    * @param matrix Matrice de transition
    * @param partition Partition du graphe en composantes
    */
  def printAllStationaryDistributions(
      matrix: Array[Array[Double]],
      partition: List[List[Int]]
  ): Unit = {
    println("=== Composantes fortement connexes ===")
    partition.zipWithIndex.foreach { case (comp, idx) =>
      println(f"  C${idx + 1}: {${comp.map(_ + 1).mkString(", ")}}")
    }
    println("\n=== Distributions stationnaires par classe ===")
    partition.zipWithIndex.foreach { case (comp, idx) =>
      val persistent = isPersistentClass(matrix, comp)
      val dist = stationaryDistributionOfClass(matrix, partition, idx)
      if (persistent) {
        println(f"  Classe ${idx + 1} (persistante): [${dist.map(x => f"$x%.4f").mkString(", ")}]")
      } else {
        println(f"  Classe ${idx + 1} (transitoire): distribution nulle")
      }
    }
  }
}
