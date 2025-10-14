package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DisplayUtilTest {

    @Test
    public void displayPhone_placeholder_returnsFriendlyText() {
        assertEquals("No phone provided", DisplayUtil.displayPhone("000"));
    }

    @Test
    public void displayPhone_normal_returnsOriginal() {
        assertEquals("12345", DisplayUtil.displayPhone("12345"));
    }

    @Test
    public void displayEmail_placeholder_returnsFriendlyText() {
        assertEquals("No email provided",
                DisplayUtil.displayEmail("noemailprovided@placeholder.com"));
    }

    @Test
    public void displayEmail_normal_returnsOriginal() {
        assertEquals("user@example.com", DisplayUtil.displayEmail("user@example.com"));
    }

    @Test
    public void displayAddress_placeholder_returnsFriendlyText() {
        assertEquals("No address provided", DisplayUtil.displayAddress("No address provided"));
    }

    @Test
    public void displayAddress_normal_returnsOriginal() {
        assertEquals("123 Main St", DisplayUtil.displayAddress("123 Main St"));
    }

    @Test
    public void displayRemark_placeholder_returnsFriendlyText() {
        assertEquals("No remark provided", DisplayUtil.displayRemark("No remark provided"));
    }

    @Test
    public void displayRemark_normal_returnsOriginal() {
        assertEquals("Trusted partner", DisplayUtil.displayRemark("Trusted partner"));
    }
}

