class SubjectFilter implements FilterCriteria {
    private Subject subject;

    public SubjectFilter(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean matches(Media media) {
        return media.getSubjects().contains(subject);
    }
}