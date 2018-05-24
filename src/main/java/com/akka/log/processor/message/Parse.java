package com.akka.log.processor.message;

import java.nio.file.Path;

public class Parse {

	public final String name;
	public final Path fileToParse;

	public Parse(final Path fileToParse) {

		this.name = "parse";
		this.fileToParse = fileToParse;

	}

	@Override
	public String toString() {
		return "Parse fileToParse : " + this.fileToParse ;
	}

}
