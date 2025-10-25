package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DisplayUtilTest {

    @Test
    public void displayPhone_nullValue_returnsDefaultMessage() {
        assertEquals("No phone provided", DisplayUtil.displayPhone(null));
    }

    @Test
    public void displayPhone_validValue_returnsValue() {
        assertEquals("91234567", DisplayUtil.displayPhone("91234567"));
        assertEquals("999", DisplayUtil.displayPhone("999"));
    }

    @Test
    public void displayEmail_nullValue_returnsDefaultMessage() {
        assertEquals("No email provided", DisplayUtil.displayEmail(null));
    }

    @Test
    public void displayEmail_validValue_returnsValue() {
        assertEquals("test@example.com", DisplayUtil.displayEmail("test@example.com"));
        assertEquals("a@bc", DisplayUtil.displayEmail("a@bc"));
    }

    @Test
    public void displayAddress_nullValue_returnsDefaultMessage() {
        assertEquals("No address provided", DisplayUtil.displayAddress(null));
    }

    @Test
    public void displayAddress_validValue_returnsValue() {
        assertEquals("123 Main St", DisplayUtil.displayAddress("123 Main St"));
        assertEquals("Blk 456, Den Road, #01-355",
                     DisplayUtil.displayAddress("Blk 456, Den Road, #01-355"));
    }
}
