package music;

import music.MusicServiceStub.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.rmi.RemoteException;

import javax.swing.*;

public class Client extends JFrame implements ActionListener {
	private final static int contentInset = 5;
	private final static int trackColumns = 130;
	private final static int trackRows = 20;
	private final static int gridLeft = GridBagConstraints.WEST;
	private final static String programTitle = "Music Album";

	private GridBagConstraints contentConstraints = new GridBagConstraints();
	private GridBagLayout contentLayout = new GridBagLayout();
	private Container contentPane = getContentPane();
	private JButton discButton = new JButton("Check");
	private JLabel discLabel = new JLabel("Disc Number:");
	private JTextField discText = new JTextField(5);
	private JButton nameButton = new JButton("Check");
	private JLabel nameLabel = new JLabel("Composer/Artiste Name:");
	private JTextField nameText = new JTextField(16);
	private Font trackFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private JLabel trackLabel = new JLabel("Tracks:");
	private JTextArea trackArea = new JTextArea(trackRows, trackColumns);
	private JScrollPane trackScroller = new JScrollPane(trackArea);
	private Tracks tracks;
	private String errorMessage = "";

	// define private variable for  Client Stub
	private MusicServiceStub stub;

	public Client() throws Exception {
		contentPane.setLayout(contentLayout);
		addComponent(0, 0, gridLeft, nameLabel);
		addComponent(1, 0, gridLeft, nameText);
		addComponent(2, 0, gridLeft, nameButton);
		addComponent(0, 1, gridLeft, discLabel);
		addComponent(1, 1, gridLeft, discText);
		addComponent(2, 1, gridLeft, discButton);
		addComponent(0, 2, gridLeft, trackLabel);
		addComponent(0, 3, gridLeft, trackScroller);
		nameButton.addActionListener(this);
		discButton.addActionListener(this);
		trackArea.setFont(trackFont);
		trackArea.setEditable(false);

		// instantiate Client Stub and assign to private class variable
		 stub = new MusicServiceStub();

	}

	public static void main(String[] args) throws Exception {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		Client window = new Client();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle(programTitle);
		window.pack();
		int windowWidth = window.getWidth();
		int windowHeight = window.getHeight();
		int windowX = (screenWidth - windowWidth) / 2;
		int windowY = (screenHeight - windowHeight) / 2;
		window.setLocation(windowX, windowY);
		window.setVisible(true);
	}

	private void addComponent(int x, int y, int position, JComponent component) {
		Insets contentInsets = new Insets(contentInset, contentInset, contentInset, contentInset);
		contentConstraints.gridx = x;
		contentConstraints.gridy = y;
		contentConstraints.anchor = position;
		contentConstraints.insets = contentInsets;
		if (component == trackArea || component == trackLabel)
			contentConstraints.gridwidth = GridBagConstraints.REMAINDER;
		contentLayout.setConstraints(component, contentConstraints);
		contentPane.add(component);
	}

	public void actionPerformed(ActionEvent event) {
		String trackRows = "";
		TrackDetail[] tracks = null;
		try {
			if (event.getSource() == nameButton) {

				tracks = getField("composer", nameText.getText());

			} else if (event.getSource() == discButton) {

				tracks = getField("disc", discText.getText());
			}

			else
				return;
			trackRows += String.format("%4s %5s %-32s %-40s %-40s\n", "Disc", "Track", "Composer/Artist", "Work",
					"Title");

			for (int i = 0; i < tracks.length; i++) {
				TrackDetail trackDetail = tracks[i];

				//The data from each track in the array is extracted and input into a formatted string 
				//that is the list of tracks displayed to the user later
				String disc = trackDetail.getDiscNumber().toString();
				String track = trackDetail.getTrackNumber().toString();
				String comp = trackDetail.getComposerName();
				String work = trackDetail.getWorkName();
				String title = trackDetail.getTitleName();

				trackRows += String.format("%4s %5s %-32s %-40s %-40s\n", disc, track, comp, work, title);

			}

		} catch (Exception exception) {

			
			//The error faults that get caught and then are displayed to the user
			System.out.println(exception.getMessage());

			String error = exception.getMessage();
			if (error == null)
				error = exception.toString();
			error = "could not get tracks - " + error;
			trackRows += error;
		}
		trackArea.setText(trackRows);
	}

	private TrackDetail[] getField(String field, String value) throws RemoteException, ErrorFault {

		
		//The trackList and trackDetails which us used to return an array of tracks to the previous method for extracting data
		MusicServiceStub.TrackDetails trackList = new MusicServiceStub.TrackDetails();
		MusicServiceStub.TrackDetail trackDetails[] = null;
		
		
		//Checks which search box is being used
		//The value of the box is then set to composer/disc
		//The tracks from the search results are returned and assigned to the Tracks variable
		//tracklList is then assigned to the tracks.getTracks which returns a tracklist as an array of tracks
		//the trackDetails array is then set to this tracklist and then used to display the search results to the user
		if (field == "composer") {

			MusicServiceStub.Composer comp = new MusicServiceStub.Composer();
			comp.setComposer(value);

			tracks = stub.getByComposer(comp);

			trackList = tracks.getTracks();

			trackDetails = trackList.getTracklist();

		}

		if (field == "disc") {

			MusicServiceStub.Disc disc = new MusicServiceStub.Disc();

			disc.setDisc(value);

			tracks = stub.getByDisc(disc);

			trackList = tracks.getTracks();

			trackDetails = trackList.getTracklist();

		}

		return trackDetails;

	}

}
