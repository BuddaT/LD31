package net.buddat.ludumdare.ld31.render;

import net.buddat.ludumdare.ld31.world.Level;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

/**
 * Basic projectile for starters
 */
public class Projectile {
	private static final Color DEFAULT_COLOR = Color.yellow;
	// Speed in distance/second
	private static final float DEFAULT_SPEED = 1;
	private static final int RADIUS = 2;
	private static final int SEGMENTS = 6;
	private static final float LIMIT = 60;

	private final Vector2f movement;
	private final Level level;
	private Vector2f position;
	private float distanceLeft = LIMIT;
	private boolean hasExpired = false;

	public Projectile(int sourceX, int sourceY, Vector2f direction, Level level) {
		position = new Vector2f(sourceX, sourceY);
		movement = new Vector2f(direction.getTheta()).scale(DEFAULT_SPEED);
		this.level = level;
	}

	public void update(int delta) {
		if (!hasExpired) {
			float distance = delta * DEFAULT_SPEED / 1000;
			if (distance > distanceLeft) {
				distanceLeft = 0;
				hasExpired = true;
			} else {
				Vector2f change = movement.scale(distance);
				position = position.add(change);
			}
		}
	}

	public void render(Graphics g) {
		g.setColor(DEFAULT_COLOR);
		g.fillOval(position.getX() - RADIUS, position.getY() - RADIUS, RADIUS * 2, RADIUS * 2, SEGMENTS);
	}

	public boolean hasExpired() {
		return hasExpired;
	}
}
