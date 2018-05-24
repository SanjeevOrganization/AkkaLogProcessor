package com.akka.log.processor.application;

import java.io.File;

import org.apache.log4j.Logger;

import com.akka.log.processor.actors.FileScanner;
import com.akka.log.processor.message.Scan;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Application {

	private final static Logger LOGGER = Logger.getLogger(Application.class);
	private static final String RESULT_FILE_NAME = "fileWordCount.txt";
	public static void main(String... args) {
		LOGGER.info("Start Application : Method main");
		try {
			String workingDirectory = "D:\\logs";

			if (args.length > 0) {
				LOGGER.info(" Using Argument as Working Directory " + args[0]);
				workingDirectory = args[0];
			} else {
				LOGGER.info(" Using Default Working Directory " + workingDirectory);
			}
			File file = new File(workingDirectory);
			if (!file.exists()) {
				LOGGER.error("*****Directory doesnt exist*****");
				System.exit(1);
			}
			if (!file.canRead()) {
				LOGGER.error("*****No Directory Access*****");
				System.exit(1);
			}
			file = new File(workingDirectory + File.separator + RESULT_FILE_NAME);
				if(file.exists()) {
					file.delete();
				}
			
			final ActorSystem actorSystem = ActorSystem.create("logProcessor");
			final ActorRef fileScannerActorRef = actorSystem.actorOf(Props.create(FileScanner.class, workingDirectory),
					"fileScanner");
			fileScannerActorRef.tell(new Scan(), null);

		} catch (final Exception exception) {
			LOGGER.error("*****Error while processing file*****" + exception);
		}finally {
			LOGGER.info("End Application : Method main");
		}

	}
}
