package com.github.greatanuragg.pdfvalidationlibrary.keywords;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	
	
	@RobotKeyword
    @ArgumentNames({ "filepath" })
    public String parsePdfFile(String filepath) throws IOException {
    	if (filepath.isEmpty()) {
    		filepath = "C:";
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
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	return output;
	}
	
	
	
}
