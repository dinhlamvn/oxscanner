package img.proc;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.Collections;

public class BubbleSheetMultipleScanner {
	
	
	
	
	static Mat warpMat(Mat originalImg, MatOfPoint2f approx){
		//calculate the center of mass of our contour image using moments
		Moments moment = Imgproc.moments(approx);
		int x = (int) (moment.get_m10() / moment.get_m00());
		int y = (int) (moment.get_m01() / moment.get_m00());

		//SORT POINTS RELATIVE TO CENTER OF MASS 
		Point[] sortedPoints = new Point[4];

		double[] data;
		for(int i=0; i<approx.rows(); i++){
		    data = approx.get(i, 0);
		    double datax = data[0];
		    double datay = data[1];
		    if(datax < x && datay < y){
		        sortedPoints[0]=new Point(datax,datay);
		    }else if(datax > x && datay < y){
		        sortedPoints[1]=new Point(datax,datay);
		    }else if (datax < x && datay > y){
		        sortedPoints[2]=new Point(datax,datay);
		    }else if (datax > x && datay > y){
		        sortedPoints[3]=new Point(datax,datay);
		    }
		}
		
		MatOfPoint2f src = new MatOfPoint2f(
                sortedPoints[0],
                sortedPoints[1],
                sortedPoints[2],
                sortedPoints[3]);

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(originalImg.cols(),0),                
                new Point(0,originalImg.rows()), 
                new Point(originalImg.cols(),originalImg.rows())
                );
        
        Mat warpMat = Imgproc.getPerspectiveTransform(src,dst);
        //This is you new image as Mat
        Mat destImage = new Mat();
        Imgproc.warpPerspective(originalImg, destImage, warpMat, originalImg.size());
        
