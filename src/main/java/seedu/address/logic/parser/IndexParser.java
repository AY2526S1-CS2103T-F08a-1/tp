package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.exceptions.ParseIndicesException;

/**
 * Parses input arguments and creates a new DeleteCommand object.
 * Supports:
 * - Single index: "1"
 * - Multiple indices: "1,3,5"
 * Duplicates are ignored (first occurrence kept).
 */
public class IndexParser {
    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer: 1, 2, 3...";
    public static final String MESSAGE_INVALID_INDICES = "Invalid indices. Please use only positive numbers or ranges. "
            + "(e.g. 1, 2, 5-8).\n"
            + "Do not include spaces within numbers (e.g. 3555 not 3 555).\n"
            + "Do not include trailing commas (e.g. 1, 3, ).\n";
    public static final String MESSAGE_DUPLICATE_INDICES = "Duplicate indices found: %1$s. "
            + "Each index should appear only once.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Index(es) out of range: %1$s. "
            + "Valid range is 1 to %2$d.";
    public static final String MESSAGE_INVALID_RANGE_ORDER =
            "Invalid range: start index (%1$d) cannot be greater than end index (%2$d).";

    /**
     * Maximum allowed size for an expanded range (inclusive). Prevents accidental OOM.
     */
    public static final int MAX_RANGE_SIZE = 10000;
    public static final String MESSAGE_RANGE_TOO_LARGE = "Range too large: %1$d-%2$d (size %3$d). "
            + "Please specify a smaller range or use fewer indices (max %4$d).";


    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading
     * and trailing whitespaces will be
     * trimmed. Range validation should be done separately in the command.
     *
     * @throws ParseIndicesException if the specified index is invalid (not non-zero
     *                        unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses comma-separated indices into a {@code List<Index>} without range
     * validation.
     * Use this when range validation will be done later in the command execution.
     *
     * @param indicesString String containing comma-separated indices (e.g., "1",
     *                      "1,2,3")
     * @return List of valid unique indices
     * @throws ParseIndicesException if any index is invalid or duplicate
     */
    public static List<Index> parseIndices(String indicesString) throws ParseIndicesException {
        requireNonNull(indicesString);
        String trimmedIndices = indicesString.trim();

        if (trimmedIndices.isEmpty()) {
            throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
        }

        String[] indexStrings = trimmedIndices.split(",", -1);
        List<Index> indexList = new ArrayList<>();
        Set<Integer> seenIndices = new HashSet<>();
        List<Index> duplicates = new ArrayList<>();
        @FunctionalInterface
        interface Function1<One> {
            public One apply(One one);
        }

        Function1<Integer> addIndex = (Integer idx) -> {
            if (seenIndices.contains(idx)) {
                duplicates.add(Index.fromOneBased(idx));
            } else {
                seenIndices.add(idx);
                indexList.add(Index.fromOneBased(idx));
            }
            return null;
        };

        for (String indexString : indexStrings) {
            String trimmedIndexString = indexString.trim();
            if (trimmedIndexString.isEmpty()) {
                throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
            }

            if (trimmedIndexString.contains("-")) {
                Range range = parseRangeToken(trimmedIndexString);
                for (int i = range.start; i <= range.end; i++) {
                    int oneBasedIndex = i + 1;
                    addIndex.apply(oneBasedIndex);
                }
            } else {
                if (trimmedIndexString.contains(" ")) {
                    throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
                }
                Index index;
                try {
                    index = parseIndex(trimmedIndexString);
                } catch (ParseException pe) {
                    throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
                }
                int oneBasedIndex = index.getOneBased();
                addIndex.apply(oneBasedIndex);
            }

        }

        // Only duplicate checking gets the specific exception
        if (!duplicates.isEmpty()) {
            throw new ParseIndicesException(String.format(MESSAGE_DUPLICATE_INDICES,
                    duplicates.stream()
                            .sorted((a, b) -> Integer.compare(a.getOneBased(), b.getOneBased()))
                            .map(index -> String.valueOf(index.getOneBased()))
                            .collect(java.util.stream.Collectors.joining(", "))));
        }

        return indexList;
    }

    /** Parses a token like "a-b" (inclusive) into a {@link Range}. */
    private static Range parseRangeToken(String token) throws ParseIndicesException {
        // keep empties to catch "3-" / "-3"
        final String[] parts = token.split("-", -1);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
        }

        final int start;
        final int end;
        try {
            start = parseIndex(parts[0].trim()).getZeroBased();
            end = parseIndex(parts[1].trim()).getZeroBased();
        } catch (ParseException pe) {
            throw new ParseIndicesException(MESSAGE_INVALID_INDICES);
        }

        // disallow "4-2"
        if (end < start) {
            throw new ParseIndicesException(String.format(IndexParser.MESSAGE_INVALID_RANGE_ORDER, start + 1, end + 1));
        }

        // prevent expanding extremely large ranges which can cause OutOfMemoryError
        long size = (long) end - (long) start + 1L;
        if (size > MAX_RANGE_SIZE) {
            throw new ParseIndicesException(String.format(MESSAGE_RANGE_TOO_LARGE,
                    start + 1, end + 1, size, MAX_RANGE_SIZE));
        }

        return new Range(start, end);
    }

    /** Simple inclusive integer range. */
    private static final class Range {
        final int start;
        final int end;

        Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
