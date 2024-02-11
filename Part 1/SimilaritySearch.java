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
      String fileNamePathString = this.datasetDirectory + "/" + fileNameString;
      ColorHistogram histogram = new ColorHistogram(fileNamePathString);

      double intersection = this.queryHistogram.compare(histogram);

      intersectionMap.put(intersection, fileNameString);
    }
  }

  public void getListOfHistogramFiles(String directoryName) {

    try {
      File directory = new File(directoryName);
      FileFilter filter = new TxtFilter();

      this.histogramFiles = directory.listFiles(filter);
    }
    catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }

  class TxtFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
      return file.getName().endsWith(".txt");
    }
  }

  public TreeMap<Double, String> getIntersectionMap() {
    return this.intersectionMap;
  }

  public void printSearchResults() {

    double firstIntersection = intersectionMap.lastKey();
    String firstFileName = intersectionMap.get(firstIntersection);
    System.out.println(firstFileName.substring(0, firstFileName.length()-4));

    double previousKey = firstIntersection;
    for(int index = 2; index < 6; index++) {
      double key = intersectionMap.lowerKey(previousKey);
      previousKey = key;
      String fileNameString = intersectionMap.get(key);
      System.out.println(fileNameString.substring(0, fileNameString.length()-4));
    }
  }

  private final int COLOR_DEPTH = 3;
  private ColorHistogram queryHistogram;
  private String datasetDirectory;
  private File[] histogramFiles = null;
  private TreeMap<Double, String> intersectionMap;

  public static void main(String args[]) {

    if (args.length != 2) {
      System.out.println("ERROR! Please specify the correct number of arguments!");
      return;
    }

    SimilaritySearch similaritySearch = new SimilaritySearch(args[0], args[1]);

    similaritySearch.populateIntersectionMap();

    similaritySearch.printSearchResults();

  }
}
