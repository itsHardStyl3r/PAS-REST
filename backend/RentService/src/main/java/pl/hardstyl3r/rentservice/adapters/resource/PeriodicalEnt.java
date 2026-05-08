package pl.hardstyl3r.rentservice.adapters.resource;

public class PeriodicalEnt extends ResourceEnt {
    private int issueNumber;

    protected PeriodicalEnt() {
    }

    public PeriodicalEnt(String name, String description, int issueNumber) {
        super(name, description);
        this.issueNumber = issueNumber;
    }

    public PeriodicalEnt(String id, String name, String description, int issueNumber) {
        super(id, name, description);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() { return issueNumber; }
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }
}
