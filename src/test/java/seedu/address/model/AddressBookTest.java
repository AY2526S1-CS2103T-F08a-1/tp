package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalCompanies.ALPHA;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.company.Company;
import seedu.address.model.company.exceptions.DuplicateCompanyException;
import seedu.address.testutil.CompanyBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getCompanyList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatecompanies_throwsDuplicateCompanyException() {
        // Two companies with the same identity fields
        Company editedAlice = new CompanyBuilder(ALPHA).withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).build();
        List<Company> newCompanies = Arrays.asList(ALPHA, editedAlice);
        AddressBookStub newData = new AddressBookStub(newCompanies);

        assertThrows(DuplicateCompanyException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasCompany_nullCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasCompany(null));
    }

    @Test
    public void hasCompany_companyNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasCompany(ALPHA));
    }

    @Test
    public void hasCompany_companyInAddressBook_returnsTrue() {
        addressBook.addCompany(ALPHA);
        assertTrue(addressBook.hasCompany(ALPHA));
    }

    @Test
    public void hasCompany_companyWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addCompany(ALPHA);
        Company editedAlice = new CompanyBuilder(ALPHA).withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).build();
        assertTrue(addressBook.hasCompany(editedAlice));
    }

    @Test
    public void getCompanyList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getCompanyList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{companies=" + addressBook.getCompanyList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose companies list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Company> companies = FXCollections.observableArrayList();

        AddressBookStub(Collection<Company> companies) {
            this.companies.setAll(companies);
        }

        @Override
        public ObservableList<Company> getCompanyList() {
            return companies;
        }
    }

}
