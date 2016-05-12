import java.util.ArrayList;
import java.util.List;


/**
 * Class Utils.
 * Utility methods for the application.
 * @author Raghav Babu
 * Date : 04/14/2016
 */

public class Utils {

	//return the list of higher Ids.
	public static List<Integer> getHigherIdentifiers(int processId){
		
		List<Integer> highIds = new ArrayList<Integer>();
		
		for(int i = processId + 1; i <= Process.totalProcess; i++){
			highIds.add(i);
		}
		return highIds;
	}
	
	//return the list of lower Ids.
	public static List<Integer> getLowerIdentifiers(int processId){
		
		List<Integer> lowIds = new ArrayList<Integer>();
		
		for(int i = processId - 1; i > 0; i--){
			lowIds.add(i);
		}
		
		return lowIds;
	}
	
}
