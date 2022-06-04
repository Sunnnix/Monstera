package de.snx.monstera.creator;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import de.snx.monstera.global_data.Registry;
import de.snx.monstera.map.Entity;
import de.snx.monstera.map.event.Event;

@SuppressWarnings("serial")
public class EventPicker extends JDialog {

	private CreatorWindow win;
	private Consumer<Event> onApply;

	public EventPicker(CreatorWindow win, Entity entity, EntityEditor parent, Consumer<Event> onApply) {
		super(parent, "Events", ModalityType.APPLICATION_MODAL);
		this.win = win;
		this.onApply = onApply;
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		initEventButtons();
		setResizable(true);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void initEventButtons() {
		List<String> events = Registry.getAllEvents();
		for (int i = 0; i < events.size(); i++) {
			final String key = events.get(i);
			JButton b = new JButton(key);
			b.addActionListener(a -> {
				openEventDialog(Registry.createEvent(key));
			});
			add(b);
		}
	}

	private void openEventDialog(Event event) {
		try {
			new EventDialog(win, event, this, onApply);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		dispose();
	}

}
