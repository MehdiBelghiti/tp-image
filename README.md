# Projet de Traitement d'Images avec BoofCV

Ce projet présente plusieurs filtres et transformations d'images réalisés avec la bibliothèque [BoofCV](https://boofcv.org). L’objectif principal est d'implémenter différentes opérations sur des images en niveaux de gris et en couleur.

---

## Table des Matières

- [Introduction](#introduction)
- [Fonctionnalités](#fonctionnalités)
- [Étapes Réalisées](#étapes-réalisées)
  - [Images en Niveaux de Gris](#images-en-niveaux-de-gris)
  - [Filtrage Spatial et Détection de Contours](#filtrage-spatial-et-détection-de-contours)
  - [Traitement des Images Couleur](#traitement-des-images-couleur)
  - [Conversion RGB ⇔ HSV et Visualisation d'Histogrammes](#conversion-rgb--hsv-et-visualisation-dhistogrammes)
  - [Histogramme 2D Teinte/Saturation](#histogramme-2d-teinte-saturation)
- [Difficultés Rencontrées](#difficultés-rencontrées)
- [Conclusion](#conclusion)

---

## Introduction

Ce projet a été développé dans le cadre d'un TP sur le traitement d'images. Il exploite la bibliothèque BoofCV pour :
- Charger et sauvegarder des images.
- Convertir entre les formats `BufferedImage`, `GrayU8` et `Planar<GrayU8>`.
- Appliquer des filtres et des transformations sur des images en niveaux de gris et en couleur.
- Effectuer des conversions entre les espaces de couleurs RGB et HSV.
- Visualiser des histogrammes pour mieux comprendre la répartition des couleurs dans les images.

---

## Fonctionnalités

Les principales fonctionnalités implémentées sont :

- **Opérations sur images en niveaux de gris :**
  - Seuillage
  - Ajustement de la luminosité
  - Extension de la dynamique (avec et sans lookup table)
  - Égalisation d'histogramme

- **Filtrage spatial et détection de contours :**
  - Filtre moyenneur (flou)
  - Détection de contours (gradient avec Sobel et Prewitt)

- **Traitement des images couleur :**
  - Conversion d'une image couleur en niveaux de gris (griser) avec la formule 0.3R + 0.59G + 0.11B.
  - Conversion entre les espaces RGB et HSV.
  - Colorisation : modification de la teinte tout en conservant la saturation et la luminosité.
  
- **Visualisation d'histogrammes :**
  - Histogramme des teintes d'une image couleur.
  - Histogramme 2D teinte/saturation.

---

## Étapes Réalisées

### Images en Niveaux de Gris

- **Chargement et Conversion :**
  - Utilisation de `UtilImageIO.loadImage` pour charger des images.
  - Conversion en format `GrayU8` pour les traitements en niveaux de gris.

- **Traitements Appliqués :**
  - **Seuillage** : Transformation des pixels selon un seuil.
  - **Ajustement de luminosité** : Modification de la luminosité avec un clamp dans [0, 255].
  - **Extension de la dynamique** : Rééchelonnement linéaire pour que le niveau minimal devienne 0 et le niveau maximal 255.
  - **Égalisation d'histogramme** : Utilisation d'un histogramme cumulé pour redistribuer les niveaux de gris.

### Filtrage Spatial et Détection de Contours

- **Filtre Moyenneur :**
  - Calcul de la moyenne sur un voisinage de taille impaire (par exemple 11x11) pour flouter l'image.
  - Gestion des bords en ne traitant pas les pixels dont le voisinage complet n'est pas disponible.

- **Détection de Contours :**
  - Implémentation des opérateurs de Sobel et de Prewitt en calculant la magnitude du gradient.

### Traitement des Images Couleur

- **Conversion en Niveaux de Gris :**
  - Utilisation de la formule pondérée (0.3R + 0.59G + 0.11B) pour convertir une image couleur en niveaux de gris.

### Conversion RGB ⇔ HSV et Visualisation d'Histogrammes

- **Conversion :**
  - Utilisation des méthodes `ColorHsv.rgbToHsv` et `ColorHsv.hsvToRgb` pour passer de l'espace RGB à l'espace HSV.
  - Attention portée aux types : BoofCV attend des tableaux de type `float[]` pour récupérer les valeurs HSV.

- **Colorisation :**
  - Filtre permettant de modifier la teinte de chaque pixel tout en conservant la saturation et la luminosité.

- **Visualisation d'Histogrammes :**
  - Histogramme des teintes (H) : valeurs entières dans [0, 360[.
  - Histogramme 2D teinte/saturation : répartition des pixels en fonction de leur teinte (0 à 359) et de leur saturation (approximée en pourcentage de 0 à 100).

### Histogramme 2D Teinte/Saturation

- **Calcul :**
  - Pour chaque pixel, la couleur RGB est convertie en HSV.
  - La teinte est arrondie dans l'intervalle [0,359] et la saturation est multipliée par 100 puis arrondie pour obtenir une valeur dans [0,100].
  - Un tableau 2D `hist[360][101]` est mis à jour avec le nombre de pixels pour chaque couple (teinte, saturation).

- **Visualisation :**
  - Une image est générée où l'axe horizontal représente la teinte et l'axe vertical la saturation (avec saturation 0 en bas).
  - L'intensité des pixels dans cette image correspond à la densité de pixels dans le bin (après normalisation).

---

## Difficultés Rencontrées

- **Conversion entre Types et Formats :**
  - Comprendre et utiliser les formats `GrayU8` et `Planar<GrayU8>` pour manipuler des images en niveaux de gris et en couleur.
  - Conversion entre `BufferedImage` et les formats BoofCV via `ConvertBufferedImage`.

- **Manipulation de l'Espace Couleur HSV :**
  - Les méthodes `ColorHsv.rgbToHsv` et `ColorHsv.hsvToRgb` nécessitent des tableaux `float[]` pour récupérer les valeurs. Une tentative d'utiliser un `int[]` en sortie générait des erreurs de type.
  - La conversion des valeurs HSV en RGB pour reconstruire des images a nécessité un soin particulier dans la conversion des valeurs en entiers.

- **Normalisation et Quantification :**
  - Pour la visualisation des histogrammes, il a fallu normaliser correctement les valeurs (par exemple, multiplier la saturation par 100 pour obtenir des pourcentages).
  - La quantification des teintes et des saturations dans des intervalles discrets (0-359 pour la teinte et 0-100 pour la saturation) a demandé une réflexion sur l'arrondi et le contrôle des bornes.

- **Gestion des Bords dans les Filtres :**
  - Lors de l’application de filtres spatiaux, la gestion des bords a été simplifiée en ignorant les pixels pour lesquels le voisinage complet n'était pas disponible.

---

## Conclusion

Ce projet a permis de mettre en œuvre diverses techniques de traitement d'image avec BoofCV, allant des opérations simples sur les images en niveaux de gris à des transformations plus complexes sur les images couleur. Les défis principaux ont concerné la manipulation des espaces de couleurs et la gestion des différents types de données, mais une lecture attentive de la documentation et une approche itérative ont permis de surmonter ces obstacles.

---

## Utilisation

Pour lancer le programme, utilisez l'une des commandes suivantes :

- **Traitement en niveaux de gris (par défaut) :**
  ```bash
  java imageprocessing.GrayLevelProcessing <inputImage> <outputImage>
