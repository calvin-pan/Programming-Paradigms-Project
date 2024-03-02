#lang racket

; CSI 2120 Project Deliverable #2 - Scheme
; Student Name:     Adwitheya Benbi              Calvin Pan
; Student ID:       300165778                    300184557

; This is the main function. It calls the function similarityScore to generate the list of histogram intersections between the query image's histogram and the dataset images' histograms.
; Then it will display the image names that correspond to the top five histogram intersections.
; Input parameters: queryHistogramFilename - String, imageDatasetDirectory - String
; Output: void - Displays String
(define (similaritySearch queryHistogramFilename imageDatasetDirectory)
  (let* ((similarityScore (generateSimilarityMap queryHistogramFilename (listAllTextFiles imageDatasetDirectory) '()))
        (sortedSimilarityScore (sortByValue similarityScore))) (displayTopFiveKeys sortedSimilarityScore 5) ) )

; This function checks if a file is a text file.
; Input parameters: filename - String
; Output: boolean
(define (isTxtFile? filename)
  (string-suffix? ".txt" (substring filename (- (string-length filename) 4))))

; This function creates a list from all the text files' names in the query image folder.
; Input parameters: path - String
; Output: entries - List
(define (listAllTextFiles path)
  
  ; This function checks if a file is readable by the user.
  ; Input parameters: f - File
  ; Output: boolean
  (define (userReadable? f)
    (member 'read (file-or-directory-permissions f)))

  ; This function recursively creates the list of the text files' names in the query image folder.
  ; Input parameters: path - String
  ; Output: entries - List
  (define (recursiveList path)
    (let ([entries (in-directory path userReadable?)])
      (apply append
             (map (lambda (entry)
                    (with-handlers ([exn:fail?
                                     (lambda (e)
                                       (if (eq? (exn-message e) "in-directory: no such file or directory")
                                           '()
                                           (raise e)))])
                      (if (directory-exists? entry)
                          (recursiveList entry)
                          (if (and (not (directory-exists? entry))
                                   (isTxtFile? (path->string entry)))
                              (list (path->string entry))
                              '()))))
                  (sequence->list entries)))))

  (recursiveList path))

; This function converts the histogram in the text file into a list.
; Input parameters: filePath - String
; Output: x - List
(define (pathToList filePath)
  (let ((p (open-input-file filePath)))
    (let f ((x (read p))) ; reading from file
      (if (eof-object? x) ; check for eof
          (begin
            (close-input-port p)
            '())
          (cons x (f (read p))))) )
  )

; This function calls the normalize-helper function which will normalize the histogram and return it.
; Input parameters: histogram - List
; Output: histogram - List
(define (normalizeHistogram histogram)
  ; Calculate the total sum of all values in the histogram
  (define total (apply + histogram))
  
  ; This function is a helper function to normalize each value in the histogram
  ; Input parameters: value - Double
  ; Output: value - Double
  (define (normalizeHelper value)
    (/ value total))
  
  ;; Map the normalize-helper function to each value in the histogram
  (map normalizeHelper histogram))

; This function compares a and b and check if they are equal.
; Input parameters: a - Integer, b - Integer
; Output: boolean
(define (notEqual? a b)
  (not (= a b)))

; This function returns the minimum between a and b.
; Input parameters: a - Integer, b - Integer
; Output: a - Integer or b - Interger
(define (min a b)
  (if (< a b)
      a
      b))

; This function compares two histograms and get their histogram intersection value.
; Input parameters: queryHistogram - List, datasetHistogram - List, intersection - Double
; Output: intersection - Double
(define (compare queryHistogram datasetHistogram intersection)
  (let ((queryHistogramLength (length queryHistogram))
        (datasetHistogramLength (length datasetHistogram)))
     (if (notEqual? queryHistogramLength datasetHistogramLength) 0.0
            (if (= queryHistogramLength 0)
                 intersection
             (compare (cdr queryHistogram)(cdr datasetHistogram)(+ intersection (min (car queryHistogram) (car datasetHistogram))))))))

; This function generates the list of histogram intersections between the query image and the dataset images.
; Input parameters: queryHistogram - List, datasetHistogram - List, intersection - Double
; Output: intersection - Double
(define (generateSimilarityMap queryHistogramFilename imageDatasetList similarityMap)
  (if (null? imageDatasetList)
      similarityMap
      (let* ((normalizedQueryHistogram (normalizeImage queryHistogramFilename))
             (datasetImagePath (car imageDatasetList))
             (normalizedDatasetHistogram (normalizeDatasetImage datasetImagePath))
             (similarityScore (compare normalizedQueryHistogram normalizedDatasetHistogram 0.0)))
        (generateSimilarityMap queryHistogramFilename (cdr imageDatasetList)
                               (cons (cons datasetImagePath similarityScore) similarityMap)))))

; This function sorts the list of histogram intersections.
; Input parameters: alist - List
; Output: Sorted alist - List
(define (sortByValue alist)
  (define (compare x y)
    (> (cdr x) (cdr y))) ; Compare the values
  (sort alist compare))

; This function calls normalizeHistogram and returns the normalized histogram of the query image.
; Input parameters: imagePath - String
; Output: normalizedHistogram - List
(define (normalizeImage imagePath)
  (let ((normalizedHistogram (normalizeHistogram (cdr (pathToList imagePath))))) normalizedHistogram))

; This function returns the dataset image histogram that is set to be normalized and compared.
; Input parameters: imageDatasetDirectory - String
; Output: datasetImagePath - String
(define (getFirstDatasetImagePath imageDatasetDirectory)
  (let ((datasetImagePath (car (listAllTextFiles imageDatasetDirectory)))) datasetImagePath ))

; This function calls normalizeHistogram and returns the normalized histogram of the dataset image.
; Input parameters: datasetImagePath - String
; Output: normalizedHistogram - List
(define (normalizeDatasetImage datasetImagePath)
  (let* ((rawHistogram (cdr (pathToList datasetImagePath)))
        (normalizedHistogram (normalizeHistogram rawHistogram)))
   normalizedHistogram))

; This function adds displays the names of the images that correspond to the top five histogram intersections.
; Input parameters: assocList - List, count - Integer
; Output: void - Displays strings
(define (displayTopFiveKeys assocList count)
  ;; Base case: stop when count reaches zero or when the list is empty
  (if (or (= count 0) (null? assocList)) (display "")(begin
                (display (removePath (caar assocList)))
                (newline)
                (displayTopFiveKeys (cdr assocList) (- count 1)))))

; This function removes the path of the file to show the image name only.
; Input parameters: str - String
; Output: Substring of str - String
(define (removePath str)

  ; This function is a helper function that allows the recursive searching of a string to find the last '\' or '/'.
  ; Then, it will take the substring after it. After, it will take the substring of everything before the last four characters. This will remove the ".txt" file extension.
  ; Input parameters: str - String, index - Integer, lastSeparator - Integer
  ; Output: Substring of str - String
  (define (removePathHelper str index lastSeparator)
    (cond ((>= index (string-length str)) (substring str (+ lastSeparator 1) (- (string-length str) 4))) ; Remove the last 4 characters (".txt")
          ((or (char=? (string-ref str index) #\/)
               (char=? (string-ref str index) #\\))
           (removePathHelper str (+ index 1) index))
          (else
           (removePathHelper str (+ index 1) lastSeparator))))
  (removePathHelper str 0 -1))