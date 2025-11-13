package pl.hardstyl3r.pas.v1.objects.resources;

public class Book extends Resource {
    private String author;
    private String isbn;

    protected Book() {

    }

    public Book(String name, String description, String author, String isbn) {
        super(name, description);
        this.author = author;
        this.isbn = isbn;
    }

    public Book(String id, String name, String description, String author, String isbn) {
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
}
