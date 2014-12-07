package net.buddat.ludumdare.ld31.render;

import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.world.Level;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

/**
 * Basic projectile for starters
 */
public class Projectile {

	private static final Color DEFAULT_COLOR = Color.yellow;

	private static final float DEFAULT_SPEED = 2;

	private static final int RADIUS = 2;
	private static final int SEGMENTS = 6;

	private static final float LIMIT = 200;

	private final Vector2f movement;
	private final Level level;
	private final Vector2f position;

	private float distanceLeft = LIMIT;

	private boolean hasExpired = false;

	public Projectile(int tileX, int tileY, Vector2f direction, Level level, int bpm) {
		position = new Vector2f(
				tileX * Constants.TILE_WIDTH + Constants.TILE_WIDTH / 2,
				tileY * Constants.TILE_WIDTH + Constants.TILE_WIDTH / 2);
		Vector2f newDirection = new Vector2f(direction.getTheta());
		
		float bps = bpm / 60f;
		float speedScale = (1.0f / bps) * DEFAULT_SPEED;
		movement = newDirection.scale(Constants.TILE_WIDTH * speedScale);
		this.level = level;
	}

	public void update(int delta) {
		if (!hasExpired) {
			float seconds = ((float) delta) / 1000;
			Vector2f change = new Vector2f(movement).scale(seconds);
			if (change.length() > distanceLeft) {
				distanceLeft = 0;
				hasExpired = true;
			} else {
				distanceLeft -= change.length();
				position.add(change);
			}
		}
	}

	public void render(Graphics g) {
		g.setColor(DEFAULT_COLOR);
		// Tile for current position
		int tileX = (int) (position.getX() / Constants.TILE_WIDTH);
		// Scaled X position for the given tile
		int scaleTileX = Level.getScaledX(level.getXPosition(), tileX);
		int scaleTileWidth = Level.getScaledX(level.getXPosition(), tileX + 1) - scaleTileX;
		float tileScalingFactor = ((float) scaleTileWidth) / Constants.TILE_WIDTH;
		float scaleXRadius = tileScalingFactor * RADIUS;
		// Scaled left x position
		float scaleXLeft = (position.getX() % Constants.TILE_WIDTH) * tileScalingFactor + scaleTileX - scaleXRadius;
		g.fillOval(scaleXLeft, position.getY() - RADIUS, scaleXRadius * 2, RADIUS * 2, SEGMENTS);
	}

	public boolean hasExpired() {
		return hasExpired;
	}
}
