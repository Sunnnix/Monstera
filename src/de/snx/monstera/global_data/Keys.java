package de.snx.monstera.global_data;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Listing of all used keys as variables
 * 
 * @author Sunnix
 *
 */
public class Keys {

	public static final int ACTION_PRESS = 0;
	public static final int ACTION_RELEASE = 1;

	private static final ArrayList<Key> register = new ArrayList<>();

	// UTIL
	public static final Key DEBUG = new Key(KeyEvent.VK_F3);

	// MOVEMENT
	public static final Key UP = new Key(KeyEvent.VK_W, KeyEvent.VK_UP);
	public static final Key DOWN = new Key(KeyEvent.VK_S, KeyEvent.VK_DOWN);
	public static final Key LEFT = new Key(KeyEvent.VK_A, KeyEvent.VK_LEFT);
	public static final Key RIGHT = new Key(KeyEvent.VK_D, KeyEvent.VK_RIGHT);

	// INTERACTION
	public static final Key CONFIRM = new Key(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE);
	public static final Key CANCEL = new Key(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_ESCAPE);

	public static final void onKeyEvent(KeyEvent k, int action) {
		for (Key key : register) {
			if (!key.is(k.getKeyCode()))
				continue;
			if (action == ACTION_PRESS)
				key.press();
			else if (action == ACTION_RELEASE)
				key.release();
			return;
		}
	}

	public static final void update() {
		register.forEach(k -> k.update());
	}

	public static class Key {

		public final int[] KEYCODE;
		private int firstChange, latestChange;
		private boolean isPressed;

		public Key(int... keycode) {
			this.KEYCODE = keycode;
			register.add(this);
		}

		private boolean is(int keyCode) {
			for (int code : KEYCODE)
				if (code == keyCode)
					return true;
			return false;
		}

		private void press() {
			if (!isPressed) {
				firstChange = 0;
				isPressed = true;
			} else
				latestChange = 0;
		}

		private void release() {
			if (isPressed) {
				firstChange = 0;
				isPressed = false;
			} else
				latestChange = 0;
		}

		private void update() {
			firstChange++;
			latestChange++;
		}

		/**
		 * Is only one time true when user pushed down the key
		 */
		public boolean isPressed() {
			return isPressed && firstChange == 0;
		}

		/**
		 * Is true if the user is hold down the key and the repress is called
		 */
		public boolean isPressedAlt() {
			return isPressed && latestChange == 0;
		}

		public boolean isReleased() {
			return !isPressed && firstChange == 0;
		}

		/**
		 * This should never happen
		 */
		public boolean isReleasedAlt() {
			return !isPressed && latestChange == 0;
		}

		public boolean isHold() {
			return isPressed;
		}

	}

}
