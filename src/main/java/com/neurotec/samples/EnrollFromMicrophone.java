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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.neurotec.biometrics.NBiometricCaptureOption;
import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NVoice;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.biometrics.swing.NVoiceView;
import com.neurotec.devices.NDevice;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.devices.NMicrophone;
import com.neurotec.io.NFile;
import com.neurotec.util.concurrent.CompletionHandler;

public final class EnrollFromMicrophone extends BasePanel implements ActionListener {

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
	private boolean recording;
	private final NDeviceManager deviceManager;

	private final CaptureCompletionHandler captureCompletionHandler = new CaptureCompletionHandler();

	private NVoiceView view;
	private JFileChooser templateFileChooser;
	private File oldTemplateFile;
	private JFileChooser saveVoiceChooser;
	private File oldVoiceFile;

	private JButton btnForce;
	private JPanel buttonsPanel;
	private JCheckBox cbAutomatic;
	private JComboBox<String> cmbExtractionMode;
	private JPanel centerPanel;
	private JLabel infoLabel;
	private JScrollPane listScrollPane;
	private JPanel mainPanel;
	private JList microphoneList;
	private JPanel microphonePanel;
	private JPanel northPanel;
	private JPanel optionsOuterPanel;
	private JPanel optionsPanel;
	private JLabel phraseIdLabel;
	private JPanel phraseIdPanel;
	private JSpinner phraseIdSpinner;
	private JButton refreshButton;
	private JPanel saveButtonPanel;
	private JButton saveTemplateButton;
	private JButton saveVoiceButton;
	private JPanel southPanel;
	private JButton startButton;
	private JButton stopButton;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public EnrollFromMicrophone() {
		super();
		licenses = new ArrayList<String>();
		licenses.add("Biometrics.VoiceExtraction");
		licenses.add("Devices.Microphones");

		VoicesTools.getInstance().getClient().setUseDeviceManager(true);
		deviceManager = VoicesTools.getInstance().getClient().getDeviceManager();
		deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.MICROPHONE));
		deviceManager.initialize();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void enableOptions(boolean enable) {
		cbAutomatic.setEnabled(enable);
		phraseIdSpinner.setEnabled(enable);
		microphoneList.setEnabled(enable);
	}

	private void startCapturing() {
		if (VoicesTools.getInstance().getClient().getVoiceCaptureDevice() == null) {
			JOptionPane.showMessageDialog(this, "Please select a microphone from the list.", "No microphone selected", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		// Set voice capture from stream.
		voice = new NVoice();
		voice.setPhraseID((Integer) phraseIdSpinner.getValue());
		EnumSet<NBiometricCaptureOption> options = EnumSet.of(NBiometricCaptureOption.STREAM);
		if (!cbAutomatic.isSelected()) {
			options.add(NBiometricCaptureOption.MANUAL);
		}
		voice.setCaptureOptions(options);
		subject = new NSubject();
		subject.getVoices().add(voice);
		view.setVoice(voice);

		// Begin capturing.
		NBiometricTask task = VoicesTools.getInstance().getClient().createTask(EnumSet.of(NBiometricOperation.CAPTURE, NBiometricOperation.SEGMENT), subject);
		recording = true;
		VoicesTools.getInstance().getClient().performTask(task, null, captureCompletionHandler);
		updateControls();
	}

	private void saveTemplate() throws IOException {
		if (subject == null) {
			return;
		}
		if (oldTemplateFile != null) {
			templateFileChooser.setSelectedFile(oldTemplateFile);
		}
		if (templateFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			oldTemplateFile = templateFileChooser.getSelectedFile();
			String fileName = templateFileChooser.getSelectedFile().getAbsolutePath();
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

			// Voice buffer is saved in Child attribute after segmentation.
			NVoice child = (NVoice) voice.getObjects().get(0).getChild();
			if (child == null) {
				JOptionPane.showMessageDialog(this, "There is no voice to save.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			NFile.writeAllBytes(fileName, child.getSoundBuffer().save());
		}
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void initGUI() {
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
				northPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
				mainPanel.add(northPanel, BorderLayout.NORTH);
				{
					infoLabel = new JLabel();
					infoLabel.setText("Select microphone, press start and say phrase");
					northPanel.add(infoLabel);
				}
			}
			{
				centerPanel = new JPanel();
				centerPanel.setLayout(new BorderLayout());
				mainPanel.add(centerPanel, BorderLayout.CENTER);
				{
					microphonePanel = new JPanel();
					microphonePanel.setBorder(BorderFactory.createTitledBorder("Microphone list"));
					microphonePanel.setLayout(new BorderLayout());
					centerPanel.add(microphonePanel, BorderLayout.CENTER);
					{
						buttonsPanel = new JPanel();
						buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
						microphonePanel.add(buttonsPanel, BorderLayout.SOUTH);
						{
							refreshButton = new JButton();
							refreshButton.setText("Refresh list");
							refreshButton.addActionListener(this);
							buttonsPanel.add(refreshButton);
						}
						{
							startButton = new JButton();
							startButton.setText("Start");
							startButton.setPreferredSize(new Dimension(87, 23));
							startButton.addActionListener(this);
							buttonsPanel.add(startButton);
						}
						{
							stopButton = new JButton();
							stopButton.setText("Stop");
							stopButton.setEnabled(false);
							stopButton.setPreferredSize(new Dimension(87, 23));
							stopButton.addActionListener(this);
							buttonsPanel.add(stopButton);
						}
						{
							btnForce = new JButton();
							btnForce.setText("Force");
							btnForce.setPreferredSize(new Dimension(87, 23));
							btnForce.addActionListener(this);
							buttonsPanel.add(btnForce);
						}
					}
					{
						listScrollPane = new JScrollPane();
						listScrollPane.setPreferredSize(new Dimension(0, 90));
						microphonePanel.add(listScrollPane, BorderLayout.CENTER);
						{
							microphoneList = new JList();
							microphoneList.setModel(new DefaultListModel());
							microphoneList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
							microphoneList.setBorder(LineBorder.createBlackLineBorder());
							microphoneList.addListSelectionListener(new ScannerSelectionListener());
							listScrollPane.setViewportView(microphoneList);
						}
					}
				}
				{
					optionsOuterPanel = new JPanel();
					optionsOuterPanel.setLayout(new BorderLayout());
					centerPanel.add(optionsOuterPanel, BorderLayout.EAST);
					{
						optionsPanel = new JPanel();
						optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
						optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
						optionsOuterPanel.add(optionsPanel, BorderLayout.NORTH);
						{
							phraseIdPanel = new JPanel();
							phraseIdPanel.setAlignmentX(0.0F);
							phraseIdPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
							optionsPanel.add(phraseIdPanel);
							{
								phraseIdLabel = new JLabel();
								phraseIdLabel.setText("Phrase id:");
								phraseIdPanel.add(phraseIdLabel);
							}
							{
								phraseIdSpinner = new JSpinner();
								phraseIdSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
								phraseIdSpinner.setPreferredSize(new Dimension(39, 20));
								phraseIdPanel.add(phraseIdSpinner);
							}
						}
						{
							cmbExtractionMode = new JComboBox<String>();
							cmbExtractionMode.addItem(textDependent);
							cmbExtractionMode.addItem(textIndependent);
							cmbExtractionMode.setSelectedIndex(0);
							optionsPanel.add(cmbExtractionMode);
						}
						{
							cbAutomatic = new JCheckBox();
							cbAutomatic.setSelected(true);
							cbAutomatic.setText("Capture automatically");
							optionsPanel.add(cbAutomatic);
						}
					}
				}
			}
			{
				southPanel = new JPanel();
				southPanel.setLayout(new BorderLayout());
				mainPanel.add(southPanel, BorderLayout.PAGE_END);
				{
					saveButtonPanel = new JPanel();
					saveButtonPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
					southPanel.add(saveButtonPanel, BorderLayout.SOUTH);
					{
						saveTemplateButton = new JButton();
						saveTemplateButton.setText("Save template");
						saveTemplateButton.setEnabled(false);
						saveTemplateButton.addActionListener(this);
						saveButtonPanel.add(saveTemplateButton);
					}
					{
						saveVoiceButton = new JButton();
						saveVoiceButton.setText("Save voice audio");
						saveVoiceButton.setEnabled(false);
						saveVoiceButton.addActionListener(this);
						saveButtonPanel.add(saveVoiceButton);
					}
				}
				{
					view = new NVoiceView();
					southPanel.add(view, BorderLayout.NORTH);
				}
			}
		}
		templateFileChooser = new JFileChooser();
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
		startButton.setEnabled(!recording);
		stopButton.setEnabled(recording);
		btnForce.setEnabled(recording && !cbAutomatic.isSelected());
		refreshButton.setEnabled(!recording);
		saveTemplateButton.setEnabled(!recording && (subject != null) && (subject.getStatus() == NBiometricStatus.OK));
		saveVoiceButton.setEnabled(!recording && (subject != null) && (subject.getStatus() == NBiometricStatus.OK));
		enableOptions(!recording);
	}

	@Override
	protected void updateVoicesTools() {
		NBiometricClient client = VoicesTools.getInstance().getClient();
		client.reset();
		client.setVoicesExtractTextDependentFeatures(cmbExtractionMode.getSelectedItem().equals(textDependent));
		client.setVoicesExtractTextIndependentFeatures(true);
	}

	// ===========================================================
	// Package private methods
	// ===========================================================

	NMicrophone getSelectedScanner() {
		return (NMicrophone) microphoneList.getSelectedValue();
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public void updateMicrophoneList() {
		DefaultListModel model = (DefaultListModel) microphoneList.getModel();
		model.clear();
		for (NDevice device : deviceManager.getDevices()) {
			model.addElement(device);
		}
		NMicrophone microphone = VoicesTools.getInstance().getClient().getVoiceCaptureDevice();
		if ((microphone == null) && (model.getSize() > 0)) {
			microphoneList.setSelectedIndex(0);
		} else if (microphone != null) {
			microphoneList.setSelectedValue(microphone, true);
		}
	}

	public void cancelCapturing() {
		VoicesTools.getInstance().getClient().cancel();
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if (ev.getSource() == refreshButton) {
				updateMicrophoneList();
			} else if (ev.getSource() == startButton) {
				startCapturing();
			} else if (ev.getSource() == stopButton) {
				cancelCapturing();
			} else if (ev.getSource() == btnForce) {
				VoicesTools.getInstance().getClient().forceStart();
			} else if (ev.getSource() == saveTemplateButton) {
				saveTemplate();
			} else if (ev.getSource() == saveVoiceButton) {
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

	private class CaptureCompletionHandler implements CompletionHandler<NBiometricTask, Object> {

		@Override
		public void completed(final NBiometricTask result, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					recording = false;
					Throwable th = result.getError();
					if (th != null) {
						th.printStackTrace();
						showErrorDialog(th);
					}
					updateControls();
				}

			});
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					th.printStackTrace();
					recording = false;
					showErrorDialog(th);
					updateControls();
				}

			});
		}

	}

	private class ScannerSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			VoicesTools.getInstance().getClient().setVoiceCaptureDevice(getSelectedScanner());
		}

	}

}
