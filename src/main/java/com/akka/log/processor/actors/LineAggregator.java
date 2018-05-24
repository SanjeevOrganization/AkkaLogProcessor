package com.akka.log.processor.actors;

import java.nio.file.Path;

import org.apache.log4j.Logger;

import com.akka.log.processor.events.EndOfFile;
import com.akka.log.processor.events.Line;
import com.akka.log.processor.events.StartOfFile;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class LineAggregator extends AbstractActor {

	private final Logger LOGGER = Logger.getLogger(FileScanner.class);
	protected final Path fileToAggregate;

	private LineAggregator(final Path fileToAggregate) {
		LOGGER.info("Start LineAggregator : Method Cnstructor" + fileToAggregate);
		this.fileToAggregate = fileToAggregate;
		receive(ReceiveBuilder.
				match(StartOfFile.class, event -> {
					if (this.fileToAggregate.equals(event.targetFile))
						LOGGER.info("Received StartOfFile event" + event);
				}).match(Line.class, event -> {
					if (this.fileToAggregate.equals(event.targetFile))
						LOGGER.info("Received Line event" + event);
				}).match(EndOfFile.class, event -> this.handle(event))
				.matchAny(o -> LOGGER.info("Unable to process this event"+ o)).build()
		);
		LOGGER.info("End LineAggregator : Method Cnstructor" + fileToAggregate);
	}

	private void handle(final EndOfFile event) {
		LOGGER.info("Start LineAggregator : Method handle" +event);
		if (this.fileToAggregate.equals(event.targetFile)) {
			LOGGER.info("Received EndOfFile event: {}" + event);
			this.outputLineCount(event);
		}
		LOGGER.info("End LineAggregator : Method handle" +event);
	}

	private void outputLineCount(final EndOfFile event) {
		LOGGER.info("File " + event.targetFile + " has " + event.finalLineNumber + " lines" + " and no of words "
				+ event.noOfWords);
	}

}