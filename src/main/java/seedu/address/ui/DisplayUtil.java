package seedu.address.ui;

/**
 * Utility methods for formatting values for display.
 */
public final class DisplayUtil {

    private DisplayUtil() {}

    public static String displayPhone(String value) {
        return "000".equals(value) ? "No phone provided" : value;
    }

    public static String displayAddress(String value) {
        return "No address provided".equals(value) ? "No address provided" : value;
    }

    public static String displayEmail(String value) {
        return "noemailprovided@placeholder.com".equals(value) ? "No email provided" : value;
    }

    public static String displayRemark(String value) {
        return "No remark provided".equals(value) ? "No remark provided" : value;
    }
}

