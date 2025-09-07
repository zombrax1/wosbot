package cl.camodev.utiles;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import cl.camodev.wosbot.ot.DTOPoint;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class UtilOCR {

        private static final String DEFAULT_TESSDATA_PATH = System.getProperty("tesseract.datapath", "./lib/tesseract");
        private static final String DEFAULT_LANGUAGE = System.getProperty("tesseract.language", "eng");

        private static final int DEFAULT_OEM = Integer.getInteger("tesseract.oem", 1); // LSTM only
        private static final int DEFAULT_PSM = Integer.getInteger("tesseract.psm", 7); // single line

        private static final boolean DEFAULT_USE_DICT = Boolean.parseBoolean(System.getProperty("tesseract.use_dict", "false"));
        private static final String OVERRIDE_LOAD_SYSTEM_DAWG = System.getProperty("tesseract.load_system_dawg");
        private static final String OVERRIDE_LOAD_FREQ_DAWG = System.getProperty("tesseract.load_freq_dawg");
        private static final String CHAR_WHITELIST = System.getProperty("tesseract.whitelist", "");

        private static final boolean PREPROCESS_ENABLE = Boolean.parseBoolean(System.getProperty("tesseract.preprocess", "true"));
        private static final int BIN_THRESHOLD = Integer.getInteger("tesseract.threshold", 160);
        private static final double UPSCALE_FACTOR = Double.parseDouble(System.getProperty("tesseract.upscale", "1.0"));

        private static final int CACHE_MAX_ENTRIES = Integer.getInteger("tesseract.cache.maxEntries", 512);

        private static final ThreadLocal<Tesseract> OCR = ThreadLocal.withInitial(() -> {
                Tesseract t = new Tesseract();
                t.setDatapath(DEFAULT_TESSDATA_PATH);
                t.setLanguage(DEFAULT_LANGUAGE);
                t.setOcrEngineMode(DEFAULT_OEM);
                t.setPageSegMode(DEFAULT_PSM);

                // Dictionaries: off by default unless explicitly enabled
                applyTessVar(t, "load_system_dawg",
                                OVERRIDE_LOAD_SYSTEM_DAWG != null ? OVERRIDE_LOAD_SYSTEM_DAWG : (DEFAULT_USE_DICT ? "1" : "0"));
                applyTessVar(t, "load_freq_dawg",
                                OVERRIDE_LOAD_FREQ_DAWG != null ? OVERRIDE_LOAD_FREQ_DAWG : (DEFAULT_USE_DICT ? "1" : "0"));

                if (!CHAR_WHITELIST.isEmpty()) {
                        applyTessVar(t, "tessedit_char_whitelist", CHAR_WHITELIST);
                }
                return t;
        });

        private static final Map<String, CacheEntry> CACHE = new LruCache<>(CACHE_MAX_ENTRIES);

        /**
         * Performs OCR on a specific region of an image loaded from disk.
         */
        public static String ocrFromRegion(String imagePath, DTOPoint p1, DTOPoint p2)
                        throws IOException, TesseractException {
                File imageFile = new File(imagePath);
                BufferedImage image = ImageIO.read(imageFile);
                if (image == null) {
                        throw new IOException("Could not load image: " + imagePath);
                }

                int x = (int) Math.min(p1.getX(), p2.getX());
                int y = (int) Math.min(p1.getY(), p2.getY());
                int width = (int) Math.abs(p1.getX() - p2.getX());
                int height = (int) Math.abs(p1.getY() - p2.getY());

                if (x < 0 || y < 0 || width <= 0 || height <= 0 || x + width > image.getWidth()
                                || y + height > image.getHeight()) {
                        throw new IllegalArgumentException("Specified region is outside the image bounds.");
                }

                return doOcrWithCache(image, x, y, width, height, imagePath);
        }

        /**
         * Performs OCR on a sub-region of a provided image (no disk I/O).
         */
        public static String ocrFromRegion(BufferedImage image, int x, int y, int width, int height)
                        throws TesseractException {
                if (image == null) {
                        throw new IllegalArgumentException("Image is null");
                }
                if (x < 0 || y < 0 || width <= 0 || height <= 0 || x + width > image.getWidth()
                                || y + height > image.getHeight()) {
                        throw new IllegalArgumentException("Specified region is outside the image bounds.");
                }
                return doOcrWithCache(image, x, y, width, height, null);
        }

        private static String doOcrWithCache(BufferedImage image, int x, int y, int width, int height, String imageKey)
                        throws TesseractException {
                BufferedImage roi = image.getSubimage(x, y, width, height);

                // Compute a quick checksum of the raw ROI to skip unchanged regions
                long crc = crc32(grayForHash(roi));
                String key = buildKey(imageKey, x, y, width, height);

                CacheEntry entry = CACHE.get(key);
                if (entry != null && entry.crc == crc) {
                        return entry.text; // unchanged, return cached value
                }

                BufferedImage prepped = PREPROCESS_ENABLE ? preprocess(roi) : roi;

                if (UPSCALE_FACTOR > 1.0) {
                        prepped = upscale(prepped, UPSCALE_FACTOR);
                }

                String result = OCR.get().doOCR(prepped);

                CACHE.put(key, new CacheEntry(crc, result));
                return result;
        }

        private static String buildKey(String imageKey, int x, int y, int w, int h) {
                String base = imageKey != null ? imageKey : "<mem>";
                return base + ":" + x + "," + y + "," + w + "," + h;
        }

        private static BufferedImage preprocess(BufferedImage src) {
                // Grayscale
                BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                op.filter(src, gray);

                // Simple binary threshold
                byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
                for (int i = 0; i < data.length; i++) {
                        int v = data[i] & 0xFF;
                        data[i] = (byte) (v < BIN_THRESHOLD ? 0 : 0xFF);
                }
                return gray;
        }

        private static BufferedImage grayForHash(BufferedImage src) {
                BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D g = gray.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(src, 0, 0, null);
                g.dispose();
                return gray;
        }

        private static long crc32(BufferedImage gray) {
                CRC32 crc = new CRC32();
                byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
                crc.update(data, 0, data.length);
                return crc.getValue();
        }

        private static BufferedImage upscale(BufferedImage src, double factor) {
                int w = Math.max(1, (int) Math.round(src.getWidth() * factor));
                int h = Math.max(1, (int) Math.round(src.getHeight() * factor));
                if (w == src.getWidth() && h == src.getHeight()) return src;
                BufferedImage out = new BufferedImage(w, h, src.getType());
                Graphics2D g = out.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(src, 0, 0, w, h, null);
                g.dispose();
                return out;
        }

        private static final class CacheEntry {
                final long crc;
                final String text;

                CacheEntry(long crc, String text) {
                        this.crc = crc;
                        this.text = text;
                }
        }

        private static void applyTessVar(Tesseract t, String key, String value) {
                try {
                        java.lang.reflect.Method m = Tesseract.class.getMethod("setTessVariable", String.class, String.class);
                        m.invoke(t, key, value);
                } catch (Exception ignore) {
                        // Method not available in this Tess4J version; skip variable.
                }
        }

        private static final class LruCache<K, V> extends LinkedHashMap<K, V> {
                private final int maxEntries;

                LruCache(int maxEntries) {
                        super(16, 0.75f, true);
                        this.maxEntries = Math.max(16, maxEntries);
                }

                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                        return size() > maxEntries;
                }
        }
}
