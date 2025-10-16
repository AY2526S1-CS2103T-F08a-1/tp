package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_DECENT_LOCATION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.company.Company;

/**
 * A utility class containing a list of {@code Company} objects to be used in tests.
 */
public class TypicalCompanies {

    public static final Company ALPHA = new CompanyBuilder().withName("Alpha Industries")
            .withAddress("123, Jurong West Ave 6, #08-111").withEmail("contact@alpha.com")
            .withPhone("94351253").withTags("supplier")
            .withRemark("Leading manufacturer of industrial equipment.")
            .withStatus("to-apply").build();
    public static final Company BETA = new CompanyBuilder().withName("Beta Corporation")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withEmail("info@beta.com").withPhone("98765432")
            .withTags("client", "partner").withRemark("Trusted partner for software solutions.")
            .withStatus("tech-interview").build();
    public static final Company CONS = new CompanyBuilder().withName("Consolidated Traders").withPhone("95352563")
            .withEmail("sales@consolidated.com").withAddress("1 Wall Street Plaza")
            .withRemark("Specializes in bulk trading operations.")
            .withStatus("applied").build();
    public static final Company DELTA = new CompanyBuilder().withName("Delta Enterprises").withPhone("87652533")
            .withEmail("contact@delta.com").withAddress("10 Enterprise Road").withTags("supplier")
            .withRemark("Supplier of high-quality raw materials.")
            .withStatus("hr-interview").build();
    public static final Company ELITE = new CompanyBuilder().withName("Elite Manufacturing").withPhone("9482224")
            .withEmail("enquiry@elite.com").withAddress("50 Michigan Avenue")
            .withRemark("Known for precision engineering.")
            .withStatus("offered").build();
    public static final Company FUSION = new CompanyBuilder().withName("Fusion Systems").withPhone("9482427")
            .withEmail("hello@fusion.com").withAddress("88 Little Tokyo Street")
            .withRemark("Innovative solutions for IT infrastructure.")
            .withStatus("oa").build();
    public static final Company GLOBAL = new CompanyBuilder().withName("Global Trading Co").withPhone("9482442")
            .withEmail("info@globaltrading.com")
            .withAddress("4 Commerce Street")
            .withRemark("Handles international trading and logistics.")
            .withStatus("rejected")
            .build();

    // Manually added
    public static final Company HOON = new CompanyBuilder().withName("Hoon Logistics").build();
    public static final Company IDA = new CompanyBuilder().withName("Ida Solutions").withPhone("8482131")
            .withEmail("info@innovative.com").withAddress("72 Chicago Avenue").build();

    // Manually added - Company's details found in {@code CommandTestUtil}
    public static final Company AIRBUS = new CompanyBuilder().withName(VALID_NAME_AIRBUS).withPhone(VALID_PHONE_AIRBUS)
            .withEmail(VALID_EMAIL_AIRBUS).withAddress(VALID_ADDRESS_AIRBUS).withTags(VALID_TAG_DECENT_LOCATION)
            .withRemark(VALID_REMARK_AIRBUS).withStatus(VALID_STATUS_AIRBUS).build();
    public static final Company BOEING = new CompanyBuilder().withName(VALID_NAME_BOEING).withPhone(VALID_PHONE_BOEING)
            .withEmail(VALID_EMAIL_BOEING).withAddress(VALID_ADDRESS_BOEING)
            .withTags(VALID_TAG_GOOD_PAY, VALID_TAG_DECENT_LOCATION)
            .withRemark(VALID_REMARK_BOEING).withStatus(VALID_STATUS_BOEING).build();

    public static final String KEYWORD_MATCHING_MEIER = "Corporation"; // A keyword that matches Corporation

    private TypicalCompanies() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical companies.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Company company : getTypicalcompanies()) {
            ab.addCompany(company);
        }
        return ab;
    }

    public static List<Company> getTypicalcompanies() {
        return new ArrayList<>(Arrays.asList(ALPHA, BETA, CONS, DELTA, ELITE, FUSION, GLOBAL));
    }
}
