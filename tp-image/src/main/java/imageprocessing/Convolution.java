package imageprocessing;

import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class Convolution {

	/**
     * Applique un filtre moyenneur (flou) à une image couleur représentée en Planar<GrayU8>.
     * Chaque canal est traité indépendamment. Le filtre moyenneur calcule la moyenne
     * des pixels dans un voisinage de taille size x size. Les bords ne sont pas traités.
     *
     * @param input  L'image couleur d'entrée (Planar<GrayU8>)
     * @param output L'image couleur de sortie (Planar<GrayU8>)
     * @param size   La taille du filtre (doit être impair)
     */
    public static void meanFilter(Planar<GrayU8> input, Planar<GrayU8> output, int size) {
        int numBands = input.getNumBands();
        int margin = size / 2;
        
        // Pour chaque canal de l'image
        for (int band = 0; band < numBands; band++) {
            GrayU8 inBand = input.getBand(band);
            GrayU8 outBand = output.getBand(band);
            
            // Parcours des pixels pour lesquels le voisinage complet est disponible
            for (int y = margin; y < inBand.height - margin; y++) {
                for (int x = margin; x < inBand.width - margin; x++) {
                    int sum = 0;
                    // Parcours du voisinage de taille size x size
                    for (int dy = -margin; dy <= margin; dy++) {
                        for (int dx = -margin; dx <= margin; dx++) {
                            sum += inBand.get(x + dx, y + dy);
                        }
                    }
                    int mean = sum / (size * size);
                    outBand.set(x, y, mean);
                }
            }
        }
    }

  public static void convolution(GrayU8 input, GrayS16 output, int[][] kernel) {
	  int kernelSize = kernel.length;
      int margin = kernelSize / 2;

      // Parcours des pixels pour lesquels le voisinage complet est disponible
      for (int y = margin; y < input.height - margin; y++) {
          for (int x = margin; x < input.width - margin; x++) {
              int sum = 0;
              // Parcours du voisinage défini par le noyau
              for (int ky = -margin; ky <= margin; ky++) {
                  for (int kx = -margin; kx <= margin; kx++) {
                      int pixel = input.get(x + kx, y + ky);
                      int weight = kernel[ky + margin][kx + margin];
                      sum += pixel * weight;
                  }
              }
              output.set(x, y, sum);
          }
      }
      
      // Les bords de l'image ne sont pas traités et restent inchangés dans 'output'
  }

  /**
   * Calcule l'image du gradient à partir de deux noyaux de convolution, qui peuvent être non carrés.
   * Pour chaque pixel (hors bordure), on applique le noyau horizontal et le noyau vertical séparément,
   * puis on combine les résultats (par la norme Euclidienne) pour obtenir la magnitude du gradient.
   * La zone de traitement est définie par les marges maximales nécessaires pour chaque noyau.
   *
   * @param input   L'image d'entrée en niveaux de gris
   * @param output  L'image de sortie dans laquelle sera stockée la magnitude du gradient (valeurs [0,255])
   * @param kernelX Le noyau pour le gradient horizontal (dimension : m×n)
   * @param kernelY Le noyau pour le gradient vertical (dimension : p×q)
   */
  public static void gradientImage(GrayU8 input, GrayU8 output, int[][] kernelX, int[][] kernelY) {
      // Dimensions du noyau horizontal
      int kxRows = kernelX.length;
      int kxCols = kernelX[0].length;
      // Dimensions du noyau vertical
      int kyRows = kernelY.length;
      int kyCols = kernelY[0].length;
      
      // Calcul des marges nécessaires pour chaque noyau (on suppose des dimensions impaires)
      int marginXForKernelX = kxCols / 2;
      int marginYForKernelX = kxRows / 2;
      int marginXForKernelY = kyCols / 2;
      int marginYForKernelY = kyRows / 2;
      
      // On prendra la marge maximale pour être sûr que les deux noyaux peuvent être appliqués
      int marginX = Math.max(marginXForKernelX, marginXForKernelY);
      int marginY = Math.max(marginYForKernelX, marginYForKernelY);
      
      // Parcours de l'image hors bordure
      for (int y = marginY; y < input.height - marginY; y++) {
          for (int x = marginX; x < input.width - marginX; x++) {
              int sumX = 0;
              // Convolution avec le noyau horizontal
              for (int i = 0; i < kxRows; i++) {
                  for (int j = 0; j < kxCols; j++) {
                      int xi = x + j - marginXForKernelX;
                      int yi = y + i - marginYForKernelX;
                      sumX += input.get(xi, yi) * kernelX[i][j];
                  }
              }
              int sumY = 0;
              // Convolution avec le noyau vertical
              for (int i = 0; i < kyRows; i++) {
                  for (int j = 0; j < kyCols; j++) {
                      int xi = x + j - marginXForKernelY;
                      int yi = y + i - marginYForKernelY;
                      sumY += input.get(xi, yi) * kernelY[i][j];
                  }
              }
              // Calcul de la magnitude du gradient
              int magnitude = (int) Math.sqrt(sumX * sumX + sumY * sumY);
              if (magnitude > 255) {
                  magnitude = 255;
              }
              output.set(x, y, magnitude);
          }
      }
  }

  public static void gradientImageSobel(GrayU8 input, GrayU8 output){
    int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    gradientImage(input, output, kernelX, kernelY);
  }

  public static void gradientImagePrewitt(GrayU8 input, GrayU8 output){
	  int[][] kernelX = {
        { -1, 0, 1 },
        { -2, 0, 2 },
        { -1, 0, 1 }
    };
    int[][] kernelY = {
        { -1, -2, -1 },
        {  0,  0,  0 },
        {  1,  2,  1 }
    };
    gradientImage(input, output, kernelX, kernelY);
  }

  public static void main(final String[] args) {
    // Chargement de l'image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();

    // Traitement: application d'un filtre moyenneur avec un noyau de taille 11x11
    //    meanFilter(input, output, 11);
    
 // Création d'une image de sortie pour la convolution.
    // Le format GrayS16 permet de contenir des valeurs négatives et des amplitudes supérieures à 255.
//    GrayS16 outputTemp = new GrayS16(input.width, input.height);
//
//    // Exemple de noyau (par exemple, un filtre de détection de contours simple)
//    int[][] kernel = {
//      { -1, -1, -1 },
//      { -1,  8, -1 },
//      { -1, -1, -1 }
//    };

    // Traitement
//    convolution(input, outputTemp, kernel);
    
    // Sauvegarde de l'image de sortie (conversion possible vers GrayU8 via ConvertImage si besoin)
    // Ici, pour visualiser directement le résultat, on peut par exemple convertir en GrayU8.
//    ConvertImage.convert(outputTemp, output);  // Nécessite la classe ConvertImage de BoofCV


    // Application de l'opérateur de Sobel
//    gradientImageSobel(input, output);
    // Application de l'opérateur de Prewitt
//    gradientImagePrewitt(input, output);
    
 // Noyau pour le gradient horizontal : 1x3
    int[][] kernelX = {
        { -1, 0, 1 }
    };
    // Noyau pour le gradient vertical : 3x1
    int[][] kernelY = {
        { -1 },
        {  0 },
        {  1 }
    };
    
    // Calcul du gradient avec les noyaux fournis
    gradientImage(input, output, kernelX, kernelY);
    
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    
    
//    System.out.println("Image saved in: " + outputPath);
//    System.out.println("Image Sobel sauvegardée dans : " + outputPath);
    System.out.println("Image Prewitt sauvegardée dans : " + outputPath);
    // Sauvegarde de l'image de sortie
    
    
  }
}
