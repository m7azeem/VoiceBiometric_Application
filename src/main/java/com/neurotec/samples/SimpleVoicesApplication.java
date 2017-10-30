package com.neurotec.samples;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.neurotec.lang.NCore;
import com.neurotec.samples.util.Utils;

public final class SimpleVoicesApplication {

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

		//clean gui
		final SimpleVoicesApplication simpleVoicesApplication = new SimpleVoicesApplication();
		simpleVoicesApplication.recorderFrame = new JFrame();
		simpleVoicesApplication.recorderFrame.setTitle("Voice registration");
		simpleVoicesApplication.recorderFrame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
		simpleVoicesApplication.recorderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simpleVoicesApplication.recorderFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				NCore.shutdown();
			}
		});

		simpleVoicesApplication.recorderHomePanel = new JPanel( new GridLayout(4, 1));

		simpleVoicesApplication.userRegistrationLabel = new JLabel("User registration");
		simpleVoicesApplication.userRegistrationLabel.setHorizontalAlignment(SwingConstants.CENTER);;
		simpleVoicesApplication.recorderHomePanel.add(simpleVoicesApplication.userRegistrationLabel);

		simpleVoicesApplication.namePanel = new JPanel( new GridLayout(1, 2));
		simpleVoicesApplication.namePanel.add( new JLabel("Name: "));
		simpleVoicesApplication.nameField = new JTextField();
		//nameField.setSize(10, 3);
		simpleVoicesApplication.namePanel.add(simpleVoicesApplication.nameField);
		simpleVoicesApplication.recorderHomePanel.add(simpleVoicesApplication.namePanel);

		simpleVoicesApplication.startBtn = new JButton("Start");
		simpleVoicesApplication.textToReadLabel = new JLabel("After clicking Start. Read the generated text here.");

		simpleVoicesApplication.recorderHomePanel.add(simpleVoicesApplication.startBtn);
		simpleVoicesApplication.recorderHomePanel.add(simpleVoicesApplication.textToReadLabel);

		simpleVoicesApplication.recorderFrame.add(simpleVoicesApplication.recorderHomePanel);
		simpleVoicesApplication.recorderFrame.pack();
		simpleVoicesApplication.recorderFrame.setLocationRelativeTo(null);
		simpleVoicesApplication.recorderFrame.setVisible(true);

		simpleVoicesApplication.startBtn.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent actionEvent ){
				//connect method to start recording, extracting template, saving.
				simpleVoicesApplication.textToReadLabel.setText(simpleVoicesApplication.nameField.getText());
				new WriteXMLFile().doSometing();
			}
		});
	}
}