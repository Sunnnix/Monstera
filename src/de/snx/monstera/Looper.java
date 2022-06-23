package de.snx.monstera;

import java.util.function.Consumer;

/**
 * The looper is responsible for the gameloop.<br>
 * It calculates the time after which it performs the updates.
 * 
 * @author Sunnix
 *
 */
public class Looper extends Thread {

	private boolean running;
	private int ticks;
	private Consumer<Integer> run;

	/**
	 * Creates a new Looper
	 * 
	 * @param ticks how often this looper calls the update function per second
	 * @param run   the function to be updated, with transfer of the ticks/s
	 */
	public Looper(int ticks, Consumer<Integer> run) {
		super("looper");
		this.ticks = ticks;
		this.run = run;
	}

	@Override
	public void run() {
		running = true;
		long latest = System.nanoTime();
		long next = latest;
		double tins = 1000000000 / ticks;
		while (running) {
			next = System.nanoTime();
			if (next >= latest + tins) {
				run.accept((int) Math.round(tins / (next - latest) * ticks));
				latest = next;
			}
		}
		System.out.println("Thread exit properly");
	}

	public void exit() {
		running = false;
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
