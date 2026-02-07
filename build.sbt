name := "graphe-markov"

version := "2.0"

scalaVersion := "2.13.12"

// Configuration pour générer la Scaladoc
Compile / doc / scalacOptions ++= Seq(
  "-doc-title", "Projet Graphes de Markov - Partie 2",
  "-doc-version", "2.0",
  "-doc-footer", "Master 1 Informatique - 2025/2026"
)

// Options de compilation
scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-unchecked"
)
