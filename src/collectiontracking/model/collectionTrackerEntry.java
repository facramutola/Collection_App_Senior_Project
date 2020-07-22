package collectiontracking.model;

import collectiontracking.model.CollectionList.ItemState;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
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
		private final SimpleStringProperty itemName;
		private final SimpleStringProperty collectionName;
		private final SimpleStringProperty origin;
		private final SimpleStringProperty itemDescription;
		private final SimpleObjectProperty<ItemState> itemState = new SimpleObjectProperty<>(ItemState.HAVE);

		CollectionEntry(String collectionName, String entryNum) {
			this(collectionName, entryNum, null);
		}

		CollectionEntry(String collectionName, String itemName, String origin) {
			assert collectionNames.contains(collectionName);
			assert !collectionsMap.get(collectionName).contains(itemName);
			assert !itemsMap.containsKey(itemName);
			this.collectionName = new SimpleStringProperty(collectionName);
			this.itemName = new SimpleStringProperty(itemName);
			this.origin = new SimpleStringProperty(origin);
			this.itemDescription = new SimpleStringProperty("");
		}

		@Override
		public String getItemName() {
			return itemName.get();
		}

		private void setItemName(String itemName) {
			this.itemName.set(itemName);
		}

		@Override
		public String getCollectionName() {
			return collectionName.get();
		}

		@Override
		public String getGameOfOrigin() {
			return origin.get();
		}

		private void setGameOfOrigin(String origin) {
			this.origin.set(origin);
		}

		@Override
		public ItemState getItemState() {
			return itemState.get();
		}

		private void setItemState(ItemState itemState) {
			this.itemState.set(itemState);
		}

		@Override
		public String getItemDescription() {
			return itemDescription.get();
		}

		private void setItemDescription(String itemDescription) {
			this.itemDescription.set(itemDescription);
		}

		@Override
		public ObservableValue<String> itemNameProperty() {
			return itemName;
		}

		@Override
		public ObservableValue<String> collectionNameProperty() {
			return collectionName;
		}

		@Override
		public ObservableValue<String> gameOfOriginProperty() {
			return origin;
		}

		@Override
		public ObservableValue<ItemState> itemStateProperty() {
			return itemState;
		}

		@Override
		public ObservableValue<String> itemDescriptionProperty() {
			return itemDescription;
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


	final AtomicInteger itemCounter = new AtomicInteger(0);
	final ObservableMap<String, CollectionEntry> itemsMap;
	{
		final Map<String, CollectionEntry> testMap = new TreeMap<>();
		itemsMap = FXCollections.observableMap(testMap);
		itemsMap.addListener(collectionMapChangeListener);
		CollectionEntry persona;
		persona = createItemFor("Jack Frost Personas");
		persona.setGameOfOrigin("Megami Tensei II");
		persona.setItemName("Jack Frost");
		persona.setItemDescription(
				"A winter fairy of European descent. He leaves ice patterns on windows and nips people's noses. Though normally an innocent creature, he will freeze his victims to death if provoked.");
		// persona.setItemState(ItemState.HAVE);

		persona = createItemFor("Cryptid and Mythology Personas");
		persona.setItemName("Mothman");
		persona.setItemDescription(
				"A cryptid sighted during the 60s-80s in West Virginia. It has shining red eyes and is named for the fin-like appendages on its sides. It uses its keen sense for blood to track down the source and feed on it.");
		persona.setGameOfOrigin("Shin Megami Tensei III: Nocturne");
		// persona.setItemState(ItemState.HAVE);
	}
	
	private static <T> List<T> newList(T... items) {
		return Arrays.asList(items);
	}

	@Override
	public void deleteCollectionItem(String collectionItem) {
		assert itemsMap.containsKey(collectionItem);
		itemsMap.remove(collectionItem);
	}

	@Override
	public CollectionEntry getItem(String itemName) {
		return itemsMap.get(itemName);
	}

	@Override
	public ObservableList<String> getItemNames(String itemNames) {
		return collectionsMap.get(itemNames);
	}

	@Override
	public CollectionEntry createItemFor(String collectionName) {
		assert collectionNames.contains(collectionName);
		final CollectionEntry entry = new CollectionEntry(collectionName, "#" + itemCounter.incrementAndGet());
		assert itemsMap.containsKey(entry.getItemName()) == false;
		assert collectionsMap.get(collectionName).contains(entry.getItemName()) == false;
		itemsMap.put(entry.getItemName(), entry);
		return entry;
	}

	@Override
	public ObservableList<String> getCollectionNames() {
		return collectionNames;
	}

	@Override
	public void saveCollectionItem(String itemName, ItemState state, String gameOfOrigin, String itemDescription) {
		CollectionEntry entry = getItem(itemName);
		entry.setItemDescription(itemDescription);
		entry.setGameOfOrigin(gameOfOrigin);
		entry.setItemState(state);
	}

}
