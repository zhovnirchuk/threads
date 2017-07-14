import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ThreadsProcessor {

	private Mat original;
	private Mat result;
	
	private String resultFilename;
	
	private static final int DOTS = 200;
	private static final int DIM = DOTS*5 + 1;
	private static final int PIXELS = DIM * DIM;

	ThreadsProcessor(File original, String result) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
			this.original = normalizeOriginal(original);
		} catch (IOException e) {e.printStackTrace();}
		
		this.result = new Mat(DIM, DIM, CvType.CV_16UC1, Scalar.all(255));
		this.resultFilename = result;
		
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
		short[] pixels = new short[PIXELS];
		short[] density = new short[PIXELS];
		
		result.get(0, 0, pixels);
		
		for (int i = 0; i < PIXELS; i++)
			density[i] = 0;
		
		
		for (int j = 0; j < DOTS; j++) {
			double slope = 2 * Math.PI / DOTS * j;
			int x = (int) (DIM/2 + DIM/2 * Math.sin(slope));
			int y = (int) (DIM/2 + DIM/2 * Math.cos(slope));
			pixels[DIM * y + x] = (short) 0;
		}
		for (int i = DOTS/8; i < DOTS/2; i++) {
			for (int k = 0; k < DOTS; k++) {
				int dotNumberSecond = (k < (DOTS - i) ? (k + i) : (k + i - DOTS));
				int startX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS * k));
				int startY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS * k));
				int endX = (int) (DIM/2 + DIM/2 * Math.sin(2 * Math.PI / DOTS
						* dotNumberSecond));
				int endY = (int) (DIM/2 + DIM/2 * Math.cos(2 * Math.PI / DOTS
						* dotNumberSecond));
				drawLine(density, pixels, startX, startY, endX, endY);
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
			drawLine(density, pixels, startX, startY, endX, endY);
		}
		result.put(0, 0, pixels);

		Imgcodecs.imwrite(resultFilename, result);
	}

	private static void drawLine(short[] density, short[] buff, int x, int y, int x2, int y2) {
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
			buff[DIM * y + x] = (short) 0;
			density[DIM * y + x]++;
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
	}

}
