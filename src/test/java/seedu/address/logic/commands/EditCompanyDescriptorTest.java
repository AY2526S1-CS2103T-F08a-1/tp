package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

public class EditCompanyDescriptorTest {

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
        EditCompanyDescriptor editedAirbus = new EditCompanyDescriptorBuilder(DESC_AIRBUS).withName(VALID_NAME_BOEING)
                .build();
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
