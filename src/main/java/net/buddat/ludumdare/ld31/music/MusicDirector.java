package net.buddat.ludumdare.ld31.music;

import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

		slices.put("level1_123", 4);
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
	private static final int QUEUE_SIZE = 10;

	private final MusicDirectorListener listener;

	/**
	 * Current music track
	 */
	private MusicPlayer musicPlayer;

	public MusicDirector(MusicDirectorListener listener) {
		this.listener = listener;
	}

	public void start(String initialMusic) throws SlickException {
		if (!BEATS_PER_MINUTE.containsKey(initialMusic)) {
			throw new SlickException("Unknown music " + initialMusic);
		}
		musicPlayer = new MusicPlayer(initialMusic, listener);
		new Thread(musicPlayer).start();
	}

	/**
	 * Stops the current music track and sets a new track, with looping.
	 *
	 * @param musicBaseName New track to set. If the track is sliced, base name for the slices.
	 */
	public void setTrack(String musicBaseName) {
		musicPlayer.playTrack(musicBaseName);
	}

	/**
	 * Sets music to a random track, for testing
	 */
	public void randomTrack() {
		Object[] musicNames = BEATS_PER_MINUTE.keySet().toArray();
		String musicName = (String) musicNames[(int) (Math.random() * musicNames.length)];
		System.out.println("Setting to random track " + musicName);
		setTrack(musicName);
	}

	/**
	 * Progress to the next slice.
	 */
	public void nextSlice() {
		musicPlayer.nextSlice();
	}

	/**
	 * @return Current position of the music.
	 */
	public float getPosition() {
		return musicPlayer.getCurrentPosition();
	}

	/**
	 * @return Beats per minute of the music currently playing.
	 */
	public int getBpm() {
		return BEATS_PER_MINUTE.get(musicPlayer.getCurrentMusicName());
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

	private class MusicPlayer implements MusicListener, Runnable {
		private final BlockingQueue<QueueAction> queue = new ArrayBlockingQueue<QueueAction>(QUEUE_SIZE);
		private volatile String currentMusicName;
		private volatile int currentSlice;
		private volatile Music currentMusic;
		private final MusicDirectorListener listener;

		MusicPlayer(String initialMusic, MusicDirectorListener listener) {
			queue.add(new QueueAction(QueueActionType.PLAY, initialMusic));
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				boolean goToNext = false;
				QueueAction action = queue.take();
				while (action != null && action.type != QueueActionType.END) {
					if (action.type == QueueActionType.LOOP) {
						// Only want to loop if there are no other actions
						if (goToNext) {
							int oldSlice = currentSlice;
							currentSlice = (currentSlice + 1) % SLICES.get(currentMusicName);
							if (currentMusic != null) {
								currentMusic.removeListener(this);
							}
							currentMusic = MUSICS.get(generateSliceName(currentMusicName, currentSlice));
							currentMusic.addListener(this);
							System.out.println("Playing next slice");
							currentMusic.play();
							listener.onSliceChanged(currentMusicName, oldSlice, currentSlice);
							goToNext = false;
						} else if (!queue.isEmpty()) {
							continue;
						} else if (currentMusic == null) {
							System.err.println("Null music when LOOP requested");
						} else {
							currentMusic.play();
						}
					} else if (action.type == QueueActionType.PLAY) {
						goToNext = false;
						String oldTrack = "";
						float oldPosition = 0;
						int oldBpm = 0;
						if (currentMusic != null) {
							currentMusic.removeListener(this);
							oldTrack = currentMusicName;
							oldPosition = currentMusic.getPosition();
							oldBpm = BEATS_PER_MINUTE.get(currentMusicName);
						}
						currentMusicName = action.details;
						if (SLICES.containsKey(currentMusicName)) {
							currentSlice = 0;
							currentMusic = MUSICS.get(generateSliceName(currentMusicName, 0));
						} else {
							currentSlice = NO_SLICE;
							currentMusic = MUSICS.get(currentMusicName);
						}
						currentMusic.addListener(this);
						currentMusic.play();
						listener.onTrackChanged(oldTrack, oldPosition, oldBpm, currentMusicName, BEATS_PER_MINUTE.get(currentMusicName));
					} else if (action.type == QueueActionType.NEXT) {
						// On the next loop notification,
						if (goToNext) {
							System.out.println("Next slice already queued for playing");
						} else if (SLICES.containsKey(currentMusicName)) {
							System.out.println("Next slice queued for playing, waiting for end of current slice");
							goToNext = true;
						} else {
							System.err.println("Can't take next slice, track " + currentMusicName + " isn't sliced");
						}
					}
					action = queue.take();
				}
				if (currentMusic != null) {
					currentMusic.removeListener(this);
				}
				System.out.println("End of music");
			} catch (InterruptedException e) {
				System.err.println("Interrupted while waiting on music queue");
			}
		}

		public void nextSlice() {
			queue.offer(new QueueAction(QueueActionType.NEXT));
		}

		public void playTrack(String musicBaseName) {
			if (!queue.offer(new QueueAction(QueueActionType.PLAY, musicBaseName))) {
				System.err.println("Couldn't add to music queue: " + musicBaseName);
			}
		}

		@Override
		public void musicEnded(Music music) {
			queue.offer(new QueueAction(QueueActionType.LOOP, null));
		}

		@Override
		public void musicSwapped(Music music, Music music2) {
			// do nothing
		}

		public String getCurrentMusicName() {
			return currentMusicName;
		}

		public float getCurrentPosition() {
			return currentMusic.getPosition();
		}
	}

	private enum QueueActionType {
		/**
		 * Play a new track, starting immediately
		 */
		PLAY,
		/**
		 * Loop the current track again
		 */
		LOOP,
		/**
		 * Play the next slice in the track
		 */
		NEXT,
		/**
		 * End play
		 */
		END
	}

	private class QueueAction {
		private final QueueActionType type;
		private final String details;

		QueueAction(QueueActionType type, String details) {
			this.type = type;
			this.details = details;
		}

		QueueAction(QueueActionType type) {
			if (type == QueueActionType.PLAY) {
				System.err.println("PLAY queue action requires a track name");
			}
			this.type = type;
			this.details = null;
		}
	}
}
