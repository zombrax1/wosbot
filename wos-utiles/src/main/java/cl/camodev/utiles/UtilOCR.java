package cl.camodev.utiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cl.camodev.wosbot.ot.DTOPoint;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class UtilOCR {

	/**
	 * Executes OCR on a specific region of an image.
	 *
	 * @param imagePath Path to the image in the file system.
	 * @param p1        First point that defines the region (can be, for example, the top-left corner).
	 * @param p2        Second point that defines the region (for example, the bottom-right corner).
	 * @return The recognized text in the region.
	 * @throws IOException              If an error occurs while loading the image.
	 * @throws TesseractException       If an error occurs during the OCR process.
	 * @throws IllegalArgumentException If the specified region exceeds the image limits.
	 */
	public static String ocrFromRegion(String imagePath, DTOPoint p1, DTOPoint p2) throws IOException, TesseractException {
		// Cargar la imagen desde el path proporcionado
		File imageFile = new File(imagePath);
		BufferedImage image = ImageIO.read(imageFile);
		if (image == null) {
			throw new IOException("No se pudo cargar la imagen: " + imagePath);
		}

		// Calcular la región a extraer:
		// Se determina el punto superior izquierdo (x, y) y se calcula el ancho y alto
		int x = (int) Math.min(p1.getX(), p2.getX());
		int y = (int) Math.min(p1.getY(), p2.getY());
		int width = (int) Math.abs(p1.getX() - p2.getX());
		int height = (int) Math.abs(p1.getY() - p2.getY());

		// Validar que la región no se salga de los límites de la imagen
		if (x + width > image.getWidth() || y + height > image.getHeight()) {
			throw new IllegalArgumentException("La región especificada se sale de los límites de la imagen.");
		}

		// Extraer la subimagen (la región de interés)
		BufferedImage subImage = image.getSubimage(x, y, width, height);

		// Configurar Tesseract
		Tesseract tesseract = new Tesseract();
		// Establece la ruta a la carpeta tessdata (asegúrate de que la ruta sea correcta)
		tesseract.setDatapath("tessdata");
		// Establecer el idioma; por ejemplo, "spa" para español
		tesseract.setLanguage("eng");

		// Ejecutar OCR sobre la subimagen y devolver el resultado
		return tesseract.doOCR(subImage);
	}

}
