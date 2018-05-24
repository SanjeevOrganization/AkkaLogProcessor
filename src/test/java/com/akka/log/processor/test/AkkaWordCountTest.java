package com.akka.log.processor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AkkaWordCountTest {

	@Test
	public void akkaWordCount() {
		final Map<String, Integer> fileWordCount = new HashMap<>();
		try {
			String fileContent = null;
			String filePath = "D:\\logs";
			final File workingPathDirectory = new File(filePath);
			filePath = filePath + File.separator + "fileWordCount.txt";;
			assertTrue("Path exists", workingPathDirectory.exists());
			assertTrue("Read Access for path", workingPathDirectory.canRead());
			assertTrue("Path is directory", workingPathDirectory.isDirectory());
			
			final File resultFile =  new File(filePath);
			
				assertTrue("Result File exists", resultFile.exists());
				
				for (final File file : workingPathDirectory.listFiles()) {
					if(file.getAbsolutePath().equals(filePath))
						continue;
					fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))).trim();
					fileWordCount.put(file.getAbsolutePath(),fileContent.split("\\s+").length);
				}
				
				final Stream<String> resultFileLines = Files.lines(Paths.get(filePath));
				resultFileLines.forEach(line-> assertEquals(line.split("=")[1], fileWordCount.get(line.split("=")[0])));

		} catch (final Exception exception) {
			exception.printStackTrace();
		}

	}
}
