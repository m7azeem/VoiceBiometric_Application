package com.neurotec.samples;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.licensing.NLicense;

public final class VoicesTools {

	// ===========================================================
	// Static fields
	// ===========================================================

	private static VoicesTools instance;

	private static final String ADDRESS = "/local";
	private static final String PORT = "5000";

	// ===========================================================
	// Static methods
	// ===========================================================

	public static VoicesTools getInstance() {
		synchronized (VoicesTools.class) {
			if (instance == null) {
				instance = new VoicesTools();
			}
			return instance;
		}
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	private final Map<String, Boolean> licenses;
	private final NBiometricClient client;
	private final NBiometricClient defaultClient;

	// ===========================================================
	// Private constructor
	// ===========================================================

	private VoicesTools() {
		licenses = new HashMap<String, Boolean>();
		client = new NBiometricClient();
		defaultClient = new NBiometricClient();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private boolean isLicenseObtained(String license) {
		if (licenses.containsKey(license)) {
			return licenses.get(license);
		} else {
			return false;
		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public synchronized boolean obtainLicenses(List<String> names) throws IOException {
		boolean result = true;
		for (String license : names) {
			if (isLicenseObtained(license)) {
				System.out.println(license + ": " + " already obtained");
			} else {
				boolean state = NLicense.obtainComponents(ADDRESS, PORT, license);
				licenses.put(license, state);
				if (state) {
					System.out.println(license + ": obtainted");
				} else {
					result = false;
					System.out.println(license + ": not obtained");
				}
			}
		}
		return result;
	}

	public NBiometricClient getClient() {
		return client;
	}

	public NBiometricClient getDefaultClient() {
		return defaultClient;
	}

}
