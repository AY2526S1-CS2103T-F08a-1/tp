package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        assertFalse(Tag.isValidTagName("")); // empty string
        assertFalse(Tag.isValidTagName(" ")); // spaces only
        assertFalse(Tag.isValidTagName("^")); // only non-alphanumeric characters
        assertFalse(Tag.isValidTagName("friend*")); // contains non-alphanumeric characters
        assertFalse(Tag.isValidTagName("-tag")); // starts with hyphen
        assertFalse(Tag.isValidTagName("tag-")); // ends with hyphen
        assertFalse(Tag.isValidTagName("tag--name")); // consecutive hyphens
        assertFalse(Tag.isValidTagName("tag name")); // contains space

        // valid tag names
        assertTrue(Tag.isValidTagName("friend")); // single word
        assertTrue(Tag.isValidTagName("DecentLocation")); // alphanumeric with capital letters
        assertTrue(Tag.isValidTagName("Good-pay")); // contains hyphen
        assertTrue(Tag.isValidTagName("Yet-to-apply")); // multiple hyphens
        assertTrue(Tag.isValidTagName("a")); // single character
        assertTrue(Tag.isValidTagName("123")); // numeric only
        assertTrue(Tag.isValidTagName("friend123")); // alphanumeric
        assertTrue(Tag.isValidTagName("multiple-words-with-number-123")); // multiple words with number
    }

}
