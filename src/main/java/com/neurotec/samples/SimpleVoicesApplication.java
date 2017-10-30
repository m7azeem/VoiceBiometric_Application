package com.neurotec.samples;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.neurotec.lang.NCore;
import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.Utils;

public final class SimpleVoicesApplication {

	//variables for Registration GUI
	JFrame recorderFrame;
	JPanel recorderHomePanel;
	JLabel userRegistrationLabel;
	JPanel namePanel;
	JTextField nameField;
	JButton startBtn;
	JLabel textToReadLabel;
	// ===========================================================
	// Private constructor
	// ===========================================================

	private SimpleVoicesApplication() {
	}

	// ===========================================================
	// Main method
	// ===========================================================

	public static void main(String[] args) {
		/*
		LibraryManager.initLibraryPath();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setTitle("Simple Voices Sample");
				frame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						NCore.shutdown();
					}
				});
				frame.add(new MainPanel(), BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
*/
		LibraryManager.initLibraryPath();


		final SimpleVoicesApplication simpleVoicesApplication = new SimpleVoicesApplication();
		simpleVoicesApplication.initRegistrationGUI();
		//simpleVoicesApplication.setup();
	}

	public void initRegistrationGUI(){
		recorderFrame = new JFrame();
		recorderFrame.setTitle("Voice registration");
		recorderFrame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
		recorderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		recorderFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				NCore.shutdown();
			}
		});

		recorderHomePanel = new JPanel( new GridLayout(4, 1));

		userRegistrationLabel = new JLabel("User registration");
		userRegistrationLabel.setHorizontalAlignment(SwingConstants.CENTER);;
		recorderHomePanel.add(userRegistrationLabel);

		namePanel = new JPanel( new GridLayout(1, 2));
		namePanel.add( new JLabel("Name: "));
		nameField = new JTextField();
		//nameField.setSize(10, 3);
		namePanel.add(nameField);
		recorderHomePanel.add(namePanel);

		startBtn = new JButton("Start");
		textToReadLabel = new JLabel("After clicking Start. Read the generated text here.");

		recorderHomePanel.add(startBtn);
		recorderHomePanel.add(textToReadLabel);

		recorderFrame.add(recorderHomePanel);
		recorderFrame.pack();
		recorderFrame.setLocationRelativeTo(null);
		recorderFrame.setVisible(true);

		startBtn.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent actionEvent ){
				//connect method to start recording, extracting template, saving.
				textToReadLabel.setText(nameField.getText());
				//new WriteXMLFile().doSometing();
				EnrollVoiceFromMicrophone enrollVoiceFromMicrophone = new EnrollVoiceFromMicrophone();
				if(enrollVoiceFromMicrophone.start(nameField.getText())){
					textToReadLabel.setText(nameField.getText()+ " enrolled.");
				} else {
					textToReadLabel.setText("enrollment failed");
				}
			}
		});
	}

	public void setup(){
		//RegisterFromMicrophone registerFromMicrophone = new RegisterFromMicrophone();
		//registerFromMicrophone.init();
		MainPanel mainPanel = new MainPanel();
	}
}