import java.io.*;
import java.util.*;

public class ColorImage {
	private int width;
	private int height;
	private int depth = 8;
	ArrayList<int[]> pixels;
	int[][][] image;

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

	public int[] getPixel(int i, int j) {

		return image[i][j];
	}

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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

}
