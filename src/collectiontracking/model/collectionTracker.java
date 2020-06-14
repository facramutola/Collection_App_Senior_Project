package collectiontracking.model;

import collectiontracking.model.collectionList.ItemState;
import javafx.collections.ObservableList;

public interface collectionTracker {

	//public ObservableList<String> getCollectionName(String collectionName);
	public ObservableList<String> getItemNames(String itemName);
	public ObservableList<String> getCollectionName(String collectionName);
	public ObservableCollectionItem getItem(String item);
	public ObservableCollectionItem createItemFor(String collectionName);
	public void deleteCollectionItem(String collectionName);
	public void saveCollection(String collectionName, ItemState state, String itemDescription);
}
