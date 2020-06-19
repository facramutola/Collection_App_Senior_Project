package collectiontracking;

import collectiontracking.model.collectionList;
import collectiontracking.model.collectionList.ItemState;
import collectiontracking.model.ObservableCollectionItem;
import collectiontracking.model.collectionTracker;
import collectiontracking.model.collectionTrackerEntry;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	@FXML //ResourceBundle for CollectionTracker given to the FXMLLoader
	private ResourceBundle resources;
	
	@FXML //URL Location of the FXML file that was given to the FXMLLoader
	private URL location;
	
	@FXML //fx:id="colItemName"
	private TableColumn<ObservableCollectionItem, String> colItemName;
	
	@FXML //fx:id="colItemState"
	private TableColumn<ObservableCollectionItem, ItemState> colItemState;
	
	@FXML //fx:id="colCollectionName"
	private TableColumn<ObservableCollectionItem, ItemState> colItemDescription;
	
	@FXML //fx:id="collectionName"
	private ListView<String> collectionName;
	
	@FXML //fx:id="newItem"
	private Button newItem;
	
	@FXML //fx:id="deleteItem"
	private Button deleteItem;
	
	@FXML //fx:id="saveItem"
	private Button saveItem;
	
	@FXML //fx:id="itemDescription"
	private TextArea itemDescription;
	
	@FXML //fx:id="descriptionArea"
	private AnchorPane descriptionArea;
	
	@FXML //fx:id="displayedItemLabel"
	private Label displayedItemLabel;
	
	@FXML //fx:id="itemDescTextField"
	private TextField itemDescTextField;
	
	@FXML //fx:id="itemTable"
	private TableView<ObservableCollectionItem> itemTable;
	
	private String displayedCollectionName;
	private String displayedItemName;
	ObservableList<String> collectionsView = FXCollections.observableArrayList();
	collectionTracker model = null;
	private TextField itemStateValue = new TextField();
	final ObservableList<ObservableCollectionItem> tableContent = FXCollections.observableArrayList();
	
	
	void initialize() {
		assert colItemName != null : "fx:id=\"colItemName\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert colItemState != null : "fx:id=\"colItemState\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert colItemDescription != null : "fx:id=\"colItemDescription\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert collectionName != null : "fx:id=\"collectionName\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert newItem != null : "fx:id=\"newItem\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert deleteItem != null : "fx:id=\"deleteItem\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert saveItem != null : "fx:id=\"saveItem\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert itemDescription != null : "fx:id=\"itemDescription\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert descriptionArea != null : "fx:id=\"descriptionArea\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert displayedItemLabel != null : "fx:id=\"displayedItemLabel\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert itemDescTextField != null : "fx:id=\"itemDescTextField\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		assert itemTable != null : "fx:id=\"itemTable\" was not injected: check your FXML file 'CollectionTracker.fxml'.";
		
		System.out.println(this.getClass().getSimpleName() + ".initialize");
		toggleButtons();
		configureDetails();
		configureTable();
		connectToService();
		if(collectionName != null) {
			collectionName.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			collectionName.getSelectionModel().selectedItemProperty().addListener(collectionItemSelected);
			displayedCollectionNames.addListener(collectionNamesListener);
		}
	}
	
	
	@FXML
    void newItemFired(ActionEvent event) {
        final String selectedCollection = getSelectedCollection();
        if (model != null && selectedCollection != null) {
            ObservableCollectionItem item = model.createItemFor(selectedCollection);
            if (itemTable != null) {
                // Select the newly created issue.
            	itemTable.getSelectionModel().clearSelection();
            	itemTable.getSelectionModel().select(item);
            }
        }
    }
	
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
	
	@FXML
	void saveItemFired(ActionEvent event) {
		final ObservableCollectionItem ref = getSelectedItem();
        final collectionList edited = new DetailsData();
        itemSaveState saveState = computeItemSaveState(edited, ref);
        if (saveState == itemSaveState.UNSAVED) {
            model.saveCollection(ref.getItemName(), edited.getItemState(),
                     edited.getItemDescription());
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
	
	private void connectToService() {
        if (model == null) {
            model = new collectionTrackerEntry();
            displayedCollectionNames = model.getCollectionName(displayedCollectionName);
        }
        collectionsView.clear();
        List<String> sortedCollections = new ArrayList<>(displayedCollectionNames);
        Collections.sort(sortedCollections);
        collectionsView.addAll(sortedCollections);
        collectionName.setItems(collectionsView);
    }
	private static enum itemSaveState{
		INVALID, UNSAVED, UNCHANGED
	}
	
	private itemSaveState computeItemSaveState(collectionList edited, collectionList item) {
        try {
            // These fields are not editable - so if they differ they are invalid
            // and we cannot save.
            if (!equal(edited.getItemName(), item.getItemName())) {
                return itemSaveState.INVALID;
            }
            if (!equal(edited.getCollectionName(), item.getCollectionName())) {
                return itemSaveState.INVALID;
            }

            // If these fields differ, the issue needs saving.
            if (!equal(edited.getItemState(), item.getItemState())) {
                return itemSaveState.UNSAVED;
            }
            if (!equal(edited.getItemDescription(), item.getItemDescription())) {
                return itemSaveState.UNSAVED;
            }
        } catch (Exception x) {
            // If there's an exception, some fields are invalid.
            return itemSaveState.INVALID;
        }
        // No field is invalid, no field needs saving.
        return itemSaveState.UNCHANGED;
    }
	
	private void toggleButtons() {
		if(newItem != null) {
			newItem.setDisable(true);
		}
		if(deleteItem != null) {
			deleteItem.setDisable(true);
		}
		if(saveItem != null) {
			saveItem.setDisable(true);
		}
	}
	
	private ObservableList<String> displayedItems;
	
	private final ChangeListener<String> collectionItemSelected = new ChangeListener<String>() {
		@Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            collectionUnselected(oldValue);
            collectionSelected(newValue);
        }
    };
    
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
    
    private void collectionUnselected(String oldCollectionName) {
        if (oldCollectionName != null) {
            displayedItems.removeListener(collectionItemListener);
            displayedItems = null;
            itemTable.getSelectionModel().clearSelection();
            itemTable.getItems().clear();
            if (newItem != null) {
                newItem.setDisable(true);
            }
            if (deleteItem != null) {
                deleteItem.setDisable(true);
            }
        }
    }
    
    private void updateItemDetails() {
        final ObservableCollectionItem selectedItem = getSelectedItem();
        if (descriptionArea != null && selectedItem != null) {
            if (displayedItemLabel != null) {
                displayedItemName = selectedItem.getItemName();
                displayedCollectionName = selectedItem.getCollectionName();
                displayedItemLabel.setText( displayedItemName + " / " + displayedCollectionName );
            }
            if (itemDescTextField != null) {
            	itemDescTextField.setText(nonNull(selectedItem.getItemDescTextField()));
            }
            if (itemStateValue != null) {
            	itemStateValue.setText(selectedItem.getItemState().toString());
            }
            if (itemDescription != null) {
            	itemDescription.selectAll();
            	itemDescription.cut();
            	itemDescription.setText(selectedItem.getItemDescription());
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
    
    private void collectionSelected(String newCollectionName) {
        if (newCollectionName != null) {
            itemTable.getItems().clear();
            displayedItems = model.getCollectionName(newCollectionName);
            for (String id : displayedItems) {
                final ObservableCollectionItem item = model.getItem(id);
                itemTable.getItems().add(item);
            }
            displayedItems.addListener(collectionItemListener);
            if (newItem != null) {
                newItem.setDisable(false);
            }
            updateDeleteItemButtonState();
            updateSaveItemButtonState();
        }
    }
	
	private ObservableList<String> displayedCollectionNames;
	
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
    
    private void updateDeleteItemButtonState() {
        boolean disable = true;
        if (deleteItem != null && itemTable != null) {
            final boolean nothingSelected = itemTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (deleteItem != null) {
            deleteItem.setDisable(disable);
        }
    }
    
    private void updateSaveItemButtonState() {
        boolean disable = true;
        if (saveItem != null && itemTable != null) {
            final boolean nothingSelected = itemTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (disable == false) {
            disable = computeItemSaveState(new DetailsData(), getSelectedItem()) != itemSaveState.UNSAVED;
        }
        if (saveItem != null) {
            saveItem.setDisable(disable);
        }
    }
    
    private void configureTable() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("item name"));
        colItemDescription.setCellValueFactory(new PropertyValueFactory<>("item description"));
        colItemState.setCellValueFactory(new PropertyValueFactory<>("item state"));

        // In order to limit the amount of setup in Getting Started we set the width
        // of the 3 columns programmatically but one can do it from SceneBuilder.
        colItemName.setPrefWidth(75);
        colItemState.setPrefWidth(75);
        colItemDescription.setPrefWidth(443);

        colItemName.setMinWidth(75);
        colItemState.setMinWidth(75);
        colItemDescription.setMinWidth(443);

        colItemName.setMaxWidth(750);
        colItemState.setMaxWidth(750);
        colItemDescription.setMaxWidth(4430);

        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        itemTable.setItems(tableContent);
        assert itemTable.getItems() == tableContent;

        final ObservableList<ObservableCollectionItem> tableSelection = itemTable.getSelectionModel().getSelectedItems();

        tableSelection.addListener(tableSelectionChanged);
    }
    
    private final class DetailsData implements collectionList {

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
        
        
        public String getItemDescTextField() {
        	if(itemDescTextField == null || isEmpty(itemDescTextField.getText())) {
        		return "";
        	}
        	return itemDescTextField.getText();
        }
        
        @Override
        public String getCollectionName() {
            if (displayedCollectionName == null || isEmpty(displayedItemLabel.getText())) {
                return null;
            }
            return displayedCollectionName;
        }

        @Override
        public String getItemDescription() {
            if (itemDescription == null || isEmpty(itemDescription.getText())) {
                return "";
            }
            return itemDescription.getText();
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
    
    public String getSelectedCollection() {
        if (model != null && collectionName != null) {
            final ObservableList<String> selectedCollectionItem = collectionName.getSelectionModel().getSelectedItems();
            final String selectedCollection = selectedCollectionItem.get(0);
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
	
    private final ListChangeListener<String> collectionItemListener = new ListChangeListener<String>() {
    	@Override
        public void onChanged(Change<? extends String> c) {
            if (itemTable == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        itemTable.getItems().add((ObservableCollectionItem) model.getCollectionName(p));
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
}
    
