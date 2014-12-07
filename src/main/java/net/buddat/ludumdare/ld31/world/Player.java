package net.buddat.ludumdare.ld31.world;

import java.util.ArrayList;
import java.util.Iterator;

import net.buddat.ludumdare.ld31.ColorDirector;
import net.buddat.ludumdare.ld31.ColorDirector.ColorType;
import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.render.PlayerDamageEffect;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Player information and behaviour
 */
public class Player {
	
	private static final float DEFAULT_WIDTH_RATIO = 0.75f;
	private static final int DEFAULT_WIDTH = (int) (Constants.TILE_WIDTH * DEFAULT_WIDTH_RATIO);
	private static final int DEFAULT_HEIGHT = 15;

	private static final int X_OFFSET = Constants.TILE_WIDTH / 2
			- DEFAULT_WIDTH / 2;
	private static final int Y_OFFSET = Constants.TILE_WIDTH / 2
			- DEFAULT_HEIGHT / 2;

	private static final int MAX_HEALTH = 100;

	private Level level;

	private int x;
	private int y;

	private int health = MAX_HEALTH;

	private final ArrayList<PlayerDamageEffect> effects = new ArrayList<PlayerDamageEffect>();

	public enum Direction {
		UP, RIGHT, DOWN, LEFT, NONE
	};

	private Direction currentDir = Direction.NONE;
	private int dirCooldown = 0;

	private int sinceLast = 0;

	public Player(int x, int y, Level level) {
		this.x = x;
		this.y = y;
		this.level = level;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		if (!level.isCollidable(x, y)) {
			this.y = y;
		}
	}

	public int getY() {
		return y;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int newHealth) {
		if (newHealth < health) {
			int scaleX = Level.getScaledX(level.getXPosition(), x);
			int width = Level.getScaledX(level.getXPosition(), x + 1) - scaleX;
			float scale = 1.0f / Constants.TILE_WIDTH * width;

			addEffect(new PlayerDamageEffect(getRenderCentreX(),
					getRenderCentreY(), scale));
		}

		health = newHealth;
	}

	public void setLevel(Level newLevel) {
		this.level = newLevel;
	}

	public Direction getCurrentDir() {
		return currentDir;
	}

	public void setDirection(Direction d) {
		if (dirCooldown < 0)
			currentDir = d;
	}

	public void addEffect(PlayerDamageEffect effect) {
		this.effects.add(effect);
	}

	public void update(int delta, boolean beat, int bpm) {
		sinceLast += delta;
		dirCooldown -= delta;
		if (sinceLast > 1000 * 60 / (bpm * 2)) {
			sinceLast = 0;

			switch (currentDir) {
				case UP:
					y--;
					break;
				case DOWN:
					y++;
					break;
				case LEFT:
					if (x != 0)
						x--;
					break;
				case RIGHT:
					x++;
					break;
				case NONE:
					break;
			}

			if (currentDir != Direction.NONE) {
				currentDir = Direction.NONE;
				dirCooldown = 1000 * 60 / (bpm * 4);
			}
		}

		if (level.isCollidable(x, y)) {
			setHealth(0);
		}

		for (Iterator<PlayerDamageEffect> iter = effects.iterator(); iter.hasNext();) {
			PlayerDamageEffect effect = iter.next();
			effect.update(delta);
			if (effect.hasExpired()) {
				iter.remove();
			}
		}
	}

	public int getRenderCentreX() {
		int xPos = Level.getScaledX(level.getXPosition(), x);
		int width = Level.getScaledX(level.getXPosition(), x + 1) - xPos;

		return xPos + width / 2;
	}

	public int getRenderCentreY() {
		return Y_OFFSET + y * Constants.TILE_WIDTH + (DEFAULT_HEIGHT + 1) / 2;
	}

	public void render(GameContainer gc, Graphics g) {
		int xPos = Level.getScaledX(level.getXPosition(), x);
		int width = Level.getScaledX(level.getXPosition(), x + 1) - xPos;

		g.setColor(ColorDirector.getCurrentPrimary(ColorType.PLAYER));
		g.fillOval(xPos + X_OFFSET * DEFAULT_WIDTH_RATIO, Y_OFFSET + y
				* Constants.TILE_WIDTH, width * DEFAULT_WIDTH_RATIO,
				DEFAULT_HEIGHT, 20);

		for (PlayerDamageEffect effect : effects) {
			effect.render(g);
		}
	}

	public boolean isDead() {
		return getHealth() <= 0;
	}
}
