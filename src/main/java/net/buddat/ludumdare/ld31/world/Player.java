package net.buddat.ludumdare.ld31.world;

import net.buddat.ludumdare.ld31.constants.Constants;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Player information and behaviour
 */
public class Player {
	private static final int DEFAULT_WIDTH = 15;
	private static final int DEFAULT_HEIGHT = 15;
	private static final int X_OFFSET = Constants.GAME_HEIGHT - DEFAULT_WIDTH / 2;
	private static final int Y_OFFSET = Constants.TILE_WIDTH / 2 - DEFAULT_HEIGHT / 2;

	private final int startingX;
	private int x;
	private int y;
	public Player(int x, int y) {
		this.x = x;
		startingX = x;
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void render(GameContainer gc, Graphics g) {
		g.setColor(Color.orange);
		g.fillOval(X_OFFSET + startingX * Constants.TILE_WIDTH, Y_OFFSET + y * Constants.TILE_WIDTH, DEFAULT_WIDTH, DEFAULT_HEIGHT, 20);
	}
}
