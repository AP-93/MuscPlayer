package com.ante.musicalappstructure;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    TextView tvsongs, tvauthors, tvalbums, tvplaylist, infotv;
    ImageButton repeat, back, play, pause, foward, shuffle, search;
    EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infotv = (TextView) findViewById(R.id.infotv);   //// TODO: 29/04/2017 delete this after review

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

        repeat.setOnClickListener(this);
        back.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        foward.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        search.setOnClickListener(this);

        infotv.setOnClickListener(this);//for delete

        tvsongs.setOnClickListener(this);
        tvauthors.setOnClickListener(this);
        tvalbums.setOnClickListener(this);
        tvplaylist.setOnClickListener(this);

        tvsongs.setTextColor(Color.parseColor("#87bf34"));
        initSongs();

        //hides search bar after done is pressed on keyboard
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchBar.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.textViewSongs:

                tvsongs.setTextColor(Color.parseColor("#87bf34"));
                tvalbums.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);

                initSongs();
                break;

            case R.id.textViewAlbum:

                tvalbums.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);

                initAlbums();
                break;

            case R.id.textViewArtist:

                tvauthors.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvalbums.setTextColor(WHITE);
                tvplaylist.setTextColor(WHITE);

                initArtist();
                break;

            case R.id.textViewPlaylists:

                tvplaylist.setTextColor(Color.parseColor("#87bf34"));
                tvsongs.setTextColor(WHITE);
                tvalbums.setTextColor(WHITE);
                tvauthors.setTextColor(WHITE);

                initPlaylists();
                break;

            case R.id.imageButtonBack:
                break;

            case R.id.imageButtonNext:
                break;

            case R.id.imageButtonPause:

                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);

                break;

            case R.id.imageButtonPlay:

                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);

                break;

            case R.id.imageButtonRepeat:
                break;

            case R.id.imageButtonShuffle:
                break;

            case R.id.imageButtonSearch:

                searchBar.setVisibility(View.VISIBLE);

                break;

            case R.id.infotv:                                // for delete

                infotv.setVisibility(View.GONE);

                break;
        }
    }

     //create rows for table depending on how much songs we have
    //populate view with song names   // TODO: 28/04/2017 conect each view with song
    public void initSongs() {

        TableLayout stk = (TableLayout) findViewById(R.id.table);
        stk.removeAllViews();   //deletes all table rows

        for (int i = 0; i < 25; i++) {    // TODO: 28/04/2017 use number of song loaded in app ass i max

            TableRow tbrow = new TableRow(this);
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(             //defined row parameters
                    TableLayout.LayoutParams.MATCH_PARENT,     //row widith
                    TableLayout.LayoutParams.WRAP_CONTENT,     //row height
                    Gravity.CENTER_VERTICAL);                  //row gravity

            rowParams.setMargins(0, 1, 0, 1); //left,top,right,bottom

            tbrow.setLayoutParams(rowParams);       //add created parameters to created row
            tbrow.setBackgroundResource(R.drawable.change_color_when_pressed);
            tbrow.setId(i);

            ImageView img = new ImageView(this);    //image for row
            img.setBackgroundResource(R.drawable.ic_music_player);  // default image for song if there is no img  TODO: 28/04/2017 check if song hase image to display it
            img.setAdjustViewBounds(true);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);


            TextView t1v = new TextView(this);    //song and author name
            String songName = "song name";
            String authorName = "author name";
            final SpannableString text = new SpannableString(songName + "\n" + authorName);   //alows us to use different text size for each letter in single textview
            text.setSpan(new RelativeSizeSpan(1.8f), 0, songName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new RelativeSizeSpan(1), 0, authorName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            t1v.setTextColor(WHITE);
            t1v.setText(text);
            t1v.setPadding(32, 10, 10, 10);

            tbrow.addView(img);
            tbrow.addView(t1v);

            tbrow.setOnClickListener(new View.OnClickListener() {     //adds onclickListener to each table row
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PlaySongActivity.class);
                    startActivity(intent);
                }
            });

            stk.addView(tbrow, rowParams);      //add created row to table
        }

    }


    private void initPlaylists() {
    }    //// TODO: 29/04/2017 create playlists and add option to manage them

    private void initArtist() {          //similar to initSongs

        TableLayout stk = (TableLayout) findViewById(R.id.table);

        stk.removeAllViews();

        for (int i = 0; i < 25; i++) {    // TODO: 28/04/2017 use number of song loaded in app ass i max

            TableRow tbrow = new TableRow(this);
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL);

            rowParams.setMargins(0, 1, 0, 1); //left,top,right,bottom

            tbrow.setLayoutParams(rowParams);
            tbrow.setBackgroundResource(R.drawable.change_color_when_pressed);
            tbrow.setId(i);

            ImageView img = new ImageView(this);
            img.setBackgroundResource(R.drawable.ic_music_player);  // default image for song if there is no img  TODO: 28/04/2017 check if song hase image to display it
            img.setAdjustViewBounds(true);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);


            TextView t1v = new TextView(this);
            String songName = "song name";
            String authorName = "author name";
            final SpannableString text = new SpannableString(authorName + "\n" + songName);
            text.setSpan(new RelativeSizeSpan(1.8f), 0, authorName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new RelativeSizeSpan(1), 0, songName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            t1v.setTextColor(WHITE);
            t1v.setText(text);
            t1v.setPadding(32, 10, 10, 10);

            tbrow.addView(img);
            tbrow.addView(t1v);

            tbrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PlaySongActivity.class);
                    startActivity(intent);
                }
            });

            stk.addView(tbrow, rowParams);
        }
    }

    private void initAlbums() {
    }    //// TODO: 29/04/2017 add option to read album name from song if there is such info

}