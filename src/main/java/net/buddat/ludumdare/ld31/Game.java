package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.constants.Constants;

import org.newdawn.slick.*;

public class Game extends BasicGame {

    MusicDirector music;
    Controller controller;

	public Game(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GameContainer gc, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// TODO Auto-generated method stub
        music = new MusicDirector("chipshit_128.ogg");
        controller = new Controller(music);
	}

	@Override
	public void update(GameContainer gc, int arg1) throws SlickException {
		// TODO Auto-generated method stub
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

}
