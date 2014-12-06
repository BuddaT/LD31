package net.buddat.ludumdare.ld31.world;

import java.awt.*;

/**
 * Level tile
 */
public class Tile {

	private final Point position;
	private boolean collidable;

	// private MapObject object;

	Tile(Point p, boolean c) {
		position = p;
		collidable = c;
	}

	public Point getPosition() {
		return position;
	}

	public boolean isCollidable() {
		return collidable;
	}
}
