package markov

import markov.MarkovGraph._

/** Point d'entrée principal pour la validation du projet Markov.
  *
  * Ce programme charge l'exemple météo et effectue les validations demandées :
  * - Affichage de la matrice M
  * - Calcul de M³ et M⁷
  * - Test de convergence diff(Mⁿ, Mⁿ⁻¹) < ε
  * - Calcul des composantes fortement connexes (Tarjan)
  * - Affichage des distributions stationnaires par classe
  */
object Main extends App {

  println("=" * 70)
  println("  Projet Markov - Partie 2 : Étude de graphes de Markov")
  println("  Application aux prévisions météorologiques")
  println("=" * 70)
  println()

  // Chargement de l'exemple météo
  val dataPath = "exemple_meteo.txt"
  val (adjMeteo, nMeteo) = loadAdjacencyFromFile(dataPath)
  val M = adjacencyListToMatrix(adjMeteo, nMeteo)

  // ===== Validation 1 : Affichage de M =====
  println("=== Matrice M (exemple météo) ===")
  printMatrix(M, "M =")

  // ===== Validation 2 : Calcul de M³ =====
  val M3 = matrixPower(M, 3)
  println("=== M^3 ===")
  printMatrix(M3, "M^3 =")

  // ===== Validation 3 : Calcul de M⁷ =====
  val M7 = matrixPower(M, 7)
  println("=== M^7 ===")
  printMatrix(M7, "M^7 =")

  // ===== Validation 4 : Convergence diff(Mⁿ, Mⁿ⁻¹) < ε =====
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
  println()

  // ===== Validation 5 : Composantes et distributions stationnaires =====
  val partitionMeteo = tarjan(M)
  println("=== 2.4 – Test sur l'exemple météo ===")
  printAllStationaryDistributions(M, partitionMeteo)
  
  println()
  println("=" * 70)
  println("  Validation terminée avec succès")
  println("=" * 70)
}
