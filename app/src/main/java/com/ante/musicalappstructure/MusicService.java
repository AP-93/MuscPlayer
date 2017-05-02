package com.ante.musicalappstructure;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.ArrayList;

import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;


import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.view.View;
import android.widget.ImageButton;

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

    private MediaPlayer player;      //media player
    private ArrayList<song> songs;   //song list
    private int songPosn;            //current position of song

    private final IBinder musicBind = new MusicBinder();    //  instance variable representing the  inner Binder class


    private String songTitle = "";
    private static final int NOTIFY_ID = 1;

    public void onCreate() {


        super.onCreate();                //create the service, calls method onCreate from Service class

        songPosn = 0;                      //initialize position

        player = new MediaPlayer();      //create player


        initMusicPlayer();
    }

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


    //method  to pass the list of songs from the MainActivity
    public void setList(ArrayList<song> theSongs) {
        songs = theSongs;
    }


    //https://developer.android.com/guide/components/bound-services.html
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this; // Return this instance of MusicService so clients can call public methods

        }
    }


    //play a song
    public void playSong() {

        player.reset();

        song playSong = songs.get(songPosn);   //get song
        long currSong = playSong.getID();      //get id

        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try {                                                               // try setting this URI as the data source for the MediaPlayer instance
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        // calling the asynchronous method of the MediaPlayer to prepare it
        player.prepareAsync();
        songTitle = playSong.getTitle();
    }

    // set the current song, call this when the user picks a song from the list
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Playing")
                .setContentText(songTitle)
                .setSmallIcon(R.drawable.ic_music_player)
                .setContentIntent(pendingIntent)
                .setTicker(songTitle)
                .build();
        startForeground(NOTIFY_ID, notification);

        player.stop();
        player.release();         //release resources when the Service instance is unbound
        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    //When the MediaPlayer is prepared, this method will be executet
    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();


    }


    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        songPosn++;
        if (songPosn >= songs.size()) songPosn = 0;
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
