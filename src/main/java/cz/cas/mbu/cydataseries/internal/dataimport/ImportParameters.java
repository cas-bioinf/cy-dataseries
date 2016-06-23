package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.List;

public class ImportParameters {
	private File file;
	private String previewData;
	
	private char separator;
	private Character commentCharacter = null;
	private boolean transposeBeforeImport;
	private boolean manualIndexData;
	private List<String> manualIndexValues;
	private boolean importRowNames;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public String getPreviewData() {
		return previewData;
	}
	public void setPreviewData(String previewData) {
		this.previewData = previewData;
	}
	
	public char getSeparator() {
		return separator;
	}
	public void setSeparator(char separator) {
		this.separator = separator;
	}
	public Character getCommentCharacter() {
		return commentCharacter;
	}
	public void setCommentCharacter(Character commentCharacter) {
		this.commentCharacter = commentCharacter;
	}
	public boolean isTransposeBeforeImport() {
		return transposeBeforeImport;
	}
	public void setTransposeBeforeImport(boolean transposeBeforeImport) {
		this.transposeBeforeImport = transposeBeforeImport;
	}
	public boolean isManualIndexData() {
		return manualIndexData;
	}
	public void setManualIndexData(boolean manualIndexData) {
		this.manualIndexData = manualIndexData;
	}
	public List<String> getManualIndexValues() {
		return manualIndexValues;
	}
	public void setManualIndexValues(List<String> manualIndexValues) {
		this.manualIndexValues = manualIndexValues;
	}
	public boolean isImportRowNames() {
		return importRowNames;
	}
	public void setImportRowNames(boolean importRowNames) {
		this.importRowNames = importRowNames;
	}
	
	
}
