package group7.android.model;

public class Playlist {
    private String idplaylist;
    private String nameplaylist;
    private int count;

    public Playlist() {
    }

    public Playlist(String idplaylist, String nameplaylist, int count) {
        this.idplaylist = idplaylist;
        this.nameplaylist = nameplaylist;
        this.count = count;
    }

    public String getIdplaylist() {
        return idplaylist;
    }

    public void setIdplaylist(String idplaylist) {
        this.idplaylist = idplaylist;
    }

    public String getNameplaylist() {
        return nameplaylist;
    }

    public void setNameplaylist(String nameplaylist) {
        this.nameplaylist = nameplaylist;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
