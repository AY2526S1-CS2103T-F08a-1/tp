package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalCompanies.ALPHA;
import static seedu.address.testutil.TypicalCompanies.BOEING;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.company.exceptions.CompanyNotFoundException;
import seedu.address.model.company.exceptions.DuplicateCompanyException;
import seedu.address.testutil.CompanyBuilder;

public class UniqueCompanyListTest {

    private final UniqueCompanyList uniqueCompanyList = new UniqueCompanyList();

    @Test
    public void contains_nullCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.contains(null));
    }

    @Test
    public void contains_companyNotInList_returnsFalse() {
        assertFalse(uniqueCompanyList.contains(ALPHA));
    }

    @Test
    public void contains_companyInList_returnsTrue() {
        uniqueCompanyList.add(ALPHA);
        assertTrue(uniqueCompanyList.contains(ALPHA));
    }

    @Test
    public void contains_companyWithSameIdentityFieldsInList_returnsTrue() {
        uniqueCompanyList.add(ALPHA);
        Company editedAlice = new CompanyBuilder(ALPHA).withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).build();
        assertTrue(uniqueCompanyList.contains(editedAlice));
    }

    @Test
    public void add_nullCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.add(null));
    }

    @Test
    public void add_duplicateCompany_throwsDuplicateCompanyException() {
        uniqueCompanyList.add(ALPHA);
        assertThrows(DuplicateCompanyException.class, () -> uniqueCompanyList.add(ALPHA));
    }

    @Test
    public void setCompany_nullTargetCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.setCompany(null, ALPHA));
    }

    @Test
    public void setCompany_nullEditedCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.setCompany(ALPHA, null));
    }

    @Test
    public void setCompany_targetCompanyNotInList_throwsCompanyNotFoundException() {
        assertThrows(CompanyNotFoundException.class, () -> uniqueCompanyList.setCompany(ALPHA, ALPHA));
    }

    @Test
    public void setCompany_editedCompanyIsSameCompany_success() {
        uniqueCompanyList.add(ALPHA);
        uniqueCompanyList.setCompany(ALPHA, ALPHA);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        expectedUniqueCompanyList.add(ALPHA);
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setCompany_editedCompanyHasSameIdentity_success() {
        uniqueCompanyList.add(ALPHA);
        Company editedAlice = new CompanyBuilder(ALPHA).withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).build();
        uniqueCompanyList.setCompany(ALPHA, editedAlice);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        expectedUniqueCompanyList.add(editedAlice);
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setCompany_editedCompanyHasDifferentIdentity_success() {
        uniqueCompanyList.add(ALPHA);
        uniqueCompanyList.setCompany(ALPHA, BOEING);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        expectedUniqueCompanyList.add(BOEING);
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setCompany_editedCompanyHasNonUniqueIdentity_throwsDuplicateCompanyException() {
        uniqueCompanyList.add(ALPHA);
        uniqueCompanyList.add(BOEING);
        assertThrows(DuplicateCompanyException.class, () -> uniqueCompanyList.setCompany(ALPHA, BOEING));
    }

    @Test
    public void remove_nullCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.remove(null));
    }

    @Test
    public void remove_companyDoesNotExist_throwsCompanyNotFoundException() {
        assertThrows(CompanyNotFoundException.class, () -> uniqueCompanyList.remove(ALPHA));
    }

    @Test
    public void remove_existingCompany_removesCompany() {
        uniqueCompanyList.add(ALPHA);
        uniqueCompanyList.remove(ALPHA);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setCompanies_nullUniqueCompanyList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.setCompanies((UniqueCompanyList) null));
    }

    @Test
    public void setCompanies_uniqueCompanyList_replacesOwnListWithProvidedUniqueCompanyList() {
        uniqueCompanyList.add(ALPHA);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        expectedUniqueCompanyList.add(BOEING);
        uniqueCompanyList.setCompanies(expectedUniqueCompanyList);
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setCompanies_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueCompanyList.setCompanies((List<Company>) null));
    }

    @Test
    public void setCompanies_list_replacesOwnListWithProvidedList() {
        uniqueCompanyList.add(ALPHA);
        List<Company> companyList = Collections.singletonList(BOEING);
        uniqueCompanyList.setCompanies(companyList);
        UniqueCompanyList expectedUniqueCompanyList = new UniqueCompanyList();
        expectedUniqueCompanyList.add(BOEING);
        assertEquals(expectedUniqueCompanyList, uniqueCompanyList);
    }

    @Test
    public void setcompanies_listWithDuplicateCompanies_throwsDuplicateCompanyException() {
        List<Company> listWithDuplicateCompanies = Arrays.asList(ALPHA, ALPHA);
        assertThrows(DuplicateCompanyException.class, () -> uniqueCompanyList.setCompanies(listWithDuplicateCompanies));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueCompanyList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueCompanyList.asUnmodifiableObservableList().toString(), uniqueCompanyList.toString());
    }
}
