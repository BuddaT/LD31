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
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame implements MusicDirectorListener {

	private static final String TITLE_TRACK = "chipshit_128.ogg";
	private static final double TOLERANCE = 0.1;
	private static final int START_X = 2;

	private MusicDirector music;
	private Controller controller;
	private final BeatCalculator beatCalculator = new BeatCalculator(TOLERANCE);

	private Level currentLevel, lastLevel, nextLevel;
	private Title title;

	private Player player;

	private Image backgroundImage;

	private boolean needsReset = false;

	public Game(String title) {
		super(title);
	}

	int sinceLast = 0;
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		backgroundImage.draw(0, 0);

		if (lastLevel != null)
			lastLevel.render(gc, g);

		if (currentLevel != null)
			currentLevel.render(gc, g);

		if (nextLevel != null)
			nextLevel.render(gc, g);

		player.render(gc, g);

		if (title.isVisible())
			title.render(g);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		try {
			music = new MusicDirector(TITLE_TRACK, this);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		new Thread(music).start();
		reset(gc);

		backgroundImage = new Image("levels/background.png");
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		sinceLast += delta;
		boolean onBeat = sinceLast > 1000 * 60 / music.getBpm();

		if (onBeat) {
			sinceLast = 0;
			ColorDirector.update(delta);
		}

		if (title.isMovedOut()) {
			if (!player.isDead()) {
				if (lastLevel != null)
					lastLevel.update(delta, onBeat, music.getBpm());
				if (currentLevel != null)
					currentLevel.update(delta, onBeat, music.getBpm());
				if (nextLevel != null)
					nextLevel.update(delta, onBeat, music.getBpm());
			}

			player.update(delta, onBeat, music.getBpm());
		}

		if (title.isVisible())
			title.update(delta, onBeat, (lastLevel == null ? currentLevel
					: lastLevel));

		if (player.getX() > currentLevel.getWidth()) {
			nextLevel(true);
		}

		controller.handleInput(gc.getInput());

		if (needsReset) {
			reset(gc);
			needsReset = false;
		}
	}

	public Title getTitleScreen() {
		return title;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void nextLevel(boolean forcePlayerChange) {
		lastLevel = currentLevel;
		currentLevel = nextLevel;
		nextLevel = new Level(this, currentLevel.getLevelNumber() + 1,
				currentLevel.getXPosition() - currentLevel.getWidth());
		nextLevel.init();

		if (forcePlayerChange) {
			player.setLevel(currentLevel);
			player.setX(player.getX() - lastLevel.getWidth());
		}
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
	public void onSliceChanged(String musicBaseName, int oldSlice, int newSlice) {
		sinceLast = 0;
	}

	public void reset(GameContainer gc) {
		lastLevel = null;
		currentLevel = new Level(this, 1, START_X);
		currentLevel.init();
		nextLevel = new Level(this, 1, -currentLevel.getWidth() + START_X);
		nextLevel.init();

		music.playTrack(TITLE_TRACK);

		player = new Player(START_X, currentLevel.getStartY(), currentLevel);
		controller = new Controller(this, music, player);
		title = new Title();

		gc.getInput().clearKeyPressedRecord();

		sinceLast = 0;
	}

	public void setNeedsReset(boolean b) {
		needsReset = b;
	}
}
