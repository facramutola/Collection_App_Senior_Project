package collectiontracking;

import collectiontracking.model.CollectionList;
import collectiontracking.model.CollectionList.ItemState;
import collectiontracking.model.ObservableCollectionItem;
import collectiontracking.model.collectionTracker;
import collectiontracking.model.collectionTrackerEntry;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class CollectionTrackingController {
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle collectionResources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML //  fx:id="colItemName"
    private TableColumn<ObservableCollectionItem, String> colItemName; 

    @FXML //  fx:id="colItemState"
    private TableColumn<ObservableCollectionItem, ItemState> colItemState; 

    @FXML //  fx:id="colGameOfOrigin"
    private TableColumn<ObservableCollectionItem, String> colGameOfOrigin; 

    @FXML //  fx:id="deleteCollectionItem"
    private Button deleteCollectionItem; 

    @FXML //  fx:id="itemDescTextArea"
    private TextArea itemDescTextArea; 

    @FXML //  fx:id="descriptionArea"
    private AnchorPane descriptionArea; 

    @FXML //  fx:id="displayedItemLabel"
    private Label displayedItemLabel; 

    @FXML //  fx:id="collectionList"
    private ListView<String> collectionList; 

    @FXML //  fx:id="newCollectionItem"
    private Button newCollectionItem; 

    @FXML //  fx:id="saveCollectionItem"
    private Button saveCollectionItem; 

    @FXML //  fx:id="gameOfOrigin"
    private TextField gameOfOrigin; 

    @FXML //  fx:id="itemTable"
    private TableView<ObservableCollectionItem> itemTable; 

    private String displayedItemName; 
    private String displayedCollectionName;
    ObservableList<String> collectionsView = FXCollections.observableArrayList();
    collectionTracker model = null;
    private TextField itemStateValue = new TextField();
    final ObservableList<ObservableCollectionItem> itemTableContent = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert colItemName != null : "fx:id=\"colItemName\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert colItemState != null : "fx:id=\"colItemState\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert colGameOfOrigin != null : "fx:id=\"colGameOfOrigin\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert deleteCollectionItem != null : "fx:id=\"deleteCollectionItem\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert itemDescTextArea != null : "fx:id=\"itemDescTextArea\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert descriptionArea != null : "fx:id=\"descriptionArea\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert displayedItemLabel != null : "fx:id=\"displayedItemLabel\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert newCollectionItem != null : "fx:id=\"newCollectionItem\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert saveCollectionItem != null : "fx:id=\"saveCollectionItem\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert gameOfOrigin != null : "fx:id=\"gameOfOrigin\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert itemTable != null : "fx:id=\"itemTable\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        assert collectionList != null : "fx:id=\"collectionList\" was not injected: check your FXML file 'collectiontracking.fxml'.";
        
        System.out.println(this.getClass().getSimpleName() + ".initialize");
        toggleButtons();
        configureDetails();
        configureTable();
        connectToService();
        if (collectionList != null) {
        	collectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        	collectionList.getSelectionModel().selectedItemProperty().addListener(collectionItemSelected);
        	displayedCollectionNames.addListener(collectionNamesListener);
        }
    }

    /**
     * Called when the newItem button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void newItemFired(ActionEvent event) {
        final String selectedCollection = getSelectedCollection();
        if (model != null && selectedCollection != null) {
            ObservableCollectionItem item = model.createItemFor(selectedCollection);
            if (itemTable != null) {
                itemTable.getSelectionModel().clearSelection();
                itemTable.getSelectionModel().select(item);
            }
        }
    }

    /**
     * Called when the deleteCollectionItem button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void deleteItemFired(ActionEvent event) {
        final String selectedCollection = getSelectedCollection();
        if (model != null && selectedCollection != null && itemTable != null) {
            final List<?> selectedItem = new ArrayList<>(itemTable.getSelectionModel().getSelectedItems());
            for (Object o : selectedItem) {
                if (o instanceof ObservableCollectionItem) {
                    model.deleteCollectionItem(((ObservableCollectionItem) o).getItemName());
                }
            }
            itemTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Called when the saveCollectionItem button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void saveItemFired(ActionEvent event) {
        final ObservableCollectionItem ref = getSelectedItem();
        final CollectionList edited = new DetailsData();
        ItemSaveState ItemSaveState = computeItemSaveState(edited, ref);
        if (ItemSaveState == ItemSaveState.UNSAVED) {
            model.saveCollectionItem(ref.getItemName(), edited.getItemState(),
                    edited.getGameOfOrigin(), edited.getItemDescription());
        }
        int selectedRowIndex = itemTable.getSelectionModel().getSelectedIndex();
        itemTable.getItems().clear();
        displayedItems = model.getItemNames(getSelectedCollection());
        for (String id : displayedItems) {
            final ObservableCollectionItem item = model.getItem(id);
            itemTable.getItems().add(item);
        }
        itemTable.getSelectionModel().select(selectedRowIndex);

        updateSaveItemButtonState();
    }
    
    private void toggleButtons() {
        if (newCollectionItem != null) {
            newCollectionItem.setDisable(true);
        }
        if (saveCollectionItem != null) {
            saveCollectionItem.setDisable(true);
        }
        if (deleteCollectionItem != null) {
            deleteCollectionItem.setDisable(true);
        }
    }
    
    private ObservableList<String> displayedCollectionNames;
   
    private ObservableList<String> displayedItems;
    
    private final ListChangeListener<String> collectionNamesListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (collectionsView == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        collectionsView.add(p);
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        collectionsView.remove(p);
                    }
                }
            }
            FXCollections.sort(collectionsView);
        }
    };
    
    private final ListChangeListener<String> collectionItemListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (itemTable == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        itemTable.getItems().add(model.getItem(p));
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        ObservableCollectionItem removed = null;
                        for (ObservableCollectionItem t : itemTable.getItems()) {
                            if (t.getItemName().equals(p)) {
                                removed = t;
                                break;
                            }
                        }
                        if (removed != null) {
                            itemTable.getItems().remove(removed);
                        }
                    }
                }
            }
        }
    };

    
    private void connectToService() {
        if (model == null) {
            model = new collectionTrackerEntry();
            displayedCollectionNames = model.getCollectionNames();
        }
        collectionsView.clear();
        List<String> sortedCollections = new ArrayList<>(displayedCollectionNames);
        Collections.sort(sortedCollections);
        collectionsView.addAll(sortedCollections);
        collectionList.setItems(collectionsView);
    }
    
    private final ListChangeListener<ObservableCollectionItem> tableSelectionChanged =
            new ListChangeListener<ObservableCollectionItem>() {

                @Override
                public void onChanged(Change<? extends ObservableCollectionItem> c) {
                    updateDeleteItemButtonState();
                    updateItemDetails();
                    updateSaveItemButtonState();
                }
            };

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private void updateItemDetails() {
        final ObservableCollectionItem selectedItem = getSelectedItem();
        if (descriptionArea != null && selectedItem != null) {
            if (displayedItemLabel != null) {
                displayedItemName = selectedItem.getItemName();
                displayedCollectionName = selectedItem.getCollectionName();
                displayedItemLabel.setText( displayedItemName + " / " + displayedCollectionName );
            }
            if (gameOfOrigin != null) {
                gameOfOrigin.setText(nonNull(selectedItem.getGameOfOrigin()));
            }
            if (itemStateValue != null) {
                itemStateValue.setText(selectedItem.getItemState().toString());
            }
            if (itemDescTextArea != null) {
                itemDescTextArea.selectAll();
                itemDescTextArea.cut();
                itemDescTextArea.setText(selectedItem.getItemDescription());
            }
        } else {
            displayedItemLabel.setText("");
            displayedItemName = null;
            displayedCollectionName = null;
        }
        if (descriptionArea != null) {
            descriptionArea.setVisible(selectedItem != null);
        }
    }

    private boolean isVoid(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else {
            return o == null;
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equal(Object o1, Object o2) {
        if (isVoid(o1)) {
            return isVoid(o2);
        }
        return o1.equals(o2);
    }

    private static enum ItemSaveState {

        INVALID, UNSAVED, UNCHANGED
    }

    private final class DetailsData implements CollectionList {

        @Override
        public String getItemName() {
            if (displayedItemName == null || isEmpty(displayedItemLabel.getText())) {
                return null;
            }
            return displayedItemName;
        }

        @Override
        public ItemState getItemState() {
            if (itemStateValue == null || isEmpty(itemStateValue.getText())) {
                return null;
            }
            return ItemState.valueOf(itemStateValue.getText().trim());
        }
        
        @Override
        public String getCollectionName() {
            if (displayedCollectionName == null || isEmpty(displayedItemLabel.getText())) {
                return null;
            }
            return displayedCollectionName;
        }

        @Override
        public String getGameOfOrigin() {
            if (gameOfOrigin == null || isEmpty(gameOfOrigin.getText())) {
                return "";
            }
            return gameOfOrigin.getText();
        }

        @Override
        public String getItemDescription() {
            if (itemDescTextArea == null || isEmpty(itemDescTextArea.getText())) {
                return "";
            }
            return itemDescTextArea.getText();
        }
    }

    private ItemSaveState computeItemSaveState(CollectionList edited, CollectionList item) {
        try {
            if (!equal(edited.getItemName(), item.getItemName())) {
                return ItemSaveState.INVALID;
            }
            if (!equal(edited.getCollectionName(), item.getCollectionName())) {
                return ItemSaveState.INVALID;
            }
            if (!equal(edited.getItemState(), item.getItemState())) {
                return ItemSaveState.UNSAVED;
            }
            if (!equal(edited.getGameOfOrigin(), item.getGameOfOrigin())) {
                return ItemSaveState.UNSAVED;
            }
            if (!equal(edited.getItemDescription(), item.getItemDescription())) {
                return ItemSaveState.UNSAVED;
            }
        } catch (Exception x) {
            return ItemSaveState.INVALID;
        }
        return ItemSaveState.UNCHANGED;
    }

    private void updateDeleteItemButtonState() {
        boolean disable = true;
        if (deleteCollectionItem != null && itemTable != null) {
            final boolean nothingSelected = itemTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (deleteCollectionItem != null) {
            deleteCollectionItem.setDisable(disable);
        }
    }

    private void updateSaveItemButtonState() {
        boolean disable = true;
        if (saveCollectionItem != null && itemTable != null) {
            final boolean nothingSelected = itemTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (disable == false) {
            disable = computeItemSaveState(new DetailsData(), getSelectedItem()) != ItemSaveState.UNSAVED;
        }
        if (saveCollectionItem != null) {
            saveCollectionItem.setDisable(disable);
        }
    }

    private void configureTable() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGameOfOrigin.setCellValueFactory(new PropertyValueFactory<>("gameOfOrigin"));
        colItemState.setCellValueFactory(new PropertyValueFactory<>("status"));

        colItemName.setPrefWidth(75);
        colItemState.setPrefWidth(75);
        colGameOfOrigin.setPrefWidth(443);

        colItemName.setMinWidth(75);
        colItemState.setMinWidth(75);
        colGameOfOrigin.setMinWidth(443);

        colItemName.setMaxWidth(750);
        colItemState.setMaxWidth(750);
        colGameOfOrigin.setMaxWidth(4430);

        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        itemTable.setItems(itemTableContent);
        assert itemTable.getItems() == itemTableContent;

        final ObservableList<ObservableCollectionItem> tableSelection = itemTable.getSelectionModel().getSelectedItems();

        tableSelection.addListener(tableSelectionChanged);
    }

    /**
     * Return the name of the project currently selected, or null if no project
     * is currently selected.
     *
     */
    public String getSelectedCollection() {
        if (model != null && collectionList != null) {
            final ObservableList<String> selectedProjectItem = collectionList.getSelectionModel().getSelectedItems();
            final String selectedCollection = selectedProjectItem.get(0);
            return selectedCollection;
        }
        return null;
    }

    public ObservableCollectionItem getSelectedItem() {
        if (model != null && itemTable != null) {
            List<ObservableCollectionItem> selectedItems = itemTable.getSelectionModel().getSelectedItems();
            if (selectedItems.size() == 1) {
                final ObservableCollectionItem selectedItem = selectedItems.get(0);
                return selectedItem;
            }
        }
        return null;
    }
    
    /**
     * Listen to changes in the collectionList selection, and updates the itemTable widget and
     * deleteCollectionItem and newItem buttons accordingly.
     */
    private final ChangeListener<String> collectionItemSelected = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            collectionUnselected(oldValue);
            collectionSelected(newValue);
        }
    };

    // Called when a collection is unselected.
    private void collectionUnselected(String oldCollectionName) {
        if (oldCollectionName != null) {
            displayedItems.removeListener(collectionItemListener);
            displayedItems = null;
            itemTable.getSelectionModel().clearSelection();
            itemTable.getItems().clear();
            if (newCollectionItem != null) {
                newCollectionItem.setDisable(true);
            }
            if (deleteCollectionItem != null) {
                deleteCollectionItem.setDisable(true);
            }
        }
    }

    // Called when a collection is selected.
    private void collectionSelected(String newCollectionName) {
        if (newCollectionName != null) {
            itemTable.getItems().clear();
            displayedItems = model.getItemNames(newCollectionName);
            for (String id : displayedItems) {
                final ObservableCollectionItem item = model.getItem(id);
                itemTable.getItems().add(item);
            }
            displayedItems.addListener(collectionItemListener);
            if (newCollectionItem != null) {
                newCollectionItem.setDisable(false);
            }
            updateDeleteItemButtonState();
            updateSaveItemButtonState();
        }
    }

    private void configureDetails() {
        if (descriptionArea != null) {
            descriptionArea.setVisible(false);
        }

        if (descriptionArea != null) {
            descriptionArea.addEventFilter(EventType.ROOT, new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    if (event.getEventType() == MouseEvent.MOUSE_RELEASED
                            || event.getEventType() == KeyEvent.KEY_RELEASED) {
                        updateSaveItemButtonState();
                    }
                }
            });
        }
    }
}