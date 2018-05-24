package com.akka.log.processor.actors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.akka.log.processor.message.Parse;
import com.akka.log.processor.message.Scan;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class FileScanner extends AbstractActor {

	private final Logger LOGGER = Logger.getLogger(FileScanner.class);
	protected final Map<Parse, ActorRef> fileParserRefs = new HashMap<Parse, ActorRef>();
	protected Path logDirectory;

	private FileScanner(final String logDirectoryName) {
		LOGGER.info("Start FileScanner : Method Constructor" + this.logDirectory);
		this.logDirectory = Paths.get(logDirectoryName).toAbsolutePath();
		receive(ReceiveBuilder.match(Scan.class, message -> this.handle(message))
				.matchAny(o -> LOGGER.info("Unable to process message" + o)).build());
		LOGGER.info("End FileScanner : Method Constructor" + this.logDirectory);
	}

	private void handle(final Scan message) throws IOException {
		LOGGER.info("Start FileScanner : Method handle" + message);
		Files.list(this.logDirectory).filter(Files::isRegularFile)
				.forEach(filePath -> this.startNewLineAggregator(filePath));
		LOGGER.info("End FileScanner : Method handle" + message);
	}

	private void startNewLineAggregator(final Path filePath) {
		LOGGER.info("Start FileScanner : Method startNewLineAggregator" + filePath);
		ActorRef fileParserRef = getContext().actorOf(Props.create(FileParser.class),
				"file-parser-" + UUID.randomUUID());

		Parse parseMessage = new Parse(filePath);
		fileParserRef.tell(parseMessage, null);
		this.fileParserRefs.put(parseMessage, fileParserRef);
		LOGGER.info("End FileScanner : Method startNewLineAggregator" + filePath);
	}

}