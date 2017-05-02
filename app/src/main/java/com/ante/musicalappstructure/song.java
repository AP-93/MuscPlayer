package com.ante.musicalappstructure;

/**
 * Created by Ante on 29/04/2017.
 * <p>
 * Class used to model the data of single audio file
 */

public class song {

    private long id;          //audio file id
    private String title;     //audio file title
    private String artist;    //audio file artist name

    //constructor method in which we instantiate the variables
    public song(long songID, String songTitle, String songArtist) {
        id = songID;
        title = songTitle;
        artist = songArtist;
    }

    //get methods for the variables
    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

}
