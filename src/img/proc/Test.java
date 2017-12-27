package img.proc;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Test {

	
	
	public static void findCircleInImage(){
		
	
		
		Mat src, dst;
		src = new Mat();
		dst = new Mat();
		// Đọc file ảnh từ bộ nhớ
		src = Imgcodecs.imread("D:\\b.jpg", 1);
		
		if(src.empty()){
			System.out.println("Ảnh không tồn tại");
		}
		else{
			// Chuyển ảnh sang dạng gray
			Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
			
			
			// Giảm những chi tiết nhỏ tránh xác  định sai
			Imgproc.GaussianBlur(dst, dst, new Size(5, 5), 2, 2);
			
			Mat circles = new Mat();
			int minRadius = 1; 
		    int maxRadius = 15; 			
			Imgproc.HoughCircles(dst, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, 120, 10, minRadius, maxRadius);
			//Imgproc.HoughCircles(dst, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 5);
			
			System.out.println(circles.total());			
			// Vẽ vào các hình tròn đã xác định được
			for(int i = 0; i < circles.total(); i++){
				
				double[] a = circles.get(0, i);
				double x = Math.round(a[0]);
				double y = Math.round(a[1]);
				int radius = (int)Math.round(a[2]);
				Point center = new Point(x, y);
												
				
				// Vòng tròn giữa
				//Imgproc.circle(src, center, 3, new Scalar(0, 0, 255), -1, 8, 0);
				
				// Viền
				Imgproc.circle(src, center, radius, new Scalar(0, 0, 255), 3, 8, 0);
			}	
		
			Imgcodecs.imwrite("D:\\c.jpg", src);
			
			
		}		
	}
	
	
	
	
	public static void main(String[] args){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		findCircleInImage();
	}
}