        return destImage;
	}
	
	
	
	
	
	static String getAnswer(){
		
		String asw = "";
		String path = "D:\\TestImage\\";
		String name = "noel";
		String extend = ".jpg";
		
		
		// Định giá trị bán kính khung tròn cần tìm
		int radius = 22;
		
		// Tổng số câu hỏi trong đề
		int totalQuestion = 100;
		
		// Số dòng trong đề
		int numOfRow = 25;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		

		// Load ảnh từ ổ đĩa máy
		Mat src = new Mat();
		src = Imgcodecs.imread(path + name + extend, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		
		
		// Copy ảnh ra 1 ảnh mới
		Mat src2 = new Mat();
		src.copyTo(src2);
		
	
		Mat gray, blurred, edged;
		gray = blurred = edged = new Mat();		
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(gray, blurred, new Size(5,5), 0);
		Imgproc.Canny(blurred, edged, 75, 100);
		
		Imgcodecs.imwrite(path + name + "_edged" + extend, edged);
		
		
		
		// find contours in the edge map, then initialize
		List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
		Mat edgedTemp = new Mat();		
		edged.copyTo(edgedTemp);
		Imgproc.findContours(edgedTemp, cnts, new Mat(),Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);			
		Collections.sort(cnts, new ComparatorMatOfPoint());
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(cnts.get(0).toArray());
		double arc = Imgproc.arcLength(matOfPoint2f, true);
		MatOfPoint2f approx = new MatOfPoint2f();
		Imgproc.approxPolyDP(matOfPoint2f, approx, arc*0.02, true);
		
		
		Imgproc.drawContours(src, cnts, -1, new Scalar(0, 0, 255), 3);
		
		Imgcodecs.imwrite(path + name + "_contours" + extend, src);
		
		Mat warp = new Mat();
		
		warp = warpMat(src, approx);
		
		//src2.copyTo(warp);
		
		
		Imgcodecs.imwrite(path + name + "_warp" + extend, warp);
		
		
		/*
		Mat thresh = new Mat();
		
		Imgproc.cvtColor(warp, warp, Imgproc.COLOR_BGR2GRAY);
		
		//Imgproc.adaptiveThreshold(warp, thresh, 255,  Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 30);
		Imgproc.threshold(warp, thresh, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
		
		//Imgproc.threshold(warp, thresh, 0, 255, Imgproc.THRESH_BINARY_INV);
		
		Imgcodecs.imwrite(path + name + "_thresh" + extend, thresh);
		
		
		
		Mat threshTemp = new Mat();
		thresh.copyTo(threshTemp);
		Imgproc.findContours(threshTemp, cnts, new Mat(),Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		//System.out.println(cnts.size());
		
		
		List<MatOfPoint> questionCnts = new ArrayList<>();
		
		for(MatOfPoint c:cnts){
			Rect r = Imgproc.boundingRect(c);
			
			float ar = r.width / (float)r.height;
			
			if(r.width >= radius && r.height >= radius && ar >= 0.9 && ar <= 1.2){
				questionCnts.add(c);
			}
			
		}
		//System.out.println(questionCnts.size());
		
		Mat srcWarp = new Mat();
		//srcWarp = warpMat(src, approx);
		src2.copyTo(srcWarp);
		
		Imgproc.drawContours(srcWarp, questionCnts, -1, new Scalar(0, 0, 255), 3);
		
		Imgcodecs.imwrite(path + name + "_question" + extend, srcWarp);
		
		
		
		// sort the question
		Collections.sort(questionCnts, new TopToBottom());
		
		Mat quest = new Mat();
		//quest = warpMat(src, approx);
		src2.copyTo(quest);
		
		//List<String> listAnswer = new ArrayList<>();
		
		// Mảng lưu câu trả lời của tổng số câu hỏi
		String[] listAnswer = new String[totalQuestion];
		
		List<MatOfPoint> answerPoint = new ArrayList<>();
		MatOfPoint m = new MatOfPoint();
		
		Scalar blue  = new Scalar(255, 0, 0);
		Scalar red = new Scalar(0, 0, 255);
		
		boolean p = true;
		
		for(int i = 15, row = 0; i < questionCnts.size(); i += 16, row++){
			List<MatOfPoint> temp = new ArrayList<MatOfPoint>();
			for(int j = i - 15; j <= i; j++){
				temp.add(questionCnts.get(j));							
			}
			//Collections.sort(temp, new TopToBottom());
			
			if(p){
				Imgproc.drawContours(quest, temp, -1, blue, 4);
			}
			else{
				Imgproc.drawContours(quest, temp, -1, red, 4);
			}
			
			p = !p;
			
			Collections.sort(temp, new LeftToRight());		
			
			
			List<MatOfPoint> singleAnswer = new ArrayList<MatOfPoint>(); // mảng lưu 1 câu trả lời
			for(int k1 = 0, c = 0; k1 < temp.size(); k1 += 4, c++){
				singleAnswer.clear();
				for(int k2 = k1; k2 < k1 + 4; k2++){
					singleAnswer.add(temp.get(k2));
				}
				
			
				Rect rect = Imgproc.boundingRect(singleAnswer.get(0));
				Mat subMat = thresh.submat(rect);
				Core.bitwise_and(subMat, subMat, subMat);
				int countNonZeroMax = Core.countNonZero(subMat);
				m = singleAnswer.get(0);
				int index = 0;
				int[] b = new int[4];				
				for(int k3 = 0; k3 < singleAnswer.size(); k3++){
					rect = Imgproc.boundingRect(singleAnswer.get(k3));
					subMat = thresh.submat(rect);
					int x = Core.countNonZero(subMat);
					b[k3] = x;
					if(x > countNonZeroMax){
						countNonZeroMax = x;
						index = k3;
						m = singleAnswer.get(k3);
					}
				}
				
				int cntChose = 0;
				for(int k3 = 0; k3 < singleAnswer.size(); k3++){
					if(b[k3]/100 >= 2) 
						cntChose++;
				}
							
				if(cntChose > 1){
					listAnswer[numOfRow*c + row] = "X";
				}
				else{
					if(countNonZeroMax/100 >= 2){
						listAnswer[numOfRow*c + row] = String.valueOf(Character.toChars(index + 'A'));
						answerPoint.add(m);
					}
					else{
						listAnswer[numOfRow*c + row] = "-";
					}
				}
				
			}
		}
																							
		Imgcodecs.imwrite(path + name + "_question_pick" + extend, quest);
		
		
		for(int i = 0; i < listAnswer.length; i++){
			asw += listAnswer[i];
		}
		
		
		Mat answ = new Mat();
		//answ = warpMat(src, approx);
		src2.copyTo(answ);
		Imgproc.drawContours(answ, answerPoint, -1, new Scalar(0, 255, 0), 4);
		
		Imgcodecs.imwrite(path + name + "_answer" + extend, answ);
								
		*/
		return asw;
	}
	
	static void printAnswer(String answerString){
		int length = answerString.length();
		for(int i = 0; i < length; i++){
			System.out.println("Question " + (i + 1) + ": " + answerString.charAt(i));
		}
	}
		
		
	
	public static void main(String[] args){
		
		
		String x = getAnswer();
		
		printAnswer(x);
		
	}
	
	

}
