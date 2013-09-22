package com.javaapps.legaltracker.pojos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GForceDataTest {

	private GForceData gforceData;
	
	@Before 
	public void setup()
	{
		gforceData=new GForceData();
	}
	
	@Test
	public void test(){
		GForceData gforceData=new GForceData();
		gforceData.setX(10);
		gforceData.setY(20);
		gforceData.setZ(30);
		gforceData.setSampleDateInMillis(40);
		GForceData averageGforceData=gforceData.averageData(null);
		assertNull(averageGforceData);
			
		GForceData gforceData2=new GForceData();
		gforceData2.setX(50);
		gforceData2.setY(70);
		gforceData2.setZ(80);
		gforceData2.setSampleDateInMillis(90);
		List<GForceData>gforceDataList=new ArrayList<GForceData>();
		gforceDataList.add(gforceData);
		gforceDataList.add(gforceData2);
		averageGforceData=GForceData.averageData(gforceDataList);
		assertTrue(30f==averageGforceData.getX());
		assertTrue(45f==averageGforceData.getY());
		assertTrue(55f==averageGforceData.getZ());
		assertEquals(65,averageGforceData.getSampleDateInMillis());

	}
}
