package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.IndexParser.MESSAGE_DUPLICATE_INDICES;
import static seedu.address.logic.parser.IndexParser.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.IndexParser.MESSAGE_INVALID_INDICES;
import static seedu.address.model.tag.Tag.MAX_TAG_LENGTH;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.exceptions.ParseIndicesException;
import seedu.address.model.company.Address;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = "651+234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_TAG_EMPTY = "";
    private static final String INVALID_TAG_TOO_LONG = "a".repeat(MAX_TAG_LENGTH + 1); // exceeds the character limit

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_COMPANY, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_COMPANY, ParserUtil.parseIndex("  1  "));
    }

    // ================ parseIndices Tests ================
    // Edge case scenarios for testing the parseIndices() were generated with AI assistance
    // (OpenAI ChatGPT) to improve coverage and identify non-trivial cases.

    @Test
    public void parseIndices_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIndices(null));
    }

    @Test
    public void parseIndices_emptyString_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices(""));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("   "));
    }

    @Test
    public void parseIndices_singleValidIndex_success() throws Exception {
        List<Index> result = ParserUtil.parseIndices("1");
        List<Index> expected = Arrays.asList(Index.fromOneBased(1));
        assertEquals(expected, result);
    }

    @Test
    public void parseIndices_singleValidIndexWithWhitespace_success() throws Exception {
        List<Index> result = ParserUtil.parseIndices("  1  ");
        List<Index> expected = Arrays.asList(Index.fromOneBased(1));
        assertEquals(expected, result);
    }

    @Test
    public void parseIndices_multipleValidIndices_success() throws Exception {
        List<Index> result = ParserUtil.parseIndices("1,2,3");
        List<Index> expected = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
        );
        assertEquals(expected, result);
    }

    @Test
    public void parseIndices_multipleValidIndicesWithWhitespace_success() throws Exception {
        List<Index> result = ParserUtil.parseIndices(" 1 , 2 , 3 ");
        List<Index> expected = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
        );
        assertEquals(expected, result);
    }

    @Test
    public void parseIndices_duplicateIndices_throwsParseException() {
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "1"), ()
                -> ParserUtil.parseIndices("1,2,1"));
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "3"), ()
                -> ParserUtil.parseIndices("3,3"));
        assertThrows(ParseIndicesException.class, String.format(MESSAGE_DUPLICATE_INDICES, "2"), ()
                -> ParserUtil.parseIndices("1,2,3,2,4"));
    }

    @Test
    public void parseIndices_invalidIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,abc"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("0,1"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,-2"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,2,0"));
    }

    @Test
    public void parseIndices_malformedCommas_throwsParseException() {
        // Empty indices after commas
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices(",2"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,,3"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1, ,3"));
    }

    @Test
    public void parseIndices_mixedInvalidAndDuplicate_throwsParseExceptionForInvalid() {
        // Invalid indices should be caught first
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("abc,1,1"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () -> ParserUtil.parseIndices("1,0,1"));
    }

    @Test
    public void parseIndices_largeNumbers_success() throws Exception {
        List<Index> result = ParserUtil.parseIndices("999,1000");
        List<Index> expected = Arrays.asList(
                Index.fromOneBased(999),
                Index.fromOneBased(1000)
        );
        assertEquals(expected, result);
    }

    @Test
    public void parseIndices_outOfIntegerRange_throwsParseException() {
        String largeNumber = Long.toString((long) Integer.MAX_VALUE + 1);
        assertThrows(ParseException.class, MESSAGE_INVALID_INDICES, () ->
                ParserUtil.parseIndices("1," + largeNumber));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_whitespaceOnly_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG_EMPTY));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_tagTooLong_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG_TOO_LONG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTag_mixedCase_convertsToLowercase() throws Exception {
        // ParserUtil should return a Tag whose internal name is lowercased by Tag constructor
        String mixedCaseTag = "FrIend";
        Tag expectedTag = new Tag(VALID_TAG_1); // "friend"
        assertEquals(expectedTag, ParserUtil.parseTag(mixedCaseTag));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseRemark_invalidRemark_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseRemark(" ")); // white space
        assertThrows(ParseException.class, () -> ParserUtil.parseRemark("")); // empty
        // Edit can still clear remarks by using white space or empty, as it bypasses isValidRemark check as a special
        // case by creating a Remark(null) object. However, "add n/co r/ " and "add n/co r/" will not be allowed
        // as it makes no sense to specify remark upon creation, but not add any remark.
    }
}
