package net.buddat.ludumdare.ld31;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Color;

public class ColorDirector {

	private static final Map<Integer, Color> PRIMARY_COLOR_MAP;
	private static final Map<Integer, Color> SECONDARY_COLOR_MAP;
	
	private static final Color BACKGROUND_COLOR = Color.black;
	
	private static final Color TEXT_COLOR_PRIMARY, TEXT_COLOR_SECONDARY;

	public static enum ColorType { PLAYER, WALL, OBJECT, PROJECTILE, SLOW_WALL };

	private static int currentWallColor = 0, currentPlayerColor = 1, currentSlowWallColor = 0;

	private static int beatCount = 0;

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

		TEXT_COLOR_PRIMARY = Color.decode("#A4A4A4");
		TEXT_COLOR_SECONDARY = Color.decode("#505050");

		colorRandom = new Random(System.currentTimeMillis());
	}

	public static void update(int delta) {
		beatCount++;

		if (beatCount % 4 == 0)
			getNextPrimary(ColorType.PLAYER);
		if (beatCount % 4 == 0) {
			getRandomPrimary(ColorType.WALL);

			if (colorRandom.nextInt(4) == 0)
				setColor(ColorType.SLOW_WALL, getColor(ColorType.PLAYER));
			else
				setColor(ColorType.SLOW_WALL, getColor(ColorType.WALL));
		}
	}

	private static Color getRandomPrimary(ColorType c) {
		int nextColor = getColor(c);
		while (nextColor == getColor(c))
			nextColor = colorRandom.nextInt(PRIMARY_COLOR_MAP.size());

		setColor(c, nextColor);

		return getCurrentPrimary(c);
	}
	
	private static Color getNextPrimary(ColorType c) {
		setColor(c, getColor(c) + 1);
		Color col = getCurrentPrimary(c);
		if (col == null) {
			setColor(c, 0);
			col = getCurrentPrimary(c);
		}

		return col;
	}

	public static Color getCurrentPrimary(ColorType c) {
		return PRIMARY_COLOR_MAP.get(getColor(c));
	}

	public static Color getCurrentSecondary(ColorType c) {
		if (c == ColorType.WALL || c == ColorType.SLOW_WALL)
			if (beatCount % 2 == 0)
				return getAltSecondary();

		return SECONDARY_COLOR_MAP.get(getColor(c));
	}

	public static Color getPrimary(int index) {
		return PRIMARY_COLOR_MAP.get(index);
	}

	public static Color getSecondary(int index) {
		return SECONDARY_COLOR_MAP.get(index);
	}

	public static Color getAltSecondary() {
		return getBackgroundColor();
	}

	public static Color getBackgroundColor() {
		return BACKGROUND_COLOR;
	}
	
	public static Color getTextPrimary() {
		return TEXT_COLOR_PRIMARY;
	}

	public static Color getTextSecondary() {
		return TEXT_COLOR_SECONDARY;
	}

	private static void setColor(ColorType c, int newColor) {
		switch (c) {
			case PLAYER:
				currentPlayerColor = newColor;
				break;
			case WALL:
				currentWallColor = newColor;
				break;
			case SLOW_WALL:
				currentSlowWallColor = newColor;
				break;
			case OBJECT:
				break;
			case PROJECTILE:
				break;
			default:
				break;
		}
	}
	
	private static int getColor(ColorType c) {
		switch (c) {
			case PLAYER:
				return currentPlayerColor;
			case WALL:
				return currentWallColor;
			case SLOW_WALL:
				return currentSlowWallColor;
			case OBJECT:
				break;
			case PROJECTILE:
				break;
			default:
				break;
		}

		return 0;
	}
}
