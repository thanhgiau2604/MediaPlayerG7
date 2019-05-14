package group7.android.mediaplayerg7;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import group7.android.adapter.AddMusicAdapter;
import group7.android.adapter.PlaylistMusicAdapter;
import group7.android.model.Music;

public class SonginplaylistActivity extends AppCompatActivity {

    ListView lvBaiHat;
    public static ArrayList<Music> dsBaiHat;
    public static PlaylistMusicAdapter adapterBaiHat;
    public static ArrayList<String> paths;
    public static String idplaylist;
    Button btnChinhSuaPl;
    Intent intent;

    public static boolean CHONTHEMBAIHATVAOPLAYLIST=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        idplaylist = intent.getStringExtra("idplaylist");
        setContentView(R.layout.activity_songinplaylist);
        AddControls();
        AddEvents();
    }

    private void AddControls() {
        lvBaiHat = (ListView)findViewById(R.id.listsonginpl);
        dsBaiHat = new ArrayList<>();
        adapterBaiHat = new PlaylistMusicAdapter(SonginplaylistActivity.this,R.layout.itemsonginplaylist,dsBaiHat);
        lvBaiHat.setAdapter(adapterBaiHat);
        LayDanhSachBaiHatTrongPlaylist();

    }

    private void AddEvents() {
        btnChinhSuaPl = (Button)findViewById(R.id.btnchinhsuapl);
        btnChinhSuaPl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CHONTHEMBAIHATVAOPLAYLIST=true;
                Intent intent = new Intent(SonginplaylistActivity.this,AddmusicActivity.class);
                intent.putExtra("idplaylist",idplaylist);
                startActivity(intent);
            }
        });

    }

    public static void LayDanhSachBaiHatTrongPlaylist() {
        Cursor cursor = MainActivity.database.query("detailplaylist",null,"idplaylist=?",new String[]{idplaylist},null,null,null);
        dsBaiHat.clear();
        paths = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Cursor cursor1 = MainActivity.database.query("music",null,"idsong=?",new String[]{cursor.getString(0)},null,null,null);
            while (cursor1.moveToNext())
            {
                Music music = new Music();
                music.setIdsong(cursor1.getString(0));
                music.setNamesong(cursor1.getString(1));
                music.setArtist(cursor1.getString(2));
                music.setAlbum(cursor1.getString(3));
                Boolean bool = cursor1.getInt(4)>0;
                music.setFavorite(bool);
                music.setPath(cursor1.getString(5));
                dsBaiHat.add(music);
                paths.add(music.getPath());
            }
            cursor1.close();
        }
        cursor.close();
        adapterBaiHat.notifyDataSetChanged();
    }
}
