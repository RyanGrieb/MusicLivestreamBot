package org.team_m.mlb.system;

public class SystemInfo {

	public static String osType() {
		if (System.getProperty("os.name").contains("Windows")) {
			return "Windows";
		}
		return System.getProperty("os.name");
	}

	public static String javaVersion() {
		return System.getProperty("java.version");
	}

	public static String javafxVersion() {
		return System.getProperty("javafx.version");
	}

	public static int threadCount() {
		// TODO: Get proper thread count
		return 4;
	}

}