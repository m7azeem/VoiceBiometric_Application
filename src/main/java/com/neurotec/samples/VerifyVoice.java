package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NVoice;
import com.neurotec.samples.util.Utils;
import com.neurotec.util.concurrent.CompletionHandler;

public final class VerifyVoice extends BasePanel implements ActionListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	private static final String SUBJECT_LEFT = "left";
	private static final String SUBJECT_RIGHT = "right";

	private static final String LEFT_LABEL_TEXT = "First template or audio file: ";
	private static final String RIGHT_LABEL_TEXT = "Second template or audio file: ";

	// ===========================================================
	// Static fields
	// ===========================================================

	private static final String FIRST_LABEL_TEXT = "First template or audio file: ";
	private static final String SECOND_LABEL_TEXT = "Second template or audio file: ";

	// ===========================================================
	// Private fields
	// ===========================================================

	private NSubject subjectLeft;
	private NSubject subjectRight;

	private JFileChooser fileChooser;

	private final TemplateCreationHandler templateCreationHandler = new TemplateCreationHandler();
	private final VerificationHandler verificationHandler = new VerificationHandler();

	private JButton defaultButton;
	private JComboBox farComboBox;
	private JLabel firstFileLabel;
	private JLabel firstLabel;
	private JPanel mainPanel;
	private JPanel northPanel;
	private JButton openFirstButton;
	private JButton openSecondButton;
	private JPanel outerPanel;
	private JLabel secondFileLabel;
	private JLabel secondLabel;
	private JPanel settingsPanel;
	private JPanel southPanel;
	private JCheckBox uniquePhraseCheckbox;
	private JButton verifyButton;
	private JLabel verifyLabel;
	private JPanel verifyPanel;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public VerifyVoice() {
		super();
		licenses = new ArrayList<String>();
		licenses.add("Biometrics.VoiceExtraction");
		licenses.add("Biometrics.VoiceMatching");
		initGUI();

		subjectLeft = new NSubject();
		subjectRight = new NSubject();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void loadItem(String position) throws IOException {
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			verifyLabel.setText("");
			NSubject subjectTmp = null;
			try {
				subjectTmp = NSubject.fromFile(fileChooser.getSelectedFile().getAbsolutePath());
				if (subjectTmp.getVoices().isEmpty()) {
					subjectTmp = null;
					throw new IllegalArgumentException("Template contains no voice records.");
				}
				templateCreationHandler.completed(NBiometricStatus.OK, position);
			} catch (UnsupportedOperationException e) {
				// Ignore. UnsupportedOperationException means file is not a valid template.
			}

			// If file is not a template, try to load it as an audio file.
			if (subjectTmp == null) {
				NVoice voice = new NVoice();
				voice.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
				subjectTmp = new NSubject();
				subjectTmp.getVoices().add(voice);
				updateVoicesTools();
				VoicesTools.getInstance().getClient().createTemplate(subjectTmp, position, templateCreationHandler);
			}

			if (SUBJECT_LEFT.equals(position)) {
				subjectLeft = subjectTmp;
				firstFileLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
			} else if (SUBJECT_RIGHT.equals(position)) {
				subjectRight = subjectTmp;
				secondFileLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
			} else {
				throw new AssertionError("Unknown subject position: " + position);
			}
		}
	}

	private void verify() {
		updateVoicesTools();
		VoicesTools.getInstance().getClient().verify(subjectLeft, subjectRight, null, verificationHandler);
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void initGUI() {
		GridBagConstraints gridBagConstraints;
		setLayout(new BorderLayout());
		{
			outerPanel = new JPanel();
			outerPanel.setLayout(new BorderLayout());
			add(outerPanel, BorderLayout.NORTH);
			{
				licensingPanel = new LicensingPanel(licenses);
				outerPanel.add(licensingPanel, java.awt.BorderLayout.NORTH);
			}
			{
				mainPanel = new JPanel();
				mainPanel.setLayout(new BorderLayout());
				outerPanel.add(mainPanel, BorderLayout.SOUTH);
				{
					northPanel = new JPanel();
					northPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(northPanel, BorderLayout.NORTH);
					{
						settingsPanel = new JPanel();
						settingsPanel.setBorder(BorderFactory.createTitledBorder("Matching FAR"));
						settingsPanel.setLayout(new GridBagLayout());
						northPanel.add(settingsPanel);
						{
							farComboBox = new JComboBox();
							char c = new DecimalFormatSymbols().getPercent();
							DefaultComboBoxModel model = (DefaultComboBoxModel) farComboBox.getModel();
							NumberFormat nf = NumberFormat.getNumberInstance();
							nf.setMaximumFractionDigits(5);
							model.addElement(nf.format(0.1) + c);
							model.addElement(nf.format(0.01) + c);
							model.addElement(nf.format(0.001) + c);
							farComboBox.setSelectedIndex(1);
							farComboBox.setEditable(true);
							farComboBox.setModel(model);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 0;
							gridBagConstraints.gridwidth = 2;
							gridBagConstraints.insets = new Insets(3, 3, 3, 3);
							settingsPanel.add(farComboBox, gridBagConstraints);
						}
						{
							defaultButton = new JButton();
							defaultButton.setText("Default");
							defaultButton.addActionListener(this);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 2;
							gridBagConstraints.gridy = 0;
							gridBagConstraints.insets = new Insets(3, 3, 3, 3);
							settingsPanel.add(defaultButton, gridBagConstraints);
						}
						{
							uniquePhraseCheckbox = new JCheckBox();
							uniquePhraseCheckbox.setText("Unique phrases only");
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 1;
							gridBagConstraints.gridwidth = 3;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							settingsPanel.add(uniquePhraseCheckbox, gridBagConstraints);
						}
					}
				}
				{
					southPanel = new JPanel();
					southPanel.setLayout(new BorderLayout());
					mainPanel.add(southPanel, BorderLayout.SOUTH);
					{
						verifyPanel = new JPanel();
						GridBagLayout verifyPanelLayout = new GridBagLayout();
						verifyPanelLayout.columnWidths = new int[] {0, 5, 0, 5, 0};
						verifyPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0};
						verifyPanel.setLayout(verifyPanelLayout);
						southPanel.add(verifyPanel, BorderLayout.WEST);
						{
							firstLabel = new JLabel();
							firstLabel.setText(FIRST_LABEL_TEXT);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 2;
							gridBagConstraints.gridy = 0;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(firstLabel, gridBagConstraints);
						}
						{
							secondLabel = new JLabel();
							secondLabel.setText(SECOND_LABEL_TEXT);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 2;
							gridBagConstraints.gridy = 2;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(secondLabel, gridBagConstraints);
						}
						{
							verifyButton = new JButton();
							verifyButton.setText("Verify");
							verifyButton.addActionListener(this);
							verifyButton.setEnabled(false);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 4;
							gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(verifyButton, gridBagConstraints);
						}
						{
							verifyLabel = new JLabel();
							verifyLabel.setText("     ");
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 6;
							gridBagConstraints.gridwidth = 5;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(verifyLabel, gridBagConstraints);
						}
						{
							openFirstButton = new JButton();
							openFirstButton.setText("Open");
							openFirstButton.addActionListener(this);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 0;
							gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(openFirstButton, gridBagConstraints);
						}
						{
							openSecondButton = new JButton();
							openSecondButton.setText("Open");
							openSecondButton.addActionListener(this);
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 0;
							gridBagConstraints.gridy = 2;
							gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(openSecondButton, gridBagConstraints);
						}
						{
							firstFileLabel = new JLabel();
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 4;
							gridBagConstraints.gridy = 0;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(firstFileLabel, gridBagConstraints);
						}
						{
							secondFileLabel = new JLabel();
							gridBagConstraints = new GridBagConstraints();
							gridBagConstraints.gridx = 4;
							gridBagConstraints.gridy = 2;
							gridBagConstraints.anchor = GridBagConstraints.LINE_START;
							verifyPanel.add(secondFileLabel, gridBagConstraints);
						}
					}
				}
			}
		}
		fileChooser = new JFileChooser();
	}

	@Override
	protected void setDefaultValues() {
		farComboBox.setSelectedItem(Utils.matchingThresholdToString(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold()));
		uniquePhraseCheckbox.setSelected(VoicesTools.getInstance().getDefaultClient().isVoicesUniquePhrasesOnly());
	}

	@Override
	protected void updateControls() {
		if (subjectLeft.getVoices().isEmpty() || subjectRight.getVoices().isEmpty()) {
			verifyButton.setEnabled(false);
		} else {
			verifyButton.setEnabled(true);
		}
	}

	@Override
	protected void updateVoicesTools() {
		VoicesTools.getInstance().getClient().reset();
		VoicesTools.getInstance().getClient().setVoicesUniquePhrasesOnly(uniquePhraseCheckbox.isSelected());
		try {
			VoicesTools.getInstance().getClient().setMatchingThreshold(Utils.matchingThresholdFromString(farComboBox.getSelectedItem().toString()));
		} catch (ParseException e) {
			e.printStackTrace();
			VoicesTools.getInstance().getClient().setMatchingThreshold(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold());
			farComboBox.setSelectedItem(Utils.matchingThresholdToString(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold()));
			JOptionPane.showMessageDialog(this, "FAR is not valid. Using default value.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ===========================================================
	// Package private methods
	// ===========================================================

	void updateLabel(String msg) {
		verifyLabel.setText(msg);
	}

	NSubject getLeft() {
		return subjectLeft;
	}

	NSubject getRight() {
		return subjectRight;
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if (ev.getSource() == defaultButton) {
				farComboBox.setSelectedItem(Utils.matchingThresholdToString(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold()));
			} else if (ev.getSource() == verifyButton) {
				verify();
			} else if (ev.getSource() == openFirstButton) {
				loadItem(SUBJECT_LEFT);
			} else if (ev.getSource() == openSecondButton) {
				loadItem(SUBJECT_RIGHT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
			updateControls();
		}
	}

	// ===========================================================
	// Inner classes
	// ===========================================================

	private class TemplateCreationHandler implements CompletionHandler<NBiometricStatus, String> {

		@Override
		public void completed(final NBiometricStatus status, final String subject) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (status != NBiometricStatus.OK) {
						JOptionPane.showMessageDialog(VerifyVoice.this, "Template was not created: " + status, "Error", JOptionPane.WARNING_MESSAGE);
					}
					updateControls();
				}

			});
		}

		@Override
		public void failed(final Throwable th, final String subject) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					showErrorDialog(th);
				}

			});
		}

	}

	private class VerificationHandler implements CompletionHandler<NBiometricStatus, String> {

		@Override
		public void completed(final NBiometricStatus status, final String subject) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (status == NBiometricStatus.OK) {
						int score = getLeft().getMatchingResults().get(0).getScore();
						String msg = "Score of matched templates: " + score;
						updateLabel(msg);
						JOptionPane.showMessageDialog(VerifyVoice.this, msg, "Match", JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(VerifyVoice.this, "Templates didn't match.", "No match", JOptionPane.WARNING_MESSAGE);
					}
				}

			});
		}

		@Override
		public void failed(final Throwable th, final String subject) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					showErrorDialog(th);
				}

			});
		}

	}

}
