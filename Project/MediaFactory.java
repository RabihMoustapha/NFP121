interface MediaFactory {
    Media createMedia(String id, String title, String author, int year, String description, Object... params);
}