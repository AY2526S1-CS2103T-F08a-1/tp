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

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AIRBUS, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AIRBUS, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AIRBUS, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AIRBUS, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Company} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser,
                "1" + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                "1" + TAG_DESC_DECENT_LOCATION + TAG_EMPTY + TAG_DESC_GOOD_PAY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                "1" + TAG_EMPTY + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser,
                "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AIRBUS + VALID_PHONE_AIRBUS,
                Name.MESSAGE_CONSTRAINTS);
    }

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

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOEING + EMAIL_DESC_AIRBUS;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BOEING)
                .withEmail(VALID_EMAIL_AIRBUS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AIRBUS;
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_AIRBUS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withEmail(VALID_EMAIL_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withAddress(VALID_ADDRESS_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_DECENT_LOCATION;
        descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        //remark
        userInput = targetIndex.getOneBased() + REMARK_DESC_AIRBUS;
        descriptor = new EditCompanyDescriptorBuilder().withRemark(VALID_REMARK_AIRBUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOEING;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOEING + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AIRBUS + ADDRESS_DESC_AIRBUS + EMAIL_DESC_AIRBUS
                + TAG_DESC_DECENT_LOCATION + PHONE_DESC_AIRBUS + ADDRESS_DESC_AIRBUS + EMAIL_DESC_AIRBUS
                + TAG_DESC_DECENT_LOCATION + PHONE_DESC_BOEING + ADDRESS_DESC_BOEING + EMAIL_DESC_BOEING
                + TAG_DESC_GOOD_PAY;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // ================ Batch Edit Integration Tests ================
    // Edge case scenarios for testing the batch editing of tags were generated with AI assistance
    // (OpenAI ChatGPT) to improve coverage and identify non-trivial cases.

    @Test
    public void parse_batchEditTwoIndices_success() {
        String userInput = "1,2" + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditThreeIndices_success() {
        String userInput = "1,2,3" + TAG_DESC_DECENT_LOCATION + TAG_DESC_GOOD_PAY;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_DECENT_LOCATION, VALID_TAG_GOOD_PAY).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditWithSpaces_success() {
        String userInput = " 1 , 2 , 3 " + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditSingleIndexInList_success() {
        String userInput = "2" + TAG_DESC_DECENT_LOCATION;
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(INDEX_SECOND_COMPANY, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditResetTags_success() {
        String userInput = "1,2" + TAG_EMPTY;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditInvalidIndices_failure() {
        // Duplicate indices
        assertParseFailure(parser, "1,2,1" + TAG_DESC_DECENT_LOCATION,
                "Duplicate indices found: 1. Each index should appear only once.");

        // Invalid index format
        assertParseFailure(parser, "1,abc" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);

        // Zero index
        assertParseFailure(parser, "0,1" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);

        // Negative index
        assertParseFailure(parser, "1,-2" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);

        // Empty indices
        assertParseFailure(parser, "1," + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, ",2" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1,,3" + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_batchEditNonTagFields_success() {
        // Batch edit currently supports all fields, though primarily intended for tags
        String userInput = "1,2" + PHONE_DESC_BOEING;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BOEING).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditMixedFields_success() {
        String userInput = "1,2" + PHONE_DESC_BOEING + TAG_DESC_DECENT_LOCATION;
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withPhone(VALID_PHONE_BOEING).withTags(VALID_TAG_DECENT_LOCATION).build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditLargeIndices_success() {
        String userInput = "999,1000" + TAG_DESC_DECENT_LOCATION;
        List<Index> indices =
                Arrays.asList(Index.fromOneBased(999), Index.fromOneBased(1000));
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_DECENT_LOCATION)
                .build();
        EditCommand expectedCommand = new EditCommand(indices, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_batchEditOutOfIntegerRange_failure() {
        String largeNumber = Long.toString((long) Integer.MAX_VALUE + 1);
        assertParseFailure(parser, "1," + largeNumber + TAG_DESC_DECENT_LOCATION, MESSAGE_INVALID_FORMAT);
    }
}
