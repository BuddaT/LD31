package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.music.BeatCalculator;
import net.buddat.ludumdare.ld31.music.MusicDirector;
import net.buddat.ludumdare.ld31.world.Player;
import net.buddat.ludumdare.ld31.world.Player.Direction;

import org.newdawn.slick.Input;

/**
 * Controller input handler, detects key presses and directs actions accordingly.
 */
public class Controller {
	private static final double TOLERANCE = 0.1;

	private final MusicDirector musicDirector;
	private final BeatCalculator beatCalculator;
	private final Player player;
	private final Game game;

	private boolean wasSpacePressed = false;

	public Controller(Game g, MusicDirector musicDirector, Player player) {
		this.game = g;
		this.musicDirector = musicDirector;
		this.beatCalculator = new BeatCalculator(TOLERANCE);
		this.player = player;
	}

	public void handleInput(Input input) {
		if (!game.getTitleScreen().isMoving()
				&& !game.getTitleScreen().isMovedOut()) {
			if (input.isKeyPressed(Input.KEY_ENTER)) {
				if (game.getTitleScreen().getSelected() == Title.START)
					game.getTitleScreen().setStartMoving(true);
				else if (game.getTitleScreen().getSelected() == Title.QUIT)
					System.exit(0);
			}

			if (input.isKeyPressed(Input.KEY_UP)) {
				game.getTitleScreen().setSelected(-1);
			} else if (input.isKeyPressed(Input.KEY_DOWN)) {
				game.getTitleScreen().setSelected(1);
			}

			return;
		}

		if (input.isKeyPressed(Input.KEY_ESCAPE))
			game.setNeedsReset(true);

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
		if (!player.isDead()) {
			if (input.isKeyDown(Input.KEY_UP)
					|| input.isKeyPressed(Input.KEY_UP)) {
				player.setDirection(Direction.UP);
			} else if (input.isKeyDown(Input.KEY_DOWN)) {
				player.setDirection(Direction.DOWN);
			} else if (input.isKeyDown(Input.KEY_LEFT)) {
				player.setDirection(Direction.LEFT);
			} else if (input.isKeyDown(Input.KEY_RIGHT)) {
				player.setDirection(Direction.RIGHT);
			}
		}

		if (input.isKeyPressed(Input.KEY_PERIOD)) {
			musicDirector.randomTrack();
		} else if (input.isKeyPressed(Input.KEY_SLASH)) {
			musicDirector.nextSlice();
		} else if (input.isKeyPressed(Input.KEY_ADD)) {
			musicDirector.increaseVolume();
		} else if (input.isKeyPressed(Input.KEY_SUBTRACT)) {
			musicDirector.decreaseVolume();
		} else if (input.isKeyPressed(Input.KEY_X)) {
			// player.addEffect(new
			// PlayerDamageEffect(player.getRenderCentreX(),
			// player.getRenderCentreY()));
		}
	}
}
