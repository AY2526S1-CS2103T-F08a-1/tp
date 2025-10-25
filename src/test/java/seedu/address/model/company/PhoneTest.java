package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_success() {
        // null is now allowed - represents no phone provided
        Phone phone = new Phone(null);
        assertTrue(phone.value == null);
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number - now returns false instead of throwing exception
        assertFalse(Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312+1534")); // "+" between digits

        // valid phone numbers
        assertTrue(Phone.isValidPhone("9312 1534")); // spaces within digits
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));

        // both null values -> returns true
        Phone nullPhone1 = new Phone(null);
        Phone nullPhone2 = new Phone(null);
        assertTrue(nullPhone1.equals(nullPhone2));

        // one null, one non-null -> returns false
        assertFalse(phone.equals(nullPhone1));
        assertFalse(nullPhone1.equals(phone));
    }

    @Test
    public void hashCode_test() {
        // same values -> same hash code
        Phone phone1 = new Phone("999");
        Phone phone2 = new Phone("999");
        assertTrue(phone1.hashCode() == phone2.hashCode());

        // both null -> same hash code (0)
        Phone nullPhone1 = new Phone(null);
        Phone nullPhone2 = new Phone(null);
        assertTrue(nullPhone1.hashCode() == nullPhone2.hashCode());
        assertTrue(nullPhone1.hashCode() == 0);
    }

    @Test
    public void toString_test() {
        // Test toString for non-null value
        Phone phone = new Phone("999");
        assertTrue(phone.toString().equals("999"));

        // Test toString for null value
        Phone nullPhone = new Phone(null);
        assertTrue(nullPhone.toString() == null);
    }
}
