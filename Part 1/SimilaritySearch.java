import java.io.*;
import java.util.*;
// import java.lang.Math;

public class SimilaritySearch {

  public SimilaritySearch(String queryImageName, String datasetDirectory) {

    this.datasetDirectory = datasetDirectory;

    ColorImage queryImage = new ColorImage(queryImageName);

    this.queryHistogram = computeHistogram(queryImage);

    this.intersectionMap = new TreeMap<Double, String>();
  }

  public ColorHistogram computeHistogram(ColorImage image) {
    // System.out.println("image depth: " + image.getDepth());
    image.reduceColor(COLOR_DEPTH);
    ColorHistogram histogram = new ColorHistogram(image.getDepth());
    histogram.setImage(image);
    histogram.computeHistogram();

    return histogram;
  }

  public void populateIntersectionMap() {
    getListOfHistogramFiles(this.datasetDirectory);
    if (histogramFiles.length == 0) {
      System.out.println("ERROR! There are no histogram files in the directory!");
      return;
    }

    for(File file: this.histogramFiles) {
      String fileNameString = file.getName();
      ColorHistogram histogram = new ColorHistogram(fileNameString);

      double intersection = this.queryHistogram.compare(histogram);

      intersectionMap.put(intersection, fileNameString);
    }
  }

  public void getListOfHistogramFiles(String directoryName) {
    // File[] histogramFiles = null;

    try {
      File directory = new File(directoryName);
      FileFilter filter = new TxtFilter();

      this.histogramFiles = directory.listFiles(filter);

      // return histogramFiles;
    }
    catch(Exception e) {
      System.out.println(e.getMessage());
    }

    // return histogramFiles;
  }

  class TxtFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
      return file.getName().endsWith(".txt");
    }
  }

  // class JpgFilter implements FileFilter {
  //   @Override
  //   public boolean accept(File file) {
  //     boolean filterResult = (file.getName().endsWith(".jpg"))||(file.getName().endsWith(".jpeg"));
  //     return filterResult;
  //   }
  // }

  public TreeMap<Double, String> getIntersectionMap() {
    return this.intersectionMap;
  }



  private final int COLOR_DEPTH = 3;
  private ColorHistogram queryHistogram;
  private String datasetDirectory;
  private File[] histogramFiles = null;
  private TreeMap<Double, String> intersectionMap;

  public static void main(String args[]) {
    // System.out.println("First argument: " + args[0] + "\nSecond argument: " + args[1]);

    if (args.length != 2) {
      System.out.println("ERROR! Please specify the correct number of arguments!");
      return;
    }

    String queryImageName = args[0];
    String datasetDirectory = args[1];

    // ColorImage queryImage = new ColorImage(queryImageName);

    SimilaritySearch obj = new SimilaritySearch(queryImageName, datasetDirectory);

    // obj.computeHistogram(queryImage);

    obj.populateIntersectionMap();

    TreeMap<Double, String> intersectionMap = obj.getIntersectionMap();

    double firstIntersection = intersectionMap.firstKey();
    System.out.println("Key 1: " + firstIntersection + " Value: " + intersectionMap.get(firstIntersection));

    double previousKey = firstIntersection;
    for(int index = 2; index < 6; index++) {
      double key = intersectionMap.higherKey(previousKey);
      previousKey = key;

      System.out.println("Key " + index + ": " + key + " Value: " + intersectionMap.get(key));
    }

  }
}
