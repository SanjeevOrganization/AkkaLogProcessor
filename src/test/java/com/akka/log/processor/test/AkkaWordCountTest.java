package com.akka.log.processor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class AkkaWordCountTest {

	@Test
	public void akkaWordCount() {
		final Map<String, Integer> fileWordCount = new HashMap<>();
		try {
			String fileContent = null;
			String filePath = "D:\\logs";
			final File workingPathDirectory = new File(filePath);
			filePath = filePath + File.separator + "fileWordCount.txt";
			assertTrue("Path exists", workingPathDirectory.exists());
			assertTrue("Read Access for path", workingPathDirectory.canRead());
			assertTrue("Path is directory", workingPathDirectory.isDirectory());
			final File resultFile = new File(filePath);
			assertTrue("Result File exists", resultFile.exists());
			for (final File file : workingPathDirectory.listFiles()) {
				if (file.getAbsolutePath().equals(filePath))
					continue;
				fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))).trim();
				fileWordCount.put(file.getAbsolutePath(), fileContent.split("\\s+").length);
			}
			Files.lines(Paths.get(filePath)).forEach(line -> {
				assertEquals(line.split("=")[1], String.valueOf(fileWordCount.get(line.split("=")[0])));
			});
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		} catch (final Exception exception) {
			exception.printStackTrace();
		}

	}

}
