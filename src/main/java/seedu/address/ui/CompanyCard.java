package seedu.address.ui;

import java.util.Comparator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

    private boolean isRemarkExpanded = false;
    private String fullRemarkText;

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
    private FlowPane tags;
    @FXML
    private Label remark;
    @FXML
    private Label status;
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
        address.setText(company.getAddress().value);
        email.setText(DisplayUtil.displayEmail(company.getEmail().value));
        status.setText("Status: " + company.getStatus().toUserInputString());

        company.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        fullRemarkText = company.getRemark().value;
        remark.setText(fullRemarkText);

        setupExpandableRemark();
    }

    private void setupExpandableRemark() {
        // Initialize in collapsed state
        isRemarkExpanded = false;
        remark.setWrapText(false);
        remark.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        // Set up button click handler
        seeMoreButton.setOnAction(event -> toggleRemarkExpansion());

        // Check overflow after UI fully rendered
        Platform.runLater(this::checkRemarkOverflow);

        // Add listener for width changes (window resizing) - for both expanded and collapsed modes
        cardPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::checkRemarkOverflow);
        });
    }

    private void toggleRemarkExpansion() {
        isRemarkExpanded = !isRemarkExpanded;

        if (isRemarkExpanded) {
            // Expanded state: show full text with wrapping
            remark.setWrapText(true);
            remark.setText(fullRemarkText);
            seeMoreButton.setText("see less");
        } else {
            // Collapsed state: show single line with ellipsis
            remark.setWrapText(false);
            remark.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
            remark.setText(fullRemarkText);
            seeMoreButton.setText("see more");
        }
    }

    // Checks remarks if overflow pass 1 line
    // And set up See More button if needed
    private void checkRemarkOverflow() {
        if (fullRemarkText == null || fullRemarkText.trim().isEmpty()) {
            seeMoreButton.setVisible(false);
            seeMoreButton.setManaged(false);
            return;
        }

        // Get available width, fallback to card width if maxWidth not set yet
        double availableWidth = cardPane.widthProperty().get();

        // Don't proceed if width is still not available
        if (availableWidth <= 0) {
            return;
        }

        // Get text width
        // Create a Text node to measure actual text width
        Text textNode = new Text(fullRemarkText);
        textNode.setFont(remark.getFont());

        double textWidth = textNode.getBoundsInLocal().getWidth();

        if (textWidth > availableWidth) {
            // Only show button if text exceeds single line width
            seeMoreButton.setVisible(true);
            seeMoreButton.setManaged(true);
        } else if (isRemarkExpanded) {
            // Expanded but text fits — hide button
            seeMoreButton.setVisible(false);
            seeMoreButton.setManaged(false);
        } else {
            // Collapsed and text fits — hide button
            seeMoreButton.setVisible(false);
            seeMoreButton.setManaged(false);
        }
    }
}
