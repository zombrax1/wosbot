package cl.camodev.utiles;

public interface CssPaths {
	String GLOBAL_CSS = "/styles/style.css";

	static String getCssPath() {
		return CssPaths.class.getResource(GLOBAL_CSS).toExternalForm();
	}
}