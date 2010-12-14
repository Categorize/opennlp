/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreemnets.  See the NOTICE file distributed with
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


package opennlp.tools.sentdetect;

/**
 * A {@link SentenceSample} contains a document with
 * begin indexes of the individual sentences.
 */
public class SentenceSample {

  private String document;
  
  private int sentences[];
  
  /**
   * Initializes the current instance.
   * 
   * @param sentences
   * @param sentenceSpans
   */
  public SentenceSample(String document, int sentences[]) {
    this.document = document;
    this.sentences = sentences;
  }
  
  /**
   * Retrieves the document.
   * 
   * @return
   */
  public String getDocument() {
    return document;
  }
  
  /**
   * Retrieves the sentences.
   * 
   * @return the begin indexes of the sentences 
   * in the document.
   */
  public int[] getSentences() {
    return sentences;
  }
}