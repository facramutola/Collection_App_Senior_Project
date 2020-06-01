package collectiontracking.model;

public interface collectionItem {
	
	public static enum itemState{
		HAVE, DO_NOT_HAVE
	}

	public String getName();
	
}
