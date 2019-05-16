package group7.android.mediaplayerg7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;

public class MainActivity extends AppCompatActivity {

    public  static String DATABASE_NAME = "dbmediaplayer.sqlite";
    String DB_PATH_SUFFIX = "/databases/";

    TextView txtListSong, txtListPlaylist, txtListFSong;

    public  static SQLiteDatabase database = null;

    public static MusicPlayer musicPlayer;


    public static TextView tvTitle;
    public static TextView tvArtist;
    public static TextView tvTimeProcess;
    public static SeekBar sbProcess;
    public static TextView tvTimeTotal;
    public static ImageView ivShuffle;
    public static ImageView ivPrevious;
    public static ImageView ivPlay;
    public static ImageView ivNext;
    public static ImageView ivRepeat;
    public static int timeProcess;
    public static int timeTotal;
    public static boolean isRunning;
    public static int UPDATE_TIME = 1;
    public static int timeCurrent;
    public static int position;

    public static String TEN_BAI_HAT, TEN_CA_SI, TOTAL_TIME;

    public static boolean DA_CHON_TAB_YEU_THICH = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
        AddControls();
        AddEvents();
        xuLySaoChepSQLTuAssetVaoHeThongMobile();
        MoKetNoiCSDL();
    }

    //Kiểm tra và yêu cầu quyền truy cập bộ nhớ điện thoại
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    //Hàm xao chép file sqlite từ thư mục Asset vào hệ thống điện thoại
    private void xuLySaoChepSQLTuAssetVaoHeThongMobile() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        //Kiểm tra có tồn tại database
        if (!dbFile.exists())
        {
            try
            {
                CopyDatabaseFromAsset();
                /*Toast.makeText(this,"Sao chép CSDL vào hệ thống thành công",Toast.LENGTH_LONG).show();*/
            }
            catch (Exception e)
            {
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDatabaseFromAsset() {
        try
        {
            //Lấy dữ liệu trong asset
            InputStream myInput = getAssets().open(DATABASE_NAME);
            //Lấy đường dẫn output;
            String outFileName = layDuongDanLuuTru();

            File f = new File(getApplicationInfo().dataDir+DB_PATH_SUFFIX);
            //Kiểm tra file có tồn tại đường dẫn
            if (!f.exists())
            {
                f.mkdir(); //Không tồn tại thì tạo mới
            }

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            //Chép input vào ouput mỗi lần 1024 byte
            while ((length=myInput.read(buffer))>0) {
                myOutput.write(buffer, 0, length);
            }
            //Đóng kết nối
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex)
        {
            Log.e("Lỗi sao chép:",ex.toString());
        }
    }

    private String layDuongDanLuuTru() {
        return getApplicationInfo().dataDir+DB_PATH_SUFFIX+DATABASE_NAME;
    }


    private void MoKetNoiCSDL()
    {
        //Bước 1: mở CSDL
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
    }

    //

    private void AddControls() {
        txtListSong = (TextView)findViewById(R.id.txtlistsong);
        txtListPlaylist = (TextView)findViewById(R.id.txtlistplaylist);
        txtListFSong = (TextView) findViewById(R.id.txtlistfsong);
        musicPlayer = new MusicPlayer();
    }


    private void AddEvents() {
        txtListSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListsongActivity.class);
                startActivity(intent);
            }
        });

        txtListPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,ListplaylistActivity.class);
                startActivity(intent1);
            }
        });

        txtListFSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DA_CHON_TAB_YEU_THICH = true;
                Intent intent2 = new Intent(MainActivity.this,ListfavsongActivity.class);
                startActivity(intent2);
            }
        });
    }
}
