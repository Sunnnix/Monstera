package de.snx.monstera.creator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.snx.monstera.Main;

@SuppressWarnings("serial")
public class CreatorWindow extends JFrame {

	public static final String TITLE = "Pokemon Creator";
	
	public MenuBar menu;
	public ToolBar tools;
	public TilesetPanel tileset;
	public MapViewPanel map;
	public InfoPanel info;

	public CreatorWindow() {
		super(TITLE);
		setLayout(new BorderLayout());
		initPanel();
		initFrame();
		loadRes();
		map.requestFocusInWindow();
	}

	private void initPanel() {
		JPanel bar = new JPanel(new BorderLayout());
		menu = new MenuBar(this);
		bar.add(menu, BorderLayout.NORTH);
		tools = new ToolBar(this);
		bar.add(tools, BorderLayout.SOUTH);
		add(bar, BorderLayout.NORTH);
		tileset = new TilesetPanel(this);
		add(tileset, BorderLayout.WEST);
		map = new MapViewPanel(this);
		JScrollPane scroll = new JScrollPane(map);
		scroll.setPreferredSize(new Dimension(900, 0));
		add(scroll, BorderLayout.CENTER);
		info = new InfoPanel(this);
		add(info, BorderLayout.SOUTH);
	}

	private void initFrame() {
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void loadRes() {
		Main.registerAll();
	}

}
