
/**
 * This is the comparator of <code>ComparatorMatOfPoint</code>
 * { @link img.proc.ComparatorMatOfPoint }
 * @author Administrator
 * @deprecated use <code>Comparator</code>
 */


package img.proc;

import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public class ComparatorMatOfPoint implements Comparator<Object>{

	@Override
	public int compare(Object arg0, Object arg1) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		MatOfPoint mapOfPoint1 = (MatOfPoint)arg0;
		MatOfPoint mapOfPoint2 = (MatOfPoint)arg1;
		
		long s1 = 0;
		long s2 = 0;
		
		int cnt1 = 0, cnt2 = 0;
		
		for(Point p: mapOfPoint1.toList()){
			s1 += p.x + p.y;
			cnt1++;
		}
		
		for(Point p: mapOfPoint2.toList()){
			s2 += p.x + p.y;
			cnt2++;
		}
		
		
		s1 /= cnt1;
		s2 /= cnt2;
		
		if(s1 > s2) return 1;
		else if(s1 < s2) return -1;
		
		return 0;
	}
}
