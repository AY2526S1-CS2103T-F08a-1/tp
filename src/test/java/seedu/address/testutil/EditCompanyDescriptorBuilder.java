package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditcompanyDescriptor;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditcompanyDescriptor objects.
 */
public class EditCompanyDescriptorBuilder {

    private EditcompanyDescriptor descriptor;

    public EditCompanyDescriptorBuilder() {
        descriptor = new EditcompanyDescriptor();
    }

    public EditCompanyDescriptorBuilder(EditcompanyDescriptor descriptor) {
        this.descriptor = new EditcompanyDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditcompanyDescriptor} with fields containing {@code company}'s details
     */
    public EditCompanyDescriptorBuilder(Company company) {
        descriptor = new EditcompanyDescriptor();
        descriptor.setName(company.getName());
        descriptor.setPhone(company.getPhone());
        descriptor.setEmail(company.getEmail());
        descriptor.setAddress(company.getAddress());
        descriptor.setTags(company.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditcompanyDescriptor} that we are building.
     */
    public EditCompanyDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditcompanyDescriptor} that we are building.
     */
    public EditCompanyDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditcompanyDescriptor} that we are building.
     */
    public EditCompanyDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditcompanyDescriptor} that we are building.
     */
    public EditCompanyDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditcompanyDescriptor}
     * that we are building.
     */
    public EditCompanyDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditcompanyDescriptor build() {
        return descriptor;
    }
}
