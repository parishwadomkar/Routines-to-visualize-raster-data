package kth.ag2411.mapalgebra;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Layer {
	// Attributes
	public String name;						// name of layer
	public int nRows;						// number of rows
	public int nCols;						// number of columns
	public double[] origin = new double[2];	// x,y-coordinates of lower-left corner
	public double resolution;				// cell size
	public double[][] values;				// raster data
	public double nullValue;				// value representing "No data"
	
	
	//Method
	public Layer(String layerName, String fileName) {
		
		// Exception may be thrown while reading (and writing) a file.
		name = layerName;
		try {
			File file = new File(fileName);
			if (file.isFile() && file.exists()) {
				FileReader fReader = new FileReader(file);
				BufferedReader bReader = new BufferedReader(fReader);
				
				String text;
				String number;
				// Read first line, which starts with "ncols"
				text = bReader.readLine();
				number = text.substring(5).trim();
				nCols=Integer.parseInt(number);
				
				// Read second line, which starts with "nrows"
				text = bReader.readLine();
				number = text.substring(5).trim();
				nRows=Integer.parseInt(number);
				
				values=new double [nRows][nCols];
				
				// Read third line, which starts with "xllcorner"
				text = bReader.readLine();
				number = text.substring(9).trim();
				origin[0]=Double.parseDouble(number);
				
				// Read forth line, which starts with "yllcorner"
				text = bReader.readLine();
				number = text.substring(9).trim();
				origin[1]=Double.parseDouble(number);
				
				// Read fifth line, which starts with "cellsize"
				text = bReader.readLine();
				number = text.substring(8).trim();
				resolution=Double.parseDouble(number);

				// Read sixth line, which starts with "NODATA_value"
				text = bReader.readLine();
				number = text.substring(12).trim();
				nullValue=Double.parseDouble(number);

				// Read each of the remaining lines, which represents a row of raster value	
				// values
				text = bReader.readLine();
				String[] value;
				int count=0;
				while(text != null) {
					value=text.split(" ");		
					for (int i=0;i<nCols;i++) {
						values[count][i]=Double.parseDouble(value[i]);
						}
					count++;
					text = bReader.readLine();
					}
				bReader.close();
				System.out.println("Loading file Finish!");
				}
			else {
				System.out.println("Can not find the file!");
				}
			}
		catch (Exception e) {
			System.out.println("Error occurs when loading file!");
			e.printStackTrace();
		}
	}
	
	
	// Print (This is complete)
	public void print(){
		//Print this layer to console
		System.out.println("layerName: "+name);
		System.out.println("ncols: "+nCols);
		System.out.println("nrows: "+nRows);
		System.out.println("xllcorner: "+origin[0]);
		System.out.println("yllcorner: "+origin[1]);
		System.out.println("cellsize: "+resolution);
		System.out.println("NODATA_value: " + nullValue);
		System.out.println("Raster data: ");
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				System.out.print(values[i][j]+" ");
				}
			System.out.println();
		}
	}
	
	// Save (This is not complete)
	public void save(String outputFileName) {
		// save this layer as an ASCII file that can be imported to ArcGIS
		File file = new File(outputFileName);
		
		// This object represents ASCII data (to be) stored in the file
		try {
			FileWriter fWriter = new FileWriter(file);
			
			fWriter.write("ncols "+nCols+"\n");
			fWriter.write("nrows "+nRows+"\n");
			fWriter.write("xllcorner "+origin[0]+"\n");
			fWriter.write("yllcorner "+origin[1]+"\n");
			fWriter.write("cellsize "+resolution+"\n");
			fWriter.write("NODATA_value " + nullValue+"\n");
			for (int i = 0; i < nRows; i++) {
				for (int j = 0; j < nCols; j++) {
					fWriter.write(values[i][j]+" ");
					}
				fWriter.write("\n");
				}
			fWriter.close();
			System.out.println("Saving file Finish!");
			} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
	}
	
	public BufferedImage toImage() {
		// create a BufferedImage of the layer in grayscale
		BufferedImage image = new BufferedImage(nCols, nRows, BufferedImage.TYPE_INT_RGB);
		// The above image is empty. To color the image, you first need to get access to
		// its raster, which is represented by the following object.
		WritableRaster raster = image.getRaster();
		// These statements make a grayscale value and assign it to the pixel at the
		// top-left corner of the raster.
		// get the max and min of the layer value
		double max=this.getMax();
		double min=this.getMin();
		// get the corresponding gray value of the cell value
		int[] color = new int[3];
		for (int i=0;i<nRows;i++) {
			for (int j=0;j<nCols;j++) {
				if (values[i][j] == nullValue) {
					continue;
				}
				//convert the double to long and convert the long to int
				int y=(int)(Math.round(255-255*(values[i][j]-min)/(max-min)));
				color[0] = y; // Red
				color[1] = y; // Green
				color[2] = y; // Blue
				raster.setPixel(j, i, color);
			}
		}
		System.out.println("Convert layer to image Finish!");
		return image;
	}
	
	public BufferedImage toImage(double[] l) {
		// visualize a BufferedImage of the layer in color
		BufferedImage image = new BufferedImage(nCols, nRows, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		
		int[][] p = new int [l.length][3];//save the color for each interested value
		int min=0;
		int max=225;
		int[] color = new int[3];
		for (int k=0;k<l.length;k++) {
			p[k][0]=min+(int)(Math.random() * (max-min+1));
			p[k][1]=min+(int)(Math.random() * (max-min+1));
			p[k][2]=min+(int)(Math.random() * (max-min+1));
		}
		for (int i=0;i<nRows;i++) {
			for (int j=0;j<nCols;j++) {
				if (values[i][j] == nullValue) {
					continue;
				}
				for (int k=0;k<l.length;k++) {
					if (values[i][j]==l[k]) {
						color[0] = p[k][0]; // Red
						color[1] = p[k][1]; // Green
						color[2] = p[k][2]; // Blue
						raster.setPixel(j, i, color);
					}
				}
			}
		}
		return image;
	}
	
	private double getMax() {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
					if (values[i][j] > max) {
						max = values[i][j];
				}
			}
		}
		return max;
	}

	private double getMin() {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				if (values[i][j] == nullValue) {
					continue;
				}
				if (values[i][j] < min) {
					min = values[i][j];
				}
			}
		}
		return min;
	}
}