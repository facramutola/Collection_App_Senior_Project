package collectiontracking.model;

public interface CollectionList {
	
	public static enum ItemState{
		HAVE, DO_NOT_HAVE
	}

	public String getItemName();
	public String getCollectionName();
	public String getGameOfOrigin();
	public ItemState getItemState();
	public String getItemDescription();
}
