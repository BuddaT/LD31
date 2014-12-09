package net.buddat.ludumdare.ld31.render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerScoreEffect extends PlayerEffect {

	private final Color COLOUR = new Color(Color.white);

	private static final int DURATION = 150;

	private static final int RADIUS = 10;

	private final float scale;

	public PlayerScoreEffect(int x, int y, float scale) {
		super(x, y);
		this.scale = scale;
	}

	@Override
	public void render(Graphics g) {
		if (!hasExpired()) {
			COLOUR.a = 1.0f / DURATION * (DURATION - getDurationCompleted());
			g.setColor(COLOUR);
			int radius = (int) Math.round((DURATION - getDurationCompleted())
					* RADIUS / (double) DURATION);
			g.fillOval(getX() - radius * scale, getY() - radius, radius * scale
					* 2, radius * 2, 20);
		}
	}

	@Override
	public int getDuration() {
		return DURATION;
	}

}
