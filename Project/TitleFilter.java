class TitleFilter implements FilterCriteria {
    private String title;

    public TitleFilter(String title) {
        this.title = title.toLowerCase();
    }

    @Override
    public boolean matches(Media media) {
        return media.getTitle().toLowerCase().contains(title);
    }
}