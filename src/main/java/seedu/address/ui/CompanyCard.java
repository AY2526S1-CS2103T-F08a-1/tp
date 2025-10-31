package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.company.Company;

/**
 * An UI component that displays information of a {@code Company}.
 */
public class CompanyCard extends UiPart<Region> {

    private static final String FXML = "CompanyListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Company company;


    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane status;
    @FXML
    private FlowPane tags;
    @FXML
    private Label remark;
    @FXML
    private VBox remarkContainer;
    @FXML
    private Button seeMoreButton;

    /**
     * Creates a {@code CompanyCard} with the given {@code Company} and index to display.
     */
    public CompanyCard(Company company, int displayedIndex) {
        super(FXML);
        this.company = company;
        id.setText(displayedIndex + ". ");
        name.setText(company.getName().fullName);
        phone.setText(DisplayUtil.displayPhone(company.getPhone().value));
        address.setText(DisplayUtil.displayAddress(company.getAddress().value));
        email.setText(DisplayUtil.displayEmail(company.getEmail().value));

        // Create status on its own line
        String statusValue = company.getStatus().toUserInputString().toUpperCase();
        Label statusLabel = new Label(statusValue);
        statusLabel.getStyleClass().addAll("status-" + statusValue, "status-color-" + statusValue);
        status.getChildren().add(statusLabel);

        // Add regular tags on separate line
        company.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        String remarkText = company.getRemark().value;
        if (remarkText == null) {
            remark.setVisible(false); // Hides remark if empty
            remark.setManaged(false); // Truncates company card to not show a blank line
        } else {
            remark.setText("Remark: " + remarkText);
            // Use RemarkExpansionManager to handle remark expansion logic
            new RemarkExpansionManager(remark, seeMoreButton, cardPane, remarkText);
        }
    }

}
