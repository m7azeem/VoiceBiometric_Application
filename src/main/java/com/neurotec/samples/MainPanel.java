package com.neurotec.samples;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class MainPanel implements ChangeListener {

	// ===========================================================
	// Static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private fields
	// ===========================================================

	private JTabbedPane tabbedPane;
	private EnrollFromMicrophone enrollFromMicrophone;
	private IdentifyVoice identifyVoice;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public MainPanel() {
		enrollFromMicrophone = new EnrollFromMicrophone();
		enrollFromMicrophone.init();
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public void obtainLicenses(BasePanel panel) throws IOException {
		if (!panel.isObtained()) {
			boolean status = VoicesTools.getInstance().obtainLicenses(panel.getLicenses());
			panel.updateLicensing(status);
		}
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	@Override
	public void stateChanged(ChangeEvent evt) {
		if (evt.getSource() == tabbedPane) {
			if ((enrollFromMicrophone != null) && (tabbedPane.getSelectedIndex() != 1)) {
				enrollFromMicrophone.cancelCapturing();
			}
			try {
				switch (tabbedPane.getSelectedIndex()) {
				case 0: {
					obtainLicenses(enrollFromMicrophone);
					enrollFromMicrophone.updateMicrophoneList();
					enrollFromMicrophone.updateVoicesTools();
					break;
				}
				case 2: {
					obtainLicenses(identifyVoice);
					identifyVoice.updateVoicesTools();
					break;
				}
				default: {
					throw new IndexOutOfBoundsException("unreachable");
				}
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "Could not obtain licenses for components: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
