package de.snx.monsteracreator.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CustomizerWin extends JDialog {

	private Window win;

	public CustomizerWin(Window win) {
		super(win, "Customizer", ModalityType.APPLICATION_MODAL);
		this.win = win;
		setLayout(new GridLayout(0, 1));
		init();
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void init() {
		add(picker("Editor Background", () -> win.map.getBackground(), c -> win.map.setBackground(c)));
//		add(picker("Grid", () -> win.map.getC_grid(), c -> {
//			win.map.setC_grid(c);
//			win.map.repaint();
//		}));
//		add(picker("Blocking", () -> win.map.getC_blocking(), c -> {
//			win.map.setC_blocking(c);
//			win.map.repaint();
//		}));
//		add(picker("Numbers", () -> win.map.getC_numbers(), c -> {
//			win.map.setC_numbers(c);
//			win.map.repaint();
//		}));
	}

	private JPanel picker(String text, Supplier<Color> defaultColor, Consumer<Color> color) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(text), BorderLayout.WEST);
		JButton button = new JButton("");
		button.setBackground(defaultColor.get());
		panel.add(button);
		button.addActionListener(action -> {
			Color c = JColorChooser.showDialog(CustomizerWin.this, text, defaultColor.get());
			if (c == null)
				c = defaultColor.get();
			button.setBackground(c);
			color.accept(c);
		});
		return panel;
	}

}
