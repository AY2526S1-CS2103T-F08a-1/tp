package seedu.address.model.company;

import static java.util.Objects.requireNonNull;

import seedu.address.model.company.exceptions.UnsupportedStatusException;

/**
 * Represents a Company's application status in the address book.
 * Now backed by a fixed enum set of stages.
 */
public class Status {

    /**
     * Fixed application stages supported by the app. Not yet enforced in parsing; added for
     * incremental migration and future use.
     */
    public enum Stage {
        TO_APPLY,
        APPLIED,
        OA,
        TECH_INTERVIEW,
        HR_INTERVIEW,
        IN_PROCESS,
        OFFERED,
        ACCEPTED,
        REJECTED
    }

    public static final String MESSAGE_CONSTRAINTS =
            "Status must be one of: TO-APPLY, APPLIED, OA, TECH-INTERVIEW, HR-INTERVIEW, IN-PROCESS, "
                    + "OFFERED, ACCEPTED, REJECTED";

    public final Stage value;

    /**
     * Constructs a {@code Status} with the given {@link Stage}.
     */
    public Status(Stage stage) {
        requireNonNull(stage);
        this.value = stage;
    }

    /**
     * Constructs a {@code Status} by parsing a user-provided token.
     */
    public Status(String token) {
        requireNonNull(token);
        this.value = ofUserInput(token);
    }

    /**
     * Constructs a {@code Status} with the default stage (TO-APPLY).
     */
    public Status() {
        this.value = Stage.TO_APPLY;
    }

    /**
     * Returns true if a given string corresponds to a supported status.
     */
    public static boolean isValidStatus(String test) {
        try {
            ofUserInput(test);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* === Enum mapping helpers (backward compatible, not yet enforced) === */

    /**
     * Maps a user-provided status token (case-insensitive; hyphen/underscore treated as separators)
     * into a {@link Stage}. Only the canonical stages are accepted.
     *
     * @throws UnsupportedStatusException if the input does not correspond to a canonical stage
     */
    public static Stage ofUserInput(String token) {
        requireNonNull(token);
        String normalized = token.trim().toUpperCase().replace('_', '-');
        switch (normalized) {
        case "TO-APPLY":
            return Stage.TO_APPLY;
        case "APPLIED":
            return Stage.APPLIED;
        case "OA":
            return Stage.OA;
        case "TECH-INTERVIEW":
            return Stage.TECH_INTERVIEW;
        case "HR-INTERVIEW":
            return Stage.HR_INTERVIEW;
        case "IN-PROCESS":
            return Stage.IN_PROCESS;
        case "OFFERED":
            return Stage.OFFERED;
        case "ACCEPTED":
            return Stage.ACCEPTED;
        case "REJECTED":
            return Stage.REJECTED;
        default:
            throw new UnsupportedStatusException("Unsupported status: " + token);
        }
    }

    /**
     * Maps stored canonical tokens into a {@link Stage}.
     *
     * @throws UnsupportedStatusException if the input is unknown
     */
    public static Stage fromStorage(String stored) {
        requireNonNull(stored);
        String s = stored.trim().toLowerCase();
        switch (s) {
        case "to-apply":
            return Stage.TO_APPLY;
        case "applied":
            return Stage.APPLIED;
        case "oa":
            return Stage.OA;
        case "tech-interview":
            return Stage.TECH_INTERVIEW;
        case "hr-interview":
            return Stage.HR_INTERVIEW;
        case "in-process":
            return Stage.IN_PROCESS;
        case "offered":
            return Stage.OFFERED;
        case "accepted":
            return Stage.ACCEPTED;
        case "rejected":
            return Stage.REJECTED;
        default:
            throw new UnsupportedStatusException(stored);
        }
    }

    /**
     * Returns the canonical hyphen form (lower-case) for a given {@link Stage}.
     */
    public static String toUserInputString(Stage stage) {
        requireNonNull(stage);
        switch (stage) {
        case TO_APPLY:
            return "to-apply";
        case APPLIED:
            return "applied";
        case OA:
            return "oa";
        case TECH_INTERVIEW:
            return "tech-interview";
        case HR_INTERVIEW:
            return "hr-interview";
        case IN_PROCESS:
            return "in-process";
        case OFFERED:
            return "offered";
        case ACCEPTED:
            return "accepted";
        case REJECTED:
            return "rejected";
        // Should never happen
        default:
            throw new IllegalStateException("Unhandled stage: " + stage);
        }
    }

    /**
     * Best-effort conversion of this instance's string value to a canonical hyphen form of the enum.
     * If the current value is not recognized (custom/legacy), the original value is returned.
     */
    public String toUserInputString() {
        return toUserInputString(this.value);
    }

    /**
     * Returns the canonical storage value (currently same as user input hyphen form).
     */
    public static String toStorageValue(Stage stage) {
        return toUserInputString(stage);
    }

    /**
     * Best-effort conversion for storage. If unrecognized, returns the original value.
     */
    public String toStorageValue() {
        return toUserInputString();
    }

    @Override
    public String toString() {
        return toUserInputString();
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
        return value == otherStatus.value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
