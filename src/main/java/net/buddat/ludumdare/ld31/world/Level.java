package net.buddat.ludumdare.ld31.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.buddat.ludumdare.ld31.ColorDirector;
import net.buddat.ludumdare.ld31.ColorDirector.ColorType;
import net.buddat.ludumdare.ld31.Game;
import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.render.Projectile;
import net.buddat.ludumdare.ld31.render.TileEffect;
import net.buddat.ludumdare.ld31.render.TileLavaGlowEffect;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

public class Level {

	private static final int CENTER_TILE_X = (Constants.GAME_WIDTH / 2 /*- Constants.TILE_WIDTH / 2*/);
	private static final float TILE_SCALE_FACTOR = 1f / Constants.TILE_WIDTH / 2.35f;
	
	private static final int SCALE_LIMIT_DIST = 44;
	private static final int scaledXDistRight = getScaledX(0, SCALE_LIMIT_DIST);
	private static final int scaledXDistLeft = getScaledX(SCALE_LIMIT_DIST, 0);
	private static final int LAVA_R = 255;
	private static final int PROJECTILE_LEFT_B = 255;
	
	private static final int VIEW_RANGE = 80;

	private final int levelNum;
	private int lvlWidth, lvlHeight;
	private int xPosition;

	private final Game game;

	private Image collisionsLayer, objectsLayer;

	private HashMap<Point, Tile> tileMap;

	private int altBeat = 0;
	private int bpm;

	private boolean setupLavaGlow = false;
	private ArrayList<TileEffect> tileEffectList;
	private ArrayList<Projectile> projectiles;
	private ArrayList<ProjectileEmitter> projectileEmitters;

	public Level(Game g, int levelNum, int startingX) {
		this.game = g;

		if (levelNum > Constants.MAX_LEVEL)
			this.levelNum = 1;
		else
			this.levelNum = levelNum;

		this.xPosition = startingX;
	}
	
