package net.buddat.ludumdare.ld31.render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Visual damage effect on the player, centred at x, y
 */
public class PlayerDamageEffect extends PlayerEffect {
	private static final Color COLOUR = Color.white;
	/**
	 * Duration of the effect, in milliseconds
	 */
	private static final int DURATION = 2000;

	private static final int RADIUS = 10;

	private final float scale;

	public PlayerDamageEffect(int x, int y, float scale) {
		super(x, y);
		this.scale = scale;
	}

	@Override
	public void render(Graphics g) {
		if (!hasExpired()) {
			g.setColor(COLOUR);
			int radius = (int) Math.round((DURATION - getDurationCompleted())
					* RADIUS / (double) DURATION);
			g.drawOval(getX() - radius * scale, getY() - radius, radius * scale
					* 2, radius * 2, 20);
		}
	}

	@Override
	public int getDuration() {
		return DURATION;
	}
}
