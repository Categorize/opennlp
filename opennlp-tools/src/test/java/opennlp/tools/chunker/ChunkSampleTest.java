/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.chunker;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

import org.junit.Test;

public class ChunkSampleTest {

  @Test(expected=IllegalArgumentException.class)
  public void testParameterValidation() {
    new ChunkSample(new String[]{""}, new String[]{""},
        new String[]{"test", "one element to much"});
  }
  
  private String[] createSentence() {
    return new String[] {
        "Forecasts",
        "for",
        "the",
        "trade",
        "figures",
        "range",
        "widely",
        "."
    };
  }
  
  private String[] createTags() {
    
    return new String[]{
        "NNS",
        "IN",
        "DT",
        "NN",
        "NNS",
        "VBP",
        "RB",
        "."
    };
  }
  
  private String[] createChunks() {
    return new String[]{
        "B-NP",
        "B-PP",
        "B-NP",
        "I-NP",
        "I-NP",
        "B-VP",
        "B-ADVP"
        ,"O"
    };
  }
  
  @Test
  public void testRetrievingContent() {
    ChunkSample sample = new ChunkSample(createSentence(), createTags(), createChunks());
    
    assertArrayEquals(createSentence(), sample.getSentence());
    assertArrayEquals(createTags(), sample.getTags());
    assertArrayEquals(createChunks(), sample.getPreds());
  }
  
  @Test
  public void testToString() throws IOException {
    
    ChunkSample sample = new ChunkSample(createSentence(), createTags(), createChunks());
    String[] sentence = createSentence();
    String[] tags = createTags();
    String[] chunks = createChunks();
    
    StringReader sr = new StringReader(sample.toString());
    BufferedReader reader = new BufferedReader(sr);
    for (int i = 0; i < sentence.length; i++) {
    	String line = reader.readLine();
    	String[] parts = line.split("\\s+");
    	assertEquals(3, parts.length);
    	assertEquals(sentence[i], parts[0]);
    	assertEquals(tags[i], parts[1]);
    	assertEquals(chunks[i], parts[2]);
		}
  }
  
  @Test
  public void testNicePrint() {
    
    ChunkSample sample = new ChunkSample(createSentence(), createTags(), createChunks());
    
    assertEquals(" [NP Forecasts_NNS ] [PP for_IN ] [NP the_DT trade_NN figures_NNS ] " +
    		"[VP range_VBP ] [ADVP widely_RB ] ._.", sample.nicePrint());
  }
	
  @Test
  public void testAsSpan() {
	ChunkSample sample = new ChunkSample(createSentence(), createTags(),
			createChunks());
	Span[] spans = sample.getPhrasesAsSpanList();

	assertEquals(5, spans.length);
	assertEquals(new Span(0, 1, "NP"), spans[0]);
	assertEquals(new Span(1, 2, "PP"), spans[1]);
	assertEquals(new Span(2, 5, "NP"), spans[2]);
	assertEquals(new Span(5, 6, "VP"), spans[3]);
	assertEquals(new Span(6, 7, "ADVP"), spans[4]);
  }

  @Test
  public void testRegions() throws IOException {
	InputStream in = getClass().getClassLoader()
			.getResourceAsStream("opennlp/tools/chunker/output.txt");

	String encoding = "UTF-8";

	DummyChunkSampleStream predictedSample = new DummyChunkSampleStream(
			new PlainTextByLineStream(new InputStreamReader(in,
					encoding)), false);

	ChunkSample cs1 = predictedSample.read();
	String[] g1 = Span.spansToStrings(cs1.getPhrasesAsSpanList(), cs1.getSentence());
	assertEquals(15, g1.length);
	
	ChunkSample cs2 = predictedSample.read();
	String[] g2 = Span.spansToStrings(cs2.getPhrasesAsSpanList(), cs2.getSentence());
	assertEquals(10, g2.length);
	
	ChunkSample cs3 = predictedSample.read();
	String[] g3 = Span.spansToStrings(cs3.getPhrasesAsSpanList(), cs3.getSentence());
	assertEquals(7, g3.length);
	assertEquals("United", g3[0]);
	assertEquals("'s directors", g3[1]);
	assertEquals("voted", g3[2]);
	assertEquals("themselves", g3[3]);
	assertEquals("their spouses", g3[4]);
	assertEquals("lifetime access", g3[5]);
	assertEquals("to", g3[6]);
	}
}
