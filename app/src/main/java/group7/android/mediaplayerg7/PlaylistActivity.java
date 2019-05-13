package group7.android.mediaplayerg7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

public class PlaylistActivity extends AppCompatActivity {

    ListView lvBaiHat;
    ArrayList<Music> dsBaiHat;
    MusicAdapter adapterBaiHat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        AddControls();
        AddEvents();
    }

    private void AddEvents() {

    }

    private void AddControls() {
        lvBaiHat = this.<ListView>findViewById(R.id.listSong);
        dsBaiHat = new ArrayList<>();
        adapterBaiHat = new MusicAdapter(PlaylistActivity.this,R.layout.itemsonginplaylist,dsBaiHat);
        lvBaiHat.setAdapter(adapterBaiHat);
    }
}
