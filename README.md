[hisyogramme.txt](https://github.com/user-attachments/files/18816328/hisyogramme.txt)# Projet de Traitement d'Images avec BoofCV

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

## Exemples d'Images
- **Luminosité :**
  Avant: ![beach](https://github.com/user-attachments/assets/6db82bb9-151d-4123-b327-011f8f5c068a)
![image-1-low_dynamic](https://github.com/user-attachments/assets/85020902-6ed8-4d5c-9cd7-0f1a1dc70136)

  Après:
![image-1-low_dynamic-BrightnessAdjust](https://github.com/user-attachments/assets/f35a13d8-a2b4-4725-9dab-94d1c6b0c53a)
![Tp-1-Q2-beach-BrightnessAdjust](https://github.com/user-attachments/assets/8984f9c7-0c37-4213-854e-e4f4993194de)

- **Réglage du contraste : dynamique de l'image :**
  Avant: 
![image-1-low_dynamic](https://github.com/user-attachments/assets/85020902-6ed8-4d5c-9cd7-0f1a1dc70136)
  Après:
![image-1-low_dynamic-corrected](https://github.com/user-attachments/assets/79b144c6-b60e-4433-b422-a6a46e90c7c5)

- **Réglage du contraste : égalisation d'histogramme :**
[UploadingHistogramme de la bande 0 :
Niveau 94 : 4790 pixels
Niveau 96 : 1 pixels
Niveau 97 : 14 pixels
Niveau 98 : 791 pixels
Niveau 99 : 8825 pixels
Niveau 100 : 14917 pixels
Niveau 101 : 20616 pixels
Niveau 102 : 16623 pixels
Niveau 103 : 14411 pixels
Niveau 104 : 9612 pixels
Niveau 105 : 10911 pixels
Niveau 106 : 9271 pixels
Niveau 107 : 8623 pixels
Niveau 108 : 6517 pixels
Niveau 109 : 9366 pixels
Niveau 110 : 10491 pixels
Niveau 111 : 11112 pixels
Niveau 112 : 11210 pixels
Niveau 113 : 8783 pixels
Niveau 114 : 11637 pixels
Niveau 115 : 10423 pixels
Niveau 116 : 8726 pixels
Niveau 117 : 5622 pixels
Niveau 118 : 6910 pixels
Niveau 119 : 6806 pixels
Niveau 120 : 6747 pixels
Niveau 121 : 4964 pixels
Niveau 122 : 7509 pixels
Niveau 123 : 8437 pixels
Niveau 124 : 8998 pixels
Niveau 125 : 6813 pixels
Niveau 126 : 9060 pixels
Niveau 127 : 10670 pixels
Niveau 128 : 13139 pixels
Niveau 129 : 12318 pixels
Niveau 130 : 7845 pixels
Niveau 131 : 10447 pixels
Niveau 132 : 12625 pixels
Niveau 133 : 14355 pixels
Niveau 134 : 11617 pixels
Niveau 135 : 18699 pixels
Niveau 136 : 18307 pixels
Niveau 137 : 13508 pixels
Niveau 138 : 7190 pixels
Niveau 139 : 7018 pixels
Niveau 140 : 5854 pixels
Niveau 141 : 5321 pixels
Niveau 142 : 3479 pixels
Niveau 143 : 4457 pixels
Niveau 144 : 3991 pixels
Niveau 145 : 3744 pixels
Niveau 146 : 3790 pixels
Niveau 147 : 2849 pixels
Niveau 148 : 3875 pixels
Niveau 149 : 4075 pixels
Niveau 150 : 4028 pixels
Niveau 151 : 2823 pixels
Niveau 152 : 4229 pixels
Niveau 153 : 5075 pixels
Niveau 154 : 5753 pixels
Niveau 155 : 3550 pixels
Niveau 156 : 2225 pixels
Niveau 157 : 577 pixels
Niveau 158 : 216 pixels
Niveau 159 : 147 pixels
Niveau 160 : 776 pixels
Niveau 161 : 3892 pixels
Appliquer l'extension de la dynamique par LUT...
Histogramme de la bande 0 :
Niveau 0 : 4790 pixels
Niveau 7 : 1 pixels
Niveau 11 : 14 pixels
Niveau 15 : 791 pixels
Niveau 19 : 8825 pixels
Niveau 22 : 14917 pixels
Niveau 26 : 20616 pixels
Niveau 30 : 16623 pixels
Niveau 34 : 14411 pixels
Niveau 38 : 9612 pixels
Niveau 41 : 10911 pixels
Niveau 45 : 9271 pixels
Niveau 49 : 8623 pixels
Niveau 53 : 6517 pixels
Niveau 57 : 9366 pixels
Niveau 60 : 10491 pixels
Niveau 64 : 11112 pixels
Niveau 68 : 11210 pixels
Niveau 72 : 8783 pixels
Niveau 76 : 11637 pixels
Niveau 79 : 10423 pixels
Niveau 83 : 8726 pixels
Niveau 87 : 5622 pixels
Niveau 91 : 6910 pixels
Niveau 95 : 6806 pixels
Niveau 98 : 6747 pixels
Niveau 102 : 4964 pixels
Niveau 106 : 7509 pixels
Niveau 110 : 8437 pixels
Niveau 114 : 8998 pixels
Niveau 117 : 6813 pixels
Niveau 121 : 9060 pixels
Niveau 125 : 10670 pixels
Niveau 129 : 13139 pixels
Niveau 133 : 12318 pixels
Niveau 137 : 7845 pixels
Niveau 140 : 10447 pixels
Niveau 144 : 12625 pixels
Niveau 148 : 14355 pixels
Niveau 152 : 11617 pixels
Niveau 156 : 18699 pixels
Niveau 159 : 18307 pixels
Niveau 163 : 13508 pixels
Niveau 167 : 7190 pixels
Niveau 171 : 7018 pixels
Niveau 175 : 5854 pixels
Niveau 178 : 5321 pixels
Niveau 182 : 3479 pixels
Niveau 186 : 4457 pixels
Niveau 190 : 3991 pixels
Niveau 194 : 3744 pixels
Niveau 197 : 3790 pixels
Niveau 201 : 2849 pixels
Niveau 205 : 3875 pixels
Niveau 209 : 4075 pixels
Niveau 213 : 4028 pixels
Niveau 216 : 2823 pixels
Niveau 220 : 4229 pixels
Niveau 224 : 5075 pixels
Niveau 228 : 5753 pixels
Niveau 232 : 3550 pixels
Niveau 235 : 2225 pixels
Niveau 239 : 577 pixels
Niveau 243 : 216 pixels
Niveau 247 : 147 pixels
Niveau 251 : 776 pixels
Niveau 255 : 3892 pixels
![image-1-low_dynamic-grayscale-1](https://github.com/user-attachments/assets/e1ad32c6-2d9a-435b-bab6-6e00efd14043)

- **Filtre moyenneur :**
  Avant: ![street](https://github.com/user-attachments/assets/6167e86e-2b21-4fea-b5af-d0c6157e8867)

  Après:
![street-meanfilter](https://github.com/user-attachments/assets/c8cbf94b-d1fb-4448-a226-bf3692a43e10)

- **Gradient :**
  Avant: ![street](https://github.com/user-attachments/assets/6167e86e-2b21-4fea-b5af-d0c6157e8867)

  Après:
![street-gradient](https://github.com/user-attachments/assets/1fdbe46f-5fe4-4635-9967-08a6ea862a62)
- **Gradient Prewitt:**
  Avant: ![street](https://github.com/user-attachments/assets/6167e86e-2b21-4fea-b5af-d0c6157e8867)

  Après:
![street-gradientPrewitt](https://github.com/user-attachments/assets/83c32c19-a977-45b5-84e4-b2e58c46dc63)
- **Gradient Sobel:**
  Avant: ![street](https://github.com/user-attachments/assets/6167e86e-2b21-4fea-b5af-d0c6157e8867)

  Après:
![street-gradientSobel](https://github.com/user-attachments/assets/59b1521b-811c-41c2-9bd1-ccca89fbdcde)

- **Luminosité Image Coloré :**
  Avant: ![birds](https://github.com/user-attachments/assets/7298e18f-8507-4b9c-bfdb-9a4b8ed3fdeb)

  Après:
![birds-brightness](https://github.com/user-attachments/assets/f2e7bc3b-7ec3-4e48-9612-e9aeadf641c1)

- **Conversion RGB/HSV :**
  Avant: ![maldives](https://github.com/user-attachments/assets/8ebe603c-8950-47f1-a16b-444a0ac3f9e3)

  Après:
![maldives-1](https://github.com/user-attachments/assets/14c062b2-57f8-4531-b7d2-1d1030f5b701)

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
