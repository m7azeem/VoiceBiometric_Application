package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.neurotec.lang.NCore;
import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.Utils;

public final class SimpleVoicesApplication {

	// ===========================================================
	// Private constructor
	// ===========================================================

	private SimpleVoicesApplication() {
	}

	// ===========================================================
	// Main method
	// ===========================================================

	public static void main(String[] args) {
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
	}

}