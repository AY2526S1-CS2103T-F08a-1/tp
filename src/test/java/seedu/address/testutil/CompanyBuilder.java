package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_AIRBUS;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Company objects.
 */
public class CompanyBuilder {

    public static final String DEFAULT_NAME = VALID_NAME_AIRBUS;
    public static final String DEFAULT_PHONE = VALID_PHONE_AIRBUS;
    public static final String DEFAULT_EMAIL = VALID_EMAIL_AIRBUS;
    public static final String DEFAULT_ADDRESS = VALID_ADDRESS_AIRBUS;
    public static final String DEFAULT_REMARK = VALID_REMARK_AIRBUS;
    public static final String DEFAULT_STATUS = VALID_STATUS_AIRBUS;

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Remark remark;
    private Status status;

    /**
     * Creates a {@code CompanyBuilder} with the default details.
     */
    public CompanyBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        remark = new Remark(DEFAULT_REMARK);
        status = new Status(DEFAULT_STATUS);
    }

    /**
     * Initializes the CompanyBuilder with the data of {@code companyToCopy}.
     */
    public CompanyBuilder(Company companyToCopy) {
        name = companyToCopy.getName();
        phone = companyToCopy.getPhone();
        email = companyToCopy.getEmail();
        address = companyToCopy.getAddress();
        tags = new HashSet<>(companyToCopy.getTags());
        remark = companyToCopy.getRemark();
        status = companyToCopy.getStatus();
    }

    /**
     * Sets the {@code Name} of the {@code Company} that we are building.
     */
    public CompanyBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the
     * {@code Company} that we are building.
     */
    public CompanyBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Company} that we are building.
     */
    public CompanyBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Company} that we are building.
     */
    public CompanyBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Company} that we are building.
     */
    public CompanyBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Remark} of the {@code Company} that we are building.
     */
    public CompanyBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

    /**
     * Sets the {@code Status} of the {@code Company} that we are building.
     */
    public CompanyBuilder withStatus(String status) {
        this.status = new Status(status);
        return this;
    }

    public Company build() {
        return new Company(name, phone, email, address, tags, remark, status);
    }

}
