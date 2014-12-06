package net.buddat.ludumdare.ld31.render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Visual damage effect on the player, centred at x, y
 */
public class PlayerDamageEffect extends PlayerEffect {
	private static final Color COLOUR = Color.red;
	/**
	 * Duration of the effect, in milliseconds
	 */
	private static final int DURATION = 200;

	private static final int RADIUS = 10;

	public PlayerDamageEffect(int x, int y) {
		super(x, y);
	}

	public void render(Graphics g) {
		if (!hasExpired()) {
			g.setColor(COLOUR);
			int radius = (int) Math.round((DURATION - getDurationCompleted()) * RADIUS / (double) DURATION);
			g.drawOval(getX() - radius, getY() - radius, radius * 2, radius * 2, 20);
		}
	}

	public int getDuration() {
		return DURATION;
	}
}
