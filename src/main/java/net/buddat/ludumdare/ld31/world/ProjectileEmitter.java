package net.buddat.ludumdare.ld31.world;

import java.util.List;
import java.util.Random;

import net.buddat.ludumdare.ld31.render.Projectile;

import org.newdawn.slick.geom.Vector2f;

/**
 * Basic emitter of projectiles.
 */
public class ProjectileEmitter {

	private static final int DEFAULT_TIME = 8;
	private final int x;
	private final int y;
	private final Level level;
	private final List<Projectile> projectiles;
	private final int beatsPerEmission;
	private final int directionMin, directionMax;
	private final int projectileCount;
	private final int distance;

	private int lastFire = 0;

	private final Random directionRandom;

	/**
	 * Create a new emitter of projectiles.
	 * @param x X tile position
	 * @param y Y tile position
	 * @param directionMin Direction of the projectile
	 */
	public ProjectileEmitter(int x, int y, int directionMin, int directionMax,
			int projectileCount, int beatsPerEmission,
			Level level, int distance, List<Projectile> projectiles) {
		this.x = x;
		this.y = y;
		this.beatsPerEmission = beatsPerEmission == 0 ? DEFAULT_TIME : beatsPerEmission;
		this.directionMin = directionMin;
		this.directionMax = directionMax;
		this.projectileCount = projectileCount;
		this.level = level;
		this.distance = distance;
		this.projectiles = projectiles;

		this.directionRandom = new Random();
	}

	public ProjectileEmitter(int x, int y, int direction,
			int beatsPerEmission, Level level, int distance,
			List<Projectile> projectiles) {
		this(x, y, direction, direction, 1, beatsPerEmission, level, distance,
				projectiles);
	}

	public void update(int delta, boolean onBeat, int bpm) {
		if (onBeat) {
			lastFire++;
			if (lastFire % beatsPerEmission == 0) {
				if (projectileCount == 1) {
					projectiles.add(emitProjectile(bpm, directionMin));
				} else {
					int dirDiff = (directionMax - directionMin)
							/ projectileCount;

					for (int i = 0; i < projectileCount; i++) {
						projectiles.add(emitProjectile(bpm, dirDiff * i));
					}
				}
				lastFire = 0;
			}
		}
	}

	public Projectile emitProjectile(int bpm, int direction) {
		return new Projectile(x, y, new Vector2f(direction), level, distance,
				bpm);
	}
}
