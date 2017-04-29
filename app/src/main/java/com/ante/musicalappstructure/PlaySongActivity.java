package com.ante.musicalappstructure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.ante.musicalappstructure.R.id.songName;

public class PlaySongActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvsongName, tvinfo2;
    ImageButton displayInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);


        tvinfo2 = (TextView) findViewById(R.id.infotv2);
        displayInfo = (ImageButton) findViewById(R.id.imageButtonDisplayInfo);
        tvsongName = (TextView) findViewById(songName);

        displayInfo.setOnClickListener(this);
        tvinfo2.setOnClickListener(this);


        String songName = "song name";
        String authorName = "author name";

        final SpannableString text = new SpannableString(songName + "\n" + authorName);
        text.setSpan(new RelativeSizeSpan(1.8f), 0, songName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new RelativeSizeSpan(1), 0, authorName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvsongName.setText(text);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageButtonDisplayInfo:
                Intent intent = new Intent(view.getContext(), songInfo.class);
                startActivity(intent);
                break;

            case R.id.infotv2:
                tvinfo2.setVisibility(View.GONE);
                break;

        }
    }
}