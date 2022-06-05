package de.snx.monstera.battle.action;

import de.snx.monstera.global_data.Keys;
import de.snx.monstera.state.BattleState;

public class ActionShowText extends BattleAction {

	private StringBuilder b = new StringBuilder();
	private String[] text;

	private int cChar, cRow;
	private int timer, delay = 3;

	private boolean waitForInput;

	public ActionShowText(BattleState state, String... text) {
		super(state);
		this.text = text;
	}
	
	@Override
	public void prepare() {
	}

	@Override
	public void keys() {
		if (waitForInput) {
			if (Keys.CONFIRM.isPressed())
				finished = true;

		} else if (Keys.CONFIRM.isHold() || Keys.CANCEL.isHold())
			timer += 2;

	}

	@Override
	public void update() {
		if (!waitForInput) {
			if (timer >= delay) {
				timer = 0;
				if (cChar == text[cRow].length()) {
					cChar = 0;
					if (cRow + 1 == text.length)
						waitForInput = true;
					else {
						cRow++;
						b.setLength(0);
					}
				} else {
					b.append(text[cRow].charAt(cChar));
					cChar++;
				}
			}
			timer++;
		}
		String[] nT = new String[cRow + 1];
		for (int i = 0; i < cRow; i++)
			nT[i] = text[i];
		nT[cRow] = b.toString();
		state.setText(nT);
	}

}
