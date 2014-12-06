package net.buddat.ludumdare.ld31;


/**
 * Calculates beats
 */
public final class BeatCalculator {
	private final double tolerance;
	private final double SECONDS_PER_MINUTE = 60.0;
	private static final double BEAT_OFFSET = 0.15;

	public BeatCalculator(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Whether or not the position is on the beat, given the beats per minute and within the tolerance.
	 *
	 * @param position Track position to check
	 * @param bpm      Beats per minute of the track
	 * @return Whether or not the position is on the beat.
	 */
	public boolean isOnBeat(float position, int bpm) {
		return beatDifference(position, bpm) <= tolerance;
	}

	/**
	 * Difference in seconds between current track position and the closest beat position.
	 *
	 * @param position Current track position
	 * @param bpm      Beats per minute of the current track
	 * @return The seconds difference between the current track position and the beat position.
	 */
	public double beatDifference(float position, int bpm) {
		double secondsPerBeat = calcSecondsPerBeat(bpm);
		double offsetPosition = position - BEAT_OFFSET;
		double beatSearchPoint = offsetPosition / secondsPerBeat;
		double prevBeat = Math.floor(beatSearchPoint) * secondsPerBeat;
		double nextBeat = Math.ceil(beatSearchPoint) * secondsPerBeat;
		return Math.min(offsetPosition - prevBeat, nextBeat - offsetPosition);
	}

	public double timeToNextBeat(float position, int bpm) {
		double secondsPerBeat = calcSecondsPerBeat(bpm);
		double offsetPosition = position - BEAT_OFFSET;
		return Math.ceil(offsetPosition / secondsPerBeat) * secondsPerBeat - offsetPosition;
	}

	public double calcSecondsPerBeat(int bpm) {
		return SECONDS_PER_MINUTE / bpm;
	}
}
