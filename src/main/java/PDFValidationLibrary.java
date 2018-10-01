import org.robotframework.javalib.library.AnnotationLibrary;

public class PDFValidationLibrary extends AnnotationLibrary {

	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	public PDFValidationLibrary() {
		super("com/github/greatanuragg/pdfvalidationlibrary/keywords/**");
	}

	@Override
	public String getKeywordDocumentation(String keywordName) {
		if (keywordName.equals("__intro__")) {
			return "PDFLibrary is a Robot Framework library for testing PDFs Content.";
		}
		return super.getKeywordDocumentation(keywordName);
	}

}
