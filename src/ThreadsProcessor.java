import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ThreadsProcessor {

	public static final int DOTS = 200;
	public static final int DIM = DOTS*5 + 1;
	public static final int PIXELS = DIM * DIM;
	
	public static final int LINE_LIMIT = 2000;
	
	private Mat original;
	private Mat result;
	
	private String resultFilename;
	
	private ArrayList<Point> points;
	private ArrayList<Line> lines;

	ThreadsProcessor(File original, String result) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
			this.original = normalizeOriginal(original);
		} catch (IOException e) {e.printStackTrace();}
		
		this.result = new Mat(DIM, DIM, CvType.CV_16UC1);
		this.resultFilename = result;
		
		
		byte[] temp = new byte[PIXELS];
		this.original.get(0, 0, temp);
		
		this.points = new ArrayList<Point>();
		for(int i=0; i<PIXELS; i++){
			this.points.add(new Point(i, (short) (temp[i] + 128) ));
		}
		
		this.lines = new ArrayList<Line>();
		
		process();
	}

	public BufferedImage originalToPanel() {
		BufferedImage originalPanel = new BufferedImage(400, 400, BufferedImage.TYPE_BYTE_GRAY);
		
		Mat originalCopy = new Mat(400, 400, CvType.CV_8UC1);
		Imgproc.resize(original, originalCopy, new Size(400, 400));
		
		byte[] data = ((DataBufferByte) originalPanel.getRaster().getDataBuffer()).getData();
		originalCopy.get(0, 0, data);
		
		return originalPanel;
	}

	public BufferedImage resultToPanel() throws IOException {
		Mat resultCopy = new Mat(400, 400, CvType.CV_16UC1);
		Imgproc.resize(result, resultCopy, new Size(400, 400));
		Imgcodecs.imwrite("resCopy.jpg", resultCopy);
		
		return ImageIO.read(new File("resCopy.jpg"));
	}

	private static Mat normalizeOriginal(File originalFile) throws IOException{
		BufferedImage originalImage = ImageIO.read(originalFile);
		
		byte[] pixels = ((DataBufferByte) originalImage.getRaster().getDataBuffer()).getData();
		
		Mat originalMat = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8UC3);		
		originalMat.put(0, 0, pixels);
		
		Mat resizedMat = new Mat(DIM, DIM, CvType.CV_8UC3);
		Imgproc.resize(originalMat, resizedMat, new Size(DIM, DIM));
		
		Mat finalMat = new Mat(DIM, DIM, CvType.CV_8UC1);
		Imgproc.cvtColor(resizedMat, finalMat, Imgproc.COLOR_RGB2GRAY);
		
		return finalMat;
	}
	
	private void process(){		
		
		/*for (int j = 0; j < DOTS; j++) {
			double slope = 2 * Math.PI / DOTS * j;
			int x = (int) (DIM/2 + DIM/2 * Math.sin(slope));
			int y = (int) (DIM/2 + DIM/2 * Math.cos(slope));
		}*/
		
		for (int i = DOTS/8; i < DOTS/2; i++) {
			for (int k = 0; k < DOTS; k++) {
				int dotNumberSecond = (k < (DOTS - i) ? (k + i) : (k + i - DOTS));
				int startX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS * k));
				int startY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS * k));
				int endX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS
						* dotNumberSecond));
				int endY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS
						* dotNumberSecond));
				drawLine(lines, points, startX, startY, endX, endY);
			}
		}
		for (int l = 0; l < DOTS/2; l++) {
			int dotNumberSecond = (l < (DOTS/2) ? (l + DOTS/2) : (l - DOTS/2));
			int startX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS * l));
			int startY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS * l));
			int endX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS
					* dotNumberSecond));
			int endY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS
					* dotNumberSecond));
			drawLine(lines, points, startX, startY, endX, endY);
		}
		
		for(int o=0; o<LINE_LIMIT; o++){
			for(Line l : lines)
				l.getWeight();
				
			Collections.sort(lines);
			Line line = lines.remove(0);
			
			line.setExists();
		}
		
		short[] putPixels = new short[PIXELS];
		for(int u=0; u<PIXELS; u++)
			putPixels[u] = (short) (points.get(u).isExists() ? 0 : 255);
		result.put(0, 0, putPixels);

		Imgcodecs.imwrite(resultFilename, result); 

	}

	private static void drawLine(ArrayList<Line> lines, ArrayList<Point> points, int x, int y, int x2, int y2) {
		ArrayList<Point> linePoints = new ArrayList<Point>();
		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			points.get(DIM * y + x).incrementDensity();
			linePoints.add(points.get(DIM * y + x));
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
		lines.add(new Line(linePoints));
	}

}
