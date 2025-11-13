package pl.hardstyl3r.pas.v1.objects.resources;

public class Newspaper extends Resource {
    private String releaseDate;

    protected Newspaper() {

    }

    public Newspaper(String name, String description, String releaseDate) {
        super(name, description);
        this.releaseDate = releaseDate;
    }

    public Newspaper(String id, String name, String description, String releaseDate) {
        super(id, name, description);
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