	public void update(int delta, boolean beat, int bpm) {
		this.bpm = bpm;

		if (beat) {
			altBeat++;

			if (game.getCurrentLevel().getLevelNumber() > 0) {
				xPosition++;
			}

			if (altBeat % 4 == 0)
				setupLavaGlow = true;
		}

		ArrayList<TileEffect> toRemove = new ArrayList<TileEffect>();
		for (TileEffect t : tileEffectList) {
			t.update(delta);
			if (t.hasExpired()) {
				toRemove.add(t);
				Tile tile = tileMap.get(new Point(t.getX(), t.getY()));
				tile.setCurrentlyHot(false);
			}
		}
		for (ProjectileEmitter emitter : projectileEmitters) {
			emitter.update(delta, beat, bpm);
		}
		Iterator<Projectile> projectileIterator = projectiles.iterator();
		while (projectileIterator.hasNext()) {
			Projectile projectile = projectileIterator.next();
			if (projectile.hasExpired()) {
				projectileIterator.remove();
			} else {
				projectile.update(delta);
			}
		}

		tileEffectList.removeAll(toRemove);
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
		tileEffectList = new ArrayList<TileEffect>();
		projectileEmitters = new ArrayList<ProjectileEmitter>();
		projectiles = new ArrayList<Projectile>();

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

					if (objectPixelColor.getRed() == LAVA_R
							&& objectPixelColor.getAlpha() > 0) {
						t.setBeatLava(true);
					} else if (objectPixelColor.getBlue() == PROJECTILE_LEFT_B
							&& objectPixelColor.getAlpha() > 0) {
						ProjectileEmitter emitter = new ProjectileEmitter(x, y, 180, this, projectiles);
						t.setProjectileEmitter(emitter);
						projectileEmitters.add(emitter);
					}

					tileMap.put(p, t);
				}
			}
		}

	}

	public void render(GameContainer gc, Graphics g) {
		Point temp = new Point(0, 0);

		drawEdge(g, 0, 1);
		drawEdge(g, Constants.GAME_WIDTH - 1, 1);
		/*
		 * Draw tiles with scaling - focused on the player.
		 */
		for (int x = xPosition - VIEW_RANGE; x < xPosition + VIEW_RANGE; x++) {
			for (int y = 0; y < lvlHeight; y++) {
				temp.setLocation(x, y);

				Tile t = tileMap.get(temp);
				if (t == null)
					continue;

				if (setupLavaGlow) {
					if (t.isBeatLava()) {
						TileLavaGlowEffect effect = new TileLavaGlowEffect(x,
								y, 1000 * 60 / bpm);
						tileEffectList.add(effect);
						t.setCurrentlyHot(true);
					}
				}

				int xPos = getScaledX(xPosition, x);
				int width = getScaledX(xPosition, x + 1) - xPos;
				drawTile(t, g, xPos, y * Constants.TILE_WIDTH, width,
						Constants.TILE_WIDTH);
			}
		}

		for (TileEffect t : tileEffectList) {
			if (t instanceof TileLavaGlowEffect)
				((TileLavaGlowEffect) t).render(g, xPosition);
		}
		setupLavaGlow = false;
		for (Projectile projectile : projectiles) {
			projectile.render(g);
		}
	}

	private void drawEdge(Graphics g, int x, int width) {
		g.setColor(ColorDirector.getCurrentPrimary(ColorType.WALL));
		g.fillRect(x, 0, width, Constants.GAME_HEIGHT);
	}

	private void drawTile(Tile t, Graphics g, int x, int y, int width,
			int height) {
		if (t.isCollidable()) {
			g.setColor(ColorDirector.getCurrentPrimary(ColorType.WALL));
			g.fillRect(x, y, width, height);

			if (x < scaledXDistLeft || x > scaledXDistRight)
				return;

			g.setColor(ColorDirector.getCurrentSecondary(ColorType.WALL));
			g.drawRect(x, y, width, height);
		}
	}

	public int getLevelNumber() {
		return levelNum;
	}

	public int getStartY() {
		return (lvlHeight + 1) / 2;
	}

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int newX) {
		xPosition = newX;
	}

	public int getWidth() {
		return lvlWidth;
	}

	public boolean isCollidable(int x, int y) {
		Tile tile = tileMap.get(new Point(x, y));
		return tile != null && tile.isCollidable();
	}

	public int getProjectileDamage(Shape shape) {
		int damage = 0;
		for (Projectile projectile : projectiles) {
			if (projectile.collidesWith(shape)) {
				damage += projectile.getPower();
				projectile.expire();
			}
		}
		return damage;
	}

	public boolean isTileHot(int x, int y) {
		Tile tile = tileMap.get(new Point(x, y));
		return tile != null && tile.isCurrentlyHot();
	}

	public void setTileHot(int x, int y, boolean h) {
		Tile tile = tileMap.get(new Point(x, y));
		if (tile != null)
			tile.setCurrentlyHot(h);
	}

	public static int getScaledX(int xPosition, int tileX) {
		int dist = Math.abs(xPosition - tileX);
		float pos = dist - TILE_SCALE_FACTOR * dist * (dist - 1) / 2f;

		if (dist > SCALE_LIMIT_DIST)
			if (xPosition < tileX)
				return scaledXDistRight + (dist - SCALE_LIMIT_DIST);
			else
				return scaledXDistLeft - (dist - SCALE_LIMIT_DIST);

		if (xPosition < tileX)
			return CENTER_TILE_X + (int) (pos * Constants.TILE_WIDTH);
		else
			return CENTER_TILE_X - (int) (pos * Constants.TILE_WIDTH);
	}

	private class Tile {

		private final Point position;

		private boolean collidable = false;
		private boolean beatLava = false;
		private boolean currentlyHot = false;

		private ProjectileEmitter projectileEmitter;

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

		private boolean isCurrentlyHot() {
			return currentlyHot;
		}

		private void setCurrentlyHot(boolean h) {
			currentlyHot = h;
		}

		private boolean isProjectileEmitter() {
			return projectileEmitter != null;
		}

		private void setProjectileEmitter(ProjectileEmitter emitter) {
			projectileEmitter = emitter;
		}
	}
}
