package net.buddat.ludumdare.ld31.world;

import net.buddat.ludumdare.ld31.render.Projectile;
import org.newdawn.slick.geom.Vector2f;

/**
 * Basic emitter of projectiles.
 */
public class ProjectileEmitter {

	private static final int DEFAULT_TIME = 1000;
	private final int x;
	private final int y;
	private final Level level;
	private int timeBetweenEmissions;
	private int timeToNextEmission;
	private final Vector2f direction;

	/**
	 * Create a new emitter of projectiles.
	 * @param x X tile position
	 * @param y Y tile position
	 * @param direction Direction of the projectile
	 */
	public ProjectileEmitter(int x, int y, double direction, Level level) {
		this.x = x;
		this.y = y;
		this.timeBetweenEmissions = DEFAULT_TIME;
		timeToNextEmission = timeBetweenEmissions;
		this.direction = new Vector2f(direction);
		this.level = level;
	}

	public void update(int delta) {
		if (timeToNextEmission > delta) {
			timeToNextEmission -= delta;
		} else {
			timeToNextEmission = timeBetweenEmissions;
			emitProjectile();
		}
	}

	public Projectile emitProjectile() {
		return new Projectile(x, y, direction, level);
	}
}
