package de.snx.monstera;

import java.awt.Dimension;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.snx.monstera.state.BattleState;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.IntroState;
import de.snx.monstera.state.MenuState;
import de.snx.monstera.state.WorldState;

public class Game {

	public static final String NAME = "Pokemon";
	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	public static final double SCALE = 2;
	public static final int TILESIZE = (int) (24 * SCALE);
	public static final int TICKS = 60;

	private Window window;
	private GameStateManager gsm;
	private Looper looper;

	public Game() {
		preInit();
		init();
	}

	private void preInit() {
		System.setOut(new PrintStream(System.out) {
			@Override
			public void println(String s) {
				String time = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
				String threadName = Thread.currentThread().getName();
				super.println("[" + time + "](" + threadName + ")[INFO]: " + s);
			}

			@Override
			public void println(Object o) {
				println(String.valueOf(o));
			}

			@Override
			public void println(char c) {
				println(String.valueOf(c));
			}

			@Override
			public void println(int i) {
				println(String.valueOf(i));
			}

			@Override
			public void println(long l) {
				println(String.valueOf(l));
			}

			@Override
			public void println(float f) {
				println(String.valueOf(f));
			}

			@Override
			public void println(double d) {
				println(String.valueOf(d));
			}

		});
		System.setErr(new PrintStream(System.err) {
			@Override
			public void println(String s) {
				String time = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
				String threadName = Thread.currentThread().getName();
				super.println("[" + time + "](" + threadName + ")[ERROR]: " + s);
			}

			@Override
			public void println(Object o) {
				println(String.valueOf(o));
			}

			@Override
			public void println(char c) {
				println(String.valueOf(c));
			}

			@Override
			public void println(int i) {
				println(String.valueOf(i));
			}

			@Override
			public void println(long l) {
				println(String.valueOf(l));
			}

			@Override
			public void println(float f) {
				println(String.valueOf(f));
			}

			@Override
			public void println(double d) {
				println(String.valueOf(d));
			}

		});
		loadRes();
		window = new Window(this);
		gsm = new GameStateManager(this);
	}

	private void init() {
		window.setVisible();
		gsm.registerStates(0, new IntroState(0), new MenuState(1), new WorldState(2), new BattleState(3));
		looper = new Looper(TICKS, i -> {
			update(i);
			render(i);
		});
		looper.start();
	}

	private void loadRes() {
		registerEvents();
	}

	private void registerEvents() {
		Main.registerAll();
	}

	private void update(int ticks) {
		gsm.update(ticks);
	}

	private void render(int fps) {
		window.render(g -> gsm.render(g, fps));
	}

	public int getScreenWidth() {
		return window.getWidth();
	}

	public int getScreenHeight() {
		return window.getHeight();
	}

	public void exit() {
		if (looper != null)
			looper.exit();
	}

}
