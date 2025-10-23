package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.REMARK_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_DECENT_LOCATION;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_GOOD_PAY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_DECENT_LOCATION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.IndexParser.MESSAGE_INVALID_INDICES;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_COMPANY;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.model.company.Address;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    /**
     * Tests parsing with missing parts (no index, no fields, or both).
     * Validates that the parser rejects incomplete edit commands.
     * Expected: Parse fails with appropriate error messages for missing index or fields.
     */
    @Test
    public void parse_missingParts_failure() {
        // no index specified (user input: edit Airbus)
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, VALID_NAME_AIRBUS, MESSAGE_INVALID_INDICES);

        // no field specified (user input: edit 1)
        // Expected error message: MESSAGE_NOT_EDITED
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified (user input: edit)
        // Expected error message: MESSAGE_INVALID_FORMAT
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    /**
     * Tests parsing with invalid preambles (index formats).
     * Validates rejection of non-numeric, negative, zero, and malformed indices.
     * Expected: Parse fails with invalid indices error message.
     */
    @Test
    public void parse_invalidPreamble_failure() {
        // Invalid index (user input: 'edit a n/Airbus')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "a" + NAME_DESC_AIRBUS, MESSAGE_INVALID_INDICES);

        // Negative index (user input: 'edit -5 n/Airbus')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "-5" + NAME_DESC_AIRBUS, MESSAGE_INVALID_INDICES);

        // Zero index (user input: 'edit 0 n/Airbus')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "0" + NAME_DESC_AIRBUS, MESSAGE_INVALID_INDICES);

        // Unexpected words before prefixes (user input: 'edit 1 some random string')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_INDICES);

        // Unexpected prefix in preamble (user input: 'edit 1 i/ string')
        // Expected error message: MESSAGE_INVALID_FORMAT
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    /**
     * Tests parsing with invalid field values.
     * Validates that the parser rejects malformed names, phones, emails, addresses, and tags.
     * Expected: Parse fails with appropriate constraint messages for each invalid field.
     */
    @Test
    public void parse_invalidValue_failure() {
        // Invalid name value (user input: 'edit 1 n/James&')
        // Expected error message: Name.MESSAGE_CONSTRAINTS
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);

        // Invalid phone value (user input: 'edit 1 p/911a')
        // Expected error message: Phone.MESSAGE_CONSTRAINTS
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // Invalid email value (user input: 'edit 1 e/bob!yahoo')
        // Expected error message: Email.MESSAGE_CONSTRAINTS
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);

        // Invalid address value (user input: 'edit 1 a/')
        // Expected error message: Address.MESSAGE_CONSTRAINTS
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS);

        // Invalid tag value (user input: 'edit 1 t/hubby*')
        // Expected error message: Tag.MESSAGE_CONSTRAINTS
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);

        // Invalid phone followed by valid email (user input: 'edit 1 p/911a e/airbus@example.com')
        // Expected error message: Phone.MESSAGE_CONSTRAINTS (first failure encountered)
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AIRBUS, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Company} being edited,
        // parsing it together with a valid tag results in error

        // Mixed tag reset and values (user input: 'edit 1 t/decent-location t/good-pay t/')
        // Expected error message: Tag.MESSAGE_CONSTRAINTS
        assertParseFailure(parser,
                "1" + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);

        // Mixed tag reset with empty between values (user input: 'edit 1 t/decent-location t/ t/good-pay')
        // Expected error message: Tag.MESSAGE_CONSTRAINTS
        assertParseFailure(parser,
                "1" + TAG_DESC_DECENT_LOCATION + TAG_EMPTY + TAG_DESC_GOOD_PAY,
                Tag.MESSAGE_CONSTRAINTS);

        // Mixed tag reset starting empty (user input: 'edit 1 t/ t/decent-location t/good-pay')
        // Expected error message: Tag.MESSAGE_CONSTRAINTS
        assertParseFailure(parser,
                "1" + TAG_EMPTY + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY,
                Tag.MESSAGE_CONSTRAINTS);

        // Multiple invalid values; only first invalid value should be reported
        // User input: 'edit 1 n/James& e/bob!yahoo Block 312, Amy Street 1 11111111'
        // Expected error message: Name.MESSAGE_CONSTRAINTS
        assertParseFailure(parser,
                "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AIRBUS + VALID_PHONE_AIRBUS,
                Name.MESSAGE_CONSTRAINTS);
    }

    /**
     * Tests parsing with all fields specified in the edit command.
     * Validates that the parser correctly handles input with all available fields.
     * Expected: Parse succeeds and creates EditCommand with all fields populated.
     */
    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_COMPANY;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOEING + TAG_DESC_GOOD_PAY
                + EMAIL_DESC_AIRBUS + ADDRESS_DESC_AIRBUS + NAME_DESC_AIRBUS + TAG_DESC_DECENT_LOCATION
                + REMARK_DESC_AIRBUS;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_AIRBUS)
                .withPhone(VALID_PHONE_BOEING).withEmail(VALID_EMAIL_AIRBUS).withAddress(VALID_ADDRESS_AIRBUS)
                .withTags(VALID_TAG_GOOD_PAY, VALID_TAG_DECENT_LOCATION).withRemark(VALID_REMARK_AIRBUS)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        // User input:
        //   'edit 2 p/22222222 t/good-pay e/airbus@example.com a/Block 312, Amy Street 1'
        //   'n/Airbus t/decent-location r/Great location and pay'
        // Expected: success with descriptor containing all supplied fields for index 2.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing with only some fields specified.
     * Validates that partial edits with a subset of fields are correctly parsed.
     * Expected: Parse succeeds with only specified fields in the descriptor.
     */
    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOEING + EMAIL_DESC_AIRBUS;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BOEING)
                .withEmail(VALID_EMAIL_AIRBUS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        // User input: 'edit 1 p/22222222 e/airbus@example.com'
        // Expected: success updating phone and email for company 1 only.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing with exactly one field specified for each field type.
     * Validates that single-field edits work for name, phone, email, address, tags, and remark.
     * Expected: Parse succeeds for each individual field edit.
     */
    @Test
    public void parse_oneFieldSpecified_success() {
        // Name only (user input: 'edit 3 n/Airbus'; expect name updated for index 3)
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AIRBUS;
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_AIRBUS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // Phone only (user input: 'edit 3 p/11111111'; expect phone updated for index 3)
        userInput = targetIndex.getOneBased() + PHONE_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // Email only (user input: 'edit 3 e/airbus@example.com'; expect email updated for index 3)
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withEmail(VALID_EMAIL_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // Address only (user input: 'edit 3 a/Block 312, Amy Street 1'; expect address updated for index 3)
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withAddress(VALID_ADDRESS_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // Tags only (user input: 'edit 3 t/decent-location'; expect tags replaced with decent-location)
        userInput = targetIndex.getOneBased() + TAG_DESC_DECENT_LOCATION;
        descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // Remark only (user input: 'edit 3 r/Great location and pay'; expect remark updated for index 3)
        userInput = targetIndex.getOneBased() + REMARK_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withRemark(VALID_REMARK_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing with multiple repeated non-tag fields.
     * Validates that duplicate prefixes (except tags) are rejected.
     * Expected: Parse fails with duplicate prefix error message.
     */
    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // Duplicate phone: invalid then valid (user input: 'edit 1 p/911a p/22222222')
        // Expected error message: duplicate PREFIX_PHONE
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOEING;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // Duplicate phone: valid then invalid (user input: 'edit 1 p/22222222 p/911a')
        // Expected error message: duplicate PREFIX_PHONE
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOEING + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // Multiple repeated fields where phone/email/address prefixes appear three times:
        //   user input starts 'edit 1 p/11111111 a/Block 312, Amy Street 1 e/airbus@example.com t/decent-location'
        //   and later repeats with 'p/22222222 a/Block 123, Bobby Street 3 e/boeing@example.com t/good-pay'
        // Expected error message: duplicate PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS
        userInput = targetIndex.getOneBased() + PHONE_DESC_AIRBUS + ADDRESS_DESC_AIRBUS + EMAIL_DESC_AIRBUS
                + TAG_DESC_DECENT_LOCATION + PHONE_DESC_AIRBUS + ADDRESS_DESC_AIRBUS + EMAIL_DESC_AIRBUS
                + TAG_DESC_DECENT_LOCATION + PHONE_DESC_BOEING + ADDRESS_DESC_BOEING + EMAIL_DESC_BOEING
                + TAG_DESC_GOOD_PAY;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // Repeated invalid values (user input: 'edit 1 p/911a a/ e/bob!yahoo p/911a a/ e/bob!yahoo')
        // Expected error message: duplicate PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    /**
     * Tests parsing with empty tag prefix to reset/clear all tags.
     * Validates that tags can be cleared by specifying the tag prefix with no value.
     * Expected: Parse succeeds with an empty tags set in the descriptor.
     */
    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        // User input: 'edit 3 t/' (empty tag value)
        // Expected: success clearing all tags for company at index 3.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // ================ Batch Edit Integration Tests ================
    // Edge case scenarios for testing the batch editing of tags were generated with AI assistance
    // (OpenAI ChatGPT) to improve coverage and identify non-trivial cases.

    /**
     * Tests parsing batch edit with two comma-separated indices.
     * Validates that the parser correctly handles multiple indices for batch operations.
     * Expected: Parse succeeds and creates batch EditCommand with both indices.
     */
    @Test
    public void parse_batchEditTwoIndices_success() {
        String userInput = "1,2" + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 1,2 t/decent-location'
        // Expected: success targeting indices [1,2] with tags set to decent-location.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with three comma-separated indices and multiple tags.
     * Validates that batch editing works with more than two indices and multiple field values.
     * Expected: Parse succeeds and creates batch EditCommand with all three indices.
     */
    @Test
    public void parse_batchEditThreeIndices_success() {
        String userInput = "1,2,3" + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_DECENT_LOCATION, VALID_TAG_GOOD_PAY).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 1,2,3 t/decent-location t/good-pay'
        // Expected: success applying both tags to indices [1,2,3].
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with spaces around commas and indices.
     * Validates that the parser handles whitespace gracefully in index lists.
     * Expected: Parse succeeds despite extra spaces around commas.
     */
    @Test
    public void parse_batchEditWithSpaces_success() {
        String userInput = " 1 , 2 , 3 " + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit  1 , 2 , 3  t/decent-location' (extra spaces around commas)
        // Expected: success with indices parsed as [1,2,3].
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing with a single index (without commas) for batch edit format.
     * Validates that single-index input is treated as a regular (non-batch) edit.
     * Expected: Parse succeeds and creates a single EditCommand, not a batch command.
     */
    @Test
    public void parse_batchEditSingleIndexInList_success() {
        String userInput = "2" + TAG_DESC_DECENT_LOCATION;
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(INDEX_SECOND_COMPANY, descriptor);

        // User input: 'edit 2 t/decent-location'
        // Expected: success treated as single-index edit for index 2.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with empty tag prefix to clear tags for multiple companies.
     * Validates that tag clearing works in batch mode.
     * Expected: Parse succeeds with empty tags set for batch operation.
     */
    @Test
    public void parse_batchEditResetTags_success() {
        String userInput = "1,2" + TAG_EMPTY;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 1,2 t/' (empty tag value)
        // Expected: success clearing tags for both indices.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with various invalid index formats.
     * Validates rejection of duplicate indices, non-numeric, zero, negative, and empty indices.
     * Expected: Parse fails with appropriate error messages for each invalid format.
     */
    @Test
    public void parse_batchEditInvalidIndices_failure() {
        // Duplicate indices (user input: 'edit 1,2,1 t/decent-location')
        // Expected error message: "Duplicate indices found: 1..."
        assertParseFailure(parser, "1,2,1" + TAG_DESC_DECENT_LOCATION,
                "Duplicate indices found: 1. Each index should appear only once.");

        // Non-numeric token (user input: 'edit 1,abc t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "1,abc" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);

        // Zero index (user input: 'edit 0,1 t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "0,1" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);

        // Negative index (user input: 'edit 1,-2 t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "1,-2" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);

        // Missing index after comma (user input: 'edit 1, t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "1," + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);
        // Missing index before comma (user input: 'edit ,2 t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, ",2" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);
        // Double comma creates empty token (user input: 'edit 1,,3 t/decent-location')
        // Expected error message: MESSAGE_INVALID_INDICES
        assertParseFailure(parser, "1,,3" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);
    }

    /**
     * Tests parsing batch edit with non-tag fields like phone.
     * Validates that the parser accepts batch edits with fields beyond just tags.
     * Expected: Parse succeeds for batch editing phone field (validation happens at execution).
     */
    @Test
    public void parse_batchEditNonTagFields_success() {
        // Batch edit currently supports all fields, though primarily intended for tags
        String userInput = "1,2" + PHONE_DESC_BOEING;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BOEING).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 1,2 p/22222222'
        // Expected: success producing batch command that updates phone for indices [1,2].
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with mixed field types (phone and tags).
     * Validates that multiple different fields can be specified in batch edit.
     * Expected: Parse succeeds with both phone and tags in the descriptor.
     */
    @Test
    public void parse_batchEditMixedFields_success() {
        String userInput = "1,2" + PHONE_DESC_BOEING + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withPhone(VALID_PHONE_BOEING).withTags(VALID_TAG_DECENT_LOCATION).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 1,2 p/22222222 t/decent-location'
        // Expected: success updating both phone and tags across the batch indices.
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with large index values (999, 1000).
     * Validates that the parser handles large but valid integer indices.
     * Expected: Parse succeeds with large index values (range validation happens at execution).
     */
    @Test
    public void parse_batchEditLargeIndices_success() {
        String userInput = "999,1000" + TAG_DESC_DECENT_LOCATION;
        List<Index> indices =
                Arrays.asList(Index.fromOneBased(999), Index.fromOneBased(1000));
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        // User input: 'edit 999,1000 t/decent-location'
        // Expected: success accepting large indices (bounds checked during execution).
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    /**
     * Tests parsing batch edit with indices exceeding Integer.MAX_VALUE.
     * Validates that the parser rejects indices outside the valid integer range.
     * Expected: Parse fails with invalid indices error message.
     */
    @Test
    public void parse_batchEditOutOfIntegerRange_failure() {
        String largeNumber = Long.toString((long) Integer.MAX_VALUE + 1);
        // User input: 'edit 1,<Integer.MAX_VALUE+1> t/decent-location'
        // Expected error message: MESSAGE_INVALID_INDICES (index exceeds integer range)
        assertParseFailure(parser, "1," + largeNumber + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_INDICES);
    }
}
