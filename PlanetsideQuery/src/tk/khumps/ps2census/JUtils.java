package tk.khumps.ps2census;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

// Holds utility methods that are used to create JMenuItems, JButtons and JTextAreas that simplify code whenever the Display class needs to create one of these objects to display on the user interface.

public class JUtils {
	public static Dimension button = new Dimension(300, 200);
	public static Dimension menuItem = new Dimension(100, 50);

	// Creates a new JMenuItem object given a Dimension for its size, String for
	// its label, String for its ActionCommand, an ActionListener to attach to
	// it and a JMenuBar to attach the JMenuItem to.

	public static JMenuItem newMenuItem(Dimension size, String text, String command, ActionListener listener,
			JMenuBar bar) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(command);
		menuItem.setPreferredSize(size);
		menuItem.setMaximumSize(size);
		bar.add(menuItem);
		return menuItem;
	}

	// Creates a new JButton object given a Dimension for its size, a String for
	// its label, a String for its ActionCommand and an ActionListener to attach
	// to it.

	public static JButton newButton(Dimension size, String text, String command, ActionListener listener,
			JComponent location) {
		JButton button = new JButton(text);
		button.addActionListener(listener);
		button.setActionCommand(command);
		button.setPreferredSize(size);
		if (location != null)
			location.add(button);
		return button;
	}

	// Creates a new JTextArea object given a String for the text to display,
	// while also setting various appearance characteristics such as opacity and
	// wrap style.

	public static JTextArea newArea(String text) {
		JTextArea area = new JTextArea(text);
		area.setEditable(false);
		area.setCursor(null);
		area.setOpaque(false);
		area.setFocusable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		return area;
	}
}