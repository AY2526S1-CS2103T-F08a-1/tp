package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StatusTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Status(null));
    }

    @Test
    public void constructor_invalidStatus_throwsIllegalArgumentException() {
        String invalidStatus = "";
        assertThrows(IllegalArgumentException.class, () -> new Status(invalidStatus));
    }

    @Test
    public void constructor_noArgument_createsDefaultStatus() {
        Status status = new Status();
        assertEquals(Status.DEFAULT_STATUS, status.value);
    }

    @Test
    public void isValidStatus() {
        // null status
        assertThrows(NullPointerException.class, () -> Status.isValidStatus(null));

        // invalid statuses
        assertFalse(Status.isValidStatus("")); // empty string
        assertFalse(Status.isValidStatus(" ")); // spaces only
        assertFalse(Status.isValidStatus("pending application")); // contains space
        assertFalse(Status.isValidStatus("technical*interview")); // contains special character
        assertFalse(Status.isValidStatus("hr_interview")); // contains underscore
        assertFalse(Status.isValidStatus("follow-up-")); // ends with hyphen
        assertFalse(Status.isValidStatus("-pending")); // starts with hyphen
        assertFalse(Status.isValidStatus("technical--interview")); // double hyphen

        // valid statuses
        assertTrue(Status.isValidStatus("pending-application")); // alphanumeric with hyphens
        assertTrue(Status.isValidStatus("technical-interview")); // alphanumeric with hyphens
        assertTrue(Status.isValidStatus("hr-interview")); // alphanumeric with hyphens
        assertTrue(Status.isValidStatus("offer-received")); // alphanumeric with hyphens
        assertTrue(Status.isValidStatus("rejected")); // single word
        assertTrue(Status.isValidStatus("accepted")); // single word
        assertTrue(Status.isValidStatus("round-1")); // with number
        assertTrue(Status.isValidStatus("2nd-interview")); // starts with number
        assertTrue(Status.isValidStatus("follow-up-round-3")); // multiple hyphens
    }

    @Test
    public void equals() {
        Status status = new Status("pending-application");

        // same values -> returns true
        assertTrue(status.equals(new Status("pending-application")));

        // same object -> returns true
        assertTrue(status.equals(status));

        // null -> returns false
        assertFalse(status.equals(null));

        // different types -> returns false
        assertFalse(status.equals(5.0f));

        // different values -> returns false
        assertFalse(status.equals(new Status("technical-interview")));
    }

    @Test
    public void hashCode_sameStatus_returnsSameHashCode() {
        Status status1 = new Status("pending-application");
        Status status2 = new Status("pending-application");
        assertEquals(status1.hashCode(), status2.hashCode());
    }

    @Test
    public void hashCode_differentStatus_returnsDifferentHashCode() {
        Status status1 = new Status("pending-application");
        Status status2 = new Status("technical-interview");
        assertNotEquals(status1.hashCode(), status2.hashCode());
    }

    @Test
    public void toString_returnsCorrectValue() {
        Status status = new Status("technical-interview");
        assertEquals("technical-interview", status.toString());
    }

    @Test
    public void toString_defaultStatus_returnsDefaultValue() {
        Status status = new Status();
        assertEquals(Status.DEFAULT_STATUS, status.toString());
    }
}
