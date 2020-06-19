package collectiontracking.model;

import collectiontracking.model.collectionList.ItemState;
import collectiontracking.model.ObservableCollectionItem;
//import collectiontracking.model.Issue.IssueStatus;
//import collectiontracking.model.collectionTrackerEntry.CollectionEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
//import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class collectionTrackerEntry implements collectionTracker {

	final ObservableMap<String, ObservableList<String>> collectionsMap;
	{
		final Map<String, ObservableList<String>> map = new TreeMap<>();
		collectionsMap = FXCollections.observableMap(map);
		for (String s : newList("Jack Frost Personas", "Cryptid and Mythology Personas")) {
			collectionsMap.put(s, FXCollections.<String>observableArrayList());
		}
	}

	final MapChangeListener<String, ObservableList<String>> collectionsMapChangeListener = new MapChangeListener<String, ObservableList<String>>() {
		@Override
		public void onChanged(Change<? extends String, ? extends ObservableList<String>> edit) {
			if (edit.wasAdded())
				collectionNames.add(edit.getKey());
			if (edit.wasRemoved())
				collectionNames.remove(edit.getKey());
		}
	};

	final ObservableList<String> collectionNames;
	{
		collectionNames = FXCollections.<String>observableArrayList();
		collectionNames.addAll(collectionsMap.keySet());
		collectionsMap.addListener(collectionsMapChangeListener);
	}

	public final class CollectionEntry implements ObservableCollectionItem {
		private final SimpleStringProperty collectionName;
		private final SimpleStringProperty itemName;
		private final SimpleStringProperty itemDescTextField;
		private final SimpleObjectProperty<ItemState> itemState = new SimpleObjectProperty<>(ItemState.DO_NOT_HAVE);
		private final SimpleStringProperty itemDescription;

		CollectionEntry(String collectionName, String itemName){
			this(collectionName, itemName, null);
		}
		
		CollectionEntry(String itemName, String collectionName, String itemDescTextField) {
			assert !collectionNames.contains(itemName);
			assert !collectionsMap.get(itemName).contains(collectionName);
			assert !itemsMap.containsKey(itemName);
			this.collectionName = new SimpleStringProperty(collectionName);
			this.itemName = new SimpleStringProperty(itemName);
			this.itemDescTextField = new SimpleStringProperty(itemDescTextField);
			this.itemDescription = new SimpleStringProperty("");
		}

		@Override
		public ItemState getItemState() {
			return itemState.get();
		}
		
		public String getCollectionName() {
			return collectionName.get();
		}

		private void setCollectionName(String collectionName) {
			this.collectionName.set(collectionName);
		}

		@Override
		public String getItemName() {
			return itemName.get();
		}

		@Override
		public String getItemDescription() {
			return itemDescription.get();
		}

		@Override
		public ObservableValue<String> collectionNameProperty() {
			return collectionName;
		}

		@Override
		public ObservableValue<String> itemNameProperty() {
			return itemName;
		}

		@Override
		public ObservableValue<String> itemDescriptionProperty() {
			return itemDescription;
		}

		@Override
		public ObservableValue<ItemState> itemStateProperty() {
			return itemState;
		}


		@Override
		public void setItemName(String string) {
			this.itemName.set(string);
		}

		@Override
		public void setItemDescription(String string) {
			this.itemDescription.set(string);
		}

		@Override
		public void setItemState(ItemState HAVE) {
			this.itemState.set(HAVE);
		}

		private void setItemDescTextField(String itemDescTextField) {
			this.itemDescTextField.set(itemDescTextField);
		}
		
		@Override
		public String getItemDescTextField() {
			// TODO Auto-generated method stub
			return itemDescTextField.get();
		}

	}

		final MapChangeListener<String, CollectionEntry> collectionMapChangeListener = new MapChangeListener<String, CollectionEntry>() {
			@Override
			public void onChanged(Change<? extends String, ? extends CollectionEntry> change) {
				if (change.wasAdded()) {
					final CollectionEntry val = change.getValueAdded();
					collectionsMap.get(val.getCollectionName()).add(val.getItemName());
				}
				if (change.wasRemoved()) {
					final CollectionEntry val = change.getValueRemoved();
					collectionsMap.get(val.getCollectionName()).remove(val.getItemName());
				}
			}
		};

	private static <T> List<T> newList(T... items) {
		return Arrays.asList(items);
	}

	final ObservableMap<String, CollectionEntry> itemsMap;{
		final Map<String, CollectionEntry> testMap = new TreeMap<>();
		itemsMap = FXCollections.observableMap(testMap);
		itemsMap.addListener(collectionMapChangeListener);
		ObservableCollectionItem persona;
		persona = createItemFor("Jack Frost Personas");
		persona.setItemName("Jack Frost");
		persona.setItemDescription("A winter fairy of European descent. He leaves ice patterns on windows and nips people's noses. Though normally an innocent creature, he will freeze his victims to death if provoked.");
		persona.setItemState(ItemState.HAVE);
	}
	
	public void deleteCollectionItem(String collectionItem) {
		assert itemsMap.containsKey(collectionItem);
		itemsMap.remove(collectionItem);
	}

	public ObservableList<String> getCollectionName() {
		return collectionNames;
	}

	public CollectionEntry getItem(String itemName) {
		return itemsMap.get(itemName);
	}

	public ObservableList<String> getItemNames(String itemNames) {
		return collectionsMap.get(itemNames);
	}
	
	@Override
	public void saveCollection(String itemName, ItemState state, String itemDescription) {
		CollectionEntry entry = getItem(itemName);
		entry.setItemDescription(itemDescription);
		entry.setItemState(state);
	}

	@Override
	public ObservableCollectionItem createItemFor(String collectionName) {
		assert collectionNames.contains(collectionName);
		final CollectionEntry entry = new CollectionEntry(collectionName, "");
		assert itemsMap.containsKey(entry.getCollectionName()) == false;
		itemsMap.put(entry.getCollectionName(), entry);
		return entry;
	}

	public ObservableList<String> getCollectionName(String collectionName) {
		return collectionsMap.get(collectionName);
	}



}
	
