package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_success() {
        // null is now allowed - represents no email provided
        Email email = new Email(null);
        assertTrue(email.value == null);
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email - now returns false instead of throwing exception
        assertFalse(Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("a@bc")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("test@localhost")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertTrue(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("123@145.com")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void equals() {
        Email email = new Email("valid@example.com");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@example.com")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@example.com")));

        // both null values -> returns true
        Email nullEmail1 = new Email(null);
        Email nullEmail2 = new Email(null);
        assertTrue(nullEmail1.equals(nullEmail2));

        // one null, one non-null -> returns false
        assertFalse(email.equals(nullEmail1));
        assertFalse(nullEmail1.equals(email));
    }

    @Test
    public void hashCode_test() {
        // same values -> same hash code
        Email email1 = new Email("valid@example.com");
        Email email2 = new Email("valid@example.com");
        assertTrue(email1.hashCode() == email2.hashCode());

        // both null -> same hash code (0)
        Email nullEmail1 = new Email(null);
        Email nullEmail2 = new Email(null);
        assertTrue(nullEmail1.hashCode() == nullEmail2.hashCode());
        assertTrue(nullEmail1.hashCode() == 0);
    }

    @Test
    public void toString_test() {
        // Test toString for non-null value
        Email email = new Email("valid@example.com");
        assertTrue(email.toString().equals("valid@example.com"));

        // Test toString for null value
        Email nullEmail = new Email(null);
        assertTrue(nullEmail.toString() == null);
    }
}
