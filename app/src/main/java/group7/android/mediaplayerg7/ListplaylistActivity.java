package group7.android.mediaplayerg7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import group7.android.adapter.PlaylistAdapter;
import group7.android.model.Playlist;

public class ListplaylistActivity extends AppCompatActivity {

    ListView lvPlaylist;
    ArrayList<Playlist> dsPlaylist;
    PlaylistAdapter adapterPlaylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listplaylist);
        AddControls();
        AddEvents();
    }


    private void AddControls() {
        lvPlaylist = (ListView)findViewById(R.id.listplaylist);
        dsPlaylist = new ArrayList<>();
        adapterPlaylist = new PlaylistAdapter(ListplaylistActivity.this,R.layout.itemlistplaylist,dsPlaylist);
        lvPlaylist.setAdapter(adapterPlaylist);
        giaLapPlaylist();
    }

    private void AddEvents() {

    }

    private void giaLapPlaylist()
    {
        dsPlaylist.add(new Playlist("pl1","Mùa xuân",2));
        dsPlaylist.add(new Playlist("pl2","Mùa hạ",3));
        dsPlaylist.add(new Playlist("pl3","Mùa thu",4));
        dsPlaylist.add(new Playlist("pl4","Mùa đông",5));
        adapterPlaylist.notifyDataSetChanged();
    }
}
