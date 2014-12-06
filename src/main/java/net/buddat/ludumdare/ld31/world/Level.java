package net.buddat.ludumdare.ld31.world;

import java.awt.Point;
import java.util.HashMap;

import net.buddat.ludumdare.ld31.ColorDirector;
import net.buddat.ludumdare.ld31.ColorDirector.ColorType;
import net.buddat.ludumdare.ld31.constants.Constants;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Level {

	private static final int CENTER_TILE_X = (Constants.GAME_WIDTH / 2 - Constants.TILE_WIDTH / 2);
	private static final float TILE_SCALE_FACTOR = 1f / Constants.TILE_WIDTH / 2.525f;
	private static final int VIEW_RANGE = 50;

	private final int levelNum;
	private int lvlWidth, lvlHeight;

	private Image collisionsLayer, objectsLayer;

	private HashMap<Point, Tile> tileMap;

	private Color collisionColor = ColorDirector
			.getCurrentPrimary(ColorType.WALL);
	private Color secondaryCollisionColor = ColorDirector
			.getCurrentSecondary(ColorType.WALL);
	private final Color altSecondaryColor = ColorDirector.getAltSecondary();

	private int altBeat = 0;
	private final int beatsPerChange;

	private float glowLavaTimer = 1.0f;

	public Level(int levelNum, int beatsPerChange) {
		this.levelNum = levelNum;
		this.beatsPerChange = beatsPerChange;
	}
	
	public void update(int delta, boolean beat, int bpm) {
		if (beat) {
			altBeat++;
			glowLavaTimer = 1.0f;

			if (altBeat % beatsPerChange == 0) {
				collisionColor = ColorDirector.getRandomPrimary(ColorType.WALL);
				secondaryCollisionColor = ColorDirector
						.getCurrentSecondary(ColorType.WALL);
			}
		} else {
			glowLavaTimer -= 0.017f;
		}
	}

	public void init() {
		try {
			collisionsLayer = new Image(Constants.LEVEL_FILE_PREFIX
					+ levelNum + Constants.LEVEL_COLLISION_LAYER
					+ Constants.LEVEL_FILE_FORMAT);
			
			objectsLayer = new Image(Constants.LEVEL_FILE_PREFIX
					+ levelNum + Constants.LEVEL_OBJECT_LAYER
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
				Color collisionPixelColor = collisionsLayer.getColor(x, y);
				Color objectPixelColor = objectsLayer.getColor(x, y);

				if (collisionPixelColor.getAlpha() > 0
						|| objectPixelColor.getAlpha() > 0) {
					Point p = new Point(x, y);
					Tile t = new Tile(p);

					if (collisionPixelColor.getAlpha() > 0)
						t.setCollidable(true);

					if (objectPixelColor.getAlpha() > 0)
						t.setBeatLava(true);

					tileMap.put(p, t);
				}
			}
		}

	}

	public void render(GameContainer gc, Graphics g, int playerX) {
		/*
		 * Reset background
		 */
		g.setColor(ColorDirector.getBackgroundColor());
		g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);

		Point temp = new Point(0, 0);

		/*
		 * Draw tiles with scaling - focused on the player.
		 */
		for (int x = playerX - VIEW_RANGE; x < playerX + VIEW_RANGE; x++) {
			for (int y = 0; y < lvlHeight; y++) {
				temp.setLocation(x, y);

				Tile t = tileMap.get(temp);
				if (t == null)
					continue;

				int xPos = getScaledX(playerX, x);
				int width = getScaledX(playerX, x + 1) - xPos;
				drawTile(t, g, xPos, y * Constants.TILE_WIDTH, width,
						Constants.TILE_WIDTH);
			}
		}
	}

	private void drawTile(Tile t, Graphics g, int x, int y, int width,
			int height) {
		if (t.isCollidable()) {
			g.setColor(collisionColor);
			g.fillRect(x, y, width, height);

			// TODO: Replace with glow in sync with beat
			g.setColor((altBeat % 2 == 0 ? altSecondaryColor
					: secondaryCollisionColor));
			g.drawRect(x, y, width, height);
		}

		if (t.isBeatLava()) {
			g.setColor(new Color(0.5f - glowLavaTimer, 0f, 0f));
			g.drawRect(x + 1, y + 1, width - 2, height - 2);
		}
	}

	public int getLevelNumber() {
		return levelNum;
	}

	public int getStartY() {
		return (lvlHeight + 1) / 2;
	}

	public boolean isCollidable(int x, int y) {
		Tile tile = tileMap.get(new Point(x, y));
		return tile != null && tile.isCollidable();
	}

	public int getScaledX(int playerX, int tileX) {
		int dist = Math.abs(playerX - tileX);
		float pos = dist - TILE_SCALE_FACTOR * dist * (dist - 1) / 2;
		if (playerX < tileX)
			return CENTER_TILE_X + (int) (pos * Constants.TILE_WIDTH);
		else
			return CENTER_TILE_X - (int) (pos * Constants.TILE_WIDTH);
	}

	private class Tile {

		private final Point position;
		private boolean collidable = false;
		private boolean beatLava = false;

		Tile(Point p) {
			position = p;
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

		private boolean isBeatLava() {
			return beatLava;
		}

		private void setBeatLava(boolean l) {
			beatLava = l;
		}
	}
}
