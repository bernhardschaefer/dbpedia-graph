package de.unima.dws.dbpediagraph.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Non-exhaustive test for the default model functionality.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestDefaultModel {

	private static DefaultSense sense1 = new DefaultSense("http://dbpedia.org/resource/Bank");
	private static DefaultSense sense1_2 = new DefaultSense("http://dbpedia.org/resource/Bank");
	private static DefaultSense sense2 = new DefaultSense("http://dbpedia.org/resource/Table");

	private static DefaultSurfaceForm surfaceForm1 = new DefaultSurfaceForm("Bank");
	private static DefaultSurfaceForm surfaceForm1_2 = new DefaultSurfaceForm("Bank");
	private static DefaultSurfaceForm surfaceForm2 = new DefaultSurfaceForm("Tisch");

	private static double score1 = 0.5;
	private static double score1_2 = 0.5;
	private static double score2 = 0.0;

	private static SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfs1 = new SurfaceFormSenseScore<>(
			surfaceForm1, sense1, score1);
	private static SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfs1_2 = new SurfaceFormSenseScore<>(
			surfaceForm1_2, sense1_2, score1_2);
	private static SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfs2 = new SurfaceFormSenseScore<>(
			surfaceForm2, sense2, score2);
	private static SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfs121 = new SurfaceFormSenseScore<>(
			surfaceForm1, sense2, score1);

	@Test
	public void testHashCodes() {
		assertEquals(sense1.hashCode(), sense1.hashCode());
		assertEquals(sense1.hashCode(), sense1_2.hashCode());
		assertEquals(surfaceForm1.hashCode(), surfaceForm1_2.hashCode());
		assertEquals(sfs1.hashCode(), sfs1_2.hashCode());

		assertNotEquals(sfs1.hashCode(), sfs121.hashCode());
		assertNotEquals(sfs2.hashCode(), sfs121.hashCode());
	}

	@Test
	public void testSenseEquality() {
		assertEquals(sense1, sense1);
		assertEquals(sense1, sense1_2);
		assertEquals(sense2, sense2);
		assertNotEquals(sense1, sense2);
		assertNotEquals(sense2, sense1);
		assertNotEquals(sense1, null);
		assertNotEquals(sense2, null);
	}

	@Test
	public void testSurfaceFormEquality() {
		assertEquals(surfaceForm1, surfaceForm1);
		assertEquals(surfaceForm1, surfaceForm1_2);
		assertEquals(surfaceForm2, surfaceForm2);
		assertNotEquals(surfaceForm1, surfaceForm2);
		assertNotEquals(surfaceForm2, surfaceForm1);
		assertNotEquals(surfaceForm1, null);
		assertNotEquals(surfaceForm2, null);
	}

	@Test
	public void testSurfaceFormSenseScoreEquality() {
		assertEquals(sfs1, sfs1);
		assertEquals(sfs1, sfs1_2);
		assertEquals(sfs2, sfs2);
		assertNotEquals(sfs1, sfs2);
		assertNotEquals(sfs2, sfs1);
		assertNotEquals(sfs1, sfs121);
		assertNotEquals(sfs2, sfs121);
		assertNotEquals(sfs1, null);
		assertNotEquals(sfs2, null);
	}

}
