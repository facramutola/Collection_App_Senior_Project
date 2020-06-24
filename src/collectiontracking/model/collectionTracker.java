package collectiontracking.model;

import collectiontracking.model.collectionList.ItemState;
import javafx.collections.ObservableList;

public interface collectionTracker {
	public ObservableList<String> getItemNames(String collectionName);
	public ObservableList<String> getCollectionNames();
	public ObservableCollectionItem getItem(String item);
	public ObservableCollectionItem createItemFor(String collectionName);
	public void deleteCollectionItem(String itemName);
	public void saveCollection(String itemName, ItemState state, String gameOfOrigin, String description);
}
