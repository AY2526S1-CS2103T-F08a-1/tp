package seedu.address.model.company;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Company's application status in the address book.
 * Guarantees: immutable; status value is valid as declared in {@link #isValidStatus(String)}
 */
public class Status {

    public static final String DEFAULT_STATUS = "pending-application";
    public static final String MESSAGE_CONSTRAINTS =
            "Status should be alphanumeric and may contain hyphens to separate words";
    public static final String VALIDATION_REGEX = "[\\p{Alnum}]+(-[\\p{Alnum}]+)*";

    public final String value;

    /**
     * Constructs a {@code Status}.
     *
     * @param status A valid status value.
     */
    public Status(String status) {
        requireNonNull(status);
        checkArgument(isValidStatus(status), MESSAGE_CONSTRAINTS);
        this.value = status;
    }

    /**
     * Constructs a {@code Status} with the default value.
     */
    public Status() {
        this.value = DEFAULT_STATUS;
    }

    /**
     * Returns true if a given string is a valid status.
     */
    public static boolean isValidStatus(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Status)) {
            return false;
        }

        Status otherStatus = (Status) other;
        return value.equals(otherStatus.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
