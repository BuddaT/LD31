package net.buddat.ludumdare.ld31.music;

/**
 * Listens to MusicDirector actions.
 */
public interface MusicDirectorListener {
	public void onTrackChanged(String oldTrack, float oldPosition, int oldBpm, String newTrack, int newBpm);

	public void onSliceChanged(String musicBaseName, int oldSlice, int newSlice);
}
