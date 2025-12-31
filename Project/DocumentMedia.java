class DocumentMedia extends Media {
    private int pageCount;

    public DocumentMedia(String id, String title, String author, int year, String desc, int pages) {
        super(id, title, author, year, desc);
        this.pageCount = pages;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int p) {
        pageCount = p;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public String getSpecificDetails() {
        return "Pages: " + pageCount;
    }
}