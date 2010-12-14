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


package opennlp.tools.util;

import java.util.Collections;
import java.util.Iterator;

import opennlp.model.Event;
import opennlp.model.EventStream;

/**
 * This is a base class for {@link EventStream} classes.
 * It takes an {@link Iterator} of sample objects as input and
 * outputs the events creates by a subclass. 
 */
public abstract class AbstractEventStream<T> implements EventStream {

  private Iterator<T> samples;
  
  @SuppressWarnings("unchecked")
  private Iterator<Event> events = Collections.EMPTY_LIST.iterator();;
  
  /**
   * Initializes the current instance with a sample {@link Iterator}.
   * 
   * @param samples the sample {@link Iterator}.
   */
  public AbstractEventStream(Iterator<T> samples) {
    this.samples = samples;
  }
  
  /**
   * Creates events for the provided sample.
   * 
   * @param sample the sample for which training {@link Event}s 
   * are be created.
   * 
   * @return an {@link Iterator} of training events or 
   * an empty {@link Iterator}.
   */
  protected abstract Iterator<Event> createEvents(T sample);
  
  /**
   * Checks if there are more training events available.
   * 
   */
  public final boolean hasNext() {
    
    if (events.hasNext()) {
      return true;
    } else {
    
      // search next event iterator which is not empty
      while (samples.hasNext() && !events.hasNext()) {
        events = createEvents(samples.next()); 
      }
      
      return events.hasNext();
    }
  }
  
  public final Event nextEvent() {
    return events.next();
  }
}