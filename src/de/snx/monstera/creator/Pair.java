package de.snx.monstera.creator;

public class Pair<O1, O2> {

	public final O1 object1;
	public final O2 object2;

	public Pair(O1 object1, O2 object2) {
		this.object1 = object1;
		this.object2 = object2;
	}

	@Override
	public String toString() {
		return "Pair[" + object1 + ", " + object2 + "]";
	}

}
