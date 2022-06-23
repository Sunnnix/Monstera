package de.snx.monstera.event;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.snx.monstera.data.mapdata.Entity;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.global_data.Keys;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.monstera.util.Pair;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;

public class TextEvent extends Event {

	public static final String REGISTRY_NAME = "TextEvent";

	private String[] text = new String[0];
	private int delay, delayStack = 3, animationTimer, animationMax = 15;

	private StringBuilder builder = new StringBuilder();
	private int currentChar, currentRow;
	private boolean waitForInput, animateUp;

	private boolean translateParams;

	public TextEvent(PSFFileIO file) throws Exception {
		super(file);
		setText(file.readStringArray("text"));
	}

	public TextEvent() {
	}

	public void setText(String... text) {
		if (text == null || text.length == 0)
			this.text = new String[] { "" };
		else
			this.text = text;
	}

	@Override
	public boolean blockAction() {
		return true;
	}

	@Override
	public void keyEvents(WorldState world, Map map) {
		if (Keys.CONFIRM.isHold() || Keys.CANCEL.isHold())
			delay += 2;
	}

	@Override
	public void update(Map map, WorldState world, GameStateManager gsm) {
		if (!translateParams)
			translateParams(map);
		if (waitForInput)
			return;
		if (animateUp) {
			if (animationTimer >= animationMax) {
				animationTimer = 0;
				animateUp = false;
				builder.setLength(0);
				currentChar = 0;
				currentRow++;
			} else
				animationTimer++;
		} else if (delay >= delayStack) {
			if (currentRow > text.length - 1) {
				waitForInput = true;
				return;
			}
			delay = 0;
			if (currentChar < text[currentRow].length()) {
				builder.append(text[currentRow].charAt(currentChar));
				currentChar++;
			} else {
				if (currentRow > 2)
					waitForInput = true;
				else {
					builder.setLength(0);
					currentChar = 0;
					currentRow++;
				}
			}
		}
		delay++;
	}

	@Override
	public void render(Graphics2D g, WorldState world, Map map) {
		g.setColor(Color.WHITE);
		g.fillRect(0, world.screenHeight() - 160,  world.screenWidth(), 160);
		g.setFont(new Font("Arial", Font.BOLD, 32));
		int x = 12;
		int y =  world.screenHeight() - 160 + 38;
		int ySpace = 35;
		int pos = 0;
		double animateProgress = (double) animationTimer / animationMax;
		g.setColor(animateUp ? new Color(0, 0, 0, (int) (255 - 255 * animateProgress)) : Color.BLACK);
		if (currentRow - 3 >= 0) {
			g.drawString(text[currentRow - 3], x, y + ySpace * pos);
			pos++;
		}
		g.setColor(Color.BLACK);
		if (currentRow - 2 >= 0) {
			g.drawString(text[currentRow - 2], x, y + ySpace * pos - (int) (animateProgress * ySpace));
			pos++;
		}
		if (currentRow - 1 >= 0) {
			g.drawString(text[currentRow - 1], x, y + ySpace * pos - (int) (animateProgress * ySpace));
			pos++;
		}
		g.drawString(builder.toString(), x, y + ySpace * pos - (int) (animateProgress * ySpace));
	}

	public String getEventInfo() {
		return text[0];
	}

	@Override
	public Pair<JPanel, Runnable> getEditorDialog(Window win) {
		JPanel panel = new JPanel();
		JTextArea area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		scroll.setPreferredSize(new Dimension(323, 67));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll);
		// Load
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < text.length; i++) {
			b.append(text[i]);
			if (i < text.length - 1)
				b.append("\n");
		}
		area.setText(b.toString());
		// Save
		Runnable onApply = () -> {
			text = area.getText().split("\n");
		};
		return new Pair<JPanel, Runnable>(panel, onApply);
	}

	@Override
	public void onSave(PSFFileIO file) throws Exception {
		file.write("text", text);
	}

	@Override
	public void interact(Map map) {
		if (waitForInput) {
			if (currentRow >= text.length - 1) {
				builder.setLength(0);
				finished = true;
			} else {
				animateUp = true;
				waitForInput = false;
			}
		}
	}

	private void translateParams(Map map) {
		String numbers = "0123456789";
		StringBuilder text = new StringBuilder();
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < this.text.length; i++) {
			String string = this.text[i];
			text.append(string);
			int index = -1;
			while ((index = string.indexOf('@', index + 1)) != -1) {
				try {
					int idChars = 0;
					b.append(string.charAt(index - 1));
					b.append(string.charAt(index));
					for (int j = index + 1; j < this.text[i].length(); j++) {
						char n = string.charAt(j);
						if (n != ' ' && numbers.contains(Character.toString(n))) {
							b.append(n);
							idChars++;
						} else
							break;
					}
					text.replace(index - 1, index + idChars, getConversion(map, b.toString()));
				} catch (IndexOutOfBoundsException e) {
				}
				b.setLength(0);
			}
			this.text[i] = text.toString();
			text.setLength(0);
		}
		translateParams = true;
	}

	private String getConversion(Map map, String parm) {
		try {
			char translator = parm.charAt(0);
			switch (translator) {
			case 'e':
				Entity e = map.getValue(Integer.parseInt(parm.substring(2)));
				if (e != null)
					return e.name;
			default:
				break;
			}
		} catch (Exception e) {
		}
		return parm;
	}

}
