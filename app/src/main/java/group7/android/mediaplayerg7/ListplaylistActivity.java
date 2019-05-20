package group7.android.mediaplayerg7;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    public static ArrayList<Playlist> dsPlaylist;
    public static PlaylistAdapter adapterPlaylist;
    ImageView btnAddPlaylist;
    TextView txtThemPlaylist;
    TextView tvTieuDe;
    ImageView imgBack;
    public static int countrow=0;
    public static boolean DA_MO_PLAYLIST = false;
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

        SonginplaylistActivity.CHONTHEMBAIHATVAOPLAYLIST = false;

        tvTieuDe = (TextView)findViewById(R.id.tvTieuDe);
        tvTieuDe.setText("Danh sách Playlist");
        imgBack = (ImageView)findViewById(R.id.imageView4);
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
        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DA_MO_PLAYLIST==false) {
                    DA_MO_PLAYLIST=true;
                    Playlist playlist = dsPlaylist.get(position);
                    Intent intent = new Intent(ListplaylistActivity.this, SonginplaylistActivity.class);
                    intent.putExtra("idplaylist", playlist.getIdplaylist());
                    startActivity(intent);
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                //Insert vào csdl
                int count = countrow+1;
                String idplaylist = "playlist".concat(String.valueOf(count));
                ContentValues row = new ContentValues();
                row.put("idplaylist",idplaylist);
                row.put("nameplaylist",txtNamePlaylist.getText().toString());
                row.put("count",0);
                long r = MainActivity.database.insert("playlist",null,row);
               //Mở màn hình mới
                Intent intent = new Intent(ListplaylistActivity.this,AddmusicActivity.class);
                intent.putExtra("nameplaylist",txtNamePlaylist.getText().toString());
                intent.putExtra("idplaylist",idplaylist);
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

    public static void LayDanhSachPlaylistTuCSDL()
    {
        Cursor cursor = MainActivity.database.query("playlist",null,null,null,null,null,null);
        dsPlaylist.clear();
        countrow=0;
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
