package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void constructor_null_success() {
        // null is now allowed - represents no address provided
        Address address = new Address(null);
        assertTrue(address.value == null);
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address - now returns false instead of throwing exception
        assertFalse(Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only

        // valid addresses
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        assertTrue(Address.isValidAddress("-")); // one character
        assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA")); // long address
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address")));

        // both null values -> returns true
        Address nullAddress1 = new Address(null);
        Address nullAddress2 = new Address(null);
        assertTrue(nullAddress1.equals(nullAddress2));

        // one null, one non-null -> returns false
        assertFalse(address.equals(nullAddress1));
        assertFalse(nullAddress1.equals(address));
    }

    @Test
    public void hashCode_test() {
        // same values -> same hash code
        Address address1 = new Address("Valid Address");
        Address address2 = new Address("Valid Address");
        assertTrue(address1.hashCode() == address2.hashCode());

        // both null -> same hash code (0)
        Address nullAddress1 = new Address(null);
        Address nullAddress2 = new Address(null);
        assertTrue(nullAddress1.hashCode() == nullAddress2.hashCode());
        assertTrue(nullAddress1.hashCode() == 0);
    }

    @Test
    public void toString_test() {
        // Test toString for non-null value
        Address address = new Address("Valid Address");
        assertTrue(address.toString().equals("Valid Address"));

        // Test toString for null value
        Address nullAddress = new Address(null);
        assertTrue(nullAddress.toString() == null);
    }
}
