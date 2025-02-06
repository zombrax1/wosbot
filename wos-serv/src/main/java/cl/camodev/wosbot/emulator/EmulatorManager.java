package cl.camodev.wosbot.emulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmulatorManager {

	private static final String MUMU_PATH = "\"C:\\Program Files\\Netease\\MuMuPlayerGlobal-12.0\\shell\\MuMuManager.exe\"";
	private static final String ADB_PATH = "C:\\Program Files\\Netease\\MuMuPlayerGlobal-12.0\\shell\\adb";
	private static final String WHITEOUT_PACKAGE = "com.gof.global";

	/**
	 * Obtiene el estado del emulador.
	 * 
	 * @return Estado del emulador como un String.
	 */
	public static boolean getPlayerState(String emulatorNumber) {
		String playerState = executeCommand(MUMU_PATH + " api -v " + emulatorNumber + " player_state");
		if (playerState.contains("state=start_finished")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Inicia el emulador si no está corriendo.
	 */
	public static void launchPlayer(String emulatorNumber) {
		executeCommand(MUMU_PATH + " api -v " + emulatorNumber + " launch_player");
	}

	public static boolean isWhiteoutSurvivalInstalled(String emulatorNumber) {
		String command = MUMU_PATH + " api -v " + emulatorNumber + " get_installed_apps";
		StringBuilder output = new StringBuilder();

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// Leer la salida del comando
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Analizar la salida y buscar el paquete "com.gof.global"
		return containsPackage(output.toString(), WHITEOUT_PACKAGE);
	}

	/**
	 * Verifica si un paquete específico está en la salida JSON.
	 * 
	 * @param output      Salida del comando.
	 * @param packageName Nombre del paquete a buscar.
	 * @return `true` si el paquete está presente, `false` en caso contrario.
	 */
	private static boolean containsPackage(String output, String packageName) {
		Pattern pattern = Pattern.compile("\"packageName\"\\s*:\\s*\"" + packageName + "\"");
		Matcher matcher = pattern.matcher(output);
		return matcher.find();
	}

	/**
	 * Ejecuta un comando en la terminal y devuelve la salida como un String.
	 * 
	 * @param command Comando a ejecutar.
	 * @return Salida del comando.
	 */
	private static String executeCommand(String command) {
		StringBuilder output = new StringBuilder();

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// Leer la salida del comando
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}

}
