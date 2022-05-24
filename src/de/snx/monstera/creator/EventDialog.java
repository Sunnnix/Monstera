package de.snx.monstera.creator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.snx.monstera.map.event.Event;

@SuppressWarnings("serial")
public class EventDialog extends JDialog {

	public EventDialog(CreatorWindow creator, Event event, Window owner, Consumer<Event> onApply) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		super(owner, (String) event.getClass().getField("REGISTRY_NAME").get(null), ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());
		Pair<JPanel, Runnable> fromEvent = event.getEditorDialog(creator);
		add(fromEvent.object1, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton add = new JButton("Apply");
		add.addActionListener(e -> {
			if (fromEvent.object2 != null)
				fromEvent.object2.run();
			if (onApply != null)
				onApply.accept(event);
			dispose();
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> dispose());
		buttons.add(add);
		buttons.add(cancel);
		add(buttons, BorderLayout.SOUTH);
		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

}
