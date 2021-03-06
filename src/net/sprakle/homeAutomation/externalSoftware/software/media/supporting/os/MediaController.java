package net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public abstract class MediaController {

	protected ArrayList<Track> tracks;
	protected Logger logger;

	protected MediaController(Logger logger) {
		this.logger = logger;
	}

	public abstract void loadTracks();

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public abstract void playbackCommand(PlaybackCommand pc);

	public void playTrack(String title, String artist) {
		Track result = levenGet(title, artist, 20);

		if (result == null) {
			logger.log("Unable to find requested track", LogSource.EXTERNAL_SOFTWARE, 2);
			return;
		}

		logger.log("Playing '" + result.getTitle() + "' by '" + result.getArtist(), LogSource.EXTERNAL_SOFTWARE, 2);
		playTrack(result);
	}
	protected abstract void playTrack(Track track);

	public void enqueueTrack(String title, String artist) {
		Track result = levenGet(title, artist, 20);

		if (result == null) {
			logger.log("Unable to find requested track", LogSource.EXTERNAL_SOFTWARE, 2);
			return;
		}

		logger.log("Enqueuing '" + result.getTitle() + "' by '" + result.getArtist(), LogSource.EXTERNAL_SOFTWARE, 2);
		enqueueTrack(result);
	}
	protected abstract void enqueueTrack(Track track);

	public abstract void setVolume(double vol);
	public abstract void changeVolume(double change);

	public void playRandomTrack(String artist) {
		ArrayList<Track> pool = null;

		// create pool of possibilities
		if (artist != null) {
			TreeMap<Integer, Track> treeMap = levenGetMulti(null, artist, 5);

			if (treeMap.size() < 1) {
				logger.log("Unable to find requested track", LogSource.EXTERNAL_SOFTWARE, 2);
				return;
			}

			pool = new ArrayList<>(treeMap.values());

		} else {
			pool = tracks;
		}

		// choose track from pool
		int max = pool.size();
		int index = (int) (Math.random() * max);

		Track result = pool.get(index);

		logger.log("Playing random song: '" + result.getTitle() + "' by '" + result.getArtist(), LogSource.EXTERNAL_SOFTWARE, 2);
		playTrack(result);
	}

	/**
	 * Search for a single track using levenshtein. (title || artist) || (title
	 * && artist) must NOT be null. Null values will be ignored
	 * 
	 * @param title
	 *            title of track wanted
	 * @param artist
	 *            artist name of track wanted
	 * @param maxDistance
	 *            max levenshtein distance to search
	 * @return returns the track with the smallest distance
	 */
	public Track levenGet(String title, String artist, int maxDistance) {
		Track result = null;

		TreeMap<Integer, Track> distances = levenGetMulti(title, artist, maxDistance);

		if (distances.firstEntry() == null)
			return null;

		result = distances.firstEntry().getValue();

		return result;
	}

	/**
	 * Search for multiple tracks using levenshtein. (title || artist) || (title
	 * && artist) must NOT be null. Null values will be ignored
	 * 
	 * @param title
	 *            title of track wanted
	 * @param artist
	 *            artist name of track wanted
	 * @param maxDistance
	 *            max levenshtein distance to search
	 * @return all tracks with a distance below maxDistance
	 */
	public TreeMap<Integer, Track> levenGetMulti(String title, String artist, int maxDistance) {
		TreeMap<Integer, Track> distances = new TreeMap<>();

		boolean useTitle = title != null;
		boolean useArtist = artist != null;

		for (Track t : tracks) {

			int titleDistance = 0;
			int artistDistance = 0;

			if (useTitle)
				titleDistance = t.levenshteinDistanceTitle(title);

			if (useArtist)
				artistDistance = t.levenshteinDistanceArtist(artist);

			int totalDistance = titleDistance + artistDistance;
			if (totalDistance < maxDistance)
				distances.put(totalDistance, t);

		}

		return distances;
	}

	public abstract Track getCurrentTrack();
}
