package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.List;

public class ImportParameters {
	public enum IndexSource { Data, ManualAdd, ManualOverride }
	
	private File file;
	private String previewData;
	
	private char separator;
	private Character commentCharacter = null;
	private boolean transposeBeforeImport;
	private IndexSource indexSource;
	private List<String> manualIndexValues;
	private boolean importRowNames;
	
	private boolean importAllColumns = true;
	private List<Integer> importedColumnIndices;
	
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
	public IndexSource getIndexSource() {
		return indexSource;
	}
	public void setIndexSource(IndexSource indexSource) {
		this.indexSource = indexSource;
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
	public boolean isImportAllColumns() {
		return importAllColumns;
	}
	public void setImportAllColumns(boolean importAllColumns) {
		this.importAllColumns = importAllColumns;
	}
	public List<Integer> getImportedColumnIndices() {
		return importedColumnIndices;
	}
	public void setImportedColumnIndices(List<Integer> importedColumnIndices) {
		this.importedColumnIndices = importedColumnIndices;
	}
	
	
	
}
