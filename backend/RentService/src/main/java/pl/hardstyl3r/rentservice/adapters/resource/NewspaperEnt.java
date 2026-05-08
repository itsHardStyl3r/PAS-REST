package pl.hardstyl3r.rentservice.adapters.resource;

public class NewspaperEnt extends ResourceEnt {
    private String releaseDate;

    protected NewspaperEnt() {
    }

    public NewspaperEnt(String name, String description, String releaseDate) {
        super(name, description);
        this.releaseDate = releaseDate;
    }

    public NewspaperEnt(String id, String name, String description, String releaseDate) {
        super(id, name, description);
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
}
