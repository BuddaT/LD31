package net.buddat.ludumdare.ld31.world;

import java.util.ArrayList;
import java.util.Iterator;

import net.buddat.ludumdare.ld31.ColorDirector;
import net.buddat.ludumdare.ld31.ColorDirector.ColorType;
import net.buddat.ludumdare.ld31.Title;
import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.render.PlayerDamageEffect;
import net.buddat.ludumdare.ld31.render.PlayerDeathEffect;
import net.buddat.ludumdare.ld31.render.PlayerEffect;
import net.buddat.ludumdare.ld31.render.PlayerScoreEffect;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;

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
	private static final int SPEED = 4;
	private final Circle collisionShape;
	private Sound deathSound;
	private Sound hurtSound;

	private Level level;

	private int x;
	private int y;

	private final float xOff = 0f;
	private final float yOff = 0f;

	private int accumulativeX = 0;
	private int lastScore = 0;

	private int health = MAX_HEALTH;

	private final ArrayList<PlayerEffect> effects = new ArrayList<PlayerEffect>();

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
		this.collisionShape = new Circle((x + 0.5F) * Constants.TILE_WIDTH,
				(y + 0.5F) * Constants.TILE_WIDTH, DEFAULT_HEIGHT / 2.0F);
		try {
			this.hurtSound = new Sound(Constants.SOUNDS_DIR + "hurt.ogg");
			this.deathSound = new Sound(Constants.SOUNDS_DIR + "death.ogg");
		} catch (SlickException e) {
			System.err.println("Can't load hurt sound");
		}
	}

	public void setX(int x) {
		this.x = x;
		collisionShape.setCenterX((x + 0.5F) * Constants.TILE_WIDTH);
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		if (!level.isCollidable(x, y)) {
			this.y = y;
			collisionShape.setCenterY((y + 0.5F) * Constants.TILE_WIDTH);
		}
	}

	public int getY() {
		return y;
	}
	
	public int getHealth() {
		return health;
	}

	private void playHurtSound() {
		if (hurtSound != null) {
			hurtSound.play();
		}
	}

	private void playDeathSound() {
		if (deathSound != null) {
			deathSound.play();
		}
	}
	public void setHealth(int newHealth) {
		if (newHealth < health) {
			int scaleX = Level.getScaledX(level.getXPosition(), x);
			int width = Level.getScaledX(level.getXPosition(), x + 1) - scaleX;
			float scale = 1.0f / Constants.TILE_WIDTH * width;

			addEffect(new PlayerDamageEffect(getRenderCentreX(),
					getRenderCentreY(), scale));

			if (newHealth <= 0) {
				addEffect(new PlayerDeathEffect(getRenderCentreX(),
						getRenderCentreY(), scale));
				playDeathSound();
			}
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

	public void addEffect(PlayerEffect effect) {
		this.effects.add(effect);
	}

	public void update(int delta, boolean beat, int bpm) {
		sinceLast += delta;
		dirCooldown -= delta;
		lastScore += delta;

		switch (currentDir) {
			case UP:
				y--;
				collisionShape.setCenterY((y + 0.5F) * Constants.TILE_WIDTH);
				break;
			case DOWN:
				y++;
				collisionShape.setCenterY((y + 0.5F) * Constants.TILE_WIDTH);
				break;
			case LEFT:
				if (x != 0) {
					x--;
					accumulativeX--;
					collisionShape.setCenterX((x + 0.5F) * Constants.TILE_WIDTH);
				}
				break;
			case RIGHT:
				x++;
				accumulativeX++;
				collisionShape.setCenterX((x + 0.5F) * Constants.TILE_WIDTH);
				break;
			case NONE:
				break;
		}

		if (currentDir != Direction.NONE) {
			currentDir = Direction.NONE;
			dirCooldown = 1000 * 60 / (bpm * SPEED);
		}

		if (level.isCollidable(x, y)) {
			if (ColorDirector
					.getCurrentPrimary(level.isSlowWall(x, y) ? ColorType.SLOW_WALL
							: ColorType.WALL) != ColorDirector
					.getCurrentPrimary(ColorType.PLAYER)) {
				setHealth(0);
			}
		}

		boolean tookDamage = false;
		int projectileDamage = level.getProjectileDamage(collisionShape);
		if (projectileDamage > 0) {
			setHealth(this.getHealth() - projectileDamage);
			tookDamage = true;
		}

		if (level.isTileHot(x, y)) {
			tookDamage = true;
			setHealth(getHealth() - 20);
			level.setTileHot(x, y, false);
		}
		
		for (Iterator<PlayerEffect> iter = effects.iterator(); iter.hasNext();) {
			PlayerEffect effect = iter.next();
			effect.update(delta);
			if (effect.hasExpired()) {
				iter.remove();
			}
		}
		if (tookDamage && !isDead()) {
			playHurtSound();
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

		if (!isDead()) {
			g.setColor(ColorDirector.getCurrentPrimary(ColorType.PLAYER));
			g.fillOval(xPos + X_OFFSET * DEFAULT_WIDTH_RATIO, Y_OFFSET + y
					* Constants.TILE_WIDTH, width * DEFAULT_WIDTH_RATIO,
					DEFAULT_HEIGHT, 20);

			g.setColor(Color.white);
			g.drawOval(xPos + X_OFFSET * DEFAULT_WIDTH_RATIO, Y_OFFSET + y
					* Constants.TILE_WIDTH, width * DEFAULT_WIDTH_RATIO,
					DEFAULT_HEIGHT, 20);
		}

		for (PlayerEffect effect : effects) {
			g.setColor(ColorDirector.getCurrentPrimary(ColorType.PLAYER));
			effect.render(g);
		}

		String health = "health: " + this.health + " / " + MAX_HEALTH;
		Title.textFontSmall.drawString(Constants.GAME_WIDTH - 20
				- Title.textFontSmall.getWidth(health),
				Constants.GAME_HEIGHT - 40, health);

		String score = "score: " + this.accumulativeX;
		Title.textFontSmall.drawString(20, Constants.GAME_HEIGHT - 40, score);
	}

	public boolean isDead() {
		return getHealth() <= 0;
	}

	public int getAccX() {
		return accumulativeX;
	}

	public void addScore(boolean onBeat, int bpm) {
		if (lastScore < 1000 * 60 / bpm / 1.5f) {
			lastScore = 0;
			return;
		}

		if (onBeat) {
			int scaleX = Level.getScaledX(level.getXPosition(), x);
			int width = Level.getScaledX(level.getXPosition(), x + 1) - scaleX;
			float scale = 1.0f / Constants.TILE_WIDTH * width;

			accumulativeX += 5;

			addEffect(new PlayerScoreEffect(getRenderCentreX(),
					getRenderCentreY(), scale));
		}

		lastScore = 0;
	}
}
