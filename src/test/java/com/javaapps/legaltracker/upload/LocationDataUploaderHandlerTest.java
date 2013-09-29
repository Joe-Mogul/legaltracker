package com.javaapps.legaltracker.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.upload.FileResultMapsWrapper;
import com.javaapps.legaltracker.upload.LocationDataUploaderHandler;
import com.javaapps.legaltracker.utils.MockHttpClientFactory;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LocationDataUploaderHandlerTest {

	private List<LegalTrackerLocation> locationDataList = new ArrayList<LegalTrackerLocation>();
	private static File testFileDir = new File("unitTestDir");
	private static long systemTimeInMillis=System.currentTimeMillis();
	private ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 2);

	@BeforeClass
	public static void setupBeforeClass() {
		try {
			if (!testFileDir.exists()) {
				testFileDir.mkdir();
			}

			Config.getInstance().setLocationDataEndpoint(
					"http://boguswebsite.go");
		} catch (Exception ex) {
			fail("LocationDataUploaderHandlerTest setup failed because "
					+ ex.getMessage());
		}
	}
	
	@Before
	public void setupBeforeTest()
	{
		for (File file:testFileDir.listFiles())
		{
			file.delete();
		}
		FileResultMapsWrapper.getInstance().getFileResultMaps().clear();
	}

	private void createLocationObjectFile(FileResult fileResult,int numberOfSamples,int timeDelta)
			throws FileNotFoundException, IOException {
		if (fileResult.file.exists()) {
			fileResult.file.delete();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				fileResult.file));
		for (int ii = 0; ii < numberOfSamples; ii=ii+timeDelta) {
			LegalTrackerLocation location = new LegalTrackerLocation(40.0,
					-80.0, 10.f, 20.0f, 10.0f, systemTimeInMillis+ii);
			oos.writeObject(location);
		}
		oos.flush();
		oos.close();
	}

		@Before
	public void setup() {
		try {
		} catch (Exception ex) {
			fail("Unable to setup test because " + ex.getMessage());
		}
	}

	private void testResults(FileResult fileResult) {
		Map<Integer, Integer> resultMap = FileResultMapsWrapper.getInstance()
				.getFileResultMaps().get(fileResult.file.getAbsolutePath())
				.getResultMap();
		assertTrue("resultMap is empty", resultMap.size() > 0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		System.out.println(resultMap);
		for (Entry<Integer, Integer> entry : resultMap.entrySet()) {
			assertTrue("expecting " + fileResult.result
					+ " status code but was " + entry.getValue(),
					entry.getValue() == fileResult.result);
		}
		if ( fileResult.fileShouldBeDeleted){
			assertFalse(fileResult.file.exists());
		}
	}

	@Test
	public void uploadDataResultMapSizeTest() throws ClientProtocolException,
			IOException {
		FileResult fileResult = new FileResult("unittest", -1,false);
		createLocationObjectFile(fileResult,100,1);
		LocationDataUploaderHandler locationDataUploaderHandler = new LocationDataUploaderHandler(
				testFileDir, "unittest");
		locationDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 400 }, "URL not found"));
		Config.getInstance().setUploadBatchSize(13);
		locationDataUploaderHandler.uploadData();
		Map<Integer, Integer> resultMap = FileResultMapsWrapper.getInstance()
				.getFileResultMaps().get(fileResult.file.getAbsolutePath())
				.getResultMap();
		assertTrue("expecting 8 but was " + resultMap.size(),
				resultMap.size() == 8);
		Config.getInstance().setUploadBatchSize(10);
		locationDataUploaderHandler.uploadData();
		assertTrue("expecting 10 but was " + resultMap.size(),
				resultMap.size() == 10);
	}

	@Test
	public void uploadDataResultMapWithBadStatusTest()
			throws ClientProtocolException, IOException {
		FileResult fileResult = new FileResult("unittest", 400,false);
		createLocationObjectFile(fileResult,100,1);
		LocationDataUploaderHandler locationDataUploaderHandler = new LocationDataUploaderHandler(
				testFileDir, "unittest");
		locationDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 400 }, "URL not found"));
		Config.getInstance().setUploadBatchSize(10);
		locationDataUploaderHandler.uploadData();
		testResults(fileResult);
	}

	@Test
	public void uploadDataResultMapWithBadStatusThanGoodStatsTest()
			throws ClientProtocolException, IOException {
		List<FileResult> fileResultList = new ArrayList<FileResult>();
		fileResultList.add(new FileResult("unittest", 400,true));
		fileResultList.add(new FileResult("unittest", 200,true));
		LocationDataUploaderHandler locationDataUploaderHandler = new LocationDataUploaderHandler(
				testFileDir, "unittest");
		for (FileResult fileResult : fileResultList) {
			createLocationObjectFile(fileResult,100,1);
		}

		for (FileResult fileResult : fileResultList) {
			locationDataUploaderHandler
					.setHttpClientFactory(new MockHttpClientFactory(
							protocolVersion, new int[] { fileResult.result },
							"URL not found"));
			Config.getInstance().setUploadBatchSize(10);
			locationDataUploaderHandler.uploadData();
		}

		for (FileResult fileResult : fileResultList) {
			fileResult.result = 200;
			testResults(fileResult);
		}

	}

	/**
	 * This test must be run last because it deletes the test file
	 */
	@Test
	public void uploadDataResultMapWithGoodStatusLastTest()
			throws ClientProtocolException, IOException {
		FileResult fileResult = new FileResult("unittest", 200,true);
		createLocationObjectFile(fileResult,100,1);
		LocationDataUploaderHandler locationDataUploaderHandler = new LocationDataUploaderHandler(
				testFileDir, "unittest");
		locationDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 200 }, "OK"));
		Config.getInstance().setUploadBatchSize(10);
		locationDataUploaderHandler.uploadData();
		testResults(fileResult);
	}


	class FileResult {
		String fileName;
		int result;
		File file;
		boolean fileShouldBeDeleted;

		public FileResult(String fileName, int result,boolean fileShouldBeDeleted) {
			super();
			this.fileName = fileName;
			this.fileShouldBeDeleted=fileShouldBeDeleted;
			this.result = result;
			file = new File(testFileDir.getPath() + "/" + fileName + LegalTrackerFile.ARCHIVE_STRING+ ".obj");
		}
	
	}
}
