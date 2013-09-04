package edu.usc.pgroup.floe.applications.iipipeline.pellets.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class IIPProperties {
	private static String propertiesFile = "config/IIPConfig.properties";

	private static Properties properties = null;

	private static void loadProperties() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Unable to load from config file");
			} catch (IOException e) {
				throw new RuntimeException("Unable to load from config file");
			}
		}
	}

	public static String getLogLocation() {
		loadProperties();
		return properties.getProperty("LOG_LOCATION");
	}
	public static String getStoreURL() {
		loadProperties();
		return properties.getProperty("STORE_URL");
	}
}
