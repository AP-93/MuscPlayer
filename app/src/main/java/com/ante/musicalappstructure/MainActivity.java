package com.ante.musicalappstructure;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.widget.Toast;

import com.ante.musicalappstructure.MusicService.MusicBinder;



import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {


    //true-play     false-pause
    LinearLayout controler;
    TextView tvsongs, tvauthors, tvalbums, tvplaylist, currentTime, totalTime, displayName;
    ImageButton repeat, back, play, pause, foward, shuffleEnabled, shuffleDisabled, search;
    EditText searchBar;
    SeekBar seekBar;

    private int startTime = 0;
    private int finalTime = 0;

    private ListView songView;

    private ArrayList<song> songList;  //array list populated by audio files defined by song class


    //  music is played in the Service class, but controled  from the MainActivity class, where the application's user interface operates
    private MusicService musicSrv;  //variable representing the Service class
    private Intent playIntent;
    private boolean musicBound = false;  // flag to keep track of whether the MainActivity class is bound to the Service class or not

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }


        songList = new ArrayList<>();     //Instantiate the list

        songView = (ListView) findViewById(R.id.song_list);

        displayName = (TextView) findViewById(R.id.textViewDisplayName);
        tvsongs = (TextView) findViewById(R.id.textViewSongs);
        tvauthors = (TextView) findViewById(R.id.textViewArtist);
        tvalbums = (TextView) findViewById(R.id.textViewAlbum);
        tvplaylist = (TextView) findViewById(R.id.textViewPlaylists);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalTime = (TextView) findViewById(R.id.duration);

        searchBar = (EditText) findViewById(R.id.editTextSearchSong);

        repeat = (ImageButton) findViewById(R.id.imageButtonRepeat);
        back = (ImageButton) findViewById(R.id.imageButtonBack);
        play = (ImageButton) findViewById(R.id.imageButtonPlay);
        pause = (ImageButton) findViewById(R.id.imageButtonPause);
        foward = (ImageButton) findViewById(R.id.imageButtonNext);
        shuffleEnabled = (ImageButton) findViewById(R.id.shuffleEnabled);
        shuffleDisabled = (ImageButton) findViewById(R.id.shufflleDisabled);
        search = (ImageButton) findViewById(R.id.imageButtonSearch);

        controler = (LinearLayout) findViewById(R.id.audioControler);


        seekBar = (SeekBar) findViewById(R.id.seekBar);

        repeat.setOnClickListener(this);
        back.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        foward.setOnClickListener(this);
        shuffleEnabled.setOnClickListener(this);
        shuffleDisabled.setOnClickListener(this);
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


        controler.setVisibility(View.GONE);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicSrv.seek(seekBar.getProgress());

            }
        });
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
            startTime = musicSrv.bindCounter;
            musicBound = true;

            if (musicSrv.bindCounter > 1) {
                pauseOrPlayButton();
                controler.setVisibility(View.VISIBLE);
            }


            updateTimer();
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

        startTime = 2;
        updateTimer();
        musicSrv.playSong();  // calling the method to start the playback


        pauseOrPlayButton();

        controler.setVisibility(View.VISIBLE);

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

            case R.id.shufflleDisabled:

                musicSrv.setShuffleTrue();

                shuffleDisabled.setVisibility(View.GONE);
                shuffleEnabled.setVisibility(View.VISIBLE);
                break;
            case R.id.shuffleEnabled:
                musicSrv.setShuffleFalse();

                shuffleEnabled.setVisibility(View.GONE);
                shuffleDisabled.setVisibility(View.VISIBLE);


            case R.id.imageButtonSearch:
                searchBar.setVisibility(View.VISIBLE);
                break;


        }
    }


    public void pause() {

        musicSrv.pausePlayer();

        pauseOrPlayButton();

    }


    public void start() {
        musicSrv.startPlayer();
        pauseOrPlayButton();

    }

    //play next
    private void playNext() {
        musicSrv.playNext();

        pauseOrPlayButton();
    }

    //play previous
    private void playPrev() {
        musicSrv.playPrev();
        pauseOrPlayButton();

    }

    public void pauseOrPlayButton() {
        if (musicSrv.paused) {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        } else if (!musicSrv.paused) {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }

    }

    public void updateTimer() {


        mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (musicBound && startTime > 1) {
                    displayName.setText(musicSrv.songName());

                    int mCurrentPosition = musicSrv.getPosn();


                    seekBar.setProgress(mCurrentPosition);
                    currentTime.setText(String.format("%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition))
                    ));

                    finalTime = (int) musicSrv.getDur();
                    seekBar.setMax(finalTime);
                    totalTime.setText(String.format("%02d : %02d ",
                            TimeUnit.MILLISECONDS.toMinutes(finalTime),
                            TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime))
                    ));


                }


                mHandler.postDelayed(this, 500);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}