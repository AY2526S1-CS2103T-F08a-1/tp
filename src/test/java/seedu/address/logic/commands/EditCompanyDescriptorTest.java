package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.model.company.Address;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

/**
 * Contains unit tests for EditCommand.EditCompanyDescriptor.
 * Tests the descriptor's field management, validation methods, and defensive copying behavior.
 */
public class EditCompanyDescriptorTest {

    /**
     * Tests that isAnyFieldEdited returns false when no fields have been set.
     * This ensures the descriptor correctly identifies when it contains no edits.
     */
    @Test
    public void isAnyFieldEdited_noFieldsSet_returnsFalse() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        assertFalse(descriptor.isAnyFieldEdited());
    }

    /**
     * Tests that isAnyFieldEdited returns true when only one field is set.
     * This validates that the descriptor detects partial edits correctly.
     */
    @Test
    public void isAnyFieldEdited_oneFieldSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setName(new Name(VALID_NAME_BOEING));
        // Descriptor mirrors a user editing just the name (e.g., 'edit 1 n/Boeing').
        // isAnyFieldEdited should flag true because exactly one field is populated.
        assertTrue(descriptor.isAnyFieldEdited());
    }

    /**
     * Tests that isAnyFieldEdited returns true when all fields are set.
     * This ensures the descriptor correctly identifies complete edits.
     */
    @Test
    public void isAnyFieldEdited_allFieldsSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setName(new Name(VALID_NAME_BOEING));
        descriptor.setPhone(new Phone(VALID_PHONE_BOEING));
        descriptor.setEmail(new Email(VALID_EMAIL_BOEING));
        descriptor.setAddress(new Address(VALID_ADDRESS_BOEING));
        descriptor.setTags(new HashSet<>());
        descriptor.setRemark(new Remark(VALID_REMARK_BOEING));
        descriptor.setStatus(new Status(VALID_STATUS_BOEING));
        // Descriptor mirrors a fully populated edit payload.
        // isAnyFieldEdited should still report true as multiple fields are present.
        assertTrue(descriptor.isAnyFieldEdited());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns true when only tags are set.
     * This validates batch edit restrictions - tags can be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_onlyTagsSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag(VALID_TAG_GOOD_PAY));
        descriptor.setTags(tags);
        // Only tags are provided (batch scenario like 'edit 1,2 t/good-pay').
        // Batch whitelist should pass -> true.
        assertTrue(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns true when only remarks are set.
     * This validates batch edit restrictions - remarks can be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_onlyRemarksSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setRemark(new Remark(VALID_REMARK_BOEING));
        // Only remark set (e.g., 'edit 1 r/Great location...').
        // Batch rule allows remarks -> expect true.
        assertTrue(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns true when only status is set.
     * This validates batch edit restrictions - status can be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_onlyStatusSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setStatus(new Status(VALID_STATUS_BOEING));
        // Status-only update should be batch safe.
        assertTrue(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns true when tags, remarks, and status are all set.
     * This validates that all three fields can be batch edited together.
     */
    @Test
    public void isTagsAndRemarksOnly_tagsAndRemarksAndStatusSet_returnsTrue() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setTags(new HashSet<>());
        descriptor.setRemark(new Remark(VALID_REMARK_BOEING));
        descriptor.setStatus(new Status(VALID_STATUS_BOEING));
        // Combination of tags+remark+status is still within the allowed batch surface.
        assertTrue(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns false when name field is set.
     * This validates batch edit restrictions - name cannot be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_nameSet_returnsFalse() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setName(new Name(VALID_NAME_BOEING));
        // Presence of name implies forbidden batch field -> expect false.
        assertFalse(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns false when phone field is set.
     * This validates batch edit restrictions - phone cannot be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_phoneSet_returnsFalse() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setPhone(new Phone(VALID_PHONE_BOEING));
        // Phone also disqualifies for batch editing -> expect false.
        assertFalse(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns false when email field is set.
     * This validates batch edit restrictions - email cannot be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_emailSet_returnsFalse() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setEmail(new Email(VALID_EMAIL_BOEING));
        // Email toggled -> should violate batch restriction.
        assertFalse(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that isTagsAndRemarksOnly returns false when address field is set.
     * This validates batch edit restrictions - address cannot be batch edited.
     */
    @Test
    public void isTagsAndRemarksOnly_addressSet_returnsFalse() {
        EditCompanyDescriptor descriptor = new EditCompanyDescriptor();
        descriptor.setAddress(new Address(VALID_ADDRESS_BOEING));
        // Address present -> batch edit must reject.
        assertFalse(descriptor.isTagsAndRemarksOnly());
    }

    /**
     * Tests that the copy constructor creates a descriptor with identical field values.
     * This ensures all fields are properly copied during descriptor duplication.
     */
    @Test
    public void copyConstructor_copiesAllFields() {
        EditCompanyDescriptor original = new EditCompanyDescriptor();
        original.setName(new Name(VALID_NAME_BOEING));
        original.setPhone(new Phone(VALID_PHONE_BOEING));
        original.setEmail(new Email(VALID_EMAIL_BOEING));
        original.setAddress(new Address(VALID_ADDRESS_BOEING));
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag(VALID_TAG_GOOD_PAY));
        original.setTags(tags);
        original.setRemark(new Remark(VALID_REMARK_BOEING));
        original.setStatus(new Status(VALID_STATUS_BOEING));

        EditCompanyDescriptor copy = new EditCompanyDescriptor(original);

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getPhone(), copy.getPhone());
        assertEquals(original.getEmail(), copy.getEmail());
        assertEquals(original.getAddress(), copy.getAddress());
        assertEquals(original.getTags(), copy.getTags());
        assertEquals(original.getRemark(), copy.getRemark());
        assertEquals(original.getStatus(), copy.getStatus());
    }

    /**
     * Tests that the copy constructor creates a defensive copy of the tags set.
     * This ensures modifications to the original tags don't affect the copy.
     */
    @Test
    public void copyConstructor_tagsDefensiveCopy() {
        EditCompanyDescriptor original = new EditCompanyDescriptor();
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag(VALID_TAG_GOOD_PAY));
        original.setTags(tags);

        EditCompanyDescriptor copy = new EditCompanyDescriptor(original);

        // Verify defensive copy - modifying original shouldn't affect copy
        Set<Tag> originalTags = original.getTags().get();
        Set<Tag> copiedTags = copy.getTags().get();
        assertNotSame(originalTags, copiedTags);
        assertEquals(originalTags, copiedTags);
    }

    /**
     * Tests the equals method with various scenarios to ensure proper equality checking.
     * This validates that descriptors are equal only when all fields match.
     */
    @Test
    public void equals() {
        // same values -> returns true
        EditCompanyDescriptor descriptorWithSameValues = new EditCompanyDescriptor(DESC_AIRBUS);
        assertTrue(DESC_AIRBUS.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AIRBUS.equals(DESC_AIRBUS));

        // null -> returns false
        assertFalse(DESC_AIRBUS.equals(null));

        // different types -> returns false
        assertFalse(DESC_AIRBUS.equals(5));

        // different values -> returns false
        assertFalse(DESC_AIRBUS.equals(DESC_BOEING));

        // different name -> returns false
        EditCompanyDescriptor editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS)
                .withName(VALID_NAME_BOEING).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));

        // different phone -> returns false
        editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withPhone(VALID_PHONE_BOEING).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));

        // different email -> returns false
        editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withEmail(VALID_EMAIL_BOEING).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));

        // different address -> returns false
        editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withAddress(VALID_ADDRESS_BOEING).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));

        // different tags -> returns false
        editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withTags(VALID_TAG_GOOD_PAY).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));

        // different remark -> returns false
        editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withRemark(VALID_REMARK_BOEING).build();
        assertFalse(DESC_AIRBUS.equals(editedAirbus));
    }

    /**
     * Tests that toString produces the correct string representation with all fields.
     * This validates that the descriptor's string format matches expectations.
     */
    @Test
    public void toStringMethod() {
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        String expected = EditCompanyDescriptor.class.getCanonicalName() + "{name="
                + editCompanyDescriptor.getName().orElse(null) + ", phone="
                + editCompanyDescriptor.getPhone().orElse(null) + ", email="
                + editCompanyDescriptor.getEmail().orElse(null) + ", address="
                + editCompanyDescriptor.getAddress().orElse(null) + ", tags="
                + editCompanyDescriptor.getTags().orElse(null) + ", remark="
                + editCompanyDescriptor.getRemark().orElse(null) + ", status="
                + editCompanyDescriptor.getStatus().orElse(null) + "}";
        assertEquals(expected, editCompanyDescriptor.toString());
    }
}
