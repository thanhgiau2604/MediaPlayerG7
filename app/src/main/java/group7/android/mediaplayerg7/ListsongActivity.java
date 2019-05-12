package group7.android.mediaplayerg7;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

public class ListsongActivity extends AppCompatActivity {
    ListView lvBaiHatGoc;
    ArrayList<Music> dsBaiHatGoc;
    MusicAdapter adapterBaiHatGoc;

    public static String DATABASE_NAME = "dbmediaplayer.sqlite";
    private ArrayList<String> paths; // lưu tất cả đường dẫn của các bài hát
    public  static SQLiteDatabase database = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsong);
        AddControls();
        AddEvents();

    }

    private void AddEvents() {
    }

    private void AddControls() {
        lvBaiHatGoc = this.<ListView>findViewById(R.id.listSong);
        dsBaiHatGoc = new ArrayList<>();
        adapterBaiHatGoc = new MusicAdapter(ListsongActivity.this,R.layout.itemlistsong,dsBaiHatGoc);
        lvBaiHatGoc.setAdapter(adapterBaiHatGoc);
        if (KiemTraLanDauChayApp()) { //nếu lần đầu tiên chạy app thì mới quét điện thoại
            KhoiTaoList();
            DanhSachMusicQuetDuoc();
        }
        else //lấy từ database ra thôi
        {
            DanhSachMusicLayTuDatabase();
        }
        adapterBaiHatGoc.notifyDataSetChanged(); //cập nhật lại apdapter
    }

    private void DanhSachMusicLayTuDatabase() {
        //Bước 1: mở CSDL
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.query("music",null,null,null,null,null,null);

        dsBaiHatGoc.clear();

        while (cursor.moveToNext())
        {
            Music music = new Music();
            music.setIdsong(cursor.getString(0));
            music.setNamesong(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setAlbum(cursor.getString(3));
            Boolean bool = Boolean.valueOf(cursor.getString(4));
            music.setFavorite(bool);
            dsBaiHatGoc.add(music);
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHatGoc.notifyDataSetChanged();
    }

    private boolean KiemTraLanDauChayApp()
    {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.query("music",null,null,null,null,null,null);
        while (cursor.moveToNext())
        {
            Toast.makeText(ListsongActivity.this, "Return falsee rồi", Toast.LENGTH_SHORT).show();
            cursor.close();
            return false;
        }
        Toast.makeText(ListsongActivity.this, "Return trueeee rồi", Toast.LENGTH_SHORT).show();
        cursor.close();
        return true;
    }
    //Duyệt tất cả đường dẫn sau đó gọi file nhạc ra đễ lấy namesong, artist, album
    // Thêm các trường vào music để add vào danh sách bài hát gốc
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

            ContentValues row = new ContentValues();
            row.put("idsong",music.getIdsong());
            row.put("namesong",music.getNamesong());
            row.put("artist",music.getArtist());
            row.put("album",music.getAlbum());
            row.put("favorite",music.getFavorite());
            long  r = database.insert("music",null,row);
            Toast.makeText(ListsongActivity.this, "Thêm r = "+r, Toast.LENGTH_SHORT).show();
            dsBaiHatGoc.add(music);
        }
    }

    //Quét mục download của thiết bị, chỉ lưu lại đường dẫn của những file nhạc (.mp3)
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
}