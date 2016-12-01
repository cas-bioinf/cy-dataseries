package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;

public class TabularFileImportParameters {
	private final FileFormatImportParameters fileFormatParameters;
	private final DataSeriesImportParameters dataSeriesParameters;
	
	
	private File file;
	//private String previewData;
	
	
	public TabularFileImportParameters(){
		fileFormatParameters = new FileFormatImportParameters();
		dataSeriesParameters = new DataSeriesImportParameters();
	}
			
	public TabularFileImportParameters(FileFormatImportParameters fileFormatParameters,
			DataSeriesImportParameters dataSeriesParameters) {
		super();
		this.fileFormatParameters = fileFormatParameters;
		this.dataSeriesParameters = dataSeriesParameters;
	}

	public FileFormatImportParameters getFileFormatParameters() {
		return fileFormatParameters;
	}

	public DataSeriesImportParameters getDataSeriesParameters() {
		return dataSeriesParameters;
	}
	

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
/*	public String getPreviewData() {
		return previewData;
	}
	public void setPreviewData(String previewData) {
		this.previewData = previewData;
	}
	*/
}
