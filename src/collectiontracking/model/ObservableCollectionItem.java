package collectiontracking.model;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface ObservableCollectionItem extends collectionList {

	public ObservableValue<String> collectionNameProperty();
	public ObservableValue<String> itemNameProperty();
	public ObservableValue<ItemState> itemStateProperty();
	public ObservableValue<String> itemDescriptionProperty();
	public void setItemName(String string);
	public void setItemDescription(String string);
	public void setItemState(ItemState HAVE);
}
