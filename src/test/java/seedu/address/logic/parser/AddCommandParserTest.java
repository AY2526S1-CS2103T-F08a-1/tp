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
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.REMARK_DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.REMARK_DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_PENDING_APPLICATION;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_PENDING_INTERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_PENDING_APPLICATION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_PENDING_INTERVIEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalCompanies.AIRBUS;
import static seedu.address.testutil.TypicalCompanies.BOEING;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.CompanyBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Company expectedCompany = new CompanyBuilder(BOEING).withTags(VALID_TAG_PENDING_APPLICATION).build();

        // whitespace only preamble
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING
                        + ADDRESS_DESC_BOEING + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                new AddCommand(expectedCompany));


        // multiple tags - all accepted
        Company expectedCompanyMultipleTags =
                new CompanyBuilder(BOEING).withTags(VALID_TAG_PENDING_APPLICATION, VALID_TAG_PENDING_INTERVIEW)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + ADDRESS_DESC_BOEING
                        + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                new AddCommand(expectedCompanyMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedCompanyString = NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING
                + ADDRESS_DESC_BOEING + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AIRBUS + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AIRBUS + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AIRBUS + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AIRBUS + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple remarks
        assertParseFailure(parser, REMARK_DESC_AIRBUS + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedCompanyString + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE,
                        PREFIX_REMARK));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedCompanyString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // remarks can take any values

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedCompanyString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedCompanyString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedCompanyString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedCompanyString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // remarks can take any values
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Company expectedCompany = new CompanyBuilder(AIRBUS).withTags().build();
        assertParseSuccess(parser,
                NAME_DESC_AIRBUS + PHONE_DESC_AIRBUS + EMAIL_DESC_AIRBUS + ADDRESS_DESC_AIRBUS
                        + REMARK_DESC_AIRBUS,
                new AddCommand(expectedCompany));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser,
                VALID_NAME_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + ADDRESS_DESC_BOEING
                        + REMARK_DESC_BOEING,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser,
                VALID_NAME_BOEING + VALID_PHONE_BOEING + VALID_EMAIL_BOEING + VALID_ADDRESS_BOEING
                        + REMARK_DESC_BOEING,
                expectedMessage);
    }

    @Test
    public void parse_someOptionalFieldsProvided_success() {
        // name + phone only
        Company expectedCompanyPhoneOnly = new CompanyBuilder()
                .withName(VALID_NAME_BOEING)
                .withPhone(VALID_PHONE_BOEING)
                .withEmail("noemailprovided@placeholder.com")
                .withAddress("No address provided")
                .withTags()
                .withRemark("No remark provided")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOEING + PHONE_DESC_BOEING,
                new AddCommand(expectedCompanyPhoneOnly));

        // name + email only
        Company expectedCompanyEmailOnly = new CompanyBuilder()
                .withName(VALID_NAME_BOEING)
                .withPhone("000")
                .withEmail(VALID_EMAIL_BOEING)
                .withAddress("No address provided")
                .withTags()
                .withRemark("No remark provided")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOEING + EMAIL_DESC_BOEING,
                new AddCommand(expectedCompanyEmailOnly));

        // name + address + tag
        Company expectedCompanyAddressTag = new CompanyBuilder()
                .withName(VALID_NAME_BOEING)
                .withPhone("000")
                .withEmail("noemailprovided@placeholder.com")
                .withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_PENDING_APPLICATION)
                .withRemark("No remark provided")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOEING + ADDRESS_DESC_BOEING + TAG_DESC_PENDING_APPLICATION,
                new AddCommand(expectedCompanyAddressTag));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser,
                INVALID_NAME_DESC + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + ADDRESS_DESC_BOEING
                        + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser,
                NAME_DESC_BOEING + INVALID_PHONE_DESC + EMAIL_DESC_BOEING + ADDRESS_DESC_BOEING
                        + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser,
                NAME_DESC_BOEING + PHONE_DESC_BOEING + INVALID_EMAIL_DESC + ADDRESS_DESC_BOEING
                        + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser,
                NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + INVALID_ADDRESS_DESC
                        + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION + REMARK_DESC_BOEING,
                Address.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser,
                NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + ADDRESS_DESC_BOEING
                        + INVALID_TAG_DESC + VALID_TAG_PENDING_APPLICATION + REMARK_DESC_BOEING,
                Tag.MESSAGE_CONSTRAINTS);

        // remarks can take any values

        // two invalid values, only first invalid value reported
        assertParseFailure(parser,
                INVALID_NAME_DESC + PHONE_DESC_BOEING + EMAIL_DESC_BOEING + INVALID_ADDRESS_DESC
                        + REMARK_DESC_BOEING,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + NAME_DESC_BOEING + PHONE_DESC_BOEING + EMAIL_DESC_BOEING
                        + ADDRESS_DESC_BOEING + TAG_DESC_PENDING_INTERVIEW + TAG_DESC_PENDING_APPLICATION
                        + REMARK_DESC_BOEING,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_simpleNameFormat_success() {
        // Test simple format with just name prefix
        Company expectedCompany = new CompanyBuilder()
                .withName(VALID_NAME_BOEING)
                .withPhone("000")
                .withEmail("noemailprovided@placeholder.com")
                .withAddress("No address provided")
                .withTags()
                .withRemark("No remark provided")
                .build();

        assertParseSuccess(parser, NAME_DESC_BOEING, new AddCommand(expectedCompany));
    }

    @Test
    public void parse_simpleNameFormatInvalidName_failure() {
        // Test simple format with invalid name
        assertParseFailure(parser, INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_simpleNameFormatDuplicateName_failure() {
        // Test simple format with duplicate name prefix
        assertParseFailure(parser, NAME_DESC_BOEING + NAME_DESC_AIRBUS,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }
}
