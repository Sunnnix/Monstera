package de.snx.monstera.util;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class WindowUtils {

	public static JPanel createRow(String name, JComponent component, int w1, int w2) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(name);
		label.setPreferredSize(new Dimension(w1, 20));
		component.setPreferredSize(new Dimension(w2, 20));
		panel.add(label);
		panel.add(component);
		return panel;
	}

	public static JSeparator space(int width, int height) {
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(
				new Dimension(width <= 0 ? separator.getWidth() : width, height <= 0 ? separator.getHeight() : height));
		return separator;
	}

}
