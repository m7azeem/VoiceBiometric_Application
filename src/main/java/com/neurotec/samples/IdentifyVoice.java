package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NMatchingResult;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NSubject.VoiceCollection;
import com.neurotec.biometrics.NVoice;
import com.neurotec.samples.util.Utils;
import com.neurotec.util.concurrent.CompletionHandler;

public final class IdentifyVoice extends BasePanel implements ActionListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private fields
	// ===========================================================

	private NSubject subject;
	private final List<NSubject> subjects;

	private final EnrollHandler enrollHandler = new EnrollHandler();
	private final IdentificationHandler identificationHandler = new IdentificationHandler();
	private final TemplateCreationHandler templateCreationHandler = new TemplateCreationHandler();

	private JFileChooser fcGallery;
	private JFileChooser fcProbe;

	private JLabel countLabel;
	private JComboBox farComboBox;
	private JButton farDefaultButton;
	private JLabel fileLabel;
	private JPanel filePanel;
	private JPanel identificationNorthPanel;
	private JPanel identificationPanel;
	private JButton identifyButton;
	private JPanel identifyButtonPanel;
	private JPanel innerSettingsPanel;
	private JPanel mainPanel;
	private JPanel northPanel;
	private JButton openProbeButton;
	private JButton openTemplatesButton;
	private JScrollPane resultsScrollPane;
	private JTable resultsTable;
	private JPanel settingsPanel;
	private JLabel templatesLabel;
	private JPanel templatesPanel;
	private JCheckBox uniquePhraseCheckbox;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public IdentifyVoice() {
		super();
		subjects = new ArrayList<NSubject>();
		licenses = new ArrayList<String>();
		licenses.add("Biometrics.VoiceExtraction");
		licenses.add("Biometrics.VoiceMatching");
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void openTemplates() throws IOException {
		if (fcGallery.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			((DefaultTableModel) resultsTable.getModel()).setRowCount(0);
			subjects.clear();

			// Create subjects from selected templates.
			for (File file : fcGallery.getSelectedFiles()) {
				NSubject s = NSubject.fromFile(file.getAbsolutePath());
				s.setId(file.getName());
				subjects.add(s);
			}
			countLabel.setText(String.valueOf(subjects.size()));
		}
		updateControls();
	}

	private void openProbe() throws IOException {
		if (fcProbe.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			((DefaultTableModel) resultsTable.getModel()).setRowCount(0);
			fileLabel.setText("");
			subject = null;
			try {
				subject = NSubject.fromFile(fcProbe.getSelectedFile().getAbsolutePath());
				subject.setId(fcProbe.getSelectedFile().getName());
				VoiceCollection voices = subject.getVoices();
				if (voices.isEmpty()) {
					subject = null;
					throw new IllegalArgumentException("Template contains no voice records.");
				}
				templateCreationHandler.completed(NBiometricStatus.OK, null);
			} catch (UnsupportedOperationException e) {
				// Ignore. UnsupportedOperationException means file is not a valid template.
			}

			// If file is not a template, try to load it as an image.
			if (subject == null) {
				NVoice voice = new NVoice();
				voice.setFileName(fcProbe.getSelectedFile().getAbsolutePath());
				subject = new NSubject();
				subject.setId(fcProbe.getSelectedFile().getName());
				subject.getVoices().add(voice);
				updateVoicesTools();
				VoicesTools.getInstance().getClient().createTemplate(subject, null, templateCreationHandler);
			}

			fileLabel.setText(fcProbe.getSelectedFile().getAbsolutePath());
		}
	}

	private void identify() {
		if ((subject != null) && !subjects.isEmpty()) {
			((DefaultTableModel) resultsTable.getModel()).setRowCount(0);
			updateVoicesTools();

			// Clean earlier data before proceeding, enroll new data
			VoicesTools.getInstance().getClient().clear();

			// Create enrollment task.
			NBiometricTask enrollmentTask = new NBiometricTask(EnumSet.of(NBiometricOperation.ENROLL));

			// Add subjects to be enrolled.
			for (NSubject s : subjects) {
				enrollmentTask.getSubjects().add(s);
			}

			// Enroll subjects.
			VoicesTools.getInstance().getClient().performTask(enrollmentTask, null, enrollHandler);
		}
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void initGUI() {
		GridBagConstraints gridBagConstraints;
		setLayout(new BorderLayout());
		{
			licensingPanel = new LicensingPanel(licenses);
			add(licensingPanel, java.awt.BorderLayout.NORTH);
		}
		{
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			add(mainPanel, BorderLayout.CENTER);
			{
				northPanel = new JPanel();
				northPanel.setLayout(new BorderLayout());
				mainPanel.add(northPanel, BorderLayout.NORTH);
				{
					templatesPanel = new JPanel();
					templatesPanel.setBorder(BorderFactory.createTitledBorder("Templates loading"));
					templatesPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					northPanel.add(templatesPanel, BorderLayout.NORTH);
					{
						openTemplatesButton = new JButton();
						openTemplatesButton.setText("Load");
						openTemplatesButton.addActionListener(this);
						templatesPanel.add(openTemplatesButton);
					}
					{
						templatesLabel = new JLabel();
						templatesLabel.setText("Templates loaded: ");
						templatesPanel.add(templatesLabel);
					}
					{
						countLabel = new JLabel();
						countLabel.setText("0");
						templatesPanel.add(countLabel);
					}
				}
				{
					filePanel = new JPanel();
					filePanel.setBorder(BorderFactory.createTitledBorder("Voice audio file or template for identification"));
					filePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					northPanel.add(filePanel, BorderLayout.SOUTH);
					{
						openProbeButton = new JButton();
						openProbeButton.setText("Open");
						openProbeButton.addActionListener(this);
						filePanel.add(openProbeButton);
					}
					{
						fileLabel = new JLabel();
						filePanel.add(fileLabel);
					}
				}
			}
			{
				identificationPanel = new JPanel();
				identificationPanel.setBorder(BorderFactory.createTitledBorder("Identification"));
				identificationPanel.setLayout(new BorderLayout());
				mainPanel.add(identificationPanel, BorderLayout.CENTER);
				{
					identificationNorthPanel = new JPanel();
					identificationNorthPanel.setLayout(new BorderLayout());
					identificationPanel.add(identificationNorthPanel, BorderLayout.NORTH);
					{
						identifyButtonPanel = new JPanel();
						identifyButtonPanel.setLayout(new BoxLayout(identifyButtonPanel, BoxLayout.Y_AXIS));
						identificationNorthPanel.add(identifyButtonPanel, BorderLayout.WEST);
						{
							identifyButton = new JButton();
							identifyButton.setText("Identify");
							identifyButton.setEnabled(false);
							identifyButton.addActionListener(this);
							identifyButtonPanel.add(identifyButton);
						}
					}
					{
						settingsPanel = new JPanel();
						settingsPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
						identificationNorthPanel.add(settingsPanel, BorderLayout.EAST);
						{
							innerSettingsPanel = new JPanel();
							innerSettingsPanel.setBorder(BorderFactory.createTitledBorder("Matching FAR"));
							innerSettingsPanel.setLayout(new GridBagLayout());
							settingsPanel.add(innerSettingsPanel);
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
								innerSettingsPanel.add(farComboBox, gridBagConstraints);
							}
							{
								farDefaultButton = new JButton();
								farDefaultButton.setText("Default");
								farDefaultButton.addActionListener(this);
								gridBagConstraints = new GridBagConstraints();
								gridBagConstraints.gridx = 2;
								gridBagConstraints.gridy = 0;
								innerSettingsPanel.add(farDefaultButton, gridBagConstraints);
							}
							{
								uniquePhraseCheckbox = new JCheckBox();
								uniquePhraseCheckbox.setText("Unique phrases only");
								gridBagConstraints = new GridBagConstraints();
								gridBagConstraints.gridx = 0;
								gridBagConstraints.gridy = 1;
								gridBagConstraints.gridwidth = 3;
								gridBagConstraints.anchor = GridBagConstraints.LINE_START;
								innerSettingsPanel.add(uniquePhraseCheckbox, gridBagConstraints);
							}
						}
					}
				}
				{
					resultsScrollPane = new JScrollPane();
					resultsScrollPane.setPreferredSize(new Dimension(0, 90));
					identificationPanel.add(resultsScrollPane, BorderLayout.CENTER);
					{
						resultsTable = new JTable();
						resultsTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Score"}) {

							private final Class<?>[] types = new Class<?>[] {String.class, Integer.class};
							private final boolean[] canEdit = new boolean[] {false, false};

							@Override
							public Class<?> getColumnClass(int columnIndex) {
								return types[columnIndex];
							}

							@Override
							public boolean isCellEditable(int rowIndex, int columnIndex) {
								return canEdit[columnIndex];
							}

						});
						DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
						leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
						resultsTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
						resultsScrollPane.setViewportView(resultsTable);
					}
				}
			}
		}
		fcProbe = new JFileChooser();
		fcGallery = new JFileChooser();
		fcGallery.setMultiSelectionEnabled(true);
	}

	@Override
	protected void setDefaultValues() {
		farComboBox.setSelectedItem(Utils.matchingThresholdToString(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold()));
		uniquePhraseCheckbox.setSelected(VoicesTools.getInstance().getDefaultClient().isVoicesUniquePhrasesOnly());
	}

	@Override
	protected void updateControls() {
		identifyButton.setEnabled(!subjects.isEmpty() && (subject != null) && ((subject.getStatus() == NBiometricStatus.OK) || (subject.getStatus() == NBiometricStatus.NONE)));
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

	NSubject getSubject() {
		return subject;
	}

	void setSubject(NSubject subject) {
		this.subject = subject;
	}

	List<NSubject> getSubjects() {
		return subjects;
	}

	void appendIdentifyResult(String name, int score) {
		((DefaultTableModel) resultsTable.getModel()).addRow(new Object[] {name, score});
	}

	void prependIdentifyResult(String name, int score) {
		((DefaultTableModel) resultsTable.getModel()).insertRow(0, new Object[] {name, score});
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if (ev.getSource() == openTemplatesButton) {
				openTemplates();
			} else if (ev.getSource() == openProbeButton) {
				openProbe();
			} else if (ev.getSource() == farDefaultButton) {
				farComboBox.setSelectedItem(Utils.matchingThresholdToString(VoicesTools.getInstance().getDefaultClient().getMatchingThreshold()));
			} else if (ev.getSource() == identifyButton) {
				identify();
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

	private class TemplateCreationHandler implements CompletionHandler<NBiometricStatus, Object> {

		@Override
		public void completed(final NBiometricStatus status, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					updateControls();
					if (status != NBiometricStatus.OK) {
						setSubject(null);
						JOptionPane.showMessageDialog(IdentifyVoice.this, "Template was not created: " + status, "Error", JOptionPane.WARNING_MESSAGE);
					}
				}

			});
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					updateControls();
					showErrorDialog(th);
				}

			});
		}

	}

	private class EnrollHandler implements CompletionHandler<NBiometricTask, Object> {

		@Override
		public void completed(final NBiometricTask task, final Object attachment) {
			if (task.getStatus() == NBiometricStatus.OK) {

				// Identify current subject in enrolled ones.
				VoicesTools.getInstance().getClient().identify(getSubject(), null, identificationHandler);
			} else {
				JOptionPane.showMessageDialog(IdentifyVoice.this, "Enrollment failed: " + task.getStatus(), "Error", JOptionPane.WARNING_MESSAGE);
			}
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					updateControls();
					showErrorDialog(th);
				}

			});
		}

	}

	private class IdentificationHandler implements CompletionHandler<NBiometricStatus, Object> {

		@Override
		public void completed(final NBiometricStatus status, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if ((status == NBiometricStatus.OK) || (status == NBiometricStatus.MATCH_NOT_FOUND)) {

						// Match subjects.
						for (NSubject s : getSubjects()) {
							boolean match = false;
							for (NMatchingResult result : getSubject().getMatchingResults()) {
								if (s.getId().equals(result.getId())) {
									match = true;
									prependIdentifyResult(result.getId(), result.getScore());
									break;
								}
							}
							if (!match) {
								appendIdentifyResult(s.getId(), 0);
							}
						}
					} else {
						JOptionPane.showMessageDialog(IdentifyVoice.this, "Identification failed: " + status, "Error", JOptionPane.WARNING_MESSAGE);
					}
				}

			});
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					updateControls();
					showErrorDialog(th);
				}

			});
		}

	}

}
