package com.ante.musicalappstructure;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

import com.ante.musicalappstructure.MusicService.MusicBinder;



import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    Boolean playOrPause;  //true-play     false-pause
    LinearLayout controler;
    TextView tvsongs, tvauthors, tvalbums, tvplaylist, infotv;
    ImageButton repeat, back, play, pause, foward, shuffle, search;
    EditText searchBar;
    private ListView songView;

    private ArrayList<song> songList;  //array list populated by audio files defined by song class

    //  music is played in the Service class, but controled  from the MainActivity class, where the application's user interface operates
    private MusicService musicSrv;  //variable representing the Service class
    private Intent playIntent;
    private boolean musicBound = false;  // flag to keep track of whether the MainActivity class is bound to the Service class or not



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        songList = new ArrayList<>();     //Instantiate the list

        songView = (ListView) findViewById(R.id.song_list);

        tvsongs = (TextView) findViewById(R.id.textViewSongs);
        tvauthors = (TextView) findViewById(R.id.textViewArtist);
        tvalbums = (TextView) findViewById(R.id.textViewAlbum);
        tvplaylist = (TextView) findViewById(R.id.textViewPlaylists);

        searchBar = (EditText) findViewById(R.id.editTextSearchSong);

        repeat = (ImageButton) findViewById(R.id.imageButtonRepeat);
        back = (ImageButton) findViewById(R.id.imageButtonBack);
        play = (ImageButton) findViewById(R.id.imageButtonPlay);
        pause = (ImageButton) findViewById(R.id.imageButtonPause);
        foward = (ImageButton) findViewById(R.id.imageButtonNext);
        shuffle = (ImageButton) findViewById(R.id.imageButtonShuffle);
        search = (ImageButton) findViewById(R.id.imageButtonSearch);

        controler = (LinearLayout) findViewById(R.id.audioControler);

        repeat.setOnClickListener(this);
        back.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        foward.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        search.setOnClickListener(this);

        tvsongs.setOnClickListener(this);
        tvauthors.setOnClickListener(this);
        tvalbums.setOnClickListener(this);
        tvplaylist.setOnClickListener(this);

        tvsongs.setTextColor(Color.parseColor("#87bf34"));


        getSongList();

        //sort songs in alphabetical order
        Collections.sort(songList, new Comparator<song>() {
            public int compare(song a, song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //creates a new instance of the Adapter class and sets it on the ListView
        songAdapter songAdt = new songAdapter(this, songList);
        songView.setAdapter(songAdt);


        //setController();
    }

    //connect to the service
    //Defines callbacks for service binding, passed to bindService()
    private ServiceConnection musicConnection = new ServiceConnection() {

        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicBinder binder = (MusicBinder) service;

            musicSrv = binder.getService();    //get service
            musicSrv.setList(songList);        //pass list

            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //method to retrieve the audio file information
    public void getSongList() {

        ContentResolver musicResolver = getContentResolver();                         // create a ContentResolver instance
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  //retrieve the URI for external music files
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);   //creates a Cursor instance using the ContentResolver instance to query the music files

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        //close cursor
        if (musicCursor != null) {
            musicCursor.close();
        }
    }

    //start the Service instance when the Activity instance starts
    @Override
    protected void onStart() {
        super.onStart();                                                              //When the MainActivity instance starts
        if (playIntent == null) {                                                         // we create the Intent object if it doesn't exist yet
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);       //, bind to it
            startService(playIntent);                                                 // , and start it
        }

    }

    //onClick method for each song in listView
    public void songPicked(View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));   //retrieve  song position as the tag for each item in the list view  and pass it to the Service instance
        musicSrv.playSong();  // calling the method to start the playback

        controler.setVisibility(View.VISIBLE);
        playOrPause = true;
        pauseOrPlayButton(playOrPause);

    }


    //protected void onDestroy() {
    //  stopService(playIntent);
    //   musicSrv=null;
    //   super.onDestroy();
    // }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.textViewSongs:
                tvsongs.setTextColor(Color.parseColor("#87bf34"));
                tvalbums.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);
                // initSongs();
                break;

            case R.id.textViewAlbum:
                tvalbums.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);
                // initAlbums();
                break;

            case R.id.textViewArtist:
                tvauthors.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvalbums.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);
                //  initArtist();
                break;

            case R.id.textViewPlaylists:
                tvplaylist.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvalbums.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);
                // initPlaylists();
                break;

            case R.id.imageButtonBack:
                playPrev();
                break;

            case R.id.imageButtonNext:
                playNext();
                break;

            case R.id.imageButtonPause:
                pause();
                break;

            case R.id.imageButtonPlay:
                start();
                break;

            case R.id.imageButtonRepeat:
                break;

            case R.id.imageButtonShuffle:
                break;

            case R.id.imageButtonSearch:
                searchBar.setVisibility(View.VISIBLE);
                break;


        }
    }


    public void pause() {

        musicSrv.pausePlayer();
        playOrPause = false;
        pauseOrPlayButton(playOrPause);
    }


    public boolean isPlaying() {
        if (musicSrv != null && musicBound) {
            return musicSrv.isPng();
        }
        return false;
    }

    public void start() {
        musicSrv.go();
        playOrPause = true;
        pauseOrPlayButton(playOrPause);
    }

    //play next
    private void playNext() {
        musicSrv.playNext();
        playOrPause = true;
        pauseOrPlayButton(playOrPause);
    }

    //play previous
    private void playPrev() {
        musicSrv.playPrev();
        playOrPause = true;
        pauseOrPlayButton(playOrPause);

    }

    public void pauseOrPlayButton(boolean playOrPause) {
        if (playOrPause == false) {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
    } else {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }

    }

}