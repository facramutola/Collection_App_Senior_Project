package collectiontracking.model;

public interface collectionList {
	
	public static enum ItemState{
		HAVE, DO_NOT_HAVE
	}

	public String getCollectionName();
	public String getItemName();
	public ItemState getItemState();
	public String getItemDescription();
}
