package group7.android.mediaplayerg7;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import group7.android.adapter.AddMusicAdapter;
import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

public class AddmusicActivity extends AppCompatActivity {

    ListView lvBaiHat;
    ArrayList<Music> dsBaiHat;
    AddMusicAdapter adapterBaiHat;
    Button btnHoanTat;

    List<String> paths;

    public static String idpl="", namepl="";

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

        if (KiemTraLanDauChayApp()) { //nếu lần đầu tiên chạy app thì mới quét điện thoại
            KhoiTaoList();
            DanhSachMusicQuetDuoc();
        }
        else //lấy từ database ra thôi
        {
            if (SonginplaylistActivity.CHONTHEMBAIHATVAOPLAYLIST==true)
                LayDanhSachNhacDeThem();
            else
                DanhSachMusicLayTuDatabase();
        }

        Toast.makeText(this, "idactivity = "+idpl, Toast.LENGTH_SHORT).show();
    }

    private void AddEvents() {
        Button btnHoanTat = (Button)findViewById(R.id.btnhoantat);
        btnHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                if (SonginplaylistActivity.CHONTHEMBAIHATVAOPLAYLIST)
                    SonginplaylistActivity.LayDanhSachBaiHatTrongPlaylist();

                ListplaylistActivity.LayDanhSachPlaylistTuCSDL();
                SonginplaylistActivity.CHONTHEMBAIHATVAOPLAYLIST=false;

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

    private void LayDanhSachNhacDeThem()
    {
        paths = new ArrayList<>();
        Intent intent = getIntent();
        String idplaylist = intent.getStringExtra("idplaylist");

        Cursor cursorall = MainActivity.database.query("music",null,null,null,null,null,null);
        while (cursorall.moveToNext())
        {

            boolean check = true;
            Cursor cursor = MainActivity.database.query("detailplaylist",null,"idplaylist=?",new String[]{idplaylist},null,null,null);
            while (cursor.moveToNext())
            {
                if (cursorall.getString(0).equals(cursor.getString(0)))
                {
                    check = false;
                }
            }
            if (check)
            {
                Music music = new Music();
                music.setIdsong(cursorall.getString(0));
                music.setNamesong(cursorall.getString(1));
                music.setArtist(cursorall.getString(2));
                music.setAlbum(cursorall.getString(3));
                Boolean bool = cursorall.getInt(4)>0;
                music.setFavorite(bool);
                music.setPath(cursorall.getString(5));
                dsBaiHat.add(music);
                paths.add(music.getPath());
            }
            cursor.close();
        }
        cursorall.close();
        adapterBaiHat.notifyDataSetChanged();
    }

    //Quét danh sách bài hát trong máy
    private void KhoiTaoList() {
        paths = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        File file = new File(path);
        File[] files = file.listFiles(); // lay tat ca cac file trong thu muc. ở đây là Download
        for (int i = 0; i < files.length; i++) {
            // doc tat ca cac file co trong download them vao list nhac
            String s = files[i].getName();
            if (s.endsWith(".mp3")) {
                // thủ thuật kiểm tra nó có phải đuôi nhạc mp3 không, có thể nó
                // là tệp ảnh hoặc thư mục lúc đó sẽ gây ra lỗi, 1 số định dạng khác có thể có của nhạc là .flat(lostless), .wav, ...
                paths.add(files[i].getAbsolutePath());
            }
        }
    }

    private boolean KiemTraLanDauChayApp()
    {
        Cursor cursor = MainActivity.database.query("music",null,null,null,null,null,null);
        while (cursor.moveToNext())
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    private void DanhSachMusicQuetDuoc()
    {
        for (int i=0; i<paths.size();i++) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            Music music = new Music();
            mmr.setDataSource(paths.get(i));
            music.setIdsong("BH"+i);
            music.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            music.setNamesong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            music.setAlbum(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            music.setFavorite(false);
            music.setPath(paths.get(i));
            ContentValues row = new ContentValues();
            row.put("idsong",music.getIdsong());
            row.put("namesong",music.getNamesong());
            row.put("artist",music.getArtist());
            row.put("album",music.getAlbum());
            row.put("favorite",music.getFavorite());
            row.put("path",music.getPath());
            long  r = MainActivity.database.insert("music",null,row);
            dsBaiHat.add(music);
        }
    }
}
