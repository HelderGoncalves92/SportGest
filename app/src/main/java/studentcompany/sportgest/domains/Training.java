package studentcompany.sportgest.domains;

import java.util.Comparator;

public class Training extends DomainPojo implements Comparator<Training>{
    private long id;
    private String title;
    private String description;
    private long date;
    private int totalDuration;
    private Team team;
    private int deleted;

    public Training(long id, String title, String description, long date, int totalDuration, Team team, int deleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.totalDuration = totalDuration;
        this.team = team;
        this.deleted = deleted;
    }

    public Training(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public Team getTeam() {
        return team;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", totalDuration=" + totalDuration +
                ", team=" + team +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public int compare(Training lhs, Training rhs) {
        return lhs.getTitle().compareTo(rhs.getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training training = (Training) o;

        if (id != training.id) return false;
        if (date != training.date) return false;
        if (totalDuration != training.totalDuration) return false;
        if (deleted != training.deleted) return false;
        if (title != null ? !title.equals(training.title) : training.title != null) return false;
        if (description != null ? !description.equals(training.description) : training.description != null)
            return false;
        return !(team != null ? !team.equals(training.team) : training.team != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + totalDuration;
        result = 31 * result + (team != null ? team.hashCode() : 0);
        result = 31 * result + deleted;
        return result;
    }
}
