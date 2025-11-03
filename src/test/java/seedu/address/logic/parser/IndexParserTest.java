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
    // HELPER METHODS
    // ================================================================

    /**
     * Helper method to assert that parsing the given input throws a ParseIndicesException
     * with the expected message.
     */
    private void assertParseIndicesThrows(String input, String expectedMessage) {
        assertThrows(ParseIndicesException.class, expectedMessage, () ->
                IndexParser.parseIndices(input));
    }

    /**
     * Helper method to assert that parsing the given input throws a ParseException
     * with the expected message.
     */
    private void assertParseThrows(String input, String expectedMessage) {
        assertThrows(ParseException.class, expectedMessage, () ->
                IndexParser.parseIndices(input));
    }

    /**
     * Helper method to assert that parsing the given input throws a ParseIndicesException
     * with a formatted duplicate message.
     */
    private void assertDuplicateThrows(String input, String duplicates) {
        assertParseIndicesThrows(input, String.format(MESSAGE_DUPLICATE_INDICES, duplicates));
    }

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
    public void parseIndices_simpleDuplicates_throwsParseIndicesException() {
        assertDuplicateThrows("1,2,1", "1");
        assertDuplicateThrows("3,1,2,3,1", "1, 3");
    }

    @Test
    public void parseIndices_rangeOverlaps_throwsParseIndicesException() {
        assertDuplicateThrows("1-3,2-4", "2, 3");
        assertDuplicateThrows("1-3,1-3", "1, 2, 3");
        assertDuplicateThrows("1-5,2-4", "2, 3, 4");
    }

    @Test
    public void parseIndices_rangeAndSingleOverlaps_throwsParseIndicesException() {
        assertDuplicateThrows("1-3,2", "2");
        assertDuplicateThrows("3-3,3", "3");
    }

    @Test
    public void parseIndices_complexMixedDuplicates_throwsParseIndicesException() {
        assertDuplicateThrows("1,2-4,3,5-7,6", "3, 6");
    }

    // ================================================================
    // INVALID INPUT & SYNTAX TESTS
    // ================================================================

    @Test
    public void parseIndices_emptyAndWhitespace_throwsParseIndicesException() {
        assertParseIndicesThrows("", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("   ", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_invalidCharacters_throwsParseIndicesException() {
        assertParseIndicesThrows("a", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1-a", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_incompleteRanges_throwsParseIndicesException() {
        assertParseIndicesThrows("3-", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("-3", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1--3", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_zeroAndNegativeIndices_throwsParseIndicesException() {
        assertParseIndicesThrows("0", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("-1", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1,0,2", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1,-3,4", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_overflowValues_throwsParseIndicesException() {
        assertParseIndicesThrows("999999999999", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1-999999999999", MESSAGE_INVALID_INDICES);
        assertParseIndicesThrows("1,2,999999999999", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_reverseRange_throwsParseIndicesException() {
        // 5-2 (end < start) should be invalid
        assertParseIndicesThrows("5-2", String.format(MESSAGE_INVALID_RANGE_ORDER, 5, 2));
    }

    @Test
    public void parseIndices_leadingTrailingCommasAndSpaces_throwsParseException() {
        assertParseThrows(",1,2", MESSAGE_INVALID_INDICES);
        assertParseThrows("1,2,", MESSAGE_INVALID_INDICES);
        assertParseThrows("1,,2", MESSAGE_INVALID_INDICES);
        assertParseThrows("1, ,2", MESSAGE_INVALID_INDICES);
    }

    @Test
    public void parseIndices_invalidThenDuplicate_reportsInvalidFirst() {
        // "abc" invalid => invalid should be prioritized over duplicates
        assertParseThrows("abc,1,2,1", MESSAGE_INVALID_INDICES);
    }

    // ================================================================
    // EDGE CASES AND BOUNDARY VALUE TESTS
    // ================================================================

    @Test
    public void parseIndices_largeValidSingleIndices_success() throws Exception {
        // Test very large but valid single indices
        List<Index> indices = IndexParser.parseIndices("2147483647"); // Integer.MAX_VALUE
        assertEquals(1, indices.size());
        assertEquals(Index.fromOneBased(2147483647), indices.get(0));
    }

    @Test
    public void parseIndices_boundaryRangeValues_success() throws Exception {
        // Test ranges with large boundary values that don't overflow
        List<Index> indices = IndexParser.parseIndices("100-102");
        assertEquals(3, indices.size());
        assertEquals(Index.fromOneBased(100), indices.get(0));
        assertEquals(Index.fromOneBased(101), indices.get(1));
        assertEquals(Index.fromOneBased(102), indices.get(2));
    }

    @Test
    public void parseIndices_largeValidRange_success() throws Exception {
        // Test a moderately large range that should work fine
        List<Index> indices = IndexParser.parseIndices("1-1000");
        assertEquals(1000, indices.size());
        assertEquals(Index.fromOneBased(1), indices.get(0));
        assertEquals(Index.fromOneBased(1000), indices.get(999));
    }

    @Test
    public void parseIndices_performanceEdgeCase_manySmallRanges() throws Exception {
        // Test parsing performance with many small ranges
        StringBuilder input = new StringBuilder();
        for (int i = 1; i <= 10; i += 2) {
            if (i > 1) {
                input.append(",");
            }
            input.append(i).append("-").append(i);
        }

        List<Index> indices = IndexParser.parseIndices(input.toString());
        assertEquals(5, indices.size()); // 5 ranges of size 1 each
    }

    @Test
    public void parseIndices_complexMixedValidScenario_success() throws Exception {
        // Test complex but valid mixed scenario
        List<Index> indices = IndexParser.parseIndices("1,5-10,15,20-25,100");
        assertEquals(15, indices.size()); // 1 + 6 + 1 + 6 + 1 = 15

        // Verify some key indices
        assertEquals(Index.fromOneBased(1), indices.get(0));
        assertEquals(Index.fromOneBased(5), indices.get(1));
        assertEquals(Index.fromOneBased(10), indices.get(6));
        assertEquals(Index.fromOneBased(15), indices.get(7));
        assertEquals(Index.fromOneBased(100), indices.get(14));
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

