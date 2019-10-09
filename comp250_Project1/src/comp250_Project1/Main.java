package comp250_Project1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

//I did all steps.
//Citation List:
// https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
// https://docs.oracle.com/javase/1.5.0/docs/api/java/lang/String.html#split(java.lang.String,%20int)
// https://www.geeksforgeeks.org/binary-search/
//https://docs.oracle.com/javase/7/docs/api/java/io/File.html


public class Main {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		boolean fileFound = false;
		String fileName = null;
		
		// prompt user for filename, will keep asking until user gives valid file name
		while (fileFound == false) {
			System.out.println("What is the name of the file?");
			fileName = in.nextLine().strip();
			if ( new File(fileName).exists()) {
				fileFound = true;
			} else {
				System.out.println("File does not exist.");
			}
		}
		
		// open file now, also makes the word file
		int wordCount = makeWordsFile(fileName);

		// this sorts and created the sorted file
		alphaSort(fileName);

		// this removes the duplicates
		int numUniqueWords = removeDuplicates(fileName);

		// this does the search and print entries.
		queryAndSearch(numUniqueWords, fileName, wordCount);
		in.close();
	}

	public static int makeWordsFile(String fileName) {
		int wordCount = 0; // tracks word count
		// try catch in case file does not exist
		try {
			FileReader fReader =  new FileReader(fileName); // open reader
			Scanner in = new Scanner(fReader);
			long time = System.currentTimeMillis(); // start time
			while (in.hasNext() == true) {
				// only counts word if it is not all punctuation
				if (in.next().replaceAll("\\p{Punct}", "").equals("") == false) {
					wordCount++;
				}
			}
			in.close();
			time = System.currentTimeMillis() - time;  // record elapsed time
			System.out.println("The elapsed time to count words is " + time + " ms.");

			System.out.println("The number of words is " + wordCount);
			try {
				fReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		PrintWriter p;
		
		// keeps track of current word location
		int currentNum = 0;
		try {
			// create word file
			p = new PrintWriter(fileName.concat("_words.txt"));
			FileReader fReader =  new FileReader(fileName);
			Scanner in = new Scanner(fReader);

			// puts # of words in the file
			p.println(wordCount);
			// continues if their is another token available
			while (in.hasNext() == true) {
			 
				String current = in.next();
				// print word to file only if the word is not all punctuation
				if (!current.replaceAll("\\p{Punct}", "").toLowerCase().equals("")) {
					p.println(current.replaceAll("\\p{Punct}", "").toLowerCase() + " " + currentNum);
					currentNum++;
				}
			}

			p.close(); // close printer and scanner
			in.close();
		} catch (FileNotFoundException e) {
			// you don't want to do make this empty because if a error happens here you won't know what happened
			e.printStackTrace();
		}
		return currentNum;
	}

	// creates file and sorts words
	public static String alphaSort(String fileName) {
		int numWords = 0;
		String[] words = null;
		
		try {
			// opens word file
			Scanner fileScan = new Scanner(new FileReader(fileName.concat("_words.txt")));
			// receives the number of words, moves line cursor, parse string to int
			numWords = Integer.parseInt(fileScan.nextLine());
			words = new String[numWords];
			
			for (int i = 0; i < numWords; i++) { // collect entries
				words[i] = fileScan.nextLine();
			}
			fileScan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

		long time = System.currentTimeMillis(); // start time
		words = mergeSort(words); // sort entries
		time = System.currentTimeMillis() - time; // print elapsed time
		System.out.println("Elapsed time to organize words alphabetically is " + time + " ms");

		// write entries to file
		try {
			PrintWriter pr2 = new PrintWriter(fileName.concat("_sorted.txt"));
			// print # of words
			pr2.println(words.length);
			// print word + index to line
			for (int i = 0; i < numWords; i++) {
				pr2.println(words[i]);
			}

			pr2.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}

		return fileName.concat("_sorted.txt");
	}

	// merge sort
	public static String[] mergeSort(String[] strs) {
		// base case if array is size 1
		if (strs.length <=1)
			return strs;
		
		// splits array into 2 halves
		String[] left = new String[strs.length/2];
		String[] right = new String[strs.length - strs.length/2];
		
		// make two list and copy left and right half
		for (int i=0; i<strs.length/2; i++) {
			left[i] = strs[i];
		}
		for (int i=0; i< strs.length - strs.length/2; i++) {
			right[i] = strs[i + strs.length/2];
		}
		
		left = mergeSort(left); // assume merge sort gives assorted list
		right = mergeSort(right);
		// both left and right are sorted
		
		strs = merge(left, right);
		
		return strs;
	}
	
	public static String[] merge(String[] left, String[] right) {
		// make a place to hold the merged list
		String[] sorted = new String[left.length + right.length];
		int l = 0; // left index
		int r = 0; // right index
		int s = 0; // index in output list
		
		// keep going until one runs out
		
		while (l<left.length && r<right.length) {
			if (left[l].compareToIgnoreCase(right[r]) <= 0) { // if low element in left list is smaller
				sorted[s] = left[l]; // copy it down
				l++;
				s++;
			} else {
				sorted[s] = right[r]; // copy it down
				r++;
				s++;
			}
		}
		// empty out any remaining in left
		while (l<left.length) {
			sorted[s] = left[l];
			s++;
			l++;
		}
		// empty out any remaining in right
		while (r<right.length) {
			sorted[s] = right[r];
			s++;
			r++;
		}
		return sorted;
	}

	public static int removeDuplicates(String fileName) {
		int numUniqueWords = 0;
		// open file
		try {
			Scanner in = new Scanner(new FileReader(fileName.concat("_sorted.txt")));
			PrintWriter pr = new PrintWriter(fileName.concat("_index.txt"));
			// keeps track of last word
			String lastWord = "";

			int numWords = in.nextInt();
			// receive 1st string 

			for (int i = 0; i < numWords; i++) {
				String word = in.next();
				int index = in.nextInt();

				// if this word is not the last word then make newline
				if (!lastWord.equals(word) && i > 0) {
					pr.println();	
				}

				// if this word is the last word, then just print index
				if (lastWord.equals(word)) {
					pr.print(" " + index);
				} else {
					// print word, does not make new line mark
					lastWord = word;
					pr.print(word + " " + index);
					// keeps track of unique words, increase unique words by one
					numUniqueWords++;
				}
			}
			pr.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return numUniqueWords;
	}

	public static void queryAndSearch(int numUniqueWords, String fileName, int numWords) {

		// receive entries
		String[] words = new String[numUniqueWords];
		String[] indexs = new String[numUniqueWords];
		// open file, and go through each line and store word and index into two separate arrays
		try {
			String storeLine; 
			String[] brokenLine; // breaks line into array , holds word and index
			Scanner scan = new Scanner( new FileReader(fileName.concat("_index.txt"))); 
			for (int i = 0; i < numUniqueWords; i++) {
				storeLine = scan.nextLine();
				brokenLine = storeLine.split(" ", 2);
				// stores word and index into appropriate position in array
				words[i] = brokenLine[0];
				indexs[i] = brokenLine[1];
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Scanner in = new Scanner(System.in);

		boolean status = true;
		// keep asking forever until program is close
		while (status == true) {
			System.out.println("Enter the word. ");
			// does binary search, then receive result
			int result = binarySearch(words, in.nextLine());
			if (result == -1) {
				// if search is not in book then this happens
				System.out.println("Sorry this word is not in the concordance.");
			} else {
				// if search is in array then print results
				System.out.print(words[result] + ": ");
				// formats then results
				organizePrintLn(indexs[result]);
				// print all entries
				printEntries(fileName, indexs[result], numWords);
			}
		}
		in.close();
	}
	
	public static void organizePrintLn(String str) {
		String[] strings = str.split(" ");
		int currentLetters = 0;
		for (String s : strings) {
			currentLetters += s.length() + 1;
			// if current line has printed words is about 100 length then it will make next line
			// if not it will print on same line
			if (currentLetters < 100) {
				System.out.print(s + " ");
			} else {
				System.out.println(s + " ");
				currentLetters = 0;
			}
		}
		System.out.println();
	}

	public static int binarySearch(String[] array, String searchInput) {
		int leftBound = 0; // left index window
		int rightBound = array.length; // right index window

		while (leftBound <= rightBound) {
			int midPoint = leftBound + (rightBound - leftBound)/2; // midpoint calculate

			if (array[midPoint].compareToIgnoreCase(searchInput) == 0) { 
				return midPoint;
				// if is alphabetically lower
			} else if (array[midPoint].compareToIgnoreCase(searchInput) < 0) {
				leftBound = midPoint +1;
				// if is alphabetically higher
			} else {
				rightBound = midPoint - 1;
			}	
		}
		// if not found 
		return -1;
	}

	public static void printEntries(String fileName, String indexs, int numWords) {
		String[] originalText = new String[numWords];
		try {
			FileReader fr = new FileReader(fileName);
			Scanner in = new Scanner(fr);
			// will put word into array only if it is not all punctuation
			// will add all valid words to array
			for (int i = 0; i < numWords; i++) {
				boolean check = true;
				String currentWord;
				// will check until it finds valid word
				while (check == true) {
					currentWord = in.next().replaceAll("\\p{Punct}", "");
					if (!currentWord.equals("")) { // checks if current word is not all punctuation, if so add to array
						originalText[i] = currentWord;
						check = false;
					}
				}
			}
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// takes string indexs
		String[] stringIndex = indexs.split(" ");
		int numIndexs = stringIndex.length;
		int[] indexInt = new int[numIndexs];

		// turn index string to numbers
		for (int i = 0 ; i < numIndexs; i++) {
			indexInt[i] = Integer.parseInt(stringIndex[i]);
		}

		// for all indexs in the list
		for (int index : indexInt ) {
			int leftBound = index; // leftindex
			int rightBound = index; // rightindex

			// checks if left bound is too close to beginning, if so the leftbound is beginning of file
			if ((leftBound - 10) < 0) {
				leftBound = 0;
			} else {
				leftBound -= 10;
			}

			// checks is right bound is too close to end of text file. if so the rightbound is end of file
			if ((rightBound + 11) > originalText.length) {
				rightBound = originalText.length;
			} else {
				rightBound += 11;
			}


			System.out.print(index + ": ");

			// prints word from the leftbound to the rightbound
			for (int i = leftBound; i < rightBound; i++) {
				System.out.print(" " + originalText[i]);
			}

			System.out.println();
		}
	}
}
