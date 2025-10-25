package seedu.address.model.company;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Company's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 * Phone can be null to represent no phone number provided.
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers must have at least 3 digits, may start with '+', and may contain single spaces "
                    + "between digits (e.g., '98765432', '+65 9123 4567').";
    public static final String VALIDATION_REGEX = "^\\+?\\d(?: ?\\d){2,}$";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number, or null if no phone number is provided.
     */
    public Phone(String phone) {
        if (phone != null) {
            checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        }
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test != null && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return value == null ? otherPhone.value == null : value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }
}
