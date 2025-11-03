package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.IndexParser.MAX_RANGE_SIZE;
import static seedu.address.logic.parser.IndexParser.MESSAGE_DUPLICATE_INDICES;
import static seedu.address.logic.parser.IndexParser.MESSAGE_INVALID_INDICES;
import static seedu.address.logic.parser.IndexParser.MESSAGE_INVALID_RANGE_ORDER;
import static seedu.address.logic.parser.IndexParser.MESSAGE_RANGE_TOO_LARGE;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.exceptions.ParseIndicesException;

/**
 * Unit tests for {@link IndexParser}.
 * Covers all valid, invalid, and duplicate scenarios for single, comma-separated, and range-based indices.
 */
public class IndexParserTest {

    // ================================================================
    // POSITIVE CASES (SUCCESSFUL PARSING)
    // ================================================================

    @Test
    public void parseIndices_singleIndex_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("1");
        assertEquals(List.of(Index.fromOneBased(1)), indices);
    }

    @Test
    public void parseIndices_validCommaSeparated_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("1, 3, 5");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(5)), indices);
    }

    @Test
    public void parseIndices_validRange_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("2-4");
        assertEquals(List.of(
                Index.fromOneBased(2),
                Index.fromOneBased(3),
                Index.fromOneBased(4)), indices);
    }

    @Test
    public void parseIndices_validMixedRange_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("1,3-5,7");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(4),
                Index.fromOneBased(5),
                Index.fromOneBased(7)), indices);
    }

    @Test
    public void parseIndices_singleElementRange_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("3-3");
        assertEquals(List.of(Index.fromOneBased(3)), indices);
    }

    @Test
    public void parseIndices_spacesAroundDash_success() throws Exception {
        List<Index> indices = IndexParser.parseIndices("1 - 3");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)), indices);
    }

    // ================================================================
    // DUPLICATE DETECTION TESTS
    // ================================================================

    @Test
    public void parseIndices_duplicate_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "1"), () ->
                IndexParser.parseIndices("1,2,1"));
    }

    @Test
    public void parseIndices_multipleDuplicates_reportsAscendingOrder() {
        // duplicates: 1, 3
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "1, 3"), () ->
                IndexParser.parseIndices("3,1,2,3,1"));
    }

    @Test
    public void parseIndices_overlappingRanges_throwsParseIndicesException() {
        // overlap between 1-3 and 2-4 => duplicates 2,3
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "2, 3"), () ->
                IndexParser.parseIndices("1-3,2-4"));
    }

    @Test
    public void parseIndices_identicalRanges_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "1, 2, 3"), () ->
                IndexParser.parseIndices("1-3,1-3"));
    }

    @Test
    public void parseIndices_rangeAndSingleOverlap_throwsParseIndicesException() {
        // 1-3 creates 1,2,3 then 2 repeats
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "2"), () ->
                IndexParser.parseIndices("1-3,2"));
    }

    @Test
    public void parseIndices_complexMixedDuplicates_reportsAllAscending() {
        // 1,2-4,3,5-7,6 => duplicates 3,6
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "3, 6"), () ->
                IndexParser.parseIndices("1,2-4,3,5-7,6"));
    }

    @Test
    public void parseIndices_rangeContainedInAnother_throwsParseIndicesException() {
        // 1-5 and 2-4 overlap => duplicates 2,3,4
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "2, 3, 4"), () ->
                IndexParser.parseIndices("1-5,2-4"));
    }

    @Test
    public void parseIndices_singleElementRangeWithDuplicate_throwsParseIndicesException() {
        // 3-3 creates 3, then 3 repeats
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "3"), () ->
                IndexParser.parseIndices("3-3,3"));
    }

    // ================================================================
    // INVALID INPUT & SYNTAX TESTS
    // ================================================================

    @Test
    public void parseIndices_emptyString_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices(""));
    }

    @Test
    public void parseIndices_onlySpaces_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("   "));
    }

    @Test
    public void parseIndices_invalidCharacters_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("a"));
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1-a"));
    }

    @Test
    public void parseIndices_incompleteRanges_throwsParseIndicesException() {
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("3-"));
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("-3"));
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1--3"));
    }

    @Test
    public void parseIndices_reverseRange_throwsParseIndicesException() {
        // 5-2 (end < start) should be invalid
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_INVALID_RANGE_ORDER, 5, 2), () ->
                IndexParser.parseIndices("5-2"));
    }

    @Test
    public void parseIndices_leadingTrailingCommasAndSpaces_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices(",1,2"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1,2,"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1,,2"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1, ,2"));
    }

    @Test
    public void parseIndices_invalidThenDuplicate_reportsInvalidFirst() {
        // "abc" invalid => invalid should be prioritized over duplicates
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("abc,1,2,1"));
    }

    @Test
    public void parseIndices_zeroIndex_throwsParseIndicesException() {
        // Zero is invalid since indices are one-based
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("0"));
    }

    @Test
    public void parseIndices_negativeIndex_throwsParseIndicesException() {
        // Negative index should be invalid
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("-1"));
    }

    @Test
    public void parseIndices_mixedValidAndZeroOrNegative_throwsParseIndicesException() {
        // 0 and -1 mixed with valid indices
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1,0,2"));
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1,-3,4"));
    }

    // Test overflow/large values
    @Test
    public void parseIndices_largeNumberOverflow_throwsParseIndicesException() {
        // Larger than Integer.MAX_VALUE (~2.1 billion)
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("999999999999"));
    }

    @Test
    public void parseIndices_largeNumberInRange_throwsParseIndicesException() {
        // Overflow during range expansion (start or end too large)
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1-999999999999"));
    }

    @Test
    public void parseIndices_mixedValidAndOverflow_throwsParseIndicesException() {
        // Mixed valid small indices and overflowed large index
        assertThrows(ParseIndicesException.class, MESSAGE_INVALID_INDICES, () ->
                IndexParser.parseIndices("1,2,999999999999"));
    }

    // ================================================================
    // MAX_RANGE_SIZE VALIDATION TESTS
    // ================================================================

    @Test
    public void parseIndices_rangeExactlyAtMaxSize_success() throws Exception {
        // Range of exactly MAX_RANGE_SIZE should be accepted (1-10000)
        List<Index> indices = IndexParser.parseIndices("1-" + MAX_RANGE_SIZE);
        assertEquals(MAX_RANGE_SIZE, indices.size());
        assertEquals(Index.fromOneBased(1), indices.get(0));
        assertEquals(Index.fromOneBased(MAX_RANGE_SIZE), indices.get(MAX_RANGE_SIZE - 1));
    }

    @Test
    public void parseIndices_rangeExceedsMaxSizeByOne_throwsParseIndicesException() {
        // Range of MAX_RANGE_SIZE + 1 should be rejected (1-10001)
        int endIndex = MAX_RANGE_SIZE + 1;
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, 1, endIndex, endIndex, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices("1-" + endIndex));
    }

    @Test
    public void parseIndices_veryLargeRange_throwsParseIndicesException() {
        // Very large range like 1-1000000 should be rejected
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, 1, 1000000, 1000000, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices("1-1000000"));
    }

    @Test
    public void parseIndices_largeRangeNotStartingAtOne_throwsParseIndicesException() {
        // Large range not starting at 1 (e.g., 100-20100) should be rejected
        int start = 100;
        int end = start + MAX_RANGE_SIZE; // size = 10001
        long size = (long) end - start + 1;
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, start, end, size, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices(start + "-" + end));
    }

    @Test
    public void parseIndices_multipleSmallRangesWithinLimit_success() throws Exception {
        // Multiple small ranges that total more than MAX_RANGE_SIZE should be accepted
        // Each individual range is small (1-100, 200-300, 400-500)
        List<Index> indices = IndexParser.parseIndices("1-100,200-300,400-500");
        assertEquals(302, indices.size()); // 100 + 101 + 101 = 302
    }

    @Test
    public void parseIndices_mixedWithOneLargeRange_throwsParseIndicesException() {
        // Mixed input where one range exceeds MAX_RANGE_SIZE
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, 1, 20000, 20000, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices("1,2,3,1-20000"));
    }

    @Test
    public void parseIndices_rangeWithLargeNumbers_throwsParseIndicesException() {
        // Range with large numbers that exceeds MAX_RANGE_SIZE (1000000-1020000)
        int start = 1000000;
        int end = start + MAX_RANGE_SIZE; // size = 10001
        long size = (long) end - start + 1;
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, start, end, size, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices(start + "-" + end));
    }

    @Test
    public void parseIndices_edgeCaseRangeSizeCalculation_throwsParseIndicesException() {
        // Test edge case where range size calculation is critical (5-10005)
        // Size = 10005 - 5 + 1 = 10001 (exceeds MAX_RANGE_SIZE)
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, 5, 10005, 10001, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices("5-10005"));
    }

    @Test
    public void parseIndices_rangeExactlyMaxSizeNotStartingAtOne_success() throws Exception {
        // Range of exactly MAX_RANGE_SIZE not starting at 1 (e.g., 100-10099) should succeed
        int start = 100;
        int end = start + MAX_RANGE_SIZE - 1; // size = 10000
        List<Index> indices = IndexParser.parseIndices(start + "-" + end);
        assertEquals(MAX_RANGE_SIZE, indices.size());
        assertEquals(Index.fromOneBased(start), indices.get(0));
        assertEquals(Index.fromOneBased(end), indices.get(MAX_RANGE_SIZE - 1));
    }

    @Test
    public void parseIndices_extremelyLargeRangeWithIntegerMaxValue_throwsParseIndicesException() {
        // Test with very large numbers close to Integer.MAX_VALUE
        int start = Integer.MAX_VALUE - 20000;
        int end = Integer.MAX_VALUE - 1;
        long size = (long) end - start + 1; // Should be 20000
        assertThrows(ParseIndicesException.class,
                String.format(MESSAGE_RANGE_TOO_LARGE, start, end, size, MAX_RANGE_SIZE), () ->
                        IndexParser.parseIndices(start + "-" + end));
    }
}

