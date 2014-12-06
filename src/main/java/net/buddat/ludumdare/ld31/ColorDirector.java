package net.buddat.ludumdare.ld31;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Color;

public class ColorDirector {

	private static final Map<Integer, Color> PRIMARY_COLOR_MAP;
	private static final Map<Integer, Color> SECONDARY_COLOR_MAP;
	
	private static int currentColor = 0;

	private static Random colorRandom;

	static {
		HashMap<Integer, Color> pcm = new HashMap<Integer, Color>();
		HashMap<Integer, Color> scm = new HashMap<Integer, Color>();

		pcm.put(0, Color.decode("#E51919"));
		scm.put(0, Color.decode("#F17777"));

		pcm.put(1, Color.decode("#E58D19"));
		scm.put(1, Color.decode("#F1BD77"));

		pcm.put(2, Color.decode("#E5C219"));
		scm.put(2, Color.decode("#F1DC77"));

		pcm.put(3, Color.decode("#BBDD18"));
		scm.put(3, Color.decode("#D9ED75"));

		pcm.put(4, Color.decode("#14B714"));
		scm.put(4, Color.decode("#6CDA6C"));

		pcm.put(5, Color.decode("#185E93"));
		scm.put(5, Color.decode("#699EC7"));

		pcm.put(6, Color.decode("#3E219D"));
		scm.put(6, Color.decode("#8771CC"));

		pcm.put(7, Color.decode("#951095"));
		scm.put(7, Color.decode("#C863C8"));

		PRIMARY_COLOR_MAP = Collections.unmodifiableMap(pcm);
		SECONDARY_COLOR_MAP = Collections.unmodifiableMap(scm);

		colorRandom = new Random(System.currentTimeMillis());
	}

	public static Color getRandomPrimary() {
		int nextColor = currentColor;
		while (nextColor == currentColor)
			nextColor = colorRandom.nextInt(PRIMARY_COLOR_MAP.size());

		currentColor = nextColor;

		return getCurrentPrimary();
	}

	public static Color getNextPrimary() {
		currentColor++;
		Color c = getCurrentPrimary();
		if (c == null) {
			currentColor = 0;
			c = getCurrentPrimary();
		}
		
		return c;
	}

	public static Color getCurrentPrimary() {
		return PRIMARY_COLOR_MAP.get(currentColor);
	}

	public static Color getCurrentSecondary() {
		return SECONDARY_COLOR_MAP.get(currentColor);
	}

	public static Color getPrimary(int index) {
		return PRIMARY_COLOR_MAP.get(index);
	}

	public static Color getSecondary(int index) {
		return SECONDARY_COLOR_MAP.get(index);
	}
}
