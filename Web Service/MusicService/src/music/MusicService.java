package music;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MusicService extends MusicServiceSkeleton {

	private final static String databaseHost = "mysql0.cs.stir.ac.uk";
	private final static String databaseName = "mko";
	private final static String databasePassword = "mko";
	private final static String databaseUser = "mko";
	private final static String discTable = "music";

	private String errorMessage = "";


	//the getby methods are the two operations of the service. They both get the multiple trackDetail complext types in the form of the trackDetails
	//These details are then set up as tracks which is the array of the different tracks returned
	//The errors are thrown here to be caught by the client
	public music.Tracks getByComposer(music.Composer composer) throws ErrorFault {

		TrackDetail[] trackDetails = null;
		TrackDetails trackList = new TrackDetails();
		Tracks tracks = new Tracks();

		String composerName = composer.getComposer();

		try {
			trackDetails = getByField("composer", composerName);
		} catch (ErrorFault e) {
			throw (new ErrorFault(errorMessage));
		}

		trackList.setTracklist(trackDetails);
		tracks.setTracks(trackList);

		return tracks;

	}

	public music.Tracks getByDisc(music.Disc disc) throws ErrorFault {

		TrackDetail[] trackDetails = null;
		TrackDetails trackList = new TrackDetails();
		Tracks tracks = new Tracks();

		String discNum = disc.getDisc();

		try {
			trackDetails = getByField("disc", discNum);
		} catch (ErrorFault e) {
			throw (new ErrorFault(errorMessage));
		}

		trackList.setTracklist(trackDetails);
		tracks.setTracks(trackList);

		return tracks;
	}

	private TrackDetail[] getByField(String field, String value) throws ErrorFault {
		try {
			if (value.length() == 0) {
				//error message for no composer/disc entered
				errorMessage = ("Please enter a " + field);
				throw (new ErrorFault(errorMessage));
			}
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String databaseDesignation = "jdbc:mysql://" + databaseHost + "/" + databaseName + "?user=" + databaseUser
					+ "&password=" + databasePassword;
			Connection connection = DriverManager.getConnection(databaseDesignation);
			Statement statement = connection.createStatement();
			String query = "SELECT disc, track, composer, work, title " + "FROM " + discTable + " " + "WHERE " + field
					+ " LIKE '%" + value + "%'";
			ResultSet result = statement.executeQuery(query);
			result.last();
			int resultCount = result.getRow();	
			if (resultCount == 0) {
				//error message for no returned results
				if (field == "disc"){
					errorMessage = (field + " '" + value + "' not found (Please enter a valid number ie. '2')");	
				}
				else{
					errorMessage = (field + " '" + value + "' not found");	
				}
				throw (new ErrorFault(errorMessage));
			}

			TrackDetail[] trackDetails = new TrackDetail[resultCount];
			result.beforeFirst();
			int resultIndex = 0;
			while (result.next()) {
				TrackDetail detail = new TrackDetail();

				//The track details from the databasde being added to a trackDetail variable
				//These tracks are added to the trackDetails array which is the group of tracks from the results.

				detail.setDiscNumber(result.getString(1));
				detail.setTrackNumber(result.getString(2));
				detail.setComposerName(result.getString(3));
				detail.setWorkName(result.getString(4));
				detail.setTitleName(result.getString(5));

				//array of tracks
				trackDetails[resultIndex++] = detail;
			}
			connection.close();
			return (trackDetails);
		} catch (Exception exception) {

			if(errorMessage.equals("")) errorMessage = "database access error - " + exception.getMessage();
			throw (new ErrorFault(errorMessage, exception));
		}
	}
}
