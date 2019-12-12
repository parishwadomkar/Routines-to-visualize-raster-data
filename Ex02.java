package kth.ag2411.mapalgebra;

import java.awt.Frame;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Ex02 {
	public static void main(String[] args) {
		if(args.length == 4){
			//Instantiate a layer
			Layer layer = new Layer(args[0], args[1]);
			
			//convert the layer to gray image
			BufferedImage image1 = layer.toImage();
			
			// Create a JFrame, which will be the main window of this demo application
			JFrame appFrame1 = new JFrame();
			int scale=Integer.parseInt(args[2]);
			MapPanel panel1= new MapPanel(image1,scale);
			appFrame1.getContentPane().add(panel1);
			appFrame1.pack();
			appFrame1.setExtendedState(Frame.MAXIMIZED_BOTH);
			appFrame1.setVisible(true);
			
			//show the values of interest in (random) color
			String str=args[3];
			String[] array = str.split(",");
			double[] l=new double[array.length];
			for (int k=0;k<array.length;k++) {
				l[k]=Double.parseDouble(array[k]);
			}
			BufferedImage image2 = layer.toImage(l);
			
			// Create a JFrame, which will be the main window of this demo application
			JFrame appFrame2 = new JFrame();
			MapPanel panel2= new MapPanel(image2,scale);
			appFrame2.getContentPane().add(panel2);
			appFrame2.pack();
			appFrame2.setExtendedState(Frame.MAXIMIZED_BOTH);
			appFrame2.setVisible(true);


			}
		else {
			System.out.println("Too many or few arguments......");
			}
		}
}