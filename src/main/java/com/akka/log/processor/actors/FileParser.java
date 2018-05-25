package com.akka.log.processor.actors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.log4j.Logger;

import com.akka.log.processor.events.EndOfFile;
import com.akka.log.processor.events.Line;
import com.akka.log.processor.events.StartOfFile;
import com.akka.log.processor.message.Parse;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class FileParser extends AbstractActor {

	private final Logger LOGGER = Logger.getLogger(FileParser.class);

	protected final byte DELIMETER = (byte) '\n';
	protected final CompletionHandler<Integer, ByteBuffer> readComplete;
	protected Path fileToParse;
	protected ActorRef lineAggregator;
	protected Long position;
	protected Long lineSequence;
	protected ByteArrayOutputStream line;
	protected AsynchronousFileChannel pipe;
	protected ByteBuffer octet = ByteBuffer.allocate(1);
	protected Long noOfWords;
	private static final String RESULT_FILE_NAME = "fileWordCount.txt";
	private FileParser() {
		LOGGER.info("Start FileParser : Method FileParser");
		this.readComplete = makeCompletionHandler();
		receive(ReceiveBuilder.match(Parse.class, message -> this.handle(message))
				.matchAny(o -> LOGGER.info("FileScanner does not understand this message {}"+ o)).build());
		LOGGER.info("End FileParser : Method FileParser");
	}

	public void handleStartOfFile() {
		context().system().eventStream().publish(new StartOfFile(this.fileToParse));
	}

	public void handleEndOfFile() {
		LOGGER.info("Start FileParser : Method handleEndOfFile");
		context().system().eventStream().publish(new EndOfFile(this.fileToParse, this.lineSequence, this.noOfWords));
		LOGGER.info("End FileParser : Method handleEndOfFile");
	}

	public void handleLine() {
		try {
			String line = this.line.toString();
			if (line.isEmpty() || line.equals("\r\n"))
				return;
			this.line.reset();
			this.lineSequence += 1;
			this.noOfWords += line.split(" ").length;
			context().system().eventStream().publish(new Line(this.fileToParse, this.lineSequence, line, noOfWords));
		} catch (final Exception exception) {
			LOGGER.error("Error FileParser : Method handleLine" + exception);
		}

	}

	public void scheduleNextByteRead() throws IOException {
		this.octet = ByteBuffer.allocate(1);
		this.pipe.read(this.octet, this.position, null, this.readComplete);
	}

	public void configureFrom(final Parse message) {
		LOGGER.info("Start FileParser : Method configureFrom");
		this.fileToParse = message.fileToParse;
		this.lineAggregator = getContext().actorOf(Props.create(LineAggregator.class, this.fileToParse),
				"lineAggregator");
		this.position = 0L;
		this.lineSequence = 0L;
		this.line = new ByteArrayOutputStream();
		this.noOfWords = 0L;
		LOGGER.info("End FileParser : Method configureFrom");
	}

	public void openAsyncChannel(final Path fileToParse) throws IOException {
		this.pipe = AsynchronousFileChannel.open(fileToParse, StandardOpenOption.READ);
	}

	private void handle(final Parse message) {
		LOGGER.info("Start FileParser : Method handle" + message);
		try {
			this.configureFrom(message);
			this.tapFileEventStream(this.lineAggregator);
			this.openAsyncChannel(this.fileToParse);
			this.handleStartOfFile();
			this.scheduleNextByteRead();
		} catch (final IOException ioException) {
			LOGGER.error("Error FileParser : Method handle" + ioException);
		}finally {
			LOGGER.info("End FileParser : Method handle");
		}

	}

	private void tapFileEventStream(final ActorRef subscriber) {
		LOGGER.info("Start FileParser : Method tapFileEventStream");
		context().system().eventStream().subscribe(subscriber, StartOfFile.class);
		context().system().eventStream().subscribe(subscriber, Line.class);
		context().system().eventStream().subscribe(subscriber, EndOfFile.class);
		LOGGER.info("End FileParser : Method tapFileEventStream");
	}

	private CompletionHandler<Integer, ByteBuffer> makeCompletionHandler() {
		FileParser self = this;
		return new CompletionHandler<Integer, ByteBuffer>() {
			public void completed(Integer result, ByteBuffer target) {
				try {
					self.processNextByte(result);
				} catch (final IOException ioException) {
				LOGGER.error("Error FileParser : Method makeCompletionHandler" + ioException);
				}
			}

			public void failed(Throwable exception, ByteBuffer target) {
				LOGGER.error("Error FileParser : Method failed" + exception);
				self.handleEndOfFile();
			}

		};

	}

	private void processNextByte(final Integer numRead) throws IOException {
		if (numRead <= 0) {
			this.handleLine();
			this.handleEndOfFile();
			this.writeResultToFile();
			return;
		}

		this.line.write(this.octet.get(0));
		if (this.octet.get(0) == this.DELIMETER) {
			this.handleLine();
		}
		this.position += 1;
		this.scheduleNextByteRead();
	}
	
	private void writeResultToFile() {
		try {
			final String content = this.fileToParse+"="+this.noOfWords;
			Files.write(Paths.get(this.fileToParse.getParent() + File.separator + RESULT_FILE_NAME), (content+System.lineSeparator()).getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		}catch(final IOException ioException) {
			LOGGER.error("Error FileParser : Method writeResultToFile" + ioException);
		}catch(final Exception exception) {
			LOGGER.error("Error FileParser : Method writeResultToFile" + exception);
		}
		
		
	}
}
