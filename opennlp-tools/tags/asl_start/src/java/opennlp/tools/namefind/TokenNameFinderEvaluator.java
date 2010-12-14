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


package opennlp.tools.namefind;

import opennlp.tools.util.FMeasureEvaluator;
import opennlp.tools.util.Span;

/**
 * The {@link TokenNameFinderEvaluator} measures the performance
 * of the given {@link TokenNameFinder} with the provided 
 * reference {@link NameSample}s.
 * 
 * @see FMeasureEvaluator
 * @see TokenNameFinder
 * @see NameSample
 */
public class TokenNameFinderEvaluator extends FMeasureEvaluator<NameSample> {
  
  /**
   * The {@link TokenNameFinder} used to create the predicted
   * {@link NameSample} objects.
   */
  private TokenNameFinder nameFinder;
  
  /**
   * Initializes the current instance with the given 
   * {@link TokenNameFinder}. 
   * 
   * @param nameFinder the {@link TokenNameFinder} to evaluate.
   */
  public TokenNameFinderEvaluator(TokenNameFinder nameFinder) {
    this.nameFinder = nameFinder;
  }
  
  /**
   * Evaluates the given reference {@link NameSample} object.
   * 
   * This is done by finding the names with the 
   * {@link TokenNameFinder} in the sentence from the reference 
   * {@link NameSample}. The found names are then used to
   * calculate and update the scores.
   * 
   * @param reference the reference {@link NameSample}.
   */
  public void evaluateSample(NameSample reference) {
    
    Span predictedNames[] = nameFinder.find(reference.getSentence());
    
    if (predictedNames.length > 0) {
      precisionScore.add(FMeasureEvaluator.precision(reference.getNames(), 
          predictedNames));
    }
    
    if (reference.getNames().length > 0) {
      recallScore.add(FMeasureEvaluator.recall(reference.getNames(), 
          predictedNames));
    }
  }
}