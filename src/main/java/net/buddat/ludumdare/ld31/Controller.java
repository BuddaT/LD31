package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.music.BeatCalculator;
import net.buddat.ludumdare.ld31.music.MusicDirector;
import net.buddat.ludumdare.ld31.render.PlayerDamageEffect;
import net.buddat.ludumdare.ld31.world.Player;
import org.newdawn.slick.Input;

/**
 * Controller input handler, detects key presses and directs actions accordingly.
 */
public class Controller {
	private static final double TOLERANCE = 0.1;
	private final MusicDirector musicDirector;
	private final BeatCalculator beatCalculator;
	private final Player player;
	private boolean wasSpacePressed = false;

	public Controller(MusicDirector musicDirector, Player player) {
		this.musicDirector = musicDirector;
		this.beatCalculator = new BeatCalculator(TOLERANCE);
		this.player = player;
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
		if (input.isKeyPressed(Input.KEY_UP)) {
			player.setY(player.getY() - 1);
		} else if (input.isKeyPressed(Input.KEY_DOWN)) {
			player.setY(player.getY() + 1);
		} else if (input.isKeyPressed(Input.KEY_PERIOD)) {
			musicDirector.randomTrack();
		} else if (input.isKeyPressed(Input.KEY_SLASH)) {
			musicDirector.nextSlice();
		} else if (input.isKeyPressed(Input.KEY_X)) {
			player.addEffect(new PlayerDamageEffect(player.getRenderCentreX(), player.getRenderCentreY()));
		}
	}
}
