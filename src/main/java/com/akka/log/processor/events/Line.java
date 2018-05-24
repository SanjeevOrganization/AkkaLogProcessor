package com.akka.log.processor.events;

import java.nio.file.Path;

public class Line {

	public final Path targetFile;
	public final Long sequenceNumber;
	public final String read;
	public final Long noOfWords;

	public Line(final Path targetFile, final Long sequenceNumber, final String read, final Long noOfWords) {
		this.targetFile = targetFile;
		this.sequenceNumber = sequenceNumber;
		this.read = read;
		this.noOfWords = noOfWords;
	}

	@Override
	public String toString() {
		return "Line{ " + "targetFile = " + this.targetFile + ", sequenceNumber = " + this.sequenceNumber + ", read = "
				+ this.read + ", no of words =" + this.noOfWords + " }";
	}

}
