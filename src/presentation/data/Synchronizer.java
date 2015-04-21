package presentation.data;

import business.exceptions.BackendException;
import javafx.collections.ObservableList;

public interface Synchronizer {
	@SuppressWarnings("rawtypes")
	public void refresh(ObservableList list) throws BackendException;
}
