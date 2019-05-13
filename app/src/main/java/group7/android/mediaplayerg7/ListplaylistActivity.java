package group7.android.mediaplayerg7;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import group7.android.adapter.PlaylistAdapter;
import group7.android.model.Playlist;

public class ListplaylistActivity extends AppCompatActivity {

    ListView lvPlaylist;
    ArrayList<Playlist> dsPlaylist;
    PlaylistAdapter adapterPlaylist;
    ImageView btnAddPlaylist;
    TextView txtThemPlaylist;
    int countrow=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listplaylist);
        AddControls();
        AddEvents();
    }


    private void AddControls() {

        btnAddPlaylist = (ImageView) findViewById(R.id.btnaddplaylist);
        txtThemPlaylist = (TextView)findViewById(R.id.txtthemplaylist);
        lvPlaylist = (ListView)findViewById(R.id.listplaylist);
        dsPlaylist = new ArrayList<>();
        adapterPlaylist = new PlaylistAdapter(ListplaylistActivity.this,R.layout.itemlistplaylist,dsPlaylist);
        lvPlaylist.setAdapter(adapterPlaylist);

        LayDanhSachPlaylistTuCSDL();
    }

    private void AddEvents() {
        txtThemPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemPlaylist();
            }
        });
        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemPlaylist();
            }
        });
    }

    private void xuLyThemPlaylist() {
        final Dialog dialog = new Dialog(ListplaylistActivity.this);
        dialog.setTitle("Tạo playlist");
        dialog.setContentView(R.layout.newplaylist);
        final EditText txtNamePlaylist = (EditText)dialog.findViewById(R.id.txtNamePlaylist);
        TextView btnDongY = (TextView)dialog.findViewById(R.id.btnDongY);
        TextView btnBoQua = (TextView)dialog.findViewById(R.id.btnBoQua);
        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ListplaylistActivity.this,AddmusicActivity.class);
                intent.putExtra("nameplaylist",txtNamePlaylist.getText().toString());
                int count = countrow+1;
                intent.putExtra("idplaylist","playlist"+countrow+1);
                //Insert vào csdl
                ContentValues row = new ContentValues();
                row.put("idplaylist","playlist"+String.valueOf(count));
                row.put("nameplaylist",txtNamePlaylist.getText().toString());
                row.put("count",0);
                //MainActivity.database.insert("playlist",null,row);
                startActivity(intent);
            }
        });
        btnBoQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void LayDanhSachPlaylistTuCSDL()
    {
        Cursor cursor = MainActivity.database.query("playlist",null,null,null,null,null,null);
        dsPlaylist.clear();

        while (cursor.moveToNext())
        {
            countrow+=1;
            Playlist playlist = new Playlist();
            playlist.setIdplaylist(cursor.getString(0));
            playlist.setNameplaylist(cursor.getString(1));
            playlist.setCount(cursor.getInt(2));
            dsPlaylist.add(playlist);
        }
        cursor.close(); //Đóng kết nối
        adapterPlaylist.notifyDataSetChanged();
    }
}
