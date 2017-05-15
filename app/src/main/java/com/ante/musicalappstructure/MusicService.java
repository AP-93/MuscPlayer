package com.ante.musicalappstructure;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;



/**
 * Created by Ante on 29/04/2017.
 * <p>
 * https://developer.android.com/reference/android/app/Service.html
 * https://www.tutorialspoint.com/android/android_services.htm
 * https://www.101apps.co.za/articles/all-about-services.html
 * <p>
 * list of songs are passed in service class and played using MediaPlayer class
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,     // implement some interfaces used for music playback
        MediaPlayer.OnCompletionListener {

    MediaPlayer player = null;      //media player
    private ArrayList<song> songs;   //song list
    private int songPosn, songPosnShuffle, rndCount;            //current position of song
    private ArrayList<Integer> shuffleSongsPos;


    // Binder given to clients
    private final IBinder musicBind = new MusicBinder();
    boolean paused;


    private Random rand;

    private String songTitle = "";
    private static final int NOTIFY_ID = 1;

    int bindCounter;

    boolean shuffle;

    double duration;
    int currPosition;

    public void onCreate() {


        super.onCreate();                //create the service, calls method onCreate from Service class



        player = new MediaPlayer();      //create player


        initMusicPlayer();

        setSong(0); //initialize position

        shuffle = false;

        rand = new Random();



    }

    ////////////////////////////BINDER/////////////////////////

    //https://developer.android.com/guide/components/bound-services.html
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this; // Return this instance of MusicService so clients can call public methods

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        bindCounter = 1;
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        bindCounter++;
        //release resources when the Service instance is unbound
        return false;
    }
/////////////////////////////////////////////////////////////////////


///////////////////MediaPlayer events ////////////////////////////////

    // method to initialize the MediaPlayer class
    public void initMusicPlayer() {


        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);                //wake lock will let playback continue when the device becomes idle
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);   //set the stream type to music

        player.setOnPreparedListener(this);                     // listener when the MediaPlayer instance is prepared
        player.setOnCompletionListener(this);                   // listener when a song has completed playback
        player.setOnErrorListener(this);                        // listener when an error is thrown
    }


    public void onCompletion(MediaPlayer mediaPlayer) {
        playNext();
    }


    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    //When the MediaPlayer is prepared, this method will be executet
    public void onPrepared(MediaPlayer mp) {

        duration = getDur();
        currPosition = getPosn();

        //start playback
        mp.start();


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setContentTitle("Playing")
                    .setContentText(songTitle)
                    .setSmallIcon(R.drawable.ic_music_player)
                    .setContentIntent(pendingIntent)
                    .setTicker(songTitle)
                    .build();
        }
        startForeground(NOTIFY_ID, notification);



    }

    //method  to pass the list of songs from the MainActivity
    public void setList(ArrayList<song> theSongs) {
        songs = theSongs;

    }

    //play a song
    public void playSong() {
        paused = false;
        player.reset();

        song playSong = songs.get(songPosn);
        long currSong = playSong.getID();      //get id

        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try {                                                               // try setting this URI as the data source for the MediaPlayer instance
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {

        }


        // calling the asynchronous method of the MediaPlayer to prepare it
        player.prepareAsync();
        songTitle = playSong.getTitle();


    }

    // set the current song, call this when the user picks a song from the list
    public void setSong(int songIndex) {
        songPosn = songIndex;



    }

    //Gets the current playback position.Returns the current position in milliseconds
    public int getPosn() {
        return player.getCurrentPosition();
    }

    //Gets the the duration in milliseconds of the file.
    public double getDur() {
        return player.getDuration();
    }

    //Checks whether the MediaPlayer is in started state. Does not know if player is paused or not
    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
        paused = true;
    }

    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();

    }

    //skip to next
    public void playNext() {
        if (!shuffle) {
            songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;
        } else if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        }
        playSong();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void startPlayer() {
        player.start();
        paused = false;
    }

    public String songName() {
        return songTitle;
    }


    public void setShuffleTrue() {

        shuffle = true;


    }

    public void setShuffleFalse() {

        shuffle = false;

    }












    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
