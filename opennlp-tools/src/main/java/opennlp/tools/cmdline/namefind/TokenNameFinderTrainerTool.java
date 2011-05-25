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

package opennlp.tools.cmdline.namefind;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import opennlp.model.TrainUtil;
import opennlp.tools.cmdline.CLI;
import opennlp.tools.cmdline.CmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.ModelUtil;

public final class TokenNameFinderTrainerTool implements CmdLineTool {

  public String getName() {
    return "TokenNameFinderTrainer";
  }
  
  public String getShortDescription() {
    return "trainer for the learnable name finder";
  }
  
  public String getHelp() {
    return "Usage: " + CLI.CMD + " " + getName() + " " + 
        TrainingParameters.getParameterUsage() + " -data trainingData -model model\n" +
        TrainingParameters.getDescription();
  }

  static ObjectStream<NameSample> openSampleData(String sampleDataName,
      File sampleDataFile, Charset encoding) {
    CmdLineUtil.checkInputFile(sampleDataName + " Data", sampleDataFile);

    FileInputStream sampleDataIn = CmdLineUtil.openInFile(sampleDataFile);

    ObjectStream<String> lineStream = new PlainTextByLineStream(sampleDataIn
        .getChannel(), encoding);

    return new NameSampleDataStream(lineStream);
  }
  
  public void run(String[] args) {
    
    if (args.length < 8) {
      System.out.println(getHelp());
      throw new TerminateToolException(1);
    }
    
    TrainingParameters parameters = new TrainingParameters(args);
    
    if(!parameters.isValid()) {
      System.out.println(getHelp());
      throw new TerminateToolException(1);
    }
    
    opennlp.tools.util.TrainingParameters mlParams = 
      CmdLineUtil.loadTrainingParameters(CmdLineUtil.getParameter("-params", args), true);
    
    File trainingDataInFile = new File(CmdLineUtil.getParameter("-data", args));
    File modelOutFile = new File(CmdLineUtil.getParameter("-model", args));
    
    
    byte featureGeneratorBytes[] = null;
    
    // load descriptor file into memory
    if (parameters.getFeatureGenDescriptorFile() != null) {
      InputStream bytesIn = 
          CmdLineUtil.openInFile(new File(parameters.getFeatureGenDescriptorFile()));
      
      try {
        featureGeneratorBytes = ModelUtil.read(bytesIn);
      } catch (IOException e) {
        CmdLineUtil.printTrainingIoError(e);
        throw new TerminateToolException(-1);
      }
      finally {
        try {
          bytesIn.close();
        } catch (IOException e) {
          // sorry that this can fail
        }
      }
    }
    
    // TODO: Support Custom resources: 
    //       Must be loaded into memory, or written to tmp file until descriptor 
    //       is loaded which defines parses when model is loaded
    
    String resourceDirectory = parameters.getResourceDirectory();
    
    Map<String, Object> resources = new HashMap<String, Object>();
    
    if (resourceDirectory != null) {
      
      Map<String, ArtifactSerializer> artifactSerializers = 
          TokenNameFinderModel.createArtifactSerializers();
      
      File resourcePath = new File(resourceDirectory);
      
      File resourceFiles[] = resourcePath.listFiles();
      
      // TODO: Filter files, also files with start with a dot
      for (File resourceFile : resourceFiles) {
        
        // TODO: Move extension extracting code to method and
        //       write unit test for it
        
        // extract file ending
        String resourceName = resourceFile.getName();
        
        int lastDot = resourceName.lastIndexOf('.');
        
        if (lastDot == -1) {
          continue;
        }
        
        String ending = resourceName.substring(lastDot + 1);
        
        // lookup serializer from map
        ArtifactSerializer serializer = artifactSerializers.get(ending);
        
        // TODO: Do different? For now just ignore ....
        if (serializer == null)
          continue;
        
        InputStream resoruceIn = CmdLineUtil.openInFile(resourceFile);
        
        try {
          resources.put(resourceName, serializer.create(resoruceIn));
        }
        catch (InvalidFormatException e) {
          // TODO: Fix exception handling
          e.printStackTrace();
        }
        catch (IOException e) {
          // TODO: Fix exception handling
          e.printStackTrace();
        }
        finally {
          try {
            resoruceIn.close();
          }
          catch (IOException e) {
          }
        }
      }
    }
    
    CmdLineUtil.checkOutputFile("name finder model", modelOutFile);
    ObjectStream<NameSample> sampleStream = openSampleData("Training", trainingDataInFile,
        parameters.getEncoding());

    TokenNameFinderModel model;
    try {
      if (mlParams == null) {
      model = opennlp.tools.namefind.NameFinderME.train(parameters.getLanguage(), parameters.getType(),
           sampleStream, featureGeneratorBytes, resources, parameters.getNumberOfIterations(),
           parameters.getCutoff());
      }
      else {
        model = opennlp.tools.namefind.NameFinderME.train(parameters.getLanguage(), parameters.getType(), sampleStream, mlParams, null,
            Collections.<String, Object>emptyMap());
      }
    } 
    catch (IOException e) {
      CmdLineUtil.printTrainingIoError(e);
      throw new TerminateToolException(-1);
    }
    finally {
      try {
        sampleStream.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }
    
    CmdLineUtil.writeModel("name finder", modelOutFile, model);
  }
}
