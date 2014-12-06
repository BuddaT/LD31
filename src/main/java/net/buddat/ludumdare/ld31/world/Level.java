package net.buddat.ludumdare.ld31.world;

import java.awt.Point;
import java.util.HashMap;

import net.buddat.ludumdare.ld31.constants.Constants;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Level {

	private final int levelNum;
	private int lvlWidth, lvlHeight;

	private Image collisionsLayer;

	private HashMap<Point, Tile> tileMap;

	private final Color collisionColor = Color.blue;

	public Level(int levelNum) {
		this.levelNum = levelNum;
	}

	public void init() {
		try {
			collisionsLayer = new Image(Constants.LEVEL_FILE_PREFIX
					+ levelNum + Constants.LEVEL_COLLISION_LAYER
					+ Constants.LEVEL_FILE_FORMAT);
		} catch (SlickException e) {
			e.printStackTrace();
		}

		if (collisionsLayer == null)
			return;

		tileMap = new HashMap<Point, Tile>();

		lvlWidth = collisionsLayer.getWidth();
		lvlHeight = collisionsLayer.getHeight();

		for (int x = 0; x < lvlWidth; x++) {
			for (int y = 0; y < lvlHeight; y++) {
				Color pixelColor = collisionsLayer.getColor(x, y);

				if (pixelColor.getAlpha() > 0) {
					Point p = new Point(x, y);
					Tile t = new Tile(p, true);
					tileMap.put(p, t);
				}
			}
		}

	}

	public void render(GameContainer gc, Graphics g, int playerX) {
		Point temp = new Point(0, 0);

		/*
		 * Draw tiles that don't have scaling - focused on the player.
		 */
		for (int x = playerX - lvlHeight + 1; x < playerX + lvlHeight; x++) {
			for (int y = 0; y < lvlHeight; y++) {
				temp.setLocation(x, y);

				Tile t = tileMap.get(temp);
				if (t == null)
					continue;

				if (t.isCollidable())
					drawTile(
							g,
							(Constants.GAME_WIDTH / 2 - Constants.TILE_WIDTH / 2)
									- ((playerX - x) * Constants.TILE_WIDTH), y
									* Constants.TILE_WIDTH,
							Constants.TILE_WIDTH, Constants.TILE_WIDTH);
			}
		}

		/*
		 * Draw tiles with scaling on the left edge of the screen.
		 */
		{
			int highX = (Constants.GAME_WIDTH / 2 - Constants.TILE_WIDTH / 2)
					- ((lvlHeight) * Constants.TILE_WIDTH);
			int accOffset = -1;
			int width = Constants.TILE_WIDTH + 2;
			for (int x = playerX - lvlHeight; x > playerX - lvlHeight - 10; x--) {
				width -= 2;
				if (accOffset == -1)
					accOffset++;
				else
					accOffset += width;

				for (int y = 0; y < lvlHeight; y++) {
					temp.setLocation(x, y);

					Tile t = tileMap.get(temp);
					if (t == null)
						continue;

					if (t.isCollidable())
						drawTile(g, highX - accOffset,
								y * Constants.TILE_WIDTH,
								width, Constants.TILE_WIDTH);
				}
			}
		}

		/*
		 * Draw tiles with scaling on the right edge of the screen.
		 */
		{
			int lowX = (Constants.GAME_WIDTH / 2 + Constants.TILE_WIDTH / 2)
					+ ((lvlHeight - 1) * Constants.TILE_WIDTH);
			int accOffset = 0;
			int width = Constants.TILE_WIDTH;
			for (int x = playerX + lvlHeight; x < playerX + lvlHeight + 10; x++) {
				for (int y = 0; y < lvlHeight; y++) {
					temp.setLocation(x, y);

					Tile t = tileMap.get(temp);
					if (t == null)
						continue;

					if (t.isCollidable())
						drawTile(g, lowX + accOffset, y * Constants.TILE_WIDTH,
								width, Constants.TILE_WIDTH);
				}
				accOffset += width;
				width -= 2;
			}
		}
	}

	private void drawTile(Graphics g, int x, int y, int width, int height) {
		g.setColor(collisionColor);
		g.fillRect(x, y, width, height);

		// TODO: Replace with glow in sync with beat
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);
	}

	public int getLevelNumber() {
		return levelNum;
	}

	private class Tile {

		private final Point position;
		private boolean collidable;

		// private MapObject object;

		Tile(Point p, boolean c) {
			position = p;
			collidable = c;
		}

		private Point getPosition() {
			return position;
		}

		private boolean isCollidable() {
			return collidable;
		}

		private void setCollidable(boolean c) {
			collidable = c;
		}
	}

}