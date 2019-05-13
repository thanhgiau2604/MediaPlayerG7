package group7.android.mediaplayerg7;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import group7.android.adapter.AddMusicAdapter;
import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

public class AddmusicActivity extends AppCompatActivity {

    ListView lvBaiHat;
    ArrayList<Music> dsBaiHat;
    AddMusicAdapter adapterBaiHat;
    Button btnHoanTat;

    public static String idpl, namepl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        idpl = intent.getStringExtra("idplaylist");
        namepl = intent.getStringExtra("nameplaylist");

        setContentView(R.layout.activity_addmusic);

        AddControls();
        AddEvents();
    }



    private void AddControls() {
        lvBaiHat = this.<ListView>findViewById(R.id.listallmusic);
        dsBaiHat = new ArrayList<>();
        adapterBaiHat = new AddMusicAdapter(AddmusicActivity.this,R.layout.itemaddmusic,dsBaiHat);
        lvBaiHat.setAdapter(adapterBaiHat);

        DanhSachMusicLayTuDatabase();
    }

    private void AddEvents() {
        Button btnHoanTat = (Button)findViewById(R.id.btnhoantat);
        btnHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void DanhSachMusicLayTuDatabase() {
        Cursor cursor = MainActivity.database.query("music",null,null,null,null,null,null);

        dsBaiHat.clear();

        while (cursor.moveToNext())
        {
            Music music = new Music();
            music.setIdsong(cursor.getString(0));
            music.setNamesong(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setAlbum(cursor.getString(3));
            Boolean bool = cursor.getInt(4)>0;
            music.setFavorite(bool);
            dsBaiHat.add(music);
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHat.notifyDataSetChanged();
    }
}
