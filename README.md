Projet de Traitement d'Images avec BoofCV
Ce projet présente plusieurs filtres et transformations d'images réalisés avec la bibliothèque BoofCV. L’objectif était d’implémenter différentes opérations sur des images en niveaux de gris et en couleur, telles que :

Seuillage
Ajustement de la luminosité
Extension de la dynamique (avec et sans lookup table)
Égalisation d'histogramme
Filtre moyenneur (floutage)
Détection de contours (gradient avec Sobel et Prewitt)
Conversion d’images couleur en niveaux de gris (griser)
Conversion RGB ⇔ HSV et visualisation des histogrammes
Modification sélective de la teinte (colorisation)
Visualisation d’un histogramme 2D teinte/saturation
Étapes Réalisées
Chargement et Affichage d'Images
Nous avons utilisé la classe UtilImageIO pour charger et sauvegarder des images, et la classe ConvertBufferedImage pour convertir entre BufferedImage et les formats de BoofCV (GrayU8 pour les images en niveaux de gris et Planar<GrayU8> pour les images couleur).

Opérations sur Images en Niveaux de Gris

Seuillage : Transformation simple qui met à 0 ou 255 selon un seuil.
Ajustement de luminosité : Ajout d'un delta à chaque pixel avec un clamp dans l'intervalle [0, 255].
Extension de dynamique : Rééchelonnement linéaire des niveaux de gris afin que la valeur minimale devienne 0 et la valeur maximale 255.
Deux versions ont été réalisées :
Directe (calcul pixel par pixel)
Avec lookup table (LUT)
Égalisation d'histogramme : Calcul de l'histogramme cumulé pour créer une LUT qui redistribue les niveaux de gris.
Filtrage Spatial et Détection de Contours

Filtre moyenneur : Adaptation du filtre pour flouter l'image en appliquant la moyenne sur un voisinage (taille impaire) pour chaque pixel.
Détection de contours (Gradient) : Implémentation des filtres de Sobel et Prewitt en calculant la magnitude du gradient à partir de deux convolutions sur l'image.
Traitement des Images Couleur

Conversion en niveaux de gris : Utilisation d’une formule pondérée (0.3R + 0.59G + 0.11B) pour griser une image couleur.
Conversion RGB/HSV : Utilisation des méthodes rgbToHsv et hsvToRgb de la classe ColorHsv pour passer de l'espace RGB à l'espace HSV et inversement.
Cela a permis de créer :
Un filtre de colorisation qui modifie la teinte de tous les pixels (tout en conservant la saturation et la luminosité).
La visualisation des histogrammes des composantes HSV.
La visualisation d’un histogramme 2D teinte/saturation, en quantifiant la teinte en [0,360[ et la saturation en pourcentage ([0,100]).
Difficultés Rencontrées
Conversion entre Types et Formats
La manipulation des formats d’images (GrayU8 pour le niveau de gris et Planar<GrayU8> pour la couleur) a nécessité une bonne compréhension des méthodes de conversion fournies par BoofCV. Par exemple, la conversion de BufferedImage en format Planar a été essentielle pour traiter chaque canal de couleur séparément.

Manipulation de l'Espace Couleur HSV
Les méthodes ColorHsv.rgbToHsv et ColorHsv.hsvToRgb de BoofCV attendent des tableaux de type float[]. Un problème fréquent a été la tentative de passer un tableau int[] en sortie lors de la conversion, ce qui a généré des erreurs de type. La solution a été de déclarer et utiliser des tableaux float[] pour récupérer les valeurs HSV, puis de convertir les valeurs en int si nécessaire.

Normalisation et Quantification
Pour la visualisation des histogrammes, il a fallu normaliser les valeurs issues des conversions (par exemple, la saturation obtenue en [0, 1] doit être multipliée par 100 et arrondie pour obtenir un entier entre 0 et 100). De même, la teinte, qui est en degrés, devait être arrondie et limitée à l’intervalle [0, 359].

Gestion des Bords dans les Filtres
Lors de l’application de filtres spatiaux (comme le filtre moyenneur), la gestion des bords de l’image a nécessité d’ignorer les pixels pour lesquels le voisinage complet n’était pas disponible, ce qui a été une décision de conception pour simplifier l’implémentation.

Conclusion
Ce projet a permis de découvrir et d’appliquer de nombreuses techniques de traitement d’image à l’aide de BoofCV, depuis des transformations simples sur des images en niveaux de gris jusqu’à des opérations plus complexes sur des images couleur et des conversions d’espaces de couleurs. Les difficultés principales ont été liées à la manipulation des types de données et à la compréhension des différents espaces de couleurs, mais ces obstacles ont été surmontés grâce à une lecture attentive de la documentation de BoofCV et à des tests itératifs.
