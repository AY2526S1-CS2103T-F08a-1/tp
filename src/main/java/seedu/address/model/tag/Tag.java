package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final String MESSAGE_CONSTRAINTS =
            "Tag names should be alphanumeric and may contain hyphens to separate words. No whitespaces allowed.";
    public static final int MAX_TAG_LENGTH = 30;
    public static final String MESSAGE_LENGTH_EXCEEDED_FORMAT =
            "Tag names must not exceed %d characters.\n"
                    + "Tag '%s' exceeds %d character limit. (%d characters)";
    public static final String VALIDATION_REGEX = "[\\p{Alnum}]+(-[\\p{Alnum}]+)*";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        checkArgument(isValidTagLength(tagName),
                String.format(MESSAGE_LENGTH_EXCEEDED_FORMAT,
                        MAX_TAG_LENGTH, tagName, MAX_TAG_LENGTH, tagName.length()));
        this.tagName = tagName.toLowerCase();
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given tag is a does not exceed max tag length.
     */
    public static boolean isValidTagLength(String test) {
        return test.length() <= MAX_TAG_LENGTH;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag)) {
            return false;
        }

        Tag otherTag = (Tag) other;
        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
