package net.buddat.ludumdare.ld31.render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Visual damage effect on the player, centred at x, y
 */
public class PlayerDamageEffect {
	private static final Color COLOUR = Color.red;
	/**
	 * Duration of the effect, in milliseconds
	 */
	private static final int DURATION = 200;

	private static final int RADIUS = 10;

	private int x;
	private int y;
	private int durationCompleted = 0;
	public PlayerDamageEffect(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void update(int delta) {
		durationCompleted += delta;
	}

	public void render(Graphics g) {
		if (!hasExpired()) {
			g.setColor(COLOUR);
			int radius = (int) Math.round((DURATION - durationCompleted) * RADIUS / (double) DURATION);
			g.drawOval(x - radius, y - radius, radius * 2, radius * 2, 20);
		}
	}

	public boolean hasExpired() {
		return durationCompleted >= DURATION;
	}
}
