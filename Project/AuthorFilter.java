class AuthorFilter implements FilterCriteria {
    private String author;

    public AuthorFilter(String author) {
        this.author = author.toLowerCase();
    }

    @Override
    public boolean matches(Media media) {
        return media.getAuthor().toLowerCase().contains(author);
    }
}