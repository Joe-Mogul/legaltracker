package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.GForceData;

public class GForceBufferTest {
	private GForceBuffer gforceBuffer;
	private File filesDir;

	@Before
	public void setup() {
		try {
			String dirName = "./target/unittestdir";
			filesDir = new File(dirName);
			if (!filesDir.exists()) {
				assertTrue(filesDir.mkdirs());
			}
			Config.getInstance().setFilesDir(filesDir);
			Config.getInstance().setGforceListenerBufferSize(0);
			gforceBuffer = GForceBuffer.getInstance();
			assertNotNull(gforceBuffer);
			gforceBuffer.clearPointsInRunningAverageBuffer();
		} catch (Exception ex) {
			fail("Unable to setup test because " + ex.getMessage());
		}
	}

	@Test
	public void testPointsInRunningAverageBufferWithDifferentialLessThan100millis() {
		for (int ii = 0; ii < 99; ii++) {
			GForceData gforceData = new GForceData();
			gforceData.setX(ii + 1);
			gforceData.setY(ii + 2);
			gforceData.setZ(ii + 3);
			gforceData.setSampleDateInMillis(ii + 4);
			gforceBuffer.logGForce(gforceData);
		}
		assertEquals(99, gforceBuffer.getPointsInRunningAverageBuffer());
	}

	@Test
	public void testPointsInRunningAverageBufferWithDifferentialOf100millis() {
		try
		{
		for (int ii = 0; ii < 101; ii++) {
			GForceData gforceData = new GForceData();
			gforceData.setX(ii + 1);
			gforceData.setY(ii + 2);
			gforceData.setZ(ii + 3);
			gforceData.setSampleDateInMillis(ii + 4);
			gforceBuffer.logGForce(gforceData);
		}
		File testFile = new File(filesDir, "gforce.obj");
		assertTrue(testFile.exists());
		ObjectInputStream objectInputSteam=new ObjectInputStream(new FileInputStream(testFile));
		Object object=objectInputSteam.readObject();
		GForceData gForceData=(GForceData)object;
		assertNotNull(gForceData);
		assertTrue(51f==gForceData.getX());
		assertTrue(52f==gForceData.getY());
		assertTrue(53f==gForceData.getZ());
		assertEquals(54,gForceData.getSampleDateInMillis());
		}catch(Exception ex){
			fail("test failed because "+ex.getMessage());
		}
	}
}
