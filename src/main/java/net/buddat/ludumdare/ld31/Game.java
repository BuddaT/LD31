package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.world.Level;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame implements MusicDirectorListener {

	private static final String TITLE_TRACK = "chipshit_128.ogg";
	private static final double TOLERANCE = 0.1;

	private MusicDirector music;
	private Controller controller;
	private BeatCalculator beatCalculator = new BeatCalculator(TOLERANCE);
	private double timeToNextBeat = 0;

	private Level l0;

	public Game(String title) {
		super(title);
	}

	int playerX = 2;
	int sinceLast = 0;
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		l0.render(gc, g, playerX);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		l0 = new Level(0);
		l0.init();

		music = new MusicDirector(TITLE_TRACK, this);
		controller = new Controller(music);
		timeToNextBeat = beatCalculator.timeToNextBeat(music.getPosition(), music.getBpm());
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		sinceLast += delta;
		if (sinceLast > 1000 * 60 / music.getBpm()) {
			playerX++;
			sinceLast = 0;
			l0.update(delta);
		}

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
	public void onMusicChanged(String oldTrack, float oldPosition, int oldBpm, String newTrack, int newBpm) {
		timeToNextBeat = beatCalculator.timeToNextBeat(music.getPosition(), music.getBpm());
	}
}
