package net.buddat.ludumdare.ld31;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and plays music, switching on demand and retrieving bpm information.
 */
public class MusicDirector {

	private static final Map<String, Integer> BEATS_PER_MINUTE;
	private static final Map<String, Music> MUSIC;
	private final MusicDirectorListener listener;

	/**
	 * Current music track
	 */
	private Music music = null;
	private int bpm;
	private String currentMusicName = null;

	static {
		HashMap<String, Integer> bpm = new HashMap<String, Integer>();
		HashMap<String, Music> musics = new HashMap<String, Music>();
		bpm.put("chipshit_128.ogg", 128);
		bpm.put("thememaybe_172.ogg", 172);
		for (String musicName : bpm.keySet()) {
			try {
				musics.put(musicName, new Music("music/" + musicName));
			} catch (SlickException e) {
				System.err.println("Couldn't load music at music/" + musicName);
			}
		}
		BEATS_PER_MINUTE = Collections.unmodifiableMap(bpm);
		MUSIC = Collections.unmodifiableMap(musics);
	}

	public MusicDirector(String initialMusic, MusicDirectorListener listener) throws SlickException {
		if (!BEATS_PER_MINUTE.containsKey(initialMusic)) {
			throw new SlickException("Unknown music " + initialMusic);
		}
		setTrack(initialMusic);
		this.listener = listener;
	}

	/**
	 * Stops the current music track and sets a new track, with looping.
	 *
	 * @param musicName New track to set
	 */
	public void setTrack(String musicName) {
		String oldMusicName = currentMusicName;
		int oldBpm = bpm;
		float oldPosition = 0;
		if (music != null && music.playing()) {
			System.out.println("Stopping music");
			oldPosition = music.getPosition();
			music.stop();
		}
		currentMusicName = musicName;
		bpm = BEATS_PER_MINUTE.get(musicName);
		music = MUSIC.get(musicName);
		music.loop();
		if (oldMusicName != null) {
			listener.onMusicChanged(oldMusicName, oldPosition, oldBpm, musicName, bpm);
		}
	}

	/**
	 * Sets music to a random track, for testing
	 *
	 * @throws SlickException
	 */
	public void randomTrack() {
		Object[] musicNames = MUSIC.keySet().toArray();
		String musicName = (String) musicNames[(int) (Math.random() * musicNames.length)];
		System.out.println("Setting to random track " + musicName);
		setTrack(musicName);
	}

	/**
	 * @return Current position of the music.
	 */
	public float getPosition() {
		return music.getPosition();
	}

	/**
	 * @return Beats per minute of the music currently playing.
	 */
	public int getBpm() {
		return bpm;
	}
}
