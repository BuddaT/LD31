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

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
	private static final float SPEED = 0.0035f;
	private final Circle collisionShape;

	private Level level;

	private int x;
	private int y;

	private float xOff = 0f;
	private float yOff = 0f;

	private int accumulativeX = 0;

	private int health = MAX_HEALTH;

	private final ArrayList<PlayerEffect> effects = new ArrayList<PlayerEffect>();

	public enum Direction {
		UP, RIGHT, DOWN, LEFT, NONE
	};

	private Direction currentDir = Direction.NONE;

	public Player(int x, int y, Level level) {
		this.x = x;
		this.y = y;
		this.level = level;
		this.collisionShape = new Circle((x + 0.5F) * Constants.TILE_WIDTH,
				(y + 0.5F) * Constants.TILE_WIDTH, DEFAULT_HEIGHT / 2.0F);
	}

	public void setX(int x) {
		this.x = x;
		updateCollisionShape();
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		if (!level.isCollidable(x, y)) {
			this.y = y;
			updateCollisionShape();
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
			int scaleX = Level.getScaledX(level.getXPosition(), x + xOff);
			int width = Level.getScaledX(level.getXPosition(), x + xOff + 1) - scaleX;
			float scale = 1.0f / Constants.TILE_WIDTH * width;

			addEffect(new PlayerDamageEffect(getRenderCentreX(),
					getRenderCentreY(), scale));

			if (newHealth <= 0)
				addEffect(new PlayerDeathEffect(getRenderCentreX(),
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
		currentDir = d;
	}

	public void addEffect(PlayerEffect effect) {
		this.effects.add(effect);
	}

	public void updateCollisionShape() {
		collisionShape.setCenterX((x + xOff + 0.5f) * Constants.TILE_WIDTH);
		collisionShape.setCenterY((y + yOff + 0.5f) * Constants.TILE_WIDTH);
	}

	public void update(int delta, boolean beat, int bpm) {
		if (!isDead()) {
			switch (currentDir) {
				case UP:
					yOff -= SPEED * delta;
					if (yOff < -0.5f) {
						y--;
						yOff += 1.0f;
					}
					break;
				case DOWN:
					yOff += SPEED * delta;
					if (yOff > 0.5f) {
						y++;
						yOff -= 1.0f;
					}
					break;
				case LEFT:
					if (x != 0) {
						xOff -= SPEED * delta;
						if (xOff < -0.5f) {
							x--;
							xOff += 1.0f;
							accumulativeX--;
						}
					}
					break;
				case RIGHT:
					xOff += SPEED * delta;
					if (xOff > 0.5f) {
						x++;
						xOff -= 1.0f;
						accumulativeX++;
					}
					break;
				case NONE:
					break;
			}
			
			updateCollisionShape();
		}

		int xStart = x - (xOff < -1.0f + DEFAULT_WIDTH_RATIO ? 1 : 0);
		int xEnd = x + (xOff > 1.0f - DEFAULT_WIDTH_RATIO ? 1 : 0);
		int yStart = y - (yOff < -1.0f + DEFAULT_WIDTH_RATIO ? 1 : 0);
		int yEnd = y + (yOff > 1.0f - DEFAULT_WIDTH_RATIO ? 1 : 0);

		for (int i = xStart; i <= xEnd; i++) {
			for (int j = yStart; j <= yEnd; j++) {
				if (level.isCollidable(i, j)) {
					if (ColorDirector
							.getCurrentPrimary(level.isSlowWall(i, j) ? ColorType.SLOW_WALL
									: ColorType.WALL) != ColorDirector
							.getCurrentPrimary(ColorType.PLAYER)) {
						setHealth(0);
					}
				}
			}
		}


		int projectileDamage = level.getProjectileDamage(collisionShape);
		if (projectileDamage > 0) {
			setHealth(this.getHealth() - projectileDamage);
		}

		if (level.isTileHot(x, y)) {
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
	}

	public int getRenderCentreX() {
		int xPos = Level.getScaledX(level.getXPosition(), x + xOff);
		int width = Level.getScaledX(level.getXPosition(), x + xOff + 1) - xPos;

		return xPos + width / 2;
	}

	public int getRenderCentreY() {
		return (int) (Y_OFFSET + (y + yOff) * Constants.TILE_WIDTH + (DEFAULT_HEIGHT + 1) / 2f);
	}

	public void render(GameContainer gc, Graphics g) {
		int xPos = Level.getScaledX(level.getXPosition(), x + xOff);
		int width = Level.getScaledX(level.getXPosition(), x + xOff + 1) - xPos;

		if (!isDead()) {
			g.setColor(ColorDirector.getCurrentPrimary(ColorType.PLAYER));
			g.fillOval(xPos + X_OFFSET * DEFAULT_WIDTH_RATIO, Y_OFFSET
					+ (y + yOff)
					* Constants.TILE_WIDTH, width * DEFAULT_WIDTH_RATIO,
					DEFAULT_HEIGHT, 20);

			g.setColor(Color.white);
			g.drawOval(xPos + X_OFFSET * DEFAULT_WIDTH_RATIO, Y_OFFSET
					+ (y + yOff)
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
}
