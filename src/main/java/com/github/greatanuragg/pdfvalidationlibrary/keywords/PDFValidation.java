package com.github.greatanuragg.pdfvalidationlibrary.keywords;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

@RobotKeywords
public class PDFValidation {

	private PdfReader reader;
	private Map<Integer, String> data;
	private int numberOfPages;
	private String output;
	
	
	/**
     * Retrieves the PDF file extract from PDF file 
     *
     * @param filePath
     *            Absolute path of the PDF file containing the data we want to retrieve.
     *
     * @return Returns a {@output} containing the data need to run the test case.
     * @throws IOException
     *             When an error occurs while reading the PDF file.
     */
    
    @RobotKeyword
    @ArgumentNames({ "filepath" })
    public String getPdfContent(String filepath) throws FileNotFoundException, IOException {
    	if (filepath.isEmpty()) {
    		throw new IllegalArgumentException("Parameter filePath can't be empty when using keyword Parse Pdf File");
		}
    try {
			/**
			 * Option to directly parse content without downloading file
			 * BufferedInputStream fileToParse = new BufferedInputStream(url.openStream());
			 * PDFParser parser = new PDFParser(fileToParse);
			 */
			// parse() -- This will parse the stream and populate the
			// COSDocument object.
			// COSDocument object -- This is the in-memory representation of the
			// PDF document
			File file = new File(filepath);
			PDFParser parser = new PDFParser(new FileInputStream(file));
			parser.parse();
			output = new PDFTextStripper().getText(parser.getPDDocument());
			System.out.println("********PDF Extract output is *******" + "\n" + output);
			parser.getPDDocument().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	return output;
	}
    	 
    /**
     * Retrieves the latest file from a specific directory 
     * @param dirPath
     *            Absolute path of the PDF file containing the data we want to retrieve.
     *
     * @return Returns a {@lastModifiedFile} last modified file in given path
     */
  
    @RobotKeyword
    @ArgumentNames({ "dirPath" })
    private File getLatestFilefromDir(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}

		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		return lastModifiedFile;
	}

    /**
     * Check the file downloaded from a specific directory 
     * @param downloadPath
     *            Absolute path of the download path of PDF file containing the data we want to retrieve.
     * @param fileName
     *            Downloaded filename in given download path
     * @return Returns a {@flag} with last modified downloaded filename
     */
    
    @RobotKeyword
    @ArgumentNames({ "downloadPath", "fileName" })
    public boolean isFileDownloaded(String downloadPath, String fileName) {
		boolean flag = false;
		File dir = new File(downloadPath);
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			if (dir_contents[i].getName().equals(fileName))
				return flag = true;
		}

		return flag;
	}
    
    /**
     * Check the file from a specific directory with extension
     * @param dirPath
     *            Absolute path of the download path of PDF file containing the data we want to retrieve.
     * @param ext
     *            Extension of filename
     * @return Returns a {@flag} with last modified downloaded filename
     */
    
    @RobotKeyword
    @ArgumentNames({ "dirPath", "ext" })
	private boolean isFileDownloaded_Ext(String dirPath, String ext) {
		boolean flag = false;
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			flag = false;
		}

		for (int i = 1; i < files.length; i++) {
			if (files[i].getName().contains(ext)) {
				flag = true;
			}
		}
		return flag;
	}
	
    /**
     * Check the file using 'Java Watch Service API' which monitors the changes in
	 * the directory
     * @param downloadDir
     *            Absolute path of the download path of PDF file containing the data we want to retrieve.
     * @param fileExtension
     *            Extension of filename
     * @return Returns a {@downloadedFileName} download filename
     */
    
    @RobotKeyword
    @ArgumentNames({ "downloadDir", "fileExtension" })
	public static String getDownloadedDocumentName(String downloadDir, String fileExtension) {
		String downloadedFileName = null;
		boolean valid = true;
		boolean found = false;

		// default timeout in seconds
		long timeOut = 20;
		try {
			Path downloadFolderPath = Paths.get(downloadDir);
			WatchService watchService = FileSystems.getDefault().newWatchService();
			downloadFolderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			long startTime = System.currentTimeMillis();
			do {
				WatchKey watchKey;
				watchKey = watchService.poll(timeOut, TimeUnit.SECONDS);
				long currentTime = (System.currentTimeMillis() - startTime) / 1000;
				if (currentTime > timeOut) {
					System.out.println("Download operation timed out.. Expected file was not downloaded");
					return downloadedFileName;
				}

				for (WatchEvent event : watchKey.pollEvents()) {
					WatchEvent.Kind kind = event.kind();
					if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
						String fileName = event.context().toString();
						System.out.println("New File Created:" + fileName);
						if (fileName.endsWith(fileExtension)) {
							downloadedFileName = fileName;
							System.out.println("Downloaded file found with extension " + fileExtension
									+ ". File name is " + fileName);
							Thread.sleep(500);
							found = true;
							break;
						}
					}
				}
				if (found) {
					return downloadedFileName;
				} else {
					currentTime = (System.currentTimeMillis() - startTime) / 1000;
					if (currentTime > timeOut) {
						System.out.println("Failed to download expected file");
						return downloadedFileName;
					}
					valid = watchKey.reset();
				}
			} while (valid);
		}

		catch (InterruptedException e) {
			System.out.println("Interrupted error - " + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Download operation timed out.. Expected file was not downloaded");
		} catch (Exception e) {
			System.out.println("Error occured - " + e.getMessage());
			e.printStackTrace();
		}
		return downloadedFileName;
	}
	
	
	
}
