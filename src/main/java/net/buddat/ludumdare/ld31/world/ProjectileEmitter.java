package net.buddat.ludumdare.ld31.world;

import java.util.List;

import net.buddat.ludumdare.ld31.render.Projectile;

import org.newdawn.slick.geom.Vector2f;

/**
 * Basic emitter of projectiles.
 */
public class ProjectileEmitter {

	private static final int DEFAULT_TIME = 4;
	private final int x;
	private final int y;
	private final Level level;
	private final List<Projectile> projectiles;
	private final int beatsPerEmission;
	private final Vector2f direction;
	private final int distance;

	private int lastFire = 0;

	/**
	 * Create a new emitter of projectiles.
	 * @param x X tile position
	 * @param y Y tile position
	 * @param direction Direction of the projectile
	 */
	public ProjectileEmitter(int x, int y, double direction, int beatsPerEmission, Level level, int distance, List<Projectile> projectiles) {
		this.x = x;
		this.y = y;
		this.beatsPerEmission = beatsPerEmission == 0 ? DEFAULT_TIME : beatsPerEmission;
		this.direction = new Vector2f(direction);
		this.level = level;
		this.distance = distance;
		this.projectiles = projectiles;
	}

	public void update(int delta, boolean onBeat, int bpm) {
		if (onBeat) {
			lastFire++;
			if (lastFire % beatsPerEmission == 0) {
				projectiles.add(emitProjectile(bpm));
				lastFire = 0;
			}
		}
	}

	public Projectile emitProjectile(int bpm) {
		return new Projectile(x, y, direction, level, distance, bpm);
	}
}
