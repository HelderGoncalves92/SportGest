package studentcompany.sportgest.domains;
//TODO all

public class Observation extends DomainPojo {
        private int id;
        private String title;
        private String description;
        private int date;
        private ObsCategory observationcategory;
        private Player player;
        private User user;
        private Game game;


        public Observation (int id,String title, String description,int date,ObsCategory observationcategory,Player player,User user,Game game) {
            this.id = id;
            this.title=title;
            this.description = description;
            this.date = date;
            this.observationcategory = observationcategory;
            this.player = player;
            this.user=user;
            this.game = game;

        }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public ObsCategory getObservationcategory() {
        return observationcategory;
    }

    public void setObservationcategory(ObsCategory observationcategory) {
        this.observationcategory = observationcategory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", observationcategory=" + observationcategory +
                ", player=" + player +
                ", user=" + user +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Observation)) return false;

        Observation that = (Observation) o;

        if (getId() != that.getId()) return false;
        if (getDate() != that.getDate()) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        if (!getObservationcategory().equals(that.getObservationcategory())) return false;
        if (!getPlayer().equals(that.getPlayer())) return false;
        if (!getUser().equals(that.getUser())) return false;
        return getGame().equals(that.getGame());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getDate();
        result = 31 * result + getObservationcategory().hashCode();
        result = 31 * result + getPlayer().hashCode();
        result = 31 * result + getUser().hashCode();
        result = 31 * result + getGame().hashCode();
        return result;
    }
}
