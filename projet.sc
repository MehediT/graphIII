// Run from repo root: scala -nc projet.sc

def adjacencyListToMatrix(
  adj: Map[Int, List[(Int, Double)]],
  n: Int
): Array[Array[Double]] = {

  // Initialisation d'une matrice n x n remplie de 0.0
  val matrix = Array.fill(n, n)(0.0)

  // Parcours de la liste d’adjacence
  for ((from, transitions) <- adj) {
    for ((to, probability) <- transitions) {
      matrix(from)(to) = probability
    }
  }

  matrix
}

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

def diffMatrices(
  M: Array[Array[Double]],
  N: Array[Array[Double]]
): Double = {

  val n = M.length
  var sum = 0.0

  for (i <- 0 until n) {
    for (j <- 0 until n) {
      sum += math.abs(M(i)(j) - N(i)(j))
    }
  }

  sum
}

/** Calcule M^k (k >= 0). M^0 = I. */
def matrixPower(M: Array[Array[Double]], k: Int): Array[Array[Double]] = {
  val n = M.length
  if (k == 0) {
    val I = Array.fill(n, n)(0.0)
    for (i <- 0 until n) I(i)(i) = 1.0
    I
  } else {
    var result = M
    for (_ <- 1 until k) result = multiplyMatrices(result, M)
    result
  }
}

/** Charge une liste d'adjacence depuis un fichier.
  * Format : première ligne = n (nombre de sommets), puis une ligne par arête "from to prob" (indices 0 à n-1).
  */
def loadAdjacencyFromFile(path: String): (Map[Int, List[(Int, Double)]], Int) = {
  val lines = scala.io.Source.fromFile(path).getLines().filter(_.trim.nonEmpty).toList
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

/** Affiche une matrice avec un libellé optionnel. */
def printMatrix(M: Array[Array[Double]], label: String = ""): Unit = {
  if (label.nonEmpty) println(label)
  val n = M.length
  for (i <- 0 until n) {
    println(M(i).map(x => f"$x%6.2f").mkString(" "))
  }
  println()
}

// ========== Validation – Calculs matriciels ==========
// À exécuter depuis le répertoire graphII (où se trouve exemple_meteo.txt).

val (adjMeteo, nMeteo) = loadAdjacencyFromFile("exemple_meteo.txt")
val M = adjacencyListToMatrix(adjMeteo, nMeteo)

println("=== Matrice M (exemple météo) ===")
printMatrix(M, "M =")

val M3 = matrixPower(M, 3)
println("=== M^3 ===")
printMatrix(M3, "M^3 =")

val M7 = matrixPower(M, 7)
println("=== M^7 ===")
printMatrix(M7, "M^7 =")

// Convergence : M^n tel que diff(M^n, M^(n-1)) < epsilon
val epsilon = 0.01
var Mn = M
var MnPrev = matrixPower(M, 0)
var n = 1
while (n <= 100 && diffMatrices(Mn, MnPrev) >= epsilon) {
  MnPrev = Mn
  Mn = multiplyMatrices(Mn, M)
  n += 1
}
println(f"=== Convergence (epsilon = $epsilon) ===")
if (n <= 100) {
  println(f"Pour n = $n, diff(M^$n, M^${n-1}) < $epsilon")
  println("Distribution stationnaire (première ligne de M^n) :")
  println(Mn(0).map(x => f"$x%.2f").mkString(" "))
} else {
  println("Convergence non atteinte en 100 itérations pour ce critère.")
}
