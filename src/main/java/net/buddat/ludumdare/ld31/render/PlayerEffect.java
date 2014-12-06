package net.buddat.ludumdare.ld31.render;

/**
 * Visual effect on the player
 */
public abstract class PlayerEffect implements Effect {
	private final int x;
	private final int y;
	private int durationCompleted = 0;

	public PlayerEffect(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return X centre of the effect
	 */
	protected int getX() {
		return x;
	}

	/**
	 * @return Y centre of the effect
	 */
	protected int getY() {
		return y;
	}

	@Override
	public void update(int delta) {
		durationCompleted += delta;
	}

	public int getDurationCompleted() {
		return durationCompleted;
	}

	public abstract int getDuration();

	public boolean hasExpired() {
		return durationCompleted >= getDuration();
	}
}
