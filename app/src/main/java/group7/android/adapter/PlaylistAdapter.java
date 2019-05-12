package group7.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

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
        ImageButton btnEditpl = row.<ImageButton>findViewById(R.id.btneditpl);
        ImageButton btnDeletepl = row.<ImageButton>findViewById(R.id.btndeletepl);

        final Playlist playlist = this.objects.get(position);
        txtNamePlaylist.setText(playlist.getNameplaylist());
        String countpl = (String.valueOf(playlist.getCount()));
        txtCountSong.setText(countpl);
        return row;
    }
}


