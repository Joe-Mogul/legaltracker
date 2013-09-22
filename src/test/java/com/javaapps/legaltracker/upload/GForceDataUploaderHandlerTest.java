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

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.upload.FileResultMapsWrapper;
import com.javaapps.legaltracker.upload.GForceDataUploaderHandler;
import com.javaapps.legaltracker.utils.MockHttpClientFactory;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GForceDataUploaderHandlerTest {

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
			fail("GForceDataUploaderHandlerTest setup failed because "
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



	private void createGForceObjectFile(FileResult fileResult,int numberOfSamples,int timeDelta)
			throws FileNotFoundException, IOException {
		if (fileResult.file.exists()) {
			fileResult.file.delete();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				fileResult.file));
		for (int ii = 0; ii < numberOfSamples; ii=ii+timeDelta) {
			GForceData gforceData = new GForceData(1,2,3,systemTimeInMillis+ii);
			oos.writeObject(gforceData);
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
		createGForceObjectFile(fileResult,100,1);
		GForceDataUploaderHandler gforceDataUploaderHandler = new GForceDataUploaderHandler(
				testFileDir, "unittest");
		gforceDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 400 }, "URL not found"));
		Config.getInstance().setUploadBatchSize(13);
		gforceDataUploaderHandler.uploadData();
		Map<Integer, Integer> resultMap = FileResultMapsWrapper.getInstance()
				.getFileResultMaps().get(fileResult.file.getAbsolutePath())
				.getResultMap();
		assertTrue("expecting 8 but was " + resultMap.size(),
				resultMap.size() == 8);
		Config.getInstance().setUploadBatchSize(10);
		gforceDataUploaderHandler.uploadData();
		assertTrue("expecting 10 but was " + resultMap.size(),
				resultMap.size() == 10);
	}

	@Test
	public void uploadDataResultMapWithBadStatusTest()
			throws ClientProtocolException, IOException {
		FileResult fileResult = new FileResult("unittest", 400,false);
		createGForceObjectFile(fileResult,100,1);
		GForceDataUploaderHandler gforceDataUploaderHandler = new GForceDataUploaderHandler(
				testFileDir, "unittest");
		gforceDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 400 }, "URL not found"));
		Config.getInstance().setUploadBatchSize(10);
		gforceDataUploaderHandler.uploadData();
		testResults(fileResult);
	}

	@Test
	public void uploadDataResultMapWithBadStatusThanGoodStatsTest()
			throws ClientProtocolException, IOException {
		List<FileResult> fileResultList = new ArrayList<FileResult>();
		fileResultList.add(new FileResult("unittest1", 400,true));
		fileResultList.add(new FileResult("unittest2", 200,true));
		GForceDataUploaderHandler gforceDataUploaderHandler = new GForceDataUploaderHandler(
				testFileDir, "unittest");
		for (FileResult fileResult : fileResultList) {
			createGForceObjectFile(fileResult,100,1);
		}

		for (FileResult fileResult : fileResultList) {
			gforceDataUploaderHandler
					.setHttpClientFactory(new MockHttpClientFactory(
							protocolVersion, new int[] { fileResult.result },
							"URL not found"));
			Config.getInstance().setUploadBatchSize(10);
			gforceDataUploaderHandler.uploadData();
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
		createGForceObjectFile(fileResult,100,1);
		GForceDataUploaderHandler gforceDataUploaderHandler = new GForceDataUploaderHandler(
				testFileDir, "unittest");
		gforceDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 200 }, "OK"));
		Config.getInstance().setUploadBatchSize(10);
		gforceDataUploaderHandler.uploadData();
		testResults(fileResult);
	}

	@Test
	public void uploadDataWithGForceDataTest()
			throws ClientProtocolException, IOException {
		FileResult fileResult = new FileResult("unittestLocation", 200,true);
		createGForceObjectFile(fileResult,100,1);
		GForceDataUploaderHandler gforceDataUploaderHandler = new GForceDataUploaderHandler(
				testFileDir, "unittest");
		gforceDataUploaderHandler
				.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { 200 }, "OK"));
		Config.getInstance().setUploadBatchSize(10);
		gforceDataUploaderHandler.uploadData();
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
			file = new File(testFileDir.getPath() + "/" + fileName + ".obj");
		}
	
	}
}
