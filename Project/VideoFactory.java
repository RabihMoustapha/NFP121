class VideoFactory implements MediaFactory {
    @Override
    public Media createMedia(String id, String title, String author, int year,
            String desc, Object... params) {
        if (params.length < 1)
            throw new IllegalArgumentException("Need duration");
        return new VideoSession(id, title, author, year, desc, (Integer) params[0]);
    }
}