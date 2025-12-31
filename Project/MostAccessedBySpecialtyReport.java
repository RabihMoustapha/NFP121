import java.util.List;

class MostAccessedBySpecialtyReport implements StatisticsReport {
    private String specialtyName;

    public MostAccessedBySpecialtyReport(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    @Override
    public String generateReport(MediaLibrary lib) {
        StringBuilder sb = new StringBuilder();
        sb.append("Most Accessed Media for Specialty: ").append(specialtyName).append("\n");
        sb.append("==========================================\n");

        Specialty spec = lib.getSpecialty(specialtyName);
        if (spec == null)
            return "Specialty not found";

        List<Media> topMedia = lib.getMostAccessedBySpecialty(spec, 10);
        for (int i = 0; i < topMedia.size(); i++) {
            Media m = topMedia.get(i);
            sb.append(String.format("%d. %s (ID: %s) - %d accesses%n",
                    i + 1, m.getTitle(), m.getId(), m.getAccessCount()));
        }

        return sb.toString();
    }
}