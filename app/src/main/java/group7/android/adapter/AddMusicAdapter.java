package group7.android.adapter;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group7.android.mediaplayerg7.AddmusicActivity;
import group7.android.mediaplayerg7.MainActivity;
import group7.android.mediaplayerg7.R;
import group7.android.model.Music;
import group7.android.model.Playlist;

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
        //Toast.makeText(context, AddmusicActivity.idpl+" "+AddmusicActivity.namepl, Toast.LENGTH_SHORT).show();
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
                xuLyThemNhac(music,pos);
            }
        });
        btnAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDaThemNhac(music,pos);
            }
        });
        return row;
    }

    private void xuLyThemNhac(Music music,int position) {
        add[position]=0;
        btnAdded.setVisibility(View.VISIBLE);
        btnAddMusic.setVisibility(View.INVISIBLE);
        //Insert nhạc đã chọn vào cơ sở dữ liệu
        ContentValues row = new ContentValues();
        row.put("idplaylist",AddmusicActivity.idpl);
        row.put("idsong",music.getIdsong());
        MainActivity.database.insert("detailplaylist",null,row);
        //Lấy số lượng bài hát trong playlist để cập nhật
        ContentValues row1 = new ContentValues();
        Toast.makeText(context, "adapter = "+AddmusicActivity.idpl, Toast.LENGTH_SHORT).show();
        Cursor cursor = MainActivity.database.query("playlist",null,"idplaylist=?",new String[] {AddmusicActivity.idpl},null,null,null);
        int curcount=-1;
        while (cursor.moveToNext())
        {
            curcount = cursor.getInt(2);
            Toast.makeText(context, "count = "+String.valueOf(curcount), Toast.LENGTH_SHORT).show();
        }
        //Update lại bảng Playlist
        ContentValues row2 = new ContentValues();
        row2.put("count",curcount+1);
        MainActivity.database.update("playlist",row2,"idplaylist=?",new String[]{AddmusicActivity.idpl});
        //row1.put("count",);
        notifyDataSetChanged();
    }

    private void xuLyDaThemNhac(Music music,int position) {
        add[position]=1;
        btnAdded.setVisibility(View.INVISIBLE);
        btnAddMusic.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
    }
}
