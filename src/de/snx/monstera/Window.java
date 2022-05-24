package de.snx.monstera;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.function.Consumer;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends Canvas {

	private Game game;
	private JFrame frame;

	private boolean createBuffer = true;

	public Window(Game game) {
		this.game = game;
		setSize(Game.DEFAULT_SIZE);
		setFrame();
	}

	private void setFrame() {
		frame = new JFrame(Game.NAME);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				game.exit();
			}
		});
		frame.addKeyListener(new KListener());
	}

	public void setVisible() {
		frame.setVisible(true);
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void render(Consumer<Graphics2D> render) {
		if (createBuffer) {
			createBuffer = false;
			createBufferStrategy(2);
		}
		BufferStrategy bs = getBufferStrategy();
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.clearRect(0, 0, getWidth(), getHeight());
		render.accept(g);
		bs.show();
	}

	private class KListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent k) {
			Keys.onKeyEvent(k, Keys.ACTION_PRESS);
		}

		@Override
		public void keyReleased(KeyEvent k) {
			Keys.onKeyEvent(k, Keys.ACTION_RELEASE);
		}

	}

}
