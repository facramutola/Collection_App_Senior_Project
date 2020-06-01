package collectiontracking.model;

import collectiontracking.model.collectionList.ItemState;
import javafx.collections.ObservableList;

public interface collectionTracker {

	public ObservableList<String> getCollectionNames(String collectionName);
	public ObservableList<String> getItemNames();
	public ObservableCollectionItem getCollection(String collectionId);
	public ObservableCollectionItem createItemFor(String collectionName);
	public void deleteCollection(String collectionId);
	public void saveCollection(String collectionId, ItemState state, String itemDescription);
}
