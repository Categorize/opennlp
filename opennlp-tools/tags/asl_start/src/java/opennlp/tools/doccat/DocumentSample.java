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


package opennlp.tools.doccat;

import opennlp.tools.tokenize.SimpleTokenizer;

/**
 * Class which holds a classified document and its category. 
 */
public class DocumentSample {
  
  private String category;
  private String text[];
  
  public DocumentSample(String category, String text) {
    this(category, new SimpleTokenizer().tokenize(text));
  }
  
  public DocumentSample(String category, String text[]) {
    if (category == null || text == null) {
      throw new IllegalArgumentException();
    }
    
    this.category = category;
    this.text = text;
  }
  
  String getCategory() {
    return category;
  }
  
  String[] getText() {
    return text;
  }
}
