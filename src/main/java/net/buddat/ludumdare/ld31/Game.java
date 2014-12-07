package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.music.BeatCalculator;
import net.buddat.ludumdare.ld31.music.MusicDirector;
import net.buddat.ludumdare.ld31.music.MusicDirectorListener;
import net.buddat.ludumdare.ld31.world.Level;
import net.buddat.ludumdare.ld31.world.Player;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame implements MusicDirectorListener {

	private static final String TITLE_TRACK = "chipshit_128.ogg";
	private static final double TOLERANCE = 0.1;
	private static final int START_X = 2;

	private MusicDirector music;
	private Controller controller;
	private final BeatCalculator beatCalculator = new BeatCalculator(TOLERANCE);

	private Level l0;

	private Player player;

	public Game(String title) {
		super(title);
	}

	int sinceLast = 0;
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		l0.render(gc, g);
		player.render(gc, g);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		l0 = new Level(0, START_X);
		l0.init();

		music = new MusicDirector(TITLE_TRACK, this);
		player = new Player(START_X, l0.getStartY(), l0);
		controller = new Controller(music, player);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		sinceLast += delta;
		if (sinceLast > 1000 * 60 / music.getBpm()) {
			if (l0.isCollidable(player.getX() + 1, player.getY())) {
				// game over
			} else {
				// player.setX(player.getX() + 1);
				sinceLast = 0;
				l0.update(delta, true, music.getBpm());
			}

			ColorDirector.update(delta);
		} else {
			l0.update(delta, false, music.getBpm());
		}
		player.update(delta);

		controller.handleInput(gc.getInput());
	}

	public static void main(String[] args) {
		try {
			final AppGameContainer gameContainer;
			gameContainer = new AppGameContainer(new Game(Constants.GAME_TITLE));
			gameContainer.setDisplayMode(Constants.GAME_WIDTH,
					Constants.GAME_HEIGHT, Constants.FULLSCREEN);
			gameContainer.setShowFPS(Constants.DEV_SHOW_FPS);
			gameContainer.setTargetFrameRate(Constants.TARGET_FPS);
			gameContainer.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTrackChanged(String oldTrack, float oldPosition, int oldBpm, String newTrack, int newBpm) {
		sinceLast = 0;
	}

	@Override
	public void onSliceChanged(String musicBaseName, float oldPosition, int oldSlice, int newSlice) {
		sinceLast = 0;
	}
}
