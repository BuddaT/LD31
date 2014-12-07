package net.buddat.ludumdare.ld31.render;

import java.util.ArrayList;

import net.buddat.ludumdare.ld31.constants.Constants;

import org.newdawn.slick.Graphics;

public class PlayerDeathEffect extends PlayerEffect {

	/**
	 * Duration of the effect, in milliseconds
	 */
	private static final int DURATION = 500;

	private static final float SPEED = 0.5f;

	private static final int SIZE = Constants.TILE_WIDTH / 4;

	private final float scale;

	private final ArrayList<DeathFlake> flakeList = new ArrayList<DeathFlake>();

	public PlayerDeathEffect(int x, int y, float scale) {
		super(x, y);
		this.scale = scale;
		
		for (int i = 0; i < 10; i++) {
			flakeList.add(new DeathFlake(0, 0, 0.6f * i, (float) (SPEED + (Math
					.random() - 0.25))));
		}
	}

	@Override
	public void render(Graphics g) {
		for (DeathFlake f : flakeList) {
			g.fillRect(getX() + f.getX(), getY() + f.getY(), SIZE * scale, SIZE);
		}
	}

	@Override
	public void update(int delta) {
		super.update(delta);

		for (DeathFlake f : flakeList)
			f.update();
	}

	@Override
	public int getDuration() {
		return DURATION;
	}

	private class DeathFlake {

		private float xOffset, yOffset;

		private final float direction, speed;

		private DeathFlake(float xOffset, float yOffset, float direction,
				float speed) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.direction = direction;
			this.speed = speed;
		}

		private void update() {
			xOffset += (Math.sin(direction) * speed) * scale;
			yOffset += (Math.cos(direction) * speed) * scale;
		}

		private float getX() {
			return xOffset;
		}

		private float getY() {
			return yOffset;
		}

	}

}
