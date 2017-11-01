package com.neurotec.samples;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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


		//working code for registration gui
		final SimpleVoicesApplication simpleVoicesApplication = new SimpleVoicesApplication();
		//simpleVoicesApplication.initRegistrationGUI();
		//simpleVoicesApplication.initIdentificationGUI();
		XMLController xmlController = new XMLController();
		xmlController.makeNewXMLDocument();
		xmlController.readXMLData();
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
				//new XMLController().makeNewXMLDocument();
				EnrollVoiceFromMicrophone enrollVoiceFromMicrophone = new EnrollVoiceFromMicrophone();
				if(enrollVoiceFromMicrophone.start(nameField.getText(), "data/registration/")){
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



	//variables for Registration GUI
	JFrame identificationFrame;
	JPanel identificationHomePanel;
	JLabel identificationLabel;
	JLabel identificationTextToReadLabel;
	JButton identifyBtn;

	public void initIdentificationGUI(){
		//read from microphone, save, have handle of username,
		//add path/username to list[0]
		//then loop through all the files at the other path.
		identificationFrame = new JFrame();
		identificationFrame.setTitle("Voice identification");
		identificationFrame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
		identificationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		identificationFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				NCore.shutdown();
			}
		});

		identificationHomePanel = new JPanel( new GridLayout(3, 1));

		identificationTextToReadLabel = new JLabel("Text to read will appear here.");
		identificationTextToReadLabel.setHorizontalAlignment(SwingConstants.CENTER);
		identificationHomePanel.add(identificationTextToReadLabel);

		identificationLabel = new JLabel("Status");
		identificationLabel.setHorizontalAlignment(SwingConstants.CENTER);


		identifyBtn = new JButton("Identify");

		identificationHomePanel.add(identifyBtn);
		identificationHomePanel.add(identificationLabel);

		identificationFrame.add(identificationHomePanel);
		identificationFrame.pack();
		identificationFrame.setLocationRelativeTo(null);
		identificationFrame.setVisible(true);

		identifyBtn.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent actionEvent ){
				//get text from DB and display. also need to match the user. so there should be someway to know who the user is.
				identificationTextToReadLabel.setText("sample text here. My name is [name]");


				//need to generate sound file of test subject using the EnrollVoiceFromMicrophone class.
				// then save this file in a testing folder, then pass as to the identification method.
				//also, need to loop through the files in the registrations/sounds folder and pass them here.
				EnrollVoiceFromMicrophone enrollVoiceFromMicrophone = new EnrollVoiceFromMicrophone();
				String fileName = "TestCase"+new Date().getTime();//new SimpleDateFormat("dd-MM-yyyy").format(new Date());

				if(enrollVoiceFromMicrophone.start(fileName, "data/testing/")){
					identificationLabel.setText("Template Extracted");

					//loop through all the files in the directory/database//later use db for this.
					//put everything in the array.
					File registrationSoundsFolder = new File("data/registration/soundFiles/");
					String[] files = new String[registrationSoundsFolder.listFiles().length+1];
					files[0] = "data/testing/SoundFiles/"+fileName;
					int i=1;
					for (final File fileEntry : registrationSoundsFolder.listFiles()) {
							files[i] = "data/registration/soundFiles/" + fileEntry.getName();
							i++;
					}
					IdentifyVoiceTemplate identifyVoiceTemplate = new IdentifyVoiceTemplate();
					//String[] sounds = { "data/registration/soundFiles/Tushar1Oct30", "data/registration/soundFiles/Oct30Test1", "data/registration/soundFiles/Tushar2Oct30"};
					//String[] templates = { "data/registration/templateFiles/Tushar1Oct30", "data/registration/templateFiles/Oct30Test1", "data/registration/templateFiles/Tushar2Oct30"};
					identifyVoiceTemplate.identify(files);
				} else {
					identificationLabel.setText("Not enough features");
				}
			}
		});

	}

}