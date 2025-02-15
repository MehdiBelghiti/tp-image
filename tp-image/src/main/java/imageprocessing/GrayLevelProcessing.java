package imageprocessing;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GrayLevelProcessing {

	/**
	 * Applique un seuillage à chaque canal de l'image couleur. Pour chaque bande,
	 * les pixels dont la valeur est inférieure à t sont mis à 0, et les autres à
	 * 255.
	 *
	 * @param input L'image couleur en Planar<GrayU8>
	 * @param t     Le seuil
	 */
	public static void threshold(Planar<GrayU8> input, int t) {
		int numBands = input.getNumBands();
		for (int band = 0; band < numBands; band++) {
			GrayU8 image = input.getBand(band);
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int gl = image.get(x, y);
					gl = (gl < t) ? 0 : 255;
					image.set(x, y, gl);
				}
			}
		}
	}

	/**
	 * Ajuste la luminosité de chaque canal de l'image couleur en ajoutant delta à
	 * chaque pixel. Le résultat est clampé dans l'intervalle [0, 255].
	 *
	 * @param input L'image couleur en Planar<GrayU8>
	 * @param delta La valeur à ajouter (peut être négative)
	 */
	public static void adjustBrightness(Planar<GrayU8> input, int delta) {
		int numBands = input.getNumBands();
		for (int band = 0; band < numBands; band++) {
			GrayU8 image = input.getBand(band);
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int value = image.get(x, y);
					int newValue = value + delta;
					newValue = Math.max(0, Math.min(255, newValue));
					image.set(x, y, newValue);
				}
			}
		}
	}

	/**
	 * Étend la dynamique de chaque canal de l'image couleur en rééchelonnant
	 * linéairement les valeurs pour que le niveau minimal devienne 0 et le niveau
	 * maximal 255. Si une bande est uniforme (min == max), elle n'est pas modifiée.
	 *
	 * @param input L'image couleur en Planar<GrayU8>
	 */
	public static void extendDynamicRange(Planar<GrayU8> input) {
		int numBands = input.getNumBands();
		for (int band = 0; band < numBands; band++) {
			GrayU8 image = input.getBand(band);
			int min = 255, max = 0;
			// Recherche des valeurs min et max dans la bande
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int v = image.get(x, y);
					if (v < min)
						min = v;
					if (v > max)
						max = v;
				}
			}
			if (max == min) {
				System.out.println("La bande " + band + " est uniforme (min == max). Aucune extension.");
				continue;
			}
			// Rééchelonnement linéaire
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int v = image.get(x, y);
					int newVal = (v - min) * 255 / (max - min);
					image.set(x, y, newVal);
				}
			}
		}
	}

	/**
	 * Étend la dynamique de chaque canal en utilisant une lookup table (LUT).
	 *
	 * @param input L'image couleur en Planar<GrayU8>
	 */
	public static void extendDynamicRangeLUT(Planar<GrayU8> input) {
		int numBands = input.getNumBands();
		for (int band = 0; band < numBands; band++) {
			GrayU8 image = input.getBand(band);
			int min = 255, max = 0;
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int v = image.get(x, y);
					if (v < min)
						min = v;
					if (v > max)
						max = v;
				}
			}
			if (max == min) {
				System.out.println("La bande " + band + " est uniforme. Aucune extension.");
				continue;
			}
			int[] lut = new int[256];
			for (int i = 0; i < 256; i++) {
				int newVal = (i - min) * 255 / (max - min);
				lut[i] = Math.max(0, Math.min(255, newVal));
			}
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int v = image.get(x, y);
					image.set(x, y, lut[v]);
				}
			}
		}
	}

	/**
	 * Calcule l'histogramme d'une bande (canal) d'une image.
	 *
	 * @param band Une image GrayU8 représentant un canal
	 * @return Un tableau de 256 entiers où chaque indice correspond au nombre de
	 *         pixels pour ce niveau
	 */
	public static int[] computeHistogram(GrayU8 band) {
		int[] histogram = new int[256];
		for (int y = 0; y < band.height; y++) {
			for (int x = 0; x < band.width; x++) {
				histogram[band.get(x, y)]++;
			}
		}
		return histogram;
	}

	/**
	 * Affiche l'histogramme d'une bande.
	 *
	 * @param histogram Le tableau de l'histogramme
	 * @param bandIndex L'indice de la bande (pour l'affichage)
	 */
	public static void printHistogram(int[] histogram, int bandIndex) {
		System.out.println("Histogramme de la bande " + bandIndex + " :");
		for (int i = 0; i < histogram.length; i++) {
			if (histogram[i] > 0) {
				System.out.println("Niveau " + i + " : " + histogram[i] + " pixels");
			}
		}
	}

	/**
	 * Applique l'égalisation d'histogramme à chaque canal de l'image couleur.
	 *
	 * @param input L'image couleur en Planar<GrayU8>
	 */
	public static void histogramEqualization(Planar<GrayU8> input) {
		int numBands = input.getNumBands();
		for (int band = 0; band < numBands; band++) {
			GrayU8 image = input.getBand(band);
			int[] histogram = computeHistogram(image);
			int[] cumHist = new int[256];
			cumHist[0] = histogram[0];
			for (int i = 1; i < 256; i++) {
				cumHist[i] = cumHist[i - 1] + histogram[i];
			}
			int totalPixels = image.width * image.height;
			int cumMin = 0;
			for (int i = 0; i < 256; i++) {
				if (cumHist[i] != 0) {
					cumMin = cumHist[i];
					break;
				}
			}
			int[] lut = new int[256];
			for (int i = 0; i < 256; i++) {
				if (totalPixels - cumMin != 0) {
					double normalized = (double) (cumHist[i] - cumMin) / (totalPixels - cumMin);
					int newVal = (int) Math.round(normalized * 255);
					lut[i] = Math.max(0, Math.min(255, newVal));
				} else {
					lut[i] = 0;
				}
			}
			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					int oldVal = image.get(x, y);
					image.set(x, y, lut[oldVal]);
				}
			}
		}
	}

	/**
	 * Méthode principale de test. Charge une image couleur, la convertit en
	 * Planar<GrayU8>, applique plusieurs filtres, affiche les histogrammes (pour
	 * chaque bande) avant et après traitement, puis sauvegarde le résultat.
	 *
	 * Usage : java imageprocessing.GrayLevelProcessing <inputImage> <outputImage>
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java imageprocessing.GrayLevelProcessing <inputImage> <outputImage>");
			System.exit(-1);
		}
		String inputPath = args[0];
		String outputPath = args[1];

		// Chargement de l'image couleur sous forme de BufferedImage
		BufferedImage inputBuffered = UtilImageIO.loadImage(inputPath);
		if (inputBuffered == null) {
			System.err.println("Impossible de charger l'image : " + inputPath);
			System.exit(-1);
		}
		// Conversion en image Planar<GrayU8>
		Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(inputBuffered, null, true, GrayU8.class);

		// Griser
//        convertToGrayscale(image);

		// Affichage des histogrammes initiaux pour chaque bande
		for (int band = 0; band < image.getNumBands(); band++) {
			int[] hist = computeHistogram(image.getBand(band));
			printHistogram(hist, band);
		}

		// Exemple de traitement : extension de la dynamique via LUT puis égalisation
		// d'histogramme
		System.out.println("Appliquer l'extension de la dynamique par LUT...");
		extendDynamicRangeLUT(image);
		for (int band = 0; band < image.getNumBands(); band++) {
			int[] hist = computeHistogram(image.getBand(band));
			printHistogram(hist, band);
		}

		System.out.println("Appliquer l'égalisation d'histogramme...");
		histogramEqualization(image);
		for (int band = 0; band < image.getNumBands(); band++) {
			int[] hist = computeHistogram(image.getBand(band));
			printHistogram(hist, band);
		}

		BufferedImage outputBuffered = ConvertBufferedImage.convertTo(image, null, true);

		// HSV

//		rgbToHsvConversion(inputPath, outputPath);
		
		// COLORIZE
		float newHue = 270;
		colorize(inputBuffered, outputBuffered, newHue);
		System.out.println("Image colorisée sauvegardée dans : " + outputPath);
        // Conversion de l'image traitée en BufferedImage pour sauvegarde
//        

		int[] histogram = computeHueHistogram(inputBuffered);
        BufferedImage histImage = createHistogramImage(histogram);
		UtilImageIO.saveImage(outputBuffered, outputPath);

		System.out.println("Image sauvegardée dans : " + outputPath);
	}

	/**
	 * Convertit une image couleur (Planar<GrayU8>) en image en niveaux de gris
	 * (GrayU8) en utilisant la formule : 0.3*R + 0.59*G + 0.11*B.
	 *
	 * @param colorImage L'image couleur en format Planar<GrayU8>
	 * @return L'image en niveaux de gris (GrayU8)
	 */
	public static GrayU8 convertToGrayscale(Planar<GrayU8> colorImage) {
		// Création de l'image de sortie de même taille
		GrayU8 gray = new GrayU8(colorImage.width, colorImage.height);

		// On suppose que l'image couleur possède 3 bandes : R, G, B.
		for (int y = 0; y < colorImage.height; y++) {
			for (int x = 0; x < colorImage.width; x++) {
				int r = colorImage.getBand(0).get(x, y);
				int g = colorImage.getBand(1).get(x, y);
				int b = colorImage.getBand(2).get(x, y);
				int grayValue = (int) (0.3 * r + 0.59 * g + 0.11 * b);
				gray.set(x, y, grayValue);
			}
		}

		return gray;
	}

