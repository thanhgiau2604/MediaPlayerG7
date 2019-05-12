package group7.android.mediaplayerg7;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

public class ListfavsongActivity extends AppCompatActivity {

    ListView lvBaiHatYeuThich;
    public static ArrayList<Music> dsBaiHatYeuThich;
    public static MusicAdapter adapterBaiHatYeuThich;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfavsong);

        AddControls();
        AddEvents();
    }

    private void AddEvents() {
    }

    private void AddControls() {
        lvBaiHatYeuThich = this.<ListView>findViewById(R.id.listfavsong);
        dsBaiHatYeuThich = new ArrayList<>();
        adapterBaiHatYeuThich = new MusicAdapter(ListfavsongActivity.this,R.layout.itemlistsong,dsBaiHatYeuThich);
        lvBaiHatYeuThich.setAdapter(adapterBaiHatYeuThich);
        LayDuLieuBaiHatYeuThichTuCSDL();
    }

    public static void LayDuLieuBaiHatYeuThichTuCSDL()
    {

        Cursor cursor = MainActivity.database.query("music",null,"favorite=?",new String[] {"1"},null,null,null);
        dsBaiHatYeuThich.clear();

        while (cursor.moveToNext())
        {
            Music music = new Music();
            music.setIdsong(cursor.getString(0));
            music.setNamesong(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setAlbum(cursor.getString(3));
            Boolean bool = cursor.getInt(4)>0;
            music.setFavorite(bool);
            dsBaiHatYeuThich.add(music);
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHatYeuThich.notifyDataSetChanged();
    }
}
