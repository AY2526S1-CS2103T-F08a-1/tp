package seedu.address.ui;

/**
 * Utility methods for formatting values for display.
 */
public final class DisplayUtil {

    private DisplayUtil() {}

    /**
     * Returns a user-friendly phone string for display.
     *
     * @param value the raw phone value
     * @return "No phone provided" if the value is the placeholder "000"; otherwise the original value
     */
    public static String displayPhone(String value) {
        return "000".equals(value) ? "No phone provided" : value;
    }

    /**
     * Returns a user-friendly address string for display.
     *
     * @param value the raw address value
     * @return "No address provided" if the value equals the placeholder; otherwise the original value
     */
    public static String displayAddress(String value) {
        return "No address provided".equals(value) ? "No address provided" : value;
    }

    /**
     * Returns a user-friendly email string for display.
     *
     * @param value the raw email value
     * @return "No email provided" if the value equals the placeholder "noemailprovided@placeholder.com";
     *         otherwise the original value
     */
    public static String displayEmail(String value) {
        return "noemailprovided@placeholder.com".equals(value) ? "No email provided" : value;
    }

    /**
     * Returns a user-friendly remark string for display.
     *
     * @param value the raw remark value
     * @return "No remark provided" if the value equals the placeholder; otherwise the original value
     */
    public static String displayRemark(String value) {
        return "No remark provided".equals(value) ? "No remark provided" : value;
    }
}

