package group7.android.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group7.android.mediaplayerg7.AddmusicActivity;
import group7.android.mediaplayerg7.R;
import group7.android.model.Music;

public class AddMusicAdapter extends ArrayAdapter<Music> {
    Activity context;
    int resource;
    List<Music> objects;

    ImageButton btnAddMusic, btnAdded;
    int[] add = new int[1000];

    String idplaylist, nameplaylist;

    public AddMusicAdapter(Activity context, int resource, List<Music> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
        for (int i=0; i<1000; i++)
        {
            add[i]=1;
        }
        Toast.makeText(context, AddmusicActivity.idpl+" "+AddmusicActivity.namepl, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource,null);
        TextView txtNameSong = (TextView)row.findViewById(R.id.txtTenNhac);
        TextView txtNameArtist = row.<TextView>findViewById(R.id.txtTenCaSi);
        btnAddMusic = row.<ImageButton>findViewById(R.id.btnaddmusic);
        btnAdded = row.<ImageButton>findViewById(R.id.btnadded);
        final Music music = this.objects.get(position);
        final int pos = position;
        txtNameSong.setText(music.getNamesong());
        txtNameArtist.setText(music.getArtist());

        if (add[position]==1) {
            btnAddMusic.setVisibility(View.VISIBLE);
            btnAdded.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnAddMusic.setVisibility(View.INVISIBLE);
            btnAdded.setVisibility(View.VISIBLE);
        }

        btnAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemNhac(pos);
            }
        });
        btnAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDaThemNhac(pos);
            }
        });
        return row;
    }

    private void xuLyThemNhac(int position) {
        add[position]=0;
        btnAdded.setVisibility(View.VISIBLE);
        btnAddMusic.setVisibility(View.INVISIBLE);
        notifyDataSetChanged();
    }

    private void xuLyDaThemNhac(int position) {
        add[position]=1;
        btnAdded.setVisibility(View.INVISIBLE);
        btnAddMusic.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
    }
}
