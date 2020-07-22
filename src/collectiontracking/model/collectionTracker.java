package collectiontracking.model;

import collectiontracking.model.CollectionList.ItemState;
import javafx.collections.ObservableList;

public interface collectionTracker {
	public ObservableList<String> getItemNames(String collectionName);
	public ObservableList<String> getCollectionNames();
	public ObservableCollectionItem getItem(String item);
	public ObservableCollectionItem createItemFor(String collectionName);
	public void deleteCollectionItem(String itemName);
	public void saveCollectionItem(String itemName, ItemState state, String gameOfOrigin, String description);
}
