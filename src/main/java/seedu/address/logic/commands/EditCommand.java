package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COMPANIES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.IndexOutOfBoundsException;
import seedu.address.model.Model;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing company in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the company identified "
            + "by the index number used in the displayed company list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_STATUS + "STATUS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com\n"
            + "For batch editing: " + COMMAND_WORD + " 1,2,3 "
            + PREFIX_STATUS + "in-process";

    public static final String MESSAGE_EDIT_COMPANY_SUCCESS = "Edited Company: %1$s";
    public static final String MESSAGE_BATCH_EDIT_SUCCESS = "Edited %1$d companies successfully";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_COMPANY = "This company already exists in the address book.";

    private final Index index;
    private final List<Index> indices;
    private final EditCompanyDescriptor editCompanyDescriptor;

    /**
     * Creates an EditCommand to edit a single company.
     *
     * @param index of the company in the filtered company list to edit
     * @param editCompanyDescriptor details to edit the company with
     */
    public EditCommand(Index index, EditCompanyDescriptor editCompanyDescriptor) {
        requireNonNull(index);
        requireNonNull(editCompanyDescriptor);

        this.index = index;
        this.indices = null;
        this.editCompanyDescriptor = new EditCompanyDescriptor(editCompanyDescriptor);
    }

    /**
     * Creates an EditCommand to edit multiple companies in batch.
     * All indices must be valid and within range before calling this constructor.
     *
     * @param indices list of indices of companies in the filtered company list to edit
     * @param editCompanyDescriptor details to edit the companies with
     */
    public EditCommand(List<Index> indices, EditCompanyDescriptor editCompanyDescriptor) {
        requireNonNull(indices);
        requireNonNull(editCompanyDescriptor);
        assert !indices.isEmpty() : "Indices list cannot be empty";

        this.index = null;
        this.indices = new ArrayList<>(indices);
        this.editCompanyDescriptor = new EditCompanyDescriptor(editCompanyDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (indices != null) {
            return executeBatchEdit(model);
        } else {
            return executeSingleEdit(model);
        }
    }

    /**
     * Executes single company edit operation.
     *
     * @param model the model containing the company data
     * @return the result of the command execution
     * @throws CommandException if the index is invalid or company already exists
     */
    private CommandResult executeSingleEdit(Model model) throws CommandException {
        List<Company> lastShownList = model.getFilteredCompanyList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new IndexOutOfBoundsException(index.getOneBased(), lastShownList.size());
        }

        Company companyToEdit = lastShownList.get(index.getZeroBased());
        Company editedCompany = createEditedCompany(companyToEdit, editCompanyDescriptor);

        if (!companyToEdit.isSameCompany(editedCompany) && model.hasCompany(editedCompany)) {
            throw new CommandException(MESSAGE_DUPLICATE_COMPANY);
        }

        model.setCompany(companyToEdit, editedCompany);
        model.updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        return new CommandResult(String.format(MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany)));
    }

    /**
     * Executes batch company edit operation with comprehensive validation.
     * Validates range and checks for duplicate companies with clear error messages.
     *
     * @param model the model containing the company data
     * @return the result of the command execution
     * @throws CommandException if any index is out of range or would create duplicate companies
     */
    private CommandResult executeBatchEdit(Model model) throws CommandException {
        List<Company> lastShownList = model.getFilteredCompanyList();

        // Validate all indices are within range with informative error
        validateIndicesRange(lastShownList.size());

        // Validate that editing won't create duplicate companies
        validateNoDuplicateCompanies(model, lastShownList);

        // All validations passed - perform batch edit
        for (Index index : indices) {
            Company companyToEdit = lastShownList.get(index.getZeroBased());
            Company editedCompany = createEditedCompany(companyToEdit, editCompanyDescriptor);
            model.setCompany(companyToEdit, editedCompany);
        }

        model.updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        return new CommandResult(String.format(MESSAGE_BATCH_EDIT_SUCCESS, indices.size()));
    }

    /**
     * Validates that all indices are within valid range.
     *
     * @param listSize the size of the current company list
     * @throws IndexOutOfBoundsException if any index is out of range
     */
    private void validateIndicesRange(int listSize) throws IndexOutOfBoundsException {
        for (Index index : indices) {
            if (index.getZeroBased() >= listSize) {
                throw new IndexOutOfBoundsException(index.getOneBased(), listSize);
            }
        }
    }

    /**
     * Validates that batch editing will not create duplicate companies.
     *
     * @param model the model containing the company data
     * @param lastShownList the current filtered company list
     * @throws CommandException if any edit would create a duplicate company
     */
    private void validateNoDuplicateCompanies(Model model, List<Company> lastShownList) throws CommandException {
        for (Index index : indices) {
            Company companyToEdit = lastShownList.get(index.getZeroBased());
            Company editedCompany = createEditedCompany(companyToEdit, editCompanyDescriptor);

            if (!companyToEdit.isSameCompany(editedCompany) && model.hasCompany(editedCompany)) {
                throw new CommandException(MESSAGE_DUPLICATE_COMPANY);
            }
        }
    }

    /**
     * Creates and returns a {@code Company} with the details of {@code companyToEdit}
     * edited with {@code editCompanyDescriptor}.
     */
    private static Company createEditedCompany(Company companyToEdit, EditCompanyDescriptor editCompanyDescriptor) {
        assert companyToEdit != null;

        Name updatedName = editCompanyDescriptor.getName().orElse(companyToEdit.getName());
        Phone updatedPhone = editCompanyDescriptor.getPhone().orElse(companyToEdit.getPhone());
        Email updatedEmail = editCompanyDescriptor.getEmail().orElse(companyToEdit.getEmail());
        Address updatedAddress = editCompanyDescriptor.getAddress().orElse(companyToEdit.getAddress());
        Set<Tag> updatedTags = editCompanyDescriptor.getTags().orElse(companyToEdit.getTags());
        Remark updatedRemark = editCompanyDescriptor.getRemark().orElse(companyToEdit.getRemark());
        Status updatedStatus = editCompanyDescriptor.getStatus().orElse(companyToEdit.getStatus());

        return new Company(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedRemark,
                updatedStatus);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return Objects.equals(index, otherEditCommand.index)
                && Objects.equals(indices, otherEditCommand.indices)
                && editCompanyDescriptor.equals(otherEditCommand.editCompanyDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("indices", indices)
                .add("editCompanyDescriptor", editCompanyDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the company with. Each non-empty field value will replace the
     * corresponding field value of the company.
     */
    public static class EditCompanyDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Remark remark;
        private Status status;

        public EditCompanyDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditCompanyDescriptor(EditCompanyDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setRemark(toCopy.remark);
            setStatus(toCopy.status);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags, remark, status);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code remark} to this object's {@code remark}.
         */
        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        /**
         * Returns an optional remark, which is empty if {@code remark} is null.
         */
        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        /**
         * Sets {@code status} to this object's {@code status}.
         */
        public void setStatus(Status status) {
            this.status = status;
        }

        /**
         * Returns an optional status, which is empty if {@code status} is null.
         */
        public Optional<Status> getStatus() {
            return Optional.ofNullable(status);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditCompanyDescriptor)) {
                return false;
            }

            EditCompanyDescriptor otherEditCompanyDescriptor = (EditCompanyDescriptor) other;
            return Objects.equals(name, otherEditCompanyDescriptor.name)
                    && Objects.equals(phone, otherEditCompanyDescriptor.phone)
                    && Objects.equals(email, otherEditCompanyDescriptor.email)
                    && Objects.equals(address, otherEditCompanyDescriptor.address)
                    && Objects.equals(tags, otherEditCompanyDescriptor.tags)
                    && Objects.equals(remark, otherEditCompanyDescriptor.remark)
                    && Objects.equals(status, otherEditCompanyDescriptor.status);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("remark", remark)
                    .add("status", status)
                    .toString();
        }
    }
}
