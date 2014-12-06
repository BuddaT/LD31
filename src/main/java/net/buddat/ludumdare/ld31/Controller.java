package net.buddat.ludumdare.ld31;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/**
 * Controller input handler, detects key presses and directs actions accordingly.
 */
public class Controller {
	private static final double TOLERANCE = 0.1;
	private final MusicDirector musicDirector;
	private final BeatCalculator beatCalculator;
	private boolean wasSpacePressed = false;

	public Controller(MusicDirector musicDirector) {
		this.musicDirector = musicDirector;
		this.beatCalculator = new BeatCalculator(TOLERANCE);
	}

	public void handleInput(Input input) {
		if (input.isKeyDown(Input.KEY_SPACE)) {
			if (!wasSpacePressed) {
				float position = musicDirector.getPosition();
				int bpm = musicDirector.getBpm();
				System.out.println("On beat: " + beatCalculator.isOnBeat(position, bpm)
						+ "\tPosition: " + position
						+ "\tDiff: " + beatCalculator.beatDifference(position, bpm));
				wasSpacePressed = true;
			}
		} else {
			wasSpacePressed = false;
		}
		if (input.isKeyPressed(Input.KEY_PERIOD)) {
			musicDirector.randomTrack();
		}
	}
}
