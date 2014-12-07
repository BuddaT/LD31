package net.buddat.ludumdare.ld31.music;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and plays music, switching on demand and retrieving bpm information. Some tracks are divided into multiple
 * slices, which are designed to be played next to one another.
 */
public class MusicDirector {

	private static final Map<String, Integer> BEATS_PER_MINUTE;
	private static final Map<String, Music> MUSICS;
	/**
	 * Number of slices for a given track
	 */
	private static final Map<String, Integer> SLICES;
	private static final String SLICE_EXT = ".ogg";
	private static final String MUSIC_DIR = "music/";

	private static final int NO_SLICE = -1;

	private final MusicDirectorListener listener;

	/**
	 * Current music track
	 */
	private Music music = null;
	private int bpm;
	private String currentMusicName = null;
	private int currentSlice = NO_SLICE;

	static {
		HashMap<String, Integer> bpm = new HashMap<String, Integer>();
		HashMap<String, Music> musics = new HashMap<String, Music>();
		HashMap<String, Integer> slices = new HashMap<String, Integer>();

		// First full tracks
		bpm.put("chipshit_128.ogg", 128);
		bpm.put("thememaybe_172.ogg", 172);
		for (String musicName : bpm.keySet()) {
			musics.put(musicName, loadMusic(musicName));
		}

		// Now add slices
		bpm.put("level1_123", 123);

		slices.put("level1_123", 3);
		for (String sliceName : slices.keySet()) {
			for (int slice = 0; slice < slices.get(sliceName); slice++) {
				String musicName = generateSliceName(sliceName, slice);
				musics.put(musicName, loadMusic(musicName));
			}
		}
		BEATS_PER_MINUTE = Collections.unmodifiableMap(bpm);
		SLICES = Collections.unmodifiableMap(slices);
		MUSICS = Collections.unmodifiableMap(musics);
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
	 * @param musicBaseName New track to set. If the track is sliced, base name for the slices.
	 */
	public void setTrack(String musicBaseName) {
		String oldMusicName = currentMusicName;
		int oldBpm = bpm;
		float oldPosition = 0;
		if (music != null && music.playing()) {
			System.out.println("Stopping music");
			oldPosition = music.getPosition();
			music.stop();
		}
		currentMusicName = musicBaseName;
		bpm = BEATS_PER_MINUTE.get(musicBaseName);
		String musicName;
		if (SLICES.containsKey(musicBaseName)) {
			currentSlice = 0;
			musicName = generateSliceName(musicBaseName, currentSlice);
		} else {
			currentSlice = NO_SLICE;
			musicName = musicBaseName;
		}
		music = MUSICS.get(musicName);
		music.loop();
		if (oldMusicName != null) {
			listener.onTrackChanged(oldMusicName, oldPosition, oldBpm, musicName, bpm);
		}
	}

	/**
	 * Sets music to a random track, for testing
	 *
	 * @throws SlickException
	 */
	public void randomTrack() {
		Object[] musicNames = BEATS_PER_MINUTE.keySet().toArray();
		String musicName = (String) musicNames[(int) (Math.random() * musicNames.length)];
		System.out.println("Setting to random track " + musicName);
		setTrack(musicName);
	}

	/**
	 * Progress to the next slice. Doesn't try to sync up the beats, no nice transitions yet
	 */
	public void nextSlice() {
		if (currentMusicName == null) {
			System.err.println("No current music, can't progress to next slice");
		} else if (!SLICES.containsKey(currentMusicName)) {
			System.err.println(currentMusicName + " is not a sliced track");
			return;
		} else if (currentSlice == NO_SLICE) {
			System.err.println("No current slice for " + currentMusicName);
			return;
		}
		int oldSlice = currentSlice;
		currentSlice = nextSliceNumber(currentSlice);
		// BPM and music base name remain the same
		float oldPosition = 0;
		if (music != null && music.playing()) {
			oldPosition = music.getPosition();
			music.stop();
		}
		String musicName = generateSliceName(currentMusicName, currentSlice);
		music = MUSICS.get(musicName);
		music.loop();
		listener.onSliceChanged(currentMusicName, oldPosition, oldSlice, currentSlice);
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

	private static String generateSliceName(String musicName, int number) {
		return musicName + "_" + number + SLICE_EXT;
	}

	private static Music loadMusic(String musicName) {
		try {
			return new Music(MUSIC_DIR + musicName);
		} catch (SlickException e) {
			System.err.println("Couldn't load music at " + MUSIC_DIR + musicName);
			return null;
		}
	}

	private int nextSliceNumber(int sliceNumber) {
		return (sliceNumber + 1) % SLICES.get(currentMusicName);
	}
}
