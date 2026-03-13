package pl.hardstyl3r.repoadapters.objects.resources;

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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "NewspaperEnt{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
