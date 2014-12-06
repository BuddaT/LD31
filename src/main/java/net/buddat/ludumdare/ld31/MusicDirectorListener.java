package net.buddat.ludumdare.ld31;

/**
 * Listens to MusicDirector actions.
 */
public interface MusicDirectorListener {
	public void onMusicChanged(String oldTrack, float oldPosition, int oldBpm, String newTrack, int newBpm);
}
