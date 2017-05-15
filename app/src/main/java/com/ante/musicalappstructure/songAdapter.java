package com.ante.musicalappstructure;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.attr.data;

/**
 * Created by Ante on 29/04/2017.
 * <p>
 * The Adapter provides access to the data items. The Adapter is also responsible for making a View for each item in the data set.
 */

public class songAdapter extends BaseAdapter {

    private LayoutInflater songInf;
    private ArrayList<song> songs;
    // pass the song list from the main Activity class and use the LayoutInflater to map the title and artist strings to the TextViews in the song layout


    // constructor method to instantiate instance variables
    public songAdapter(Context context, ArrayList<song> theSongs) {
        songs = theSongs;
        songInf = LayoutInflater.from(context);         //Obtains the LayoutInflater from the given context
        //The LayoutInflater takes layout XML-files and creates different View-objects from its contents.
    }

    @Override
    public int getCount() {
        return songs.size();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {      //// TODO: 30/04/2017  add viewHolder
        //map to song layout

        View songLay = convertView;
        if (songLay == null) {
            songLay = songInf.inflate(R.layout.song, parent, false);

        }
        //get title and artist views
        TextView songView = (TextView) songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
        //get song using position
        song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);          //We set the song position as the tag for each item in the list view when we defined the Adapter class.
        return songLay;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
