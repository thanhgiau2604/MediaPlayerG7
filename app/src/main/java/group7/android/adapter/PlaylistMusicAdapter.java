package group7.android.adapter;

import android.app.Activity;
import android.content.ContentValues;
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
import group7.android.mediaplayerg7.ListfavsongActivity;
import group7.android.mediaplayerg7.ListplaylistActivity;
import group7.android.mediaplayerg7.ListsongActivity;
import group7.android.mediaplayerg7.MainActivity;
import group7.android.mediaplayerg7.SonginplaylistActivity;
import group7.android.model.Music;
import group7.android.mediaplayerg7.R;

public class PlaylistMusicAdapter extends ArrayAdapter<Music>{
    Activity context;
    int resource;
    List<Music> objects;
    ImageButton btnLike, btnDislike, btnDelete;

    public PlaylistMusicAdapter(Activity context, int resource, List<Music> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource,null);
        TextView txtNameSong = (TextView)row.findViewById(R.id.txtNameSong);
        TextView txtNameArtist = row.<TextView>findViewById(R.id.txtNameArtist);
        btnLike = row.<ImageButton>findViewById(R.id.btnlikeinpl);
        btnDislike = row.<ImageButton>findViewById(R.id.btndislikeinpl);
        btnDelete = row.<ImageButton>findViewById(R.id.btndelsong);
        
        final Music music = this.objects.get(position);
        txtNameSong.setText(music.getNamesong());
        txtNameArtist.setText(String.valueOf(music.getArtist()));

        if (music.getFavorite()){
            btnLike.setVisibility(View.INVISIBLE);
            btnDislike.setVisibility(View.VISIBLE);
        }
        else
        {
            btnLike.setVisibility(View.VISIBLE);
            btnDislike.setVisibility(View.INVISIBLE);
        }

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThich(music);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyKhongThich(music);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXoaBaiHatKhoiPlaylist(music);
            }
        });
        
        return row;
    }

    private void xuLyXoaBaiHatKhoiPlaylist(Music music) {
        MainActivity.database.delete("detailplaylist","idsong=? and idplaylist=?",new String[]{music.getIdsong(),
                SonginplaylistActivity.idplaylist});
        //Lấy số lượng bài hát trong playlist để cập nhật
        ContentValues row1 = new ContentValues();
        Cursor cursor = MainActivity.database.query("playlist",null,"idplaylist=?",new String[] {SonginplaylistActivity.idplaylist},null,null,null);
        int curcount=-1;
        while (cursor.moveToNext())
        {
            curcount = cursor.getInt(2);
        }
        //Update lại bảng Playlist
        ContentValues row2 = new ContentValues();
        row2.put("count",curcount-1);
        MainActivity.database.update("playlist",row2,"idplaylist=?",new String[]{SonginplaylistActivity.idplaylist});
        SonginplaylistActivity.LayDanhSachBaiHatTrongPlaylist();
        ListplaylistActivity.LayDanhSachPlaylistTuCSDL();
        notifyDataSetChanged();
    }

    //Xử lý khi chọn không thích bài hát
    private void xuLyKhongThich(Music music) {
        music.setFavorite(false);
        btnDislike.setVisibility(View.INVISIBLE);
        btnLike.setVisibility(View.VISIBLE);
        ContentValues row = new ContentValues();
        row.put("favorite",false);
        MainActivity.database.update("music",row,"idsong=?",new String[]{music.getIdsong()});
        notifyDataSetChanged();
        if (MainActivity.DA_CHON_TAB_YEU_THICH)
            ListfavsongActivity.LayDuLieuBaiHatYeuThichTuCSDL();
    }
    //Xử lý chọn thich bài hát
    private void xuLyThich(Music music) {
        music.setFavorite(true);
        btnDislike.setVisibility(View.VISIBLE);
        btnLike.setVisibility(View.INVISIBLE);
        ContentValues row = new ContentValues();
        row.put("favorite",true);
        MainActivity.database.update("music",row,"idsong=?",new String[]{music.getIdsong()});
        notifyDataSetChanged();
        if (MainActivity.DA_CHON_TAB_YEU_THICH)
            ListfavsongActivity.LayDuLieuBaiHatYeuThichTuCSDL();
    }
}
