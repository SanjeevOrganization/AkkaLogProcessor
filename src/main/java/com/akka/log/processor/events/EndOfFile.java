package com.akka.log.processor.events;

import java.nio.file.Path;

public class EndOfFile {

	public final Path targetFile;
	public final long finalLineNumber;
	public final Long noOfWords;

	public EndOfFile(final Path targetFile, final Long finalLineNumber, final Long noOfWords) {

		this.targetFile = targetFile;
		this.finalLineNumber = finalLineNumber;
		this.noOfWords = noOfWords;
	}

	@Override
	public String toString() {
		return "EndOfFile{ targetFile = " + this.targetFile + ", finalLineNumber = " + this.finalLineNumber
				+ ", no of words =" + this.noOfWords + "}";
	}

}
