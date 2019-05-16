package group7.android.mediaplayerg7;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;
import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PAUSE;
public class ListsongActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener{
    ListView lvBaiHatGoc;
    ArrayList<Music> dsBaiHatGoc;
    MusicAdapter adapterBaiHatGoc;
    //


    private ArrayList<String> paths; // lưu tất cả đường dẫn của các bài hát
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsong);
        AddControls();
        AddEvents();
        initComponents();
        TiepTucBaiHat();
    }


    private  void TiepTucBaiHat()
    {
        if (MainActivity.musicPlayer.getState()==PLAYER_PLAY || MainActivity.musicPlayer.getState()==PLAYER_PAUSE)
        {
            MainActivity.tvArtist.setText(MainActivity.TEN_BAI_HAT);
            MainActivity.tvTitle.setText(MainActivity.TEN_CA_SI);
            MainActivity.tvTimeTotal.setText(MainActivity.TOTAL_TIME);
            MainActivity.isRunning = true;
            if (MainActivity.musicPlayer.getState()==PLAYER_PLAY)
                MainActivity.ivPlay.setImageResource(R.drawable.pause);
            else
                MainActivity.ivPlay.setImageResource(R.drawable.play);
        }
    }

    private void AddControls() {
        lvBaiHatGoc = this.<ListView>findViewById(R.id.listSong);
        dsBaiHatGoc = new ArrayList<>();
        adapterBaiHatGoc = new MusicAdapter(ListsongActivity.this,R.layout.itemlistsong,dsBaiHatGoc);
        lvBaiHatGoc.setAdapter(adapterBaiHatGoc);

        initViews();

        if (KiemTraLanDauChayApp()) { //nếu lần đầu tiên chạy app thì mới quét điện thoại
            /*KhoiTaoList();
            DanhSachMusicQuetDuoc();*/
            getListSongs();
        }
        else //lấy từ database ra thôi
        {
            DanhSachMusicLayTuDatabase();
        }
        adapterBaiHatGoc.notifyDataSetChanged(); //cập nhật lại apdapter
    }

    private void initComponents() {
        /*MainActivity.musicPlayer.setOnCompletionListener(this);*/
    }

    private void AddEvents() {
        initListeners();
    }

    private void initListeners() {
        lvBaiHatGoc.setOnItemClickListener(this);
        MainActivity.ivShuffle.setOnClickListener(this);
        MainActivity.ivPrevious.setOnClickListener(this);
        MainActivity.ivPlay.setOnClickListener(this);
        MainActivity.ivNext.setOnClickListener(this);
        MainActivity.ivRepeat.setOnClickListener(this);
        MainActivity.sbProcess.setOnSeekBarChangeListener(this);
        MainActivity.musicPlayer.setOnCompletionListener(this);
    }

    private void initViews() {
        //lvPlayList = (ListView) findViewById(R.id.lv_play_list);
        MainActivity.tvTitle = (TextView) findViewById(R.id.tv_title);
        MainActivity.tvArtist = (TextView) findViewById(R.id.tv_artist);
        MainActivity.tvTimeProcess = (TextView) findViewById(R.id.tv_time_process);
        MainActivity.sbProcess = (SeekBar) findViewById(R.id.sb_process);
        MainActivity.tvTimeTotal = (TextView) findViewById(R.id.tv_time_total);
        MainActivity.ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);
        MainActivity.ivPrevious = (ImageView) findViewById(R.id.iv_previous);
        MainActivity.ivPlay = (ImageView) findViewById(R.id.iv_play);
        MainActivity.ivNext = (ImageView) findViewById(R.id.iv_next);
        MainActivity.ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
    }
    private void DanhSachMusicLayTuDatabase() {
        Cursor cursor = MainActivity.database.query("music",null,null,null,null,null,null);

        dsBaiHatGoc.clear();
        paths = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Music music = new Music();
            music.setIdsong(cursor.getString(0));
            music.setNamesong(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setAlbum(cursor.getString(3));
            Boolean bool = cursor.getInt(4)>0;
            music.setFavorite(bool);
            music.setPath(cursor.getString(5));
            dsBaiHatGoc.add(music);
            paths.add(music.getPath());
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHatGoc.notifyDataSetChanged();
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

    void getListSongs(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(songUri, null, null, null, null);
        paths = new ArrayList<>();
        if(songUri != null && cursor.moveToFirst()){
            do{
                String s = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (s.endsWith(".mp3")) {
                    Music music = new Music();
                    music.setIdsong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                    music.setNamesong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    music.setFavorite(false);

                    ContentValues row = new ContentValues();
                    row.put("idsong", music.getIdsong());
                    row.put("namesong", music.getNamesong());
                    row.put("artist", music.getArtist());
                    row.put("album", music.getAlbum());
                    row.put("favorite", music.getFavorite());
                    row.put("path", music.getPath());

                    long r = MainActivity.database.insert("music", null, row);
                    dsBaiHatGoc.add(music);
                    paths.add(music.getPath());
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
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
            music.setPath(paths.get(i));
            ContentValues row = new ContentValues();
            row.put("idsong",music.getIdsong());
            row.put("namesong",music.getNamesong());
            row.put("artist",music.getArtist());
            row.put("album",music.getAlbum());
            row.put("favorite",music.getFavorite());
            row.put("path",music.getPath());
            long  r = MainActivity.database.insert("music",null,row);
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



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainActivity.UPDATE_TIME) {
                MainActivity.timeCurrent = MainActivity.musicPlayer.getTimeCurrent();
                MainActivity.tvTimeProcess.setText(getTimeFormat(MainActivity.timeCurrent));
                MainActivity.sbProcess.setProgress(MainActivity.timeCurrent);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.position = position;
        String path = paths.get(position);
        playMusic(path);
    }

    private void playMusic(String path) {
        if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
            MainActivity.musicPlayer.stop();
        }
        MainActivity.musicPlayer.setup(path);
        MainActivity.musicPlayer.play();
        MainActivity.ivPlay.setImageResource(R.drawable.pause);
        // set up tên bài hát + ca sĩ
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(paths.get(MainActivity.position));
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        MainActivity.TEN_BAI_HAT= title;
        MainActivity.TEN_CA_SI=artist;
        MainActivity.tvArtist.setText(artist);
        MainActivity.tvTitle.setText(title);
        MainActivity.isRunning = true;

        // set up time
        // total time
        MainActivity.tvTimeTotal.setText(getTimeFormat(MainActivity.musicPlayer.getTimeTotal()));
        MainActivity.TOTAL_TIME = MainActivity.tvTimeTotal.getText().toString();
        // process time // set up seekbar
        MainActivity.sbProcess.setMax(MainActivity.musicPlayer.getTimeTotal());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (MainActivity.isRunning) {
                    Message message = new Message();
                    message.what = MainActivity.UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_next:
                nextMusic();
                break;

            case R.id.iv_play:
                if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
                    MainActivity.ivPlay.setImageResource(R.drawable.play);
                    MainActivity.musicPlayer.pause();
                } else {
                    MainActivity.ivPlay.setImageResource(R.drawable.pause);
                    MainActivity.musicPlayer.play();
                }
                break;

            case R.id.iv_previous:
                previousMusic();
                break;

            default:
                break;
        }
    }

    private void previousMusic() {
        MainActivity.position--;
        if (MainActivity.position < 0) {
            MainActivity.position = paths.size() - 1;
        }
        String path = paths.get(MainActivity.position);
        playMusic(path);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (MainActivity.timeCurrent != progress && MainActivity.timeCurrent != 0)
            MainActivity.musicPlayer.seek(MainActivity.sbProcess.getProgress() * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void OnEndMusic() {
        // khi kết thúc bài hát nó sẽ vào đây
        nextMusic();
        Log.d("chanh", "vào đây");
        // như vậy khi kết thúc bài hát nó có thể next bài tiếp theo
        // nếu hết danh sách bài hát nó sẽ quay lại từ bài đầu tiên
    }

    private void nextMusic() {
        MainActivity.position++;
        if (MainActivity.position >= paths.size()) {
            MainActivity.position = 0;
        }
        String path = paths.get(MainActivity.position);
        playMusic(path);
    }
}