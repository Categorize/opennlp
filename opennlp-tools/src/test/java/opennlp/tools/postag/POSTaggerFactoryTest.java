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

package opennlp.tools.postag;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import opennlp.tools.postag.DummyPOSTaggerFactory.DummyPOSContextGenerator;
import opennlp.tools.postag.DummyPOSTaggerFactory.DummyPOSDictionary;
import opennlp.tools.postag.DummyPOSTaggerFactory.DummyPOSSequenceValidator;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelType;

import org.junit.Test;

/**
 * Tests for the {@link POSTaggerFactory} class.
 */
public class POSTaggerFactoryTest {

  private static ObjectStream<POSSample> createSampleStream()
      throws IOException {
    InputStream in = POSTaggerFactoryTest.class.getClassLoader()
        .getResourceAsStream("opennlp/tools/postag/AnnotatedSentences.txt");

    return new WordTagSampleStream((new InputStreamReader(in)));
  }

  static POSModel trainPOSModel(ModelType type, POSTaggerFactory factory)
      throws IOException {
    return POSTaggerME.train("en", createSampleStream(),
        TrainingParameters.defaultParams(), factory, null, null);
  }

  @Test
  public void testPOSTaggerWithCustomFactory() throws IOException {
    DummyPOSDictionary posDict = new DummyPOSDictionary(
        POSDictionary.create(POSDictionaryTest.class
            .getResourceAsStream("TagDictionaryCaseSensitive.xml")));

    POSModel posModel = trainPOSModel(ModelType.MAXENT,
        new DummyPOSTaggerFactory(null, posDict));

    POSTaggerFactory factory = posModel.getFactory();
    assertTrue(factory.getPOSDictionary() instanceof DummyPOSDictionary);
    assertTrue(factory.getPOSContextGenerator() instanceof DummyPOSContextGenerator);
    assertTrue(factory.getSequenceValidator() instanceof DummyPOSSequenceValidator);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    posModel.serialize(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

    POSModel fromSerialized = new POSModel(in);

    factory = fromSerialized.getFactory();
    assertTrue(factory.getPOSDictionary() instanceof DummyPOSDictionary);
    assertTrue(factory.getPOSContextGenerator() instanceof DummyPOSContextGenerator);
    assertTrue(factory.getSequenceValidator() instanceof DummyPOSSequenceValidator);
  }

  @Test
  public void testBuildNGramDictionary() throws IOException {
    ObjectStream<POSSample> samples = createSampleStream();

    POSTaggerME.buildNGramDictionary(samples, 0);
  }
}