package pl.hardstyl3r.pas.v1.objects.resources;

public class Periodical extends Resource {
    private int issueNumber;

    protected Periodical() {

    }

    public Periodical(String name, String description, int issueNumber) {
        super(name, description);
        this.issueNumber = issueNumber;
    }

    public Periodical(String id, String name, String description, int issueNumber) {
        super(id, name, description);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }
}
