package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Company[] getSampleCompanies() {
        return new Company[] {
            new Company(new Name("Acme Corporation"), new Phone("62345678"), new Email("contact@acme.com"),
                new Address("1 Raffles Place, #12-01"),
                getTagSet("supplier"), new Remark("Reliable supplier for electronics."),
                new Status(Status.Stage.TO_APPLY)),
            new Company(new Name("TechVision Solutions"), new Phone("65551234"), new Email("info@techvision.com"),
                new Address("50 Collyer Quay, #05-02"),
                getTagSet("client", "technology"), new Remark("Key client in the tech industry."),
                new Status(Status.Stage.APPLIED)),
            new Company(new Name("Global Logistics Pte Ltd"), new Phone("67778888"),
                new Email("enquiry@globallogistics.com"), new Address("10 Anson Road, #23-05"),
                getTagSet("partner"), new Remark("Partner for international shipping."),
                new Status(Status.Stage.OA)),
            new Company(new Name("Sunrise Manufacturing"), new Phone("63334567"), new Email("sales@sunrise.com"),
                new Address("18 Cross Street, #08-15"),
                getTagSet("supplier"), new Remark("Specializes in custom manufacturing."),
                new Status(Status.Stage.TECH_INTERVIEW)),
            new Company(new Name("Digital Innovations Hub"), new Phone("69876543"),
                new Email("hello@digitalinnovations.com"), new Address("3 Temasek Boulevard, #15-20"),
                getTagSet("client", "technology"), new Remark("Focuses on digital transformation."),
                new Status(Status.Stage.HR_INTERVIEW)),
            new Company(new Name("Pacific Trading Co"), new Phone("64445566"), new Email("info@pacifictrading.com"),
                new Address("9 Battery Road, #11-10"),
                getTagSet("partner", "trading"), new Remark("Handles bulk trading operations."),
                new Status(Status.Stage.IN_PROCESS)),
            new Company(new Name("Nexus Robotics"), new Phone("60112233"), new Email("careers@nexusrobotics.com"),
                new Address("21 Science Park Road, #02-18"),
                getTagSet("client", "robotics"), new Remark("Exploring automation collaborations."),
                new Status(Status.Stage.OFFERED)),
            new Company(new Name("Orion Analytics"), new Phone("60998877"),
                new Email("talent@orionanalytics.com"), new Address("75 Pasir Panjang Road, #07-09"),
                getTagSet("client", "data"), new Remark("Negotiations completed, offer signed."),
                new Status(Status.Stage.ACCEPTED))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Company sampleCompany : getSampleCompanies()) {
            sampleAb.addCompany(sampleCompany);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }
}
