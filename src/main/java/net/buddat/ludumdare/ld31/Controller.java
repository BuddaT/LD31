package net.buddat.ludumdare.ld31;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * Controller input handler, detects key presses and directs actions accordingly.
 */
public class Controller {
    private final MusicDirector musicDirector;

    public Controller(MusicDirector musicDirector) {
        this.musicDirector = musicDirector;
    }
    public void handleInput(Input input) {
        if (input.isKeyPressed(Input.KEY_PERIOD)) {
            musicDirector.randomTrack();
        }
    }
}
