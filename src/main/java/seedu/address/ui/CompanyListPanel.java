package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.company.Company;

public class CompanyListPanel extends UiPart<Region> {

    private static final String FXML = "CompanyListPanel.fxml";

    private final ResultDisplay resultDisplay;

    @FXML
    private ListView<Object> companyListView;

    @FXML
    private VBox emptyPlaceholder;

    public CompanyListPanel(ObservableList<Company> companyList) {
        super(FXML);

        // Create ResultDisplay component
        resultDisplay = new ResultDisplay();

        // Create combined list with header
        ObservableList<Object> mixedItems = FXCollections.observableArrayList();
        mixedItems.add(resultDisplay.getRoot());
        mixedItems.addAll(companyList);

        // Keep mixedItems in sync with companyList
        companyList.addListener((ListChangeListener<Company>) change -> {
            // Preserve header at index 0
            mixedItems.setAll(resultDisplay.getRoot());
            mixedItems.addAll(companyList);
        });

        companyListView.setItems(mixedItems);

        // Custom rendering for both nodes and company cards
        companyListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (item instanceof Node node) {
                    setGraphic(node);
                } else if (item instanceof Company company) {
                    setGraphic(new CompanyCard(company, getIndex()).getRoot());
                }
            }
        });

        // Show placeholder only when there are no companies
        emptyPlaceholder.visibleProperty().bind(Bindings.isEmpty(companyList));
        emptyPlaceholder.managedProperty().bind(emptyPlaceholder.visibleProperty());
    }

    public void setFeedbackToUser(String feedbackToUser) {
        resultDisplay.setFeedbackToUser(feedbackToUser);
    }
}
