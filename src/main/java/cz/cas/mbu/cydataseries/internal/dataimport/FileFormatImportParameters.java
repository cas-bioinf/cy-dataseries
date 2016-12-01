package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.List;

public class FileFormatImportParameters {
	
	private char separator;
	private Character commentCharacter = null;
	
	private boolean transposeBeforeImport;

	public boolean isTransposeBeforeImport() {
		return transposeBeforeImport;
	}
	public void setTransposeBeforeImport(boolean transposeBeforeImport) {
		this.transposeBeforeImport = transposeBeforeImport;
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
	
}
