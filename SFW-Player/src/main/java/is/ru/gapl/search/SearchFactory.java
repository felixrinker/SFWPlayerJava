package is.ru.gapl.search;

import is.ru.gapl.exception.SearchMethodException;

import java.util.HashMap;

public class SearchFactory {
	
	private HashMap< String, String > searchMethods;
	
	/**
	 * 
	 * @return an instance of the search factory
	 */
	public static SearchFactory getInstance() {
	     return SEARCHFACTROYINSTANCE;
	  }

	/**
	 * Add a new search method to the search factory
	 * 
	 * @param searchName the name of the search method
	 * @param searchMethodClass the search method class
	 */
	public void addSearchMethod(String searchName, String searchMethodClass) {
		
		this.searchMethods.put(searchName, searchMethodClass);
	}
	
	/**
	 * 
	 * @param searchName
	 * @return
	 * @throws SearchMethodException
	 */
	public ISearch createSearchMethod(String searchName) throws SearchMethodException {
		
		if(!this.searchMethods.containsKey(searchName)) {
			throw new SearchMethodException("Search Method "+searchName+" not found");
		}
		
		ISearch searchMethod = null;
		
		try {
			Class<?> searchClass = Class.forName(this.searchMethods.get(searchName));
			Object searchObject	 = searchClass.newInstance();
			
			if(searchObject instanceof ISearch ) {
				searchMethod = (ISearch) searchObject;	
			}else {
				throw new SearchMethodException("Error instanciate search method ");
			}
		} catch (ClassNotFoundException e) {
			throw new SearchMethodException("Error instanciate search method "+e.getMessage());
		} catch (InstantiationException e) {
			throw new SearchMethodException("Error instanciate search method "+e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SearchMethodException("Error instanciate search method "+e.getMessage());
		}
		
		return searchMethod;
	}
	
/*********************** PRIVATE SECTION *****************************/

	  private static final SearchFactory SEARCHFACTROYINSTANCE = new SearchFactory();

	  /**
	   * Constructor of the search factory
	   */
	  private SearchFactory() {
	   this.searchMethods = new HashMap<String, String >();
	  }
}
