//
// CSI 2120 Project Deliverable #1 - Java
// Student Name:     Adwitheya Benbi              Calvin Pan
// Student ID:       300165778                    300184557
// Class: ColorHistogram
//

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
    initializeHistogram();
  }

  /**
  * @brief Parameterized Constructor to make a histogram from a text file
  * @param filename name of the text file where histogram data is stored
  */
  public ColorHistogram (String filename) {
    try {
      Scanner sc = new Scanner(new File(filename));

      if (sc.hasNextInt()) {
        int numberOfColors = sc.nextInt();
        if (numberOfColors <= 0) {
          System.out.println("ColorHistogram :: numberOfColors is WRONG!");
          return;
        }

        double d = Math.log(numberOfColors)/NATURAL_LOG_2;
        this.depth = (int)(d/COLOR_CHANNELS);
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
        index++;
      }
      sc.close();

      normalizeHistogram(numberOfPixels);
    }
    catch(Exception e) {
      System.out.println("ColorHistogram :: Scanner threw an exception!!");
      System.out.println(e.getMessage());
    }
  }

  /**
  * @brief Method to associate image with histogram instance
  * @param image ColorImage instance to be liked to a histogram object
  */
  public void setImage (ColorImage image) {
    if (image != null) {
      this.image = image;

      if (this.sum != 0.0) {
        // matchesImage = false;
        System.out.println("WARNING! setImage() called on an object that already has a histogram calculated!");
        System.out.println("Please re-calculate image histogram if required!");
      }
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
    if (this.normalizedHistogram.length != hist.getHistogram().length) {
      System.out.println("compare :: Histogram lengths mismatch! ERROR!");
      return 0.0;
    }

    double intersection = 0.0;
    double otherHistogram[] = hist.getHistogram();
    for (int index = 0; index < this.normalizedHistogram.length; index++) {
      intersection += Math.min(this.normalizedHistogram[index], otherHistogram[index]);
    }
    // System.out.println("\nIntersection: " + intersection);
    return intersection;
  }

  /**
  * @brief Saves the current histogram information in a text file
  * @param filename name of the file to store histogram data
  */
  public void save (String filename) {
    if (filename.length() == 0) {
      System.out.println("save :: filename has ZERO length!");
      return;
    }

    try {
      PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

      writer.println(this.histogram.length);
      for (int index = 0; index < this.histogram.length; index++) {
        writer.print(this.histogram[index] + " ");
      }
      writer.flush();
      writer.close();
    }
    catch(Exception e) {
      System.out.println("save :: Could not create " + filename);
      System.out.println(e.getMessage());
    }
  }

  /**
  * @brief Computes histogram and normalized histogram for an image
  */
  public void computeHistogram() {
    createHistogramFromImage();
    normalizeHistogramFromImage();
  }

  /**
  * @brief Creates a normalized histogram from member 'image'
  */
  private void normalizeHistogramFromImage() {
    normalizeHistogram(this.image.getWidth() * this.image.getHeight());
  }

  /**
  * @brief Creates a normalized histogram from an ordinary histogram and number
  *        of pixels
  * @param numberOfPixels total number of pixels in the image
  */
  private void normalizeHistogram(int numberOfPixels) {
    if (numberOfPixels <= 0) {
      System.out.println("normalizeHistogram :: Number of pixels is " + numberOfPixels
                        + ". INVALID VALUE!");
      return;
    }

    for (int index = 0; index < this.normalizedHistogram.length; index++) {
      this.normalizedHistogram[index] = (double)this.histogram[index]/(double)numberOfPixels;
      this.sum += this.normalizedHistogram[index];
    }
  }

  /**
  * @brief Creates histogram for the 'image' member
  */
  private void createHistogramFromImage() {
    createHistogram(this.image.getWidth(), this.image.getHeight());
  }

  /**
  * @brief Creates histogram for an image using its height and width
  * @param width width of image in number of numberOfPixels
  * @param height height of image in number of pixels
  */
  private void createHistogram(int width, int height) {
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

  /**
  * @brief Calculates the index of a color in histogram
  * @param pixelColor int[3] with RGB pixel colors
  * @returns index the index of color in histogram
  */
  private int calculateIndex(int[] pixelColor) {
    int index = 0;
    index = ((pixelColor[0] << (BASE*this.depth)) + (pixelColor[1] << this.depth) + pixelColor[2]);
    return index;
  }

  /**
  * @brief Initializes 'histogram' and 'normalizeHistogram' members with zeroes
  */
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

  // Private class members
  private ColorImage image;
  private int depth = 0;
  private int histogram[];
  private double normalizedHistogram[];
  private float sum = 0;

  // Constants
  private final int COLOR_CHANNELS = 3;
  private final int BASE = 2;
  private final double NATURAL_LOG_2 = Math.log(2);

}
