package cl.camodev.utiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilCV {
        private static final Logger logger = LoggerFactory.getLogger(UtilCV.class);
        private static final String OPENCV_DIR = "lib/opencv";
        private static final int BUFFER_SIZE = 4096;

        public static void loadNativeLibrary(String resourcePath) throws IOException {
                // Obtener el nombre del archivo a partir de la ruta del recurso
                String[] parts = resourcePath.split("/");
                String libFileName = parts[parts.length - 1];

                // Crear el directorio lib/opencv si no existe
                File libDir = new File(OPENCV_DIR);
                if (!libDir.exists()) {
                        libDir.mkdirs();
                }

                // Crear el archivo destino en lib/opencv
                File destLib = new File(libDir, libFileName);

                // Abrir el recurso como stream
                try (InputStream in = UtilCV.class.getResourceAsStream(resourcePath);
                                OutputStream out = new FileOutputStream(destLib)) {
                        if (in == null) {
                                logger.error("Resource not found: {}", resourcePath);
                                throw new IOException("Resource not found: " + resourcePath);
                        }
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                        }
                } catch (IOException e) {
                        logger.error("Error extracting native library: {}", e.getMessage());
                        throw e;
                }

                // Cargar la librería usando la ruta absoluta del archivo destino
                System.load(destLib.getAbsolutePath());
                logger.info("Native library loaded from: {}", destLib.getAbsolutePath());
        }

}
