//
// CSI 2120 Project Deliverable #1 - Java
// Student Name:     Adwitheya Benbi              Calvin Pan
// Student ID:       300165778                    300184557
// Class: ColorImage
//

import java.io.*;
import java.util.*;

public class ColorImage {
	private int width;
	private int height;
	private int depth = 8;
	private ArrayList<int[]> pixels;
	private int[][][] image;

	/**
  * @brief Parameterized Constructor to make a ColorImage object from a filename
  * @param filename file name string for the image
  */
	public ColorImage(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			String p3 = br.readLine();

			if(!(p3.equals("P3"))) {
				throw new IllegalArgumentException("Invalid PPM file format");
			}

			br.readLine();

			String[] dimensions = (br.readLine()).split("\\s+");
			width = Integer.parseInt(dimensions[0]);
			height = Integer.parseInt(dimensions[1]);

			int maxValue = Integer.parseInt(br.readLine());

			pixels  = new ArrayList<int[]>();

			String line;

			while((line = br.readLine()) != null) {

				String[] parts = line.split("\\s+");

				for(int i = 0; i < parts.length; i += 3) {

					int[] toAdd = new int[3];

					toAdd[0] = (Integer.parseInt(parts[i]));
					toAdd[1] = (Integer.parseInt(parts[i + 1]));
					toAdd[2] = (Integer.parseInt(parts[i + 2]));

					pixels.add(toAdd);

				}

			}

			image = new int[width][height][1];

			int index = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < 1; z++) {
						image[x][y] = pixels.get(index++);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
  * @brief Returns the color values of a pixel in am image
  * @param i row number of pixel
	* @param j column number of pixel
	* @return int[] array of RBG values for pixel color
  */
	public int[] getPixel(int i, int j) {

		return image[i][j];
	}

	/**
  * @brief Reduce image color space by d bits
  * @param d bits to reduce the color space
  */
	public void reduceColor(int d){
		for(int i = 0; i < image.length; i++) {
			for(int j = 0; j < image[i].length; j++) {
				for(int k = 0; k < image[i][j].length; k++) {
					image[i][j][k] = image[i][j][k] >> (8 - d);
				}
			}

			depth = d;
		}

	}

  // Getters

	/**
	* @brief Return the width of image
	* @return width of the image in pixels
	*/
	public int getWidth() {
		return width;
	}

	/**
	* @brief Return the height of image
	* @return height of the image in pixels
	*/
	public int getHeight() {
		return height;
	}

	/**
	* @brief Return the depth of image
	* @return depth of the image in bits
	*/
	public int getDepth() {
		return depth;
	}

}
