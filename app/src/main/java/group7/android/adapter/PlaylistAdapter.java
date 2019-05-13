package group7.android.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group7.android.mediaplayerg7.AddmusicActivity;
import group7.android.mediaplayerg7.ListplaylistActivity;
import group7.android.mediaplayerg7.MainActivity;
import group7.android.mediaplayerg7.R;
import group7.android.model.Playlist;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    Activity context;
    int resource;
    List<Playlist> objects;

    public PlaylistAdapter(Activity context, int resource, List<Playlist> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource,null);
        TextView txtNamePlaylist = (TextView)row.findViewById(R.id.txtNamePlaylist);
        TextView txtCountSong = row.<TextView>findViewById(R.id.txtCountSong);
        ImageView btnEditpl = row.<ImageView>findViewById(R.id.btneditpl);
        ImageView btnDeletepl = row.<ImageView>findViewById(R.id.btndeletepl);

        final Playlist playlist = this.objects.get(position);
        txtNamePlaylist.setText(playlist.getNameplaylist());
        String countpl = (String.valueOf(playlist.getCount()));
        txtCountSong.setText(countpl);

        btnEditpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyChinhSuaTenPlaylist(playlist);
            }
        });
        btnDeletepl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXoaPlaylist(playlist);
            }
        });
        return row;
    }

    private void xuLyXoaPlaylist(final Playlist playlist) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Xóa playlist");
        dialog.setContentView(R.layout.newplaylist);
        final EditText txtNamePlaylist = (EditText)dialog.findViewById(R.id.txtNamePlaylist);
        txtNamePlaylist.setEnabled(false);
        txtNamePlaylist.setText("Bạn thật sự muốn xóa playlist này?");
        TextView btnDongY = (TextView)dialog.findViewById(R.id.btnDongY);
        TextView btnBoQua = (TextView)dialog.findViewById(R.id.btnBoQua);
        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.database.delete("detailplaylist","idplaylist=?",new String[] {playlist.getIdplaylist()});
                MainActivity.database.delete("playlist","idplaylist=?", new String[] {playlist.getIdplaylist()});
                notifyDataSetChanged();
                ListplaylistActivity.LayDanhSachPlaylistTuCSDL();
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

    private void xuLyChinhSuaTenPlaylist(final Playlist playlist) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Thay đổi tên playlist");
        dialog.setContentView(R.layout.newplaylist);
        final EditText txtNamePlaylist = (EditText)dialog.findViewById(R.id.txtNamePlaylist);
        TextView btnDongY = (TextView)dialog.findViewById(R.id.btnDongY);
        TextView btnBoQua = (TextView)dialog.findViewById(R.id.btnBoQua);

        txtNamePlaylist.setText(playlist.getNameplaylist());
        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ContentValues row = new ContentValues();
                row.put("nameplaylist",txtNamePlaylist.getText().toString());
                MainActivity.database.update("playlist",row,"idplaylist=?", new String[] {playlist.getIdplaylist()});
                ListplaylistActivity.LayDanhSachPlaylistTuCSDL();
                notifyDataSetChanged();
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
}