// ------------------ Méthode pour conversion RGB/HSV et histogrammes sur image couleur ------------------

	/**
	 * Effectue la conversion RGB → HSV sur chaque pixel d'une image couleur, met à
	 * jour les histogrammes pour Hue, Saturation et Value, puis réalise le
	 * round-trip HSV → RGB pour vérifier la conversion. La méthode utilise la
	 * représentation Planar (chaque canal est stocké séparément).
	 * 
	 * Pour BoofCV, la conversion RGB→HSV retourne :
	 * 
	 * H (Teinte) : une valeur en degrés, typiquement dans l'intervalle 0 , 360
	 * 0,360 S (Saturation) : une valeur comprise entre 0 et 1 V (Valeur) :
	 * également une valeur comprise entre 0 et 1 Pour un pixel gris, c'est-à-dire
	 * lorsque R = G = B, la couleur n'a pas de dominante (le gris est neutre) ;
	 * ainsi :
	 * 
	 * La Saturation est 0 (car il n'y a aucune couleur dominante). La Teinte (H)
	 * est indéfinie en théorie, mais par convention (et dans BoofCV) elle est
	 * souvent fixée à 0. La Valeur (V) est simplement le niveau de gris normalisé,
	 * c'est-à-dire R/255 (par exemple, un gris moyen avec R=G=B=128 donnera V ≈
	 * 0.5). En résumé, pour un pixel gris, la conversion donne généralement H = 0,
	 * S = 0, et V = (R/255).
	 *
	 * @param inputPath  Chemin de l'image couleur d'entrée.
	 * @param outputPath Chemin de sauvegarde de l'image traitée.
	 */
	public static void rgbToHsvConversion(String inputPath, String outputPath) {
		BufferedImage inputImage = UtilImageIO.loadImage(inputPath);
		if (inputImage == null) {
			System.err.println("Impossible de charger l'image : " + inputPath);
			System.exit(-1);
		}
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Histogrammes :
		// Hue sur [0, 360] (361 bins)
		// Saturation et Value sur [0,1] convertis en pourcentage (0 à 100)
		int[] histogramHue = new int[361];
		int[] histogramSat = new int[101];
		int[] histogramVal = new int[101];

		float[] hsv = new float[3];
		int[] rgbArray = new int[3];

		// Parcours de chaque pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = inputImage.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;

				// Conversion RGB → HSV (hue en degrés, sat et value dans [0,1])
				ColorHsv.rgbToHsv(r, g, b, hsv);

				int hueBin = (int) Math.floor(hsv[0]);
				hueBin = Math.max(0, Math.min(360, hueBin));
				histogramHue[hueBin]++;

				int satBin = (int) Math.floor(hsv[1] * 100);
				satBin = Math.max(0, Math.min(100, satBin));
				histogramSat[satBin]++;

				int valBin = (int) Math.floor(hsv[2] * 100);
				valBin = Math.max(0, Math.min(100, valBin));
				histogramVal[valBin]++;

				// Conversion HSV → RGB (round-trip)
				ColorHsv.hsvToRgb(hueBin, satBin, valBin, hsv);
				int newRgb = (rgbArray[0] << 16) | (rgbArray[1] << 8) | rgbArray[2];
				outputImage.setRGB(x, y, newRgb);
			}
		}

		// Affichage des histogrammes
		System.out.println("Histogramme Hue:");
		for (int i = 0; i < histogramHue.length; i++) {
			if (histogramHue[i] > 0) {
				System.out.println("Hue " + i + "° : " + histogramHue[i] + " pixels");
			}
		}
		System.out.println("\nHistogramme Saturation:");
		for (int i = 0; i < histogramSat.length; i++) {
			if (histogramSat[i] > 0) {
				System.out.println("Saturation " + i + "% : " + histogramSat[i] + " pixels");
			}
		}
		System.out.println("\nHistogramme Value:");
		for (int i = 0; i < histogramVal.length; i++) {
			if (histogramVal[i] > 0) {
				System.out.println("Value " + i + "% : " + histogramVal[i] + " pixels");
			}
		}

		UtilImageIO.saveImage(outputImage, outputPath);
		System.out.println("Image sauvegardée dans : " + outputPath);
	}

	/**
	 * Ce filtre remplace la teinte (H) de chaque pixel par la valeur newHue
	 * spécifiée, en conservant la saturation (S) et la valeur (V) inchangées.
	 * 
	 * Fixer la saturation à 0 pour chaque pixel revient à supprimer toute la «
	 * couleur » de l'image. En effet, dans l'espace HSV, la saturation contrôle
	 * l'intensité de la couleur. Lorsque la saturation est à 0, la teinte devient
	 * sans effet et l'image est représentée uniquement par la valeur (V),
	 * c'est-à-dire par des nuances de gris allant du noir au blanc.
	 * 
	 * Ainsi, l'effet visuel est celui d'une image en niveaux de gris. Cela revient
	 * en pratique à appliquer un filtre de grisage (bien que, selon la méthode de
	 * conversion, le résultat puisse légèrement différer du grisage obtenu par une
	 * pondération linéaire classique, par exemple 0.3R + 0.59G + 0.11B).
	 * 
	 * @param input  L'image d'entrée (RGB)
	 * @param output L'image de sortie (RGB)
	 * @param newHue La nouvelle teinte dans l'intervalle [0, 360] (par exemple,
	 *               270)
	 */
	public static void colorize(BufferedImage input, BufferedImage output, float newHue) {
		int width = input.getWidth();
		int height = input.getHeight();
		float[] hsv = new float[3];
		float[] rgbOut = new float[3];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Récupère le pixel RGB
				int pixel = input.getRGB(x, y);
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;

				// Conversion RGB → HSV
				ColorHsv.rgbToHsv(r, g, b, hsv);

				// Remplacer la teinte par la nouvelle valeur (newHue)
				hsv[0] = newHue;

				// Conversion HSV → RGB
				ColorHsv.hsvToRgb(hsv[0], hsv[1], hsv[2], rgbOut);

				// Conversion des valeurs float en int (arrondies)
				int newR = (int) Math.round(rgbOut[0]);
				int newG = (int) Math.round(rgbOut[1]);
				int newB = (int) Math.round(rgbOut[2]);
				// On s'assure que les valeurs restent dans [0, 255]
				newR = Math.max(0, Math.min(255, newR));
				newG = Math.max(0, Math.min(255, newG));
				newB = Math.max(0, Math.min(255, newB));

				int newPixel = (newR << 16) | (newG << 8) | newB;
				output.setRGB(x, y, newPixel);
			}
		}
	}
	
	/**
     * Calcule l'histogramme des teintes d'une image couleur.
     * Pour chaque pixel, la teinte (H) est calculée via la conversion RGB→HSV et
     * est quantifiée en entier dans l'intervalle [0, 360[.
     * 
     * @param input L'image couleur
     * @return Un tableau de 360 entiers contenant les comptes pour chaque teinte
     */
    public static int[] computeHueHistogram(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int[] histogram = new int[360]; // pour les teintes de 0 à 359
        float[] hsv = new float[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = input.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Conversion RGB → HSV
                ColorHsv.rgbToHsv(r, g, b, hsv);
                // hsv[0] est la teinte en degrés dans [0,360[.
                int hue = (int)Math.floor(hsv[0]);
                if (hue < 0) {
                    hue = 0;
                }
                if (hue >= 360) {
                    hue = 359;
                }
                histogram[hue]++;
            }
        }
        return histogram;
    }
    
    /**
     * Crée une image représentant l'histogramme des teintes.
     * Chaque colonne correspond à une valeur de teinte (0 à 359) et la hauteur de la barre
     * est proportionnelle au nombre de pixels pour cette teinte.
     * Les barres sont colorées avec la couleur correspondante (en utilisant HSB).
     *
     * @param histogram Le tableau des comptes par teinte
     * @return Une image (BufferedImage) du histogramme
     */
    public static BufferedImage createHistogramImage(int[] histogram) {
        int histWidth = 360;      // une colonne par teinte
        int histHeight = 300;     // hauteur fixe (peut être ajustée)
        BufferedImage histImage = new BufferedImage(histWidth, histHeight, BufferedImage.TYPE_INT_RGB);
        
        // Trouver le maximum pour l'échelle
        int maxCount = 0;
        for (int count : histogram) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        
        Graphics2D g2 = histImage.createGraphics();
        // Fond blanc
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, histWidth, histHeight);
        
        // Pour chaque teinte, dessiner une barre verticale
        for (int h = 0; h < 360; h++) {
            // Hauteur de la barre proportionnelle au compte (avec une marge en haut)
            double barHeight = ((double) histogram[h] / maxCount) * (histHeight - 20);
            // Couleur de la barre : conversion de la teinte en couleur.
            // Java utilise HSB où H est dans [0,1]
            float hueFraction = h / 360.0f;
            int rgb = Color.HSBtoRGB(hueFraction, 1.0f, 1.0f);
            g2.setColor(new Color(rgb));
            // La barre est dessinée à x = h, de la base (histHeight - 1) vers le haut.
            int x = h;
            int yBottom = histHeight - 1;
            int yTop = (int) (histHeight - barHeight);
            g2.drawLine(x, yBottom, x, yTop);
        }
        g2.dispose();
        return histImage;
    }
}
