package com.demo.opennlp.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

@Component
public class ChatBean implements InitializingBean {
	
	private  Map<String, String> chatResponse = new HashMap();
	private DoccatModel model;
	
	private  void trainCategorizerModel() throws FileNotFoundException, IOException, URISyntaxException {
		// train.txt is a custom training data with categories
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(this.getClass().getClassLoader().getResource("train.txt").toURI()  ));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
	 
		DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });
	 
		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.CUTOFF_PARAM, 0);
	 
		// Train a model with classifications from above file.
		model = DocumentCategorizerME.train("en", sampleStream, params, factory);
		 
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
        // Your code here
		chatResponse.put("greeting", "Hello, how can I help you?");
		chatResponse.put("places","There are several lakes in your locality, for example Lake Lavon, Lake Lexie, Lake Newsteen");
		chatResponse.put("species", "In all of these lakes you can get Bass, Crappie, Salmon and Sunfish");
		chatResponse.put("times", "The best times are early morning and early evening.");
		chatResponse.put("continue", "How else may I help you?");
		chatResponse.put("complete", "Thank you for using our service.");
		
		trainCategorizerModel();
		
    }
	
	public DoccatModel getModel() {
		return model;
	}

	public Map<String, String> getQuestionAnswer() {
		return chatResponse;
	}
	
	

}
