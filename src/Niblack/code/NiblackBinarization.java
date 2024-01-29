package Niblack.code;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class NiblackBinarization {

	public static void main(String[] args) {
		// Charger l'image en niveaux de gris (assurez-vous que l'image est en
		// niveaux de gris)
		BufferedImage inputImage = loadImage("images/input.jpg");

		// Paramètres de l'algorithme
		int windowSize = 15; // Taille de la fenêtre
		double kValue = -0.2; // Facteur K (ajustez selon vos besoins)

		// Appliquer l'algorithme de Niblack
		BufferedImage outputImage = applyNiblack(inputImage, windowSize, kValue);

		// Sauvegarder l'image binarisée
		saveImage(outputImage, "images/output.jpg");
	}

	private static BufferedImage applyNiblack(BufferedImage inputImage,
			int windowSize, double kValue) {
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage outputImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Extraire la sous-image centrée sur le pixel (x, y)
				int startX = Math.max(0, x - windowSize / 2);
				int startY = Math.max(0, y - windowSize / 2);
				int endX = Math.min(width - 1, x + windowSize / 2);
				int endY = Math.min(height - 1, y + windowSize / 2);

				// Calculer la moyenne et l'écart-type local
				double mean = calculateMean(inputImage, startX, startY, endX,
						endY);
				double stdDev = calculateStdDev(inputImage, mean, startX,
						startY, endX, endY);

				// Calculer le seuil local
				int threshold = (int) (mean + kValue * stdDev);

				// Binariser l'image en fonction du seuil local
				int pixelValue = getGrayValue(inputImage.getRGB(x, y));
				int binaryValue = (pixelValue < threshold) ? 0 : 255;

				// Affecter la valeur au pixel dans l'image de sortie
				int binaryColor = (binaryValue << 16) | (binaryValue << 8)
						| binaryValue;
				outputImage.setRGB(x, y, binaryColor);
			}
		}

		return outputImage;
	}

	private static double calculateMean(BufferedImage image, int startX,
			int startY, int endX, int endY) {
		int sum = 0;
		int count = 0;

		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				sum += getGrayValue(image.getRGB(x, y));
				count++;
			}
		}

		return (double) sum / count;
	}

	private static double calculateStdDev(BufferedImage image, double mean,
			int startX, int startY, int endX, int endY) {
		double sumSquaredDiff = 0;
		int count = 0;

		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				int pixelValue = getGrayValue(image.getRGB(x, y));
				double diff = pixelValue - mean;
				sumSquaredDiff += diff * diff;
				count++;
			}
		}

		return Math.sqrt(sumSquaredDiff / count);
	}

	private static int getGrayValue(int rgb) {
		// Extraire la composante de luminosité (niveaux de gris) d'un pixel RGB
		return (rgb >> 16) & 0xFF;
	}

	private static BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void saveImage(BufferedImage image, String filePath) {
		try {
			ImageIO.write(image, "jpg", new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
