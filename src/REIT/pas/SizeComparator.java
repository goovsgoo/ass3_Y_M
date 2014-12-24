package REIT.pas;

import java.util.Comparator;

/**  @author Meni & Yoed
 * this class implements Comparator for Requests
 */
public class SizeComparator implements Comparator<RentalRequest>
{
	/** 
	 *  Compare size of asset
	 *  @param x, RentalRequest
	 *  @param y, RentalRequest
	 */
    @Override
    public int compare(RentalRequest x, RentalRequest y)
    {
        if (x.getSize() < y.getSize())
        {
            return -1;
        }
        if (x.getSize() > y.getSize())
        {
            return 1;
        }
        return 0;
    }
}