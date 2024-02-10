import java.io.*;
import java.util.*;
import java.lang.Math;

public class ColorHistogram {
  /**
  * @brief Parameterized Constructor to make a histogram for a d-bit image
  * @param d color depth of image in bits; 3 bits = 512 colors
  */
  public ColorHistogram (int d) {
    this.depth = d;
    // this.histogram = new int[(int)Math.pow(BASE,(this.depth*COLOR_CHANNELS))];

    initializeHistogram();
  }

  /**
  * @brief Parameterized Constructor to make a histogram from a text file
  * @param filename name of the text file where histogram data is stored
  */
  public ColorHistogram (String filename) {
    // this.image = new ColorImage(filename);
    // this.depth = this.image.getDepth();
    // // this.histogram = new int[(int)Math.pow(BASE,(this.depth*COLOR_CHANNELS))];
    //
    // initializeHistogram();

    // Scanner sc;
    try {
      Scanner sc = new Scanner(new File(filename));
      // sc.useDelimiter(" |\\n");

      if (sc.hasNextInt()) {
        int numberOfColors = sc.nextInt();
        System.out.println("ColorHistogram :: first number read from the file is: " + numberOfColors);

        if (numberOfColors <= 0) {
          System.out.println("ColorHistogram :: numberOfColors is WRONG!");
          return;
        }

        double d = Math.log(numberOfColors)/NATURAL_LOG_2;
        System.out.println("ColorHistogram :: Value of d: " + d);

        this.depth = (int)d;
      }

      if (this.depth <= 0) {
        System.out.println("ColorHistogram :: Depth is WRONG! Value is: " + this.depth);
        return;
      }

      initializeHistogram();

      int index = 0;
      int numberOfPixels = 0;
      while (sc.hasNextInt()) {
        this.histogram[index] = sc.nextInt();
        numberOfPixels+= this.histogram[index];
        System.out.print(Integer.toString(this.histogram[index]) + " ");
        index++;
      }
      System.out.println("\nColorHistogram :: Number of Pixels: " + numberOfPixels);

      normalizeHistogram(numberOfPixels);
    }
    catch(Exception e) {
      System.out.println("ColorHistogram :: Scanner threw an exception!!");
      System.out.println(e.getMessage());
    }
    // Scanner sc = new Scanner(new File(filename));
    // sc.useDelimiter(" |\\n");
    //
    // if (sc.hasNextInt()) {
    //   int numberOfColors = sc.nextInt();
    //   System.out.println("ColorHistogram :: first number read from the file is: " + numberOfColors);
    //
    //   if (numberOfColors <= 0) {
    //     System.out.println("ColorHistogram :: numberOfColors is WRONG!");
    //     return;
    //   }
    //
    //   this.depth = (int)(Math.log(numberOfColors)/NATURAL_LOG_2);
    // }
    //
    // if (this.depth <= 0) {
    //   System.out.println("ColorHistogram :: Depth is WRONG! Value is: " + this.depth);
    //   return;
    // }
    //
    // initializeHistogram();
    //
    // int index = 0;
    // int numberOfPixels = 0;
    // while (sc.hasNextInt()) {
    //   this.histogram[index] = sc.nextInt();
    //   numberOfPixels+= this.histogram[index];
    //   index++;
    // }
    //
    // normalizeHistogram(numberOfPixels);
  }

  /**
  * @brief Method to associate image with histogram instance
  * @param image ColorImage instance to be liked to a histogram object
  */
  public void setImage (ColorImage image) {
    if (image != null) {
      this.image = image;
    }
    else {
      System.out.println("setImage :: ERROR! the ColorImage argument is NULL.");
    }
  }

  /**
  * @brief Returns normalized histogram of the image
  */
  public double[] getHistogram () {
    return this.normalizedHistogram;
  }

  /**
  * @brief Returns intersection between two histograms
  * @param hist the histogram to compare the present histogram instance with
  */
  public double compare (ColorHistogram hist) {
    return 0.0;
  }

  /**
  * @brief Saves the current histogram information in a text file
  * @param filename name of the file to store histogram data
  */
  public void save (String filename) {

  }

  private void normalizeHistogramFromImage() {
    normalizeHistogram(this.image.getWidth() * this.image.getHeight());
  }

  private void normalizeHistogram(int numberOfPixels) {
    // int numberOfPixels = this.image.getHeight() * this.image.getWidth();
    // int numberOfPixels = height * width;

    if (numberOfPixels <= 0) {
      System.out.println("normalizeHistogram :: Number of pixels is " + numberOfPixels
                        + ". INVALID VALUE!");
      return;
    }

    for (int index = 0; index < this.normalizedHistogram.length; index++) {
      this.normalizedHistogram[index] = this.histogram[index]/numberOfPixels;
    }
  }

  private void createHistogramFromImage() {
    // int width = this.image.getWidth();
    // int height = this.image.getHeight();
    createHistogram(this.image.getWidth(), this.image.getHeight());
  }

  private void createHistogram(int width, int height) {
    // int width = this.image.getWidth();
    // int height = this.image.getHeight();
    int pixelColor[] = new int[3];
    for (int row = 0; row < width; row++) {
      for (int column = 0; column < height; column++) {
        pixelColor[0] = 0;
        pixelColor[1] = 0;
        pixelColor[2] = 0;
        pixelColor = this.image.getPixel(row, column);
        this.histogram[calculateIndex(pixelColor)]++;
      }
    }
  }

  private int calculateIndex(int[] pixelColor) {
    int index = 0;
    index = ((pixelColor[0] << (BASE*this.depth)) + (pixelColor[1] << this.depth) + pixelColor[2]);
    return index;
  }

  private void initializeHistogram() {

    if (this.depth <= 0) {
      System.out.println("initializeHistogram :: ERROR bit depth is set to zero!");
      return;
    }

    this.histogram = new int[(int)Math.pow(BASE,(this.depth*COLOR_CHANNELS))];
    this.normalizedHistogram = new double[(int)Math.pow(BASE,(this.depth*COLOR_CHANNELS))];

    for (int i = 0; i < this.histogram.length; i++) {
      this.histogram[i] = 0;
      this.normalizedHistogram[i] = 0.0;
    }
  }

  private ColorImage image;
  private int depth = 0;
  private int histogram[];
  private double normalizedHistogram[];

  private final int COLOR_CHANNELS = 3;
  private final int BASE = 2;
  private final double NATURAL_LOG_2 = Math.log(2);

  // Test method
	public static void main(String args[]){
		// ColorImage colorImage = new ColorImage("queryImages\\q00.ppm");
		// System.out.println(Arrays.toString(colorImage.getPixel(0,1)));
    //
		// colorImage.reduceColor(3);

    ColorHistogram hist = new ColorHistogram("25.jpg.txt");

	}

}
