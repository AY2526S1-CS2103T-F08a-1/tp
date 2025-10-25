package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_success() {
        // null is allowed - represents no remark provided
        Remark remark = new Remark(null);
        assertTrue(remark.value == null);
    }

    @Test
    public void constructor_validRemark_success() {
        // Valid remark should be stored correctly
        Remark remark = new Remark("Good company");
        assertEquals("Good company", remark.value);
    }

    @Test
    public void isValidRemark() {
        // null remark - returns false
        assertFalse(Remark.isValidRemark(null));

        // invalid remarks
        assertFalse(Remark.isValidRemark("")); // empty string
        assertFalse(Remark.isValidRemark(" ")); // spaces only
        assertFalse(Remark.isValidRemark("  ")); // multiple spaces only

        // valid remarks
        assertTrue(Remark.isValidRemark("Good company")); // alphabets only
        assertTrue(Remark.isValidRemark("12345")); // numbers only
        assertTrue(Remark.isValidRemark("Company with 123")); // alphanumeric
        assertTrue(Remark.isValidRemark("Good company!")); // with special characters
        assertTrue(Remark.isValidRemark("a")); // single character
        assertTrue(Remark.isValidRemark("This is a very long remark about the company")); // long remark
    }

    @Test
    public void toString_test() {
        // Test toString for non-null value
        Remark remark = new Remark("Good company");
        assertEquals("Good company", remark.toString());

        // Test toString for null value
        Remark nullRemark = new Remark(null);
        assertEquals(null, nullRemark.toString());
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Good company");

        // same values -> returns true
        assertTrue(remark.equals(new Remark("Good company")));

        // same object -> returns true
        assertTrue(remark.equals(remark));

        // null -> returns false
        assertFalse(remark.equals(null));

        // different types -> returns false
        assertFalse(remark.equals(5.0f));

        // different values -> returns false
        assertFalse(remark.equals(new Remark("Bad company")));

        // both null values -> returns true
        Remark nullRemark1 = new Remark(null);
        Remark nullRemark2 = new Remark(null);
        assertTrue(nullRemark1.equals(nullRemark2));

        // one null, one non-null -> returns false
        assertFalse(remark.equals(nullRemark1));
        assertFalse(nullRemark1.equals(remark));
    }

    @Test
    public void hashCode_test() {
        // same values -> same hash code
        Remark remark1 = new Remark("Good company");
        Remark remark2 = new Remark("Good company");
        assertEquals(remark1.hashCode(), remark2.hashCode());

        // both null -> same hash code (0)
        Remark nullRemark1 = new Remark(null);
        Remark nullRemark2 = new Remark(null);
        assertEquals(nullRemark1.hashCode(), nullRemark2.hashCode());
        assertEquals(0, nullRemark1.hashCode());
    }
}
