package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;



/**
 * Parses input arguments and creates a new DeleteCommand object.
 * Supports:
 *   - Single index: "1"
 *   - Multiple indices: "1,3,5"
 * Duplicates are ignored (first occurrence kept).
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {
    private static final Pattern COMMA_LIST =
            Pattern.compile("^\\s*\\d+(?:\\s*,\\s*\\d+)*\\s*$");

    @Override
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);

        final String[] tokens = tokenize(args);
        final Set<Integer> zeroBased = collectZeroBased(tokens);
        if (zeroBased.isEmpty()) {
            throw invalidFormat();
        }

        final List<Index> indices = toIndexList(zeroBased);
        return new DeleteCommand(indices);
    }

    /** Splits and validates the raw input into non-empty tokens. */
    private static String[] tokenize(String raw) throws ParseException {
        final String trimmed = raw.trim();
        if (!COMMA_LIST.matcher(trimmed).matches()) {
            throw invalidFormat();
        }
        return trimmed.split(",");
    }

    /** Parses tokens into a de-duplicated, insertion-ordered set of zero-based indices. */
    private static Set<Integer> collectZeroBased(String[] tokens) throws ParseException {
        final Set<Integer> out = new LinkedHashSet<>();
        for (String tok : tokens) {
            final String trimmed = tok.trim();
            if (trimmed.isEmpty()) {
                throw invalidFormat();
            }
            out.add(parseIndexToken(trimmed));
        }
        return out;
    }

    /** Converts a set of zero-based ints to a list of {@link Index}. */
    private static List<Index> toIndexList(Set<Integer> zeroBased) {
        final List<Index> indices = new ArrayList<>(zeroBased.size());
        for (int zb : zeroBased) {
            indices.add(Index.fromZeroBased(zb));
        }
        return indices;
    }

    /** Parses a token like "a-b" (inclusive) into a {@link Range}. */
    private static Range parseRangeToken(String token) throws ParseException {
        // keep empties to catch "3-" / "-3"
        final String[] parts = token.split("-", -1);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw invalidFormat();
        }

        final int start = parseIndexToken(parts[0].trim());
        final int end = parseIndexToken(parts[1].trim());

        // disallow "4-2"
        if (end < start) {
            throw invalidFormat();
        }
        return new Range(start, end);
    }

    /** Adds all indices in [start, end] (inclusive) to the set. */
    private static void addRange(Set<Integer> set, int start, int end) {
        for (int i = start; i <= end; i++) {
            set.add(i);
        }
    }

    /** Parses a single index token to zero-based, mapping errors to a standard invalid-format message. */
    private static int parseIndexToken(String token) throws ParseException {
        try {
            return ParserUtil.parseIndex(token).getZeroBased();
        } catch (ParseException e) {
            throw invalidFormat();
        }
    }

    private static ParseException invalidFormat() {
        return new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
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
