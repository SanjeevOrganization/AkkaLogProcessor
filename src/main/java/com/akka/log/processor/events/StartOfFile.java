package com.akka.log.processor.events;

import java.nio.file.Path;

public class StartOfFile {

	public final Path targetFile;

	public StartOfFile(final Path targetFile) {
		this.targetFile = targetFile;
	}

	@Override
	public String toString() {
		return "StartOfFile{ targetFile = " + this.targetFile + " }";
	}

}