class VideoSession extends Media {
    private int durationMinutes;

    public VideoSession(String id, String title, String author, int year, String desc, int duration) {
        super(id, title, author, year, desc);
        this.durationMinutes = duration;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int d) {
        durationMinutes = d;
    }

    @Override
    public String getType() {
        return "Video Session";
    }

    @Override
    public String getSpecificDetails() {
        return "Duration: " + durationMinutes + " minutes";
    }
}