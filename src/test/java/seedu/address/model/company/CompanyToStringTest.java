package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.CompanyBuilder;

public class CompanyToStringTest {

    @Test
    public void toString_containsKeyFields() {
        Company company = new CompanyBuilder().build();
        String s = company.toString();
        assertTrue(s.contains("name=" + company.getName()));
        assertTrue(s.contains("phone=" + company.getPhone()));
        assertTrue(s.contains("email=" + company.getEmail()));
        assertTrue(s.contains("address=" + company.getAddress()));
        assertTrue(s.contains("remark=" + company.getRemark()));
        assertTrue(s.contains("tags="));
    }
}

