package advisor;

public class SpotifyResponseData {
    private String album;
    private String artists;
    private String category ;
    private String link;

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {

        StringBuilder info = new StringBuilder();
        if (album != null) {
            info.append(album).append("\n");
        }

        if (artists != null) {
            info.append(artists).append("\n");
        }

        if (link != null) {
            info.append(link).append("\n");
        }

        if (category != null) {
            info.append(category).append("\n");
        }
        return info.toString().replaceAll("\"", "");
    }
}
