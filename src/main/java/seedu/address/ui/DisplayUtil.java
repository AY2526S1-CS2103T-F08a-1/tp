package seedu.address.ui;

/**
 * Utility methods for formatting values for display.
 */
public final class DisplayUtil {

    private DisplayUtil() {
    }

    /**
     * Returns a user-friendly phone string for display.
     *
     * @param value the raw phone value
     * @return "No phone provided" if the value is null; otherwise the original value
     */
    public static String displayPhone(String value) {
        return value == null ? "No phone provided" : value;
    }

    /**
     * Returns a user-friendly email string for display.
     *
     * @param value the raw email value
     * @return "No email provided" if the value is null, otherwise the original value
     */
    public static String displayEmail(String value) {
        return value == null ? "No email provided" : value;
    }

    /**
     * Returns a user-friendly address string for display.
     *
     * @param value the raw address value
     * @return "No address provided" if the value is null, otherwise the original value
     */
    public static String displayAddress(String value) {
        return value == null ? "No address provided" : value;
    }
}
