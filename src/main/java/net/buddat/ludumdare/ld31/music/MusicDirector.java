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
public class MusicDirector implements MusicListener, Runnable {

	public static final int MAX_VOLUME = 10;
	private static final Map<String, Integer> BEATS_PER_MINUTE;
	private static final Map<String, Music> MUSICS;
	/**
	 * Number of slices for a given track
	 */
	private static final Map<String, Integer> SLICES;
	private static final String SLICE_EXT = ".ogg";
	private static final String MUSIC_DIR = "music/";

	private static final int NO_SLICE = -1;

	public static final String TITLE_TRACK = "chipshit_128.ogg";
	private static final String DEFAULT_TRACK = "level1_123";
	// Probably better done as metadata but whatever for now
	private static final String[] LEVEL_TRACKS = new String[3];

	static {
		HashMap<String, Integer> bpm = new HashMap<String, Integer>();
		HashMap<String, Music> musics = new HashMap<String, Music>();
		HashMap<String, Integer> slices = new HashMap<String, Integer>();

		// First full tracks
		bpm.put(TITLE_TRACK, 128);
		bpm.put("thememaybe_172.ogg", 172);
		for (String musicName : bpm.keySet()) {
			musics.put(musicName, loadMusic(musicName));
		}

		// Now add slices
		LEVEL_TRACKS[0] = "level1_123";
		bpm.put(LEVEL_TRACKS[0], 123);
		LEVEL_TRACKS[1] = "level2_123";
		bpm.put(LEVEL_TRACKS[1], 123);
		LEVEL_TRACKS[2] = "level3_140";
		bpm.put(LEVEL_TRACKS[1], 140);

		for (String slicedTrack : LEVEL_TRACKS) {
			slices.put(slicedTrack, 4); // all are sliced into 4 at the moment
		}
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

	/**
	 * Returns the track name for the given level number
	 * @param levelNumber Level number for which the track is returned
	 * @return Track for the given level
	 */
	public String getMusicForLevel(int levelNumber) {
		return LEVEL_TRACKS.length >= levelNumber
				|| LEVEL_TRACKS[levelNumber - 1] == null ?
				DEFAULT_TRACK : LEVEL_TRACKS[levelNumber - 1];
	}

	public int getNumSlices(String sliceName) {
		return SLICES.containsKey(sliceName) ? SLICES.get(sliceName) : NO_SLICE;
	}

	private final BlockingQueue<QueueAction> queue = new ArrayBlockingQueue<QueueAction>(QUEUE_SIZE);
	private int volume = 10;
	// Wrap music info in single volatile. Should only be written to in one thread
	private volatile MusicDetails currentMusicDetails;

	private final MusicDirectorListener listener;

	public MusicDirector(MusicDirectorListener listener) throws SlickException {
		this.currentMusicDetails = new MusicDetails("", NO_SLICE, null);
		this.listener = listener;
	}

	/**
	 * Sets music to a random track, for testing
	 */
	public void randomTrack() {
		Object[] musicNames = BEATS_PER_MINUTE.keySet().toArray();
		String musicName = (String) musicNames[(int) (Math.random() * musicNames.length)];
		System.out.println("Setting to random track " + musicName);
		playTrack(musicName);
	}

	/**
	 * @return Current position of the music.
	 */
	public float getPosition() {
		return currentMusicDetails.music.getPosition();
	}

	/**
	 * @return Beats per minute of the music currently playing.
	 */
	public int getBpm() {
		return BEATS_PER_MINUTE.get(getCurrentMusicName());
	}

	@Override
	public void run() {
		try {
			boolean goToNext = false;
			QueueAction action = queue.take();
			while (action != null && action.type != QueueActionType.END) {
				if (action.type == QueueActionType.PLAY_ENDED) {
					// Only want to loop if there are no other actions
					if (goToNext) {
						MusicDetails oldDetails = currentMusicDetails;
						int currentSlice = (oldDetails.slice + 1) % SLICES.get(oldDetails.track);
						if (oldDetails != null) {
							System.out.println("Switching slices, old slice " + oldDetails.slice);
						}
						Music music = MUSICS.get(generateSliceName(oldDetails.track, currentSlice));
						music.addListener(this);
						System.out.println("Playing next slice, track " + oldDetails.track + " slice " + currentSlice);
						music.play();
						listener.onSliceChanged(oldDetails.track, oldDetails.slice, currentSlice);
						currentMusicDetails = new MusicDetails(oldDetails.track, currentSlice, music);
						goToNext = false;
					} else if (!queue.isEmpty()) {
						continue;
					} else if (currentMusicDetails.music == null) {
						System.err.println("Null music when PLAY requested");
					} else if (!currentMusicDetails.music.playing()) {
						currentMusicDetails.music.addListener(this);
						currentMusicDetails.music.play();
					}
				} if (action.type == QueueActionType.PLAY) {
					if (action.details == null) {
						System.err.println("Null music details supplied for " + action.type + " request");
					} else {
						MusicDetails oldDetails = currentMusicDetails;
						System.out.println("Next track queued: " + (String) action.details);
						currentMusicDetails = new MusicDetails((String) action.details);
					}
				} else if (action.type == QueueActionType.PLAY_IMMEDIATE) {
					goToNext = false;
					String oldTrack = "";
					float oldPosition = 0;
					int oldBpm = 0;
					MusicDetails oldMusicDetails = currentMusicDetails;
					if (oldMusicDetails.music != null) {
						oldMusicDetails.music.removeListener(this);
						oldPosition = oldMusicDetails.music.getPosition();
						oldBpm = BEATS_PER_MINUTE.get(oldMusicDetails.track);
					}
					currentMusicDetails = new MusicDetails((String) action.details);
					currentMusicDetails.music.addListener(this);
					System.out.println("Playing immediate track "
							+ currentMusicDetails.track
							+ (currentMusicDetails.slice == NO_SLICE ? "" : " slice " + currentMusicDetails.slice));
					currentMusicDetails.music.play();
					listener.onTrackChanged(oldTrack, oldPosition, oldBpm, currentMusicDetails.track, BEATS_PER_MINUTE.get(currentMusicDetails.track));
				} else if (action.type == QueueActionType.NEXT) {
					// On the next loop notification,
					if (goToNext) {
						// Next slice already queued for playing
					} else if (SLICES.containsKey(currentMusicDetails.track)) {
						System.out.println("Next slice queued for playing, waiting for end of current slice");
						goToNext = true;
					} else {
						System.err.println("Can't take next slice, track " + currentMusicDetails.track + " isn't sliced");
					}
				}
				action = queue.take();
			}
			if (currentMusicDetails.music != null) {
				currentMusicDetails.music.removeListener(this);
				currentMusicDetails.music.stop();
			}
			System.out.println("End of music");
		} catch (InterruptedException e) {
			System.err.println("Interrupted while waiting on music queue");
		}
	}

	/**
	 * Progress to the next slice.
	 */
	public void nextSlice() {
		queue.offer(new QueueAction(QueueActionType.NEXT));
	}

	/**
	 * Progress to the next slice if current progression exceeds the slice position in the music track
	 * @param position Current position
	 * @param end End position
	 */
	public void ratchetSlice(int position, int end) {
		// race conditions here but don't expect any problems for most cases
		MusicDetails musicDetails = currentMusicDetails;
		if (musicDetails.track == null || !SLICES.containsKey(musicDetails.track)) {
			return;
		}
		float sliceProgression = ((float) musicDetails.slice + 1) / SLICES.get(musicDetails.track);
		float levelProgression = ((float) position) / end;
		if (levelProgression > sliceProgression) {
			nextSlice();
		}
	}

	public void playTrack(String musicBaseName) {
		if (!queue.offer(new QueueAction(QueueActionType.PLAY_IMMEDIATE, musicBaseName))) {
			System.err.println("Couldn't add to music queue: " + musicBaseName);
		}
	}

	public void queueTrack(String musicBaseName) {
		if (!queue.offer(new QueueAction(QueueActionType.PLAY, musicBaseName))) {
			System.err.println("Couldn't add next track to music queue: " + musicBaseName);
		}
	}

	public void increaseVolume() {
		volume = Math.min(volume + 1, MAX_VOLUME);
		setVolume(volume);
	}

	public void decreaseVolume() {
		volume = Math.max(volume - 1, 0);
		setVolume(volume);
	}

	public void setVolume(int volume) {
		for (String musicName : MUSICS.keySet()) {
			MUSICS.get(musicName).setVolume(volume / (float) MAX_VOLUME);
		}
	}

	public int getVolume() {
		return volume;
	}

	@Override
	public void musicEnded(Music music) {
		music.removeListener(this);
		if (!queue.offer(new QueueAction(QueueActionType.PLAY_ENDED, null))) {
			System.err.println("Couldn't offer end loop, queue is full");
		}
	}

	@Override
	public void musicSwapped(Music music, Music music2) {
		// do nothing
	}

	public void stop() {
		queue.offer(new QueueAction(QueueActionType.END));
	}

	public String getCurrentMusicName() {
		return currentMusicDetails.track;
	}

	private class MusicDetails {
		private final String track;
		private final int slice;
		private final Music music;
		public MusicDetails(String track) {
			this.track = track;
			if (SLICES.containsKey(track)) {
				slice = 0;
				music = MUSICS.get(generateSliceName(track, slice));
			} else {
				slice = NO_SLICE;
				if (MUSICS.containsKey(track)) {
					music = MUSICS.get(track);
				} else {
					music = null;
				}
			}
		}
		public MusicDetails(String track, int slice, Music music) {
			this.track = track;
			this.slice = slice;
			this.music = music;
		}
	}

	private enum QueueActionType {
		/**
		 * Play a new track, starting immediately
		 */
		PLAY_IMMEDIATE,
		/**
		 * Play the next track at the end of the previous track
		 */
		PLAY,
		/**
		 * Last track has completed play
		 */
		PLAY_ENDED,
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
		private final Object details;

		QueueAction(QueueActionType type, Object details) {
			this.type = type;
			this.details = details;
		}

		QueueAction(QueueActionType type) {
			if (type == QueueActionType.PLAY_IMMEDIATE) {
				System.err.println("PLAY_IMMEDIATE queue action requires a track name");
			}
			this.type = type;
			this.details = null;
		}
	}
}
