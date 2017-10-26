package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class LicensingPanel extends JPanel {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	private static final String REQUIRED_COMPONENT_LICENSES_LABEL_TEXT = "Required component licenses: ";
	private static final String COMPONENTS_OBTAINED_STATUS_TEXT = "Component licenses successfuly obtained";
	private static final String COMPONENTS_NOT_OBTAINED_STATUS_TEXT = "Component licenses not obtained";

	private static final Color COMPONENTS_OBTAINED_STATUS_TEXT_COLOR = Color.green.darker();
	private static final Color COMPONENTS_NOT_OBTAINED_STATUS_TEXT_COLOR = Color.red.darker();

	private static final int BORDER_WIDTH_TOP = 5;
	private static final int BORDER_WIDTH_LEFT = 5;
	private static final int BORDER_WIDTH_BOTTOM = 5;
	private static final int BORDER_WIDTH_RIGHT = 5;

	// ===========================================================
	// Private fields
	// ===========================================================

	private JLabel requiredComponentLicensesLabel;
	private JLabel requiredComponentLicensesList;
	private JLabel status;

	// ===========================================================
	// Constructor
	// ===========================================================

	public LicensingPanel(List<String> licenses) {
		init();
		setRequiredComponentLicensesList(licenses);
	}

	public LicensingPanel() {
		init();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void init() {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setLayout(new BorderLayout());

		{
			requiredComponentLicensesLabel = new JLabel(REQUIRED_COMPONENT_LICENSES_LABEL_TEXT);
			requiredComponentLicensesLabel.setFont(new Font(requiredComponentLicensesLabel.getFont().getName(), Font.BOLD, 11));
			requiredComponentLicensesLabel.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH_TOP, BORDER_WIDTH_LEFT, BORDER_WIDTH_BOTTOM, BORDER_WIDTH_RIGHT));
			this.add(requiredComponentLicensesLabel, BorderLayout.LINE_START);
		}
		{
			requiredComponentLicensesList = new JLabel();
			requiredComponentLicensesList.setFont(new Font(requiredComponentLicensesList.getFont().getName(), Font.PLAIN, 11));
			requiredComponentLicensesList.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH_TOP, BORDER_WIDTH_LEFT, BORDER_WIDTH_BOTTOM, BORDER_WIDTH_RIGHT));
			this.add(requiredComponentLicensesList, BorderLayout.CENTER);
		}
		{
			status = new JLabel();
			status.setFont(new Font(status.getFont().getName(), Font.PLAIN, 11));
			status.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH_TOP, BORDER_WIDTH_LEFT, BORDER_WIDTH_BOTTOM, BORDER_WIDTH_RIGHT));
			setComponentObtainingStatus(false);
			this.add(status, BorderLayout.PAGE_END);
		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public void setRequiredComponentLicensesList(List<String> licenses) {
		requiredComponentLicensesList.setText(licenses.toString().replace("[", "").replace("]", "").replace(" ", ""));
	}

	public void setComponentObtainingStatus(boolean succeeded) {
		if (succeeded) {
			status.setText(COMPONENTS_OBTAINED_STATUS_TEXT);
			status.setForeground(COMPONENTS_OBTAINED_STATUS_TEXT_COLOR);
		} else {
			status.setText(COMPONENTS_NOT_OBTAINED_STATUS_TEXT);
			status.setForeground(COMPONENTS_NOT_OBTAINED_STATUS_TEXT_COLOR);
		}
	}
}
