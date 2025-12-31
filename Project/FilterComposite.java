import java.util.ArrayList;
import java.util.List;

class FilterComposite implements FilterCriteria {
    public enum Operator {
        AND, OR
    }

    private List<FilterCriteria> criteria = new ArrayList<>();
    private Operator operator;

    public FilterComposite(Operator operator) {
        this.operator = operator;
    }

    public void addCriterion(FilterCriteria c) {
        criteria.add(c);
    }

    @Override
    public boolean matches(Media media) {
        if (criteria.isEmpty())
            return true;

        if (operator == Operator.AND) {
            for (FilterCriteria c : criteria) {
                if (!c.matches(media))
                    return false;
            }
            return true;
        } else { // OR
            for (FilterCriteria c : criteria) {
                if (c.matches(media))
                    return true;
            }
            return false;
        }
    }
}