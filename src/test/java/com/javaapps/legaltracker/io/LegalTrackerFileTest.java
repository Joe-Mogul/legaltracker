package com.javaapps.legaltracker.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class LegalTrackerFileTest {

	private LegalTrackerFile legalTrackerFile;
	private File filesDir;

	@Before
	public void setup() {
		try {
			 String dirName="./target//unittestdir";
			filesDir = new File(dirName);
			if (! filesDir.exists()){
				assertTrue(filesDir.mkdirs());
			}
			Config.getInstance().setFilesDir(filesDir);
			legalTrackerFile = new LegalTrackerFile( "unittest", "obj");
		} catch (Exception ex) {
			fail("Unable to setup LegalTrackerFileTest because "
					+ ex.getMessage());
		}
	}

	@Test
	public void writeReadArchiveTest() {
		try {
			List<LegalTrackerLocation> locationList = new ArrayList<LegalTrackerLocation>();
			for (int ii = 0; ii < 10; ii++) {
				LegalTrackerLocation location = new LegalTrackerLocation(40.0,
						-80, 10.0f, 270.0f, 20.0f, System.currentTimeMillis());
				locationList.add(location);
			}
			legalTrackerFile.writeToObjectFile(locationList);
			File file = new File(filesDir, "unittest.obj");
			assertTrue(file.exists());
			assertTrue(file.length() > 0);
			legalTrackerFile.closeOutObjectFile();
			File archiveFile = null;
			for (File tmpFile : filesDir.listFiles()) {
				if (tmpFile.getName().startsWith("unittest_archive_")) {
					archiveFile = tmpFile;
				}
			}
			assertNotNull(archiveFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					new FileInputStream(archiveFile));
			int objectCounter=0;
			try
			{
				while(objectInputStream.readObject() != null){
					objectCounter++;
					
				}
			}catch(EOFException ex){
			}
			assertTrue(objectCounter==10);
		} catch (Exception ex) {
			fail("Failed to write location file because " + ex.getMessage());
		}
	}
}