class QuizFactory implements MediaFactory {
    @Override
    public Media createMedia(String id, String title, String author, int year,
            String desc, Object... params) {
        if (params.length < 2)
            throw new IllegalArgumentException("Need duration and difficulty");
        return new OnlineQuiz(id, title, author, year, desc,
                (Integer) params[0], (String) params[1]);
    }
}