package de.snx.monsteracreator;

import java.util.ArrayList;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.mapdata.Map;

public class MemoryStacks {

	private static MemoryStack currentStack;
	private static ArrayList<MemoryStack> undoStacks = new ArrayList<>(), redoStacks = new ArrayList<>();

	public static void addStack() {
		if (currentStack != null) {
			undoStacks.add(0, currentStack);
			redoStacks.clear();
		}
		Map map = ProjectHandler.getMaps().getSelected();
		if (map != null)
			currentStack = new MemoryStack(map);
	}

	public static void loadUndoStack() {
		redoStacks.add(0, currentStack);
		currentStack = undoStacks.get(0);
		undoStacks.remove(0);
		currentStack.load();
	}

	public static void loadRedoStack() {
		undoStacks.add(0, currentStack);
		currentStack = redoStacks.get(0);
		redoStacks.remove(0);
		currentStack.load();
	}

	public static void clearStacks() {
		undoStacks.clear();
		redoStacks.clear();
		currentStack = null;
		addStack();
	}

	public static boolean hasUndo() {
		return !undoStacks.isEmpty();
	}

	public static boolean hasRedo() {
		return !redoStacks.isEmpty();
	}

}
