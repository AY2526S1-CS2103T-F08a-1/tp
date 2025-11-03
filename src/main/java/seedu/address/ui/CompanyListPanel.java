package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.company.Company;

/**
 * Panel containing the list of companies.
 */
public class CompanyListPanel extends UiPart<Region> {
    private static final String FXML = "CompanyListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(CompanyListPanel.class);

    @FXML
    private ListView<Object> companyListView; // <— changed from <Company> to <Object>

    @FXML
    private VBox emptyPlaceholder;

    /**
     * Creates a {@code CompanyListPanel} with the given {@code ObservableList}.
     */
    public CompanyListPanel(ObservableList<Company> companyList) {
        super(FXML);

        // Create ResultDisplay component
        ResultDisplay resultDisplay = new ResultDisplay();

        // Combine it with your company list
        ObservableList<Object> mixedItems = FXCollections.observableArrayList();
        mixedItems.add(resultDisplay.getRoot()); // insert at top
        mixedItems.addAll(companyList);

        companyListView.setItems(mixedItems);

        // Custom rendering for both nodes and company cards
        companyListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else if (item instanceof Node node) {
                    // Render UI components directly (e.g., ResultDisplay)
                    setGraphic(node);
                } else if (item instanceof Company company) {
                    // Render company cards
                    setGraphic(new CompanyCard(company, getIndex()).getRoot());
                }
            }
        });

        // Show placeholder only when there are no companies (ignore ResultDisplay)
        emptyPlaceholder.visibleProperty().bind(Bindings.isEmpty(companyList));
        emptyPlaceholder.managedProperty().bind(emptyPlaceholder.visibleProperty());
    }
}
