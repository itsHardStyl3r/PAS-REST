package pl.hardstyl3r.repoadapters.objects.resources;

public class BookEnt extends ResourceEnt {
    private String author;
    private String isbn;

    protected BookEnt() {

    }

    public BookEnt(String name, String description, String author, String isbn) {
        super(name, description);
        this.author = author;
        this.isbn = isbn;
    }

    public BookEnt(String id, String name, String description, String author, String isbn) {
        super(id, name, description);
        this.author = author;
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "BookEnt{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
