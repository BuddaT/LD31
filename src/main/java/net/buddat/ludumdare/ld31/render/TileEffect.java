package net.buddat.ludumdare.ld31.render;


public abstract class TileEffect implements Effect {

	private final int tileX, tileY;

	private final int duration;
	private int timeElapsed = 0;

	public TileEffect(int tileX, int tileY, int duration) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.duration = duration;
	}

	public int getX() {
		return tileX;
	}

	public int getY() {
		return tileY;
	}

	protected int getDuration() {
		return duration;
	}

	protected int getTimeElapsed() {
		return timeElapsed;
	}

	protected float getRemaining() {
		return 1.0f - (1.0f / duration * timeElapsed);
	}

	@Override
	public void update(int delta) {
		timeElapsed += delta;
	}

	@Override
	public boolean hasExpired() {
		return timeElapsed >= duration;
	}

}
