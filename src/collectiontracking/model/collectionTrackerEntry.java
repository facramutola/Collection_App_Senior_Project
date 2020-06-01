package collectiontracking.model;

import collectiontracking.model.collectionList.ItemState;
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
		for (String s : newList("Collection1", "Collection2")) {
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

	private static <T> List<T> newList(T... items) {
		return Arrays.asList(items);
	}
}
