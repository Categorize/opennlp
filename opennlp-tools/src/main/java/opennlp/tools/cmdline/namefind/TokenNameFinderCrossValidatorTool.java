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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import opennlp.tools.cmdline.ArgumentParser;
import opennlp.tools.cmdline.CLI;
import opennlp.tools.cmdline.CVParams;
import opennlp.tools.cmdline.CmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderCrossValidator;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.MissclassifiedSampleListener;

public final class TokenNameFinderCrossValidatorTool implements CmdLineTool {
  
  interface CVToolParams extends TrainingParams, CVParams{
    
  }

  public String getName() {
    return "TokenNameFinderCrossValidator";
  }

  public String getShortDescription() {
    return "K-fold cross validator for the learnable Name Finder";
  }

  public String getHelp() {
    return "Usage: " + CLI.CMD + " " + getName() + " "
        + ArgumentParser.createUsage(CVToolParams.class);
  }

  public void run(String[] args) {
    if (!ArgumentParser.validateArguments(args, CVToolParams.class)) {
      System.err.println(getHelp());
      throw new TerminateToolException(1);
    }
    
    CVToolParams params = ArgumentParser.parse(args, CVToolParams.class);

    opennlp.tools.util.TrainingParameters mlParams = CmdLineUtil
        .loadTrainingParameters(params.getParams(),false);

    byte featureGeneratorBytes[] = TokenNameFinderTrainerTool
        .openFeatureGeneratorBytes(params.getFeaturegen());

    Map<String, Object> resources = TokenNameFinderTrainerTool
        .loadResources(params.getResources());

    File trainingDataInFile = params.getData();
    CmdLineUtil.checkInputFile("Training Data", trainingDataInFile);
    
    Charset encoding = params.getEncoding();

    ObjectStream<NameSample> sampleStream = TokenNameFinderTrainerTool
        .openSampleData("Training Data", trainingDataInFile, encoding);

    TokenNameFinderCrossValidator validator;
    
    MissclassifiedSampleListener<NameSample> errorListener = null;
    if (params.getMisclassified()) {
      errorListener = new NameEvaluationErrorListener();
    }

    try {
      if (mlParams == null) {
        validator = new TokenNameFinderCrossValidator(params.getLang(), params.getType(),
             featureGeneratorBytes, resources, params.getIterations(),
            params.getCutoff());
      } else {
        validator = new TokenNameFinderCrossValidator(params.getLang(), params.getType(), mlParams,
            featureGeneratorBytes, resources);
      }
      validator.evaluate(sampleStream, params.getFolds(), errorListener);
    } catch (IOException e) {
      CmdLineUtil.printTrainingIoError(e);
      throw new TerminateToolException(-1);
    } finally {
      try {
        sampleStream.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

    System.out.println("done");

    System.out.println();

    System.out.println(validator.getFMeasure());
  }
}
