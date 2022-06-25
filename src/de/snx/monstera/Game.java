package de.snx.monstera;

import java.awt.Dimension;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.state.BattleState;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.IntroState;
import de.snx.monstera.state.MenuState;
import de.snx.monstera.state.WorldState;
import lombok.Setter;

/**
 * Prepares everything for the game and starts the GameWindow. All resources
 * will be loaded after a game is selected.
 * 
 * @author Sunnix
 *
 */
public class Game {

	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	public static final int TICKS = 60;

	private Window window;
	private GameStateManager gsm;
	private Looper looper;

	@Setter
	private boolean lowFPSMode; // halves the ticks per second and runs updates twice to gain the same effect of
								// the normal game (for low performance)

	public Game(String[] args0) {
		List<String> args = Arrays.asList(args0);
		String[] projectData = null;
		if (args.contains("low_fps"))
			lowFPSMode = true;
		int index = args.indexOf("start from creator");
		if (index >= 0 && args.size() - 1 >= index + 2) {
			projectData = new String[] { args.get(index + 1), args.get(index + 2) };
		}
		preInit();
		init(projectData);
	}

	/**
	 * Prepare game independent data
	 */
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
		window = new Window(this);
		gsm = new GameStateManager(this);
	}

	/**
	 * Load and start game
	 * 
	 * @param projectArgs the project path and name, if the user starts this game
	 *                    from the creator, otherwise null
	 */
	private void init(String[] projectArgs) {
		window.setVisible();
		if (!ProjectHandler.loadProject(window.getFrame(), projectArgs)) {
			JOptionPane.showMessageDialog(window, "Error loading data!");
			System.exit(-1);
		}
		window.getFrame().setTitle(ProjectHandler.getProject().getName());
		gsm.registerStates(0, new IntroState(0), new MenuState(1), new WorldState(2), new BattleState(3));
		looper = new Looper(lowFPSMode ? TICKS / 2 : TICKS, i -> {
			update(i);
			render(i);
		});
		looper.start();
	}

	private void update(int ticks) {
		gsm.update(ticks);
		if (lowFPSMode)
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
