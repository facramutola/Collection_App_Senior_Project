package collectiontracking.model;

import javafx.beans.value.ObservableValue;

public interface ObservableCollectionItem extends collectionList {

	public ObservableValue<String> collectionNameProperty();
	public ObservableValue<String> itemNameProperty();
	public ObservableValue<collectionList> itemStateProperty();
	public ObservableValue<String> itemDescriptionProperty();
}
