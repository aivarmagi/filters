package ee.aivar.filters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import ee.aivar.filters.enums.CriteriaName;
import ee.aivar.filters.enums.Selection;
import ee.aivar.filters.model.db.Criteria;
import ee.aivar.filters.model.db.Filter;

public class DataHelpers {

    public static final String TEST_1 = "Test1";
    public static final String TEST_2 = "Test2";

    public static Filter getFilterWithoutCriterias(String name, Selection selection) {
        var filter = new Filter();
        filter.setName(name);
        filter.setSelection(selection);
        filter.setCriterias(new HashSet<>());

        return filter;
    }

    public static Criteria getCriteria(CriteriaName name, String operator, String value, Filter filter) {
        var criteria = new Criteria();
        criteria.setName(name);
        criteria.setOperator(operator);
        criteria.setValue(value);
        criteria.setFilter(filter);

        return criteria;
    }

    public static String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
}
