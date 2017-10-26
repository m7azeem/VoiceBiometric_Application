package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NVoice;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.io.NFile;
import com.neurotec.util.concurrent.CompletionHandler;

public final class EnrollFromFile extends BasePanel implements ActionListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;
	private static final String textDependent = "Extract text dependent features";
	private static final String textIndependent = "Extract text independent features";

	// ===========================================================
	// Private fields
	// ===========================================================

	private NSubject subject;
	private NVoice voice;

	private final TemplateCreationHandler templateCreationHandler = new TemplateCreationHandler();

	private JFileChooser openFileChooser;
	private JFileChooser saveTemplateChooser;
	private File oldTemplateFile;
	private JFileChooser saveVoiceChooser;
	private File oldVoiceFile;

	private JButton btnSaveAudio;
	private JButton btnSaveTemplate;
	private JComboBox<String> cmbExtractionMode;
	private JButton extractButton;
	private JPanel extractPanel;
	private JLabel fileLabel;
	private JPanel filePanel;
	private JPanel mainPanel;
	private JButton openButton;
	private JLabel openLabel;
	private JPanel optionsPanel;
	private JPanel optionsPanelOuter;
	private JPanel outerPanel;
	private JLabel phraseIdLabel;
	private JSpinner phraseIdSpinner;
	private JPanel savePanel;
	private JLabel statusLabel;
	private JPanel statusPanel;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public EnrollFromFile() {
		super();
		licenses = new ArrayList<String>();
		licenses.add("Media");
		licenses.add("Biometrics.VoiceExtraction");
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void openSoundFile() {
		if (openFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			subject = new NSubject();
			voice = new NVoice();
			voice.setFileName(openFileChooser.getSelectedFile().getAbsolutePath());
			subject.getVoices().add(voice);
			statusLabel.setText("");
			fileLabel.setText(openFileChooser.getSelectedFile().getAbsolutePath());
			updateControls();
		}
	}

	private void extract() {
		disableControls();
		statusLabel.setText("");
		updateVoicesTools();
		voice.setPhraseID((Integer) phraseIdSpinner.getValue());

		// Do voice extraction and segment voice from audio.

		NBiometricTask task = VoicesTools.getInstance().getClient().createTask(EnumSet.of(NBiometricOperation.SEGMENT, NBiometricOperation.CREATE_TEMPLATE), subject);
		VoicesTools.getInstance().getClient().performTask(task, null, templateCreationHandler);
	}

	private void saveTemplate() throws IOException {
		if (subject == null) {
			return;
		}
		if (oldTemplateFile != null) {
			saveTemplateChooser.setSelectedFile(oldTemplateFile);
		}
		if (saveTemplateChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			oldTemplateFile = saveTemplateChooser.getSelectedFile();
			String fileName = saveTemplateChooser.getSelectedFile().getAbsolutePath();
			NFile.writeAllBytes(fileName, subject.getTemplateBuffer());
		}
	}

	private void saveVoice() throws IOException {
		if (voice == null) {
			return;
		}
		if (oldVoiceFile != null) {
			saveVoiceChooser.setSelectedFile(oldVoiceFile);
		}
		if (saveVoiceChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			oldVoiceFile = saveVoiceChooser.getSelectedFile();
			String fileName = saveVoiceChooser.getSelectedFile().getAbsolutePath();
			NFile.writeAllBytes(fileName, voice.getSoundBuffer().save());
		}
	}

	private void disableControls() {
		openButton.setEnabled(false);
		extractButton.setEnabled(false);
		btnSaveTemplate.setEnabled(false);
		btnSaveAudio.setEnabled(false);
	}

	private void updateTemplateCreationStatus(NBiometricStatus status) {
		if (status == NBiometricStatus.OK) {
			statusLabel.setText("Template extracted");
		} else if (status == null) {
			statusLabel.setText("Extraction failed");
		} else {
			statusLabel.setText("Extraction failed: " + status);
		}
		updateControls();
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void initGUI() {
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
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
				outerPanel.add(mainPanel, BorderLayout.SOUTH);
				{
					filePanel = new JPanel();
					filePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(filePanel);
					{
						openButton = new JButton();
						openButton.setText("Open file");
						openButton.addActionListener(this);
						filePanel.add(openButton);
					}
					{
						openLabel = new JLabel();
						openLabel.setText("Sound file:");
						filePanel.add(openLabel);
					}
					{
						fileLabel = new JLabel();
						filePanel.add(fileLabel);
					}
				}
				{
					optionsPanelOuter = new JPanel();
					optionsPanelOuter.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(optionsPanelOuter);
					{
						optionsPanel = new JPanel();
						optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
						optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
						optionsPanelOuter.add(optionsPanel);
						{
							cmbExtractionMode = new JComboBox<String>();
							cmbExtractionMode.addItem(textDependent);
							cmbExtractionMode.addItem(textIndependent);
							cmbExtractionMode.setSelectedIndex(0);
							optionsPanel.add(cmbExtractionMode);
						}
					}
				}
				{
					extractPanel = new JPanel();
					extractPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(extractPanel);
					{
						extractButton = new JButton();
						extractButton.setText("Extract");
						extractButton.setEnabled(false);
						extractButton.addActionListener(this);
						extractPanel.add(extractButton);
					}
					{
						phraseIdLabel = new JLabel();
						phraseIdLabel.setText("Phrase id:");
						extractPanel.add(phraseIdLabel);
					}
					{
						phraseIdSpinner = new JSpinner();
						phraseIdSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
						phraseIdSpinner.setPreferredSize(new Dimension(70, 20));
						extractPanel.add(phraseIdSpinner);
					}
				}
				{
					statusPanel = new JPanel();
					statusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(statusPanel);
					{
						statusLabel = new JLabel();
						statusPanel.add(statusLabel);
					}
				}
				{
					savePanel = new JPanel();
					savePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					mainPanel.add(savePanel);
					{
						btnSaveTemplate = new JButton();
						btnSaveTemplate.setText("Save template");
						btnSaveTemplate.setEnabled(false);
						btnSaveTemplate.setPreferredSize(new Dimension(113, 23));
						btnSaveTemplate.addActionListener(this);
						savePanel.add(btnSaveTemplate);
					}
					{
						btnSaveAudio = new JButton();
						btnSaveAudio.setText("Save voice audio");
						btnSaveAudio.setEnabled(false);
						btnSaveAudio.addActionListener(this);
						savePanel.add(btnSaveAudio);
					}
				}
			}
		}
		openFileChooser = new JFileChooser();
		saveTemplateChooser = new JFileChooser();
		saveVoiceChooser = new JFileChooser();
	}

	@Override
	protected void setDefaultValues() {
		if (VoicesTools.getInstance().getDefaultClient().isVoicesExtractTextDependentFeatures()) {
			cmbExtractionMode.setSelectedItem(textDependent);
		} else {
			cmbExtractionMode.setSelectedItem(textIndependent);
		}
	}

	@Override
	protected void updateControls() {
		openButton.setEnabled(true);
		extractButton.setEnabled((openFileChooser.getSelectedFile() != null));
		btnSaveTemplate.setEnabled((subject != null) && (subject.getStatus() == NBiometricStatus.OK));
		btnSaveAudio.setEnabled((subject != null) && (subject.getStatus() == NBiometricStatus.OK));
	}

	@Override
	protected void updateVoicesTools() {
		NBiometricClient client = VoicesTools.getInstance().getClient();
		client.reset();
		client.setVoicesExtractTextDependentFeatures(cmbExtractionMode.getSelectedItem().equals(textDependent));
		client.setVoicesExtractTextIndependentFeatures(true);
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if (ev.getSource() == openButton) {
				openSoundFile();
			} else if (ev.getSource() == extractButton) {
				extract();
			} else if (ev.getSource() == btnSaveTemplate) {
				saveTemplate();
			} else if (ev.getSource() == btnSaveAudio) {
				saveVoice();
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

	private class TemplateCreationHandler implements CompletionHandler<NBiometricTask, Object> {

		@Override
		public void completed(final NBiometricTask result, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Throwable th = result.getError();
					if (th != null) {
						th.printStackTrace();
						showErrorDialog(th);
					}
					updateTemplateCreationStatus(result.getStatus());
				}

			});
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					showErrorDialog(th);
					updateTemplateCreationStatus(null);
				}

			});
		}

	}

}
