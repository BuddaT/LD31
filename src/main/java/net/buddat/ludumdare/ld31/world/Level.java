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
				Color pixelColor = collisionsLayer.getColor(x, y);

				if (pixelColor.getAlpha() > 0) {
					Point p = new Point(x, y);
					Tile t = new Tile(p, true);
					tileMap.put(p, t);
				}
				
				pixelColor = objectsLayer.getColor(x, y);

				if (pixelColor.getAlpha() > 0) {
					if (pixelColor.getRed() == 255) {
						Point p = new Point(x, y);
						Tile t = tileMap.get(p);
						if (t == null) {
							t = new Tile(p, false);
							tileMap.put(p, t);
						}

						t.setBeatLava(true);
					}
				}
			}
		}

	}

	public void render(GameContainer gc, Graphics g, int playerX) {
		g.setColor(ColorDirector.getBackgroundColor());
		g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);

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

				drawTile(
						t,
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

					drawTile(t, g, highX - accOffset,
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

					drawTile(t, g, lowX + accOffset, y
							* Constants.TILE_WIDTH,
							width, Constants.TILE_WIDTH);
				}
				accOffset += width;
				width -= 2;
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
			g.drawRect(x, y, width, height);
		}
	}

	public int getLevelNumber() {
		return levelNum;
	}

	public int getStartY() {
		return (lvlHeight + 1) / 2;
	}

	private class Tile {

		private final Point position;
		private boolean collidable;
		private boolean beatLava = false;

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

		private boolean isBeatLava() {
			return beatLava;
		}

		private void setBeatLava(boolean l) {
			beatLava = l;
		}
	}

}
