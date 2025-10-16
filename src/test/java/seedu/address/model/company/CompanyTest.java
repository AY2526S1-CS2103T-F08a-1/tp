package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalCompanies.ALPHA;
import static seedu.address.testutil.TypicalCompanies.BOEING;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.CompanyBuilder;

public class CompanyTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Company company = new CompanyBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> company.getTags().remove(0));
    }

    @Test
    public void isSameCompany() {
        // same object -> returns true
        assertTrue(ALPHA.isSameCompany(ALPHA));

        // null -> returns false
        assertFalse(ALPHA.isSameCompany(null));

        // same name, all other attributes different -> returns true
        Company editedAirbus = new CompanyBuilder(ALPHA).withPhone(VALID_PHONE_BOEING).withEmail(VALID_EMAIL_BOEING)
                .withAddress(VALID_ADDRESS_BOEING).withTags(VALID_TAG_GOOD_PAY).build();
        assertTrue(ALPHA.isSameCompany(editedAirbus));

        // different name, all other attributes same -> returns false
        editedAirbus = new CompanyBuilder(ALPHA).withName(VALID_NAME_BOEING).build();
        assertFalse(ALPHA.isSameCompany(editedAirbus));

        // name differs in case, all other attributes same -> returns false
        Company editedBoeing = new CompanyBuilder(BOEING).withName(VALID_NAME_BOEING.toLowerCase()).build();
        assertFalse(BOEING.isSameCompany(editedBoeing));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOEING + " ";
        editedBoeing = new CompanyBuilder(BOEING).withName(nameWithTrailingSpaces).build();
        assertFalse(BOEING.isSameCompany(editedBoeing));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Company aliceCopy = new CompanyBuilder(ALPHA).build();
        assertTrue(ALPHA.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALPHA.equals(ALPHA));

        // null -> returns false
        assertFalse(ALPHA.equals(null));

        // different type -> returns false
        assertFalse(ALPHA.equals(5));

        // different company -> returns false
        assertFalse(ALPHA.equals(BOEING));

        // different name -> returns false
        Company editedAlice = new CompanyBuilder(ALPHA).withName(VALID_NAME_BOEING).build();
        assertFalse(ALPHA.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new CompanyBuilder(ALPHA).withPhone(VALID_PHONE_BOEING).build();
        assertFalse(ALPHA.equals(editedAlice));

        // different email -> returns false
        editedAlice = new CompanyBuilder(ALPHA).withEmail(VALID_EMAIL_BOEING).build();
        assertFalse(ALPHA.equals(editedAlice));

        // different address -> returns false
        editedAlice = new CompanyBuilder(ALPHA).withAddress(VALID_ADDRESS_BOEING).build();
        assertFalse(ALPHA.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new CompanyBuilder(ALPHA).withTags(VALID_TAG_GOOD_PAY).build();
        assertFalse(ALPHA.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Company.class.getCanonicalName() + "{name=" + ALPHA.getName() + ", phone=" + ALPHA.getPhone()
                + ", email=" + ALPHA.getEmail() + ", address=" + ALPHA.getAddress() + ", tags=" + ALPHA.getTags()
                + ", remark=" + ALPHA.getRemark() + ", status=" + ALPHA.getStatus() + "}";
        assertEquals(expected, ALPHA.toString());
    }
}
