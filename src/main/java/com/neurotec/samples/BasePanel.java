package com.neurotec.samples;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.neurotec.util.concurrent.AggregateExecutionException;

public abstract class BasePanel extends JPanel {

	// ===========================================================
	// Private fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Protected fields
	// ===========================================================

	protected LicensingPanel licensingPanel;
	protected List<String> licenses;
	protected boolean obtained;

	// ===========================================================
	// Public methods
	// ===========================================================

	public void init() {
		initGUI();
		setDefaultValues();
		updateControls();
	}

	public final List<String> getLicenses() {
		return licenses;
	}

	public final LicensingPanel getLicensingPanel() {
		return licensingPanel;
	}

	public final void updateLicensing(boolean status) {
		licensingPanel.setComponentObtainingStatus(status);
		obtained = status;
	}

	public boolean isObtained() {
		return obtained;
	}

	public void showErrorDialog(Throwable e) {
		if (e instanceof AggregateExecutionException) {
			StringBuilder sb = new StringBuilder(64);
			sb.append("Execution resulted in one or more errors:\n");
			for (Throwable cause : ((AggregateExecutionException) e).getCauses()) {
				sb.append(cause.toString()).append('\n');
			}
			JOptionPane.showMessageDialog(this, sb.toString(), "Execution failed", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ===========================================================
	// Abstract methods
	// ===========================================================

	protected abstract void initGUI();
	protected abstract void setDefaultValues();
	protected abstract void updateControls();
	protected abstract void updateVoicesTools();

}
