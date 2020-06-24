package collectiontracking.model;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface ObservableCollectionItem extends collectionList {
	
	public ObservableValue<String> itemNameProperty();
	public ObservableValue<String> collectionNameProperty();
	public ObservableValue<String> gameOfOriginProperty();
	public ObservableValue<ItemState> itemStateProperty();
	public ObservableValue<String> itemDescriptionProperty();
}
