package com.demo.opennlp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.opennlp.bean.ChatBean;
import com.demo.opennlp.model.RequestModel;
import com.demo.opennlp.model.ResponseModel;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
 

@RestController
@RequestMapping("/nlp")
public class OpenNLPController {
	
	@Autowired
	ChatBean chatBean;
	
	@PostMapping("/chat")	 
	public ResponseModel chat(@RequestBody RequestModel request) {
		try {
	     
		 String input = request.getInputData();
		 String outputResponse = "";
		 if(input!=null && input.length()>0) {
			 for(String sentence:nlpSentences(input)) {
				 String[] tokens = nlpTokens(sentence);
				 String[] lemmas = nlpLemma(tokens);
				 String category = detectCategory(chatBean.getModel(), lemmas);
				 outputResponse = outputResponse + " " + chatBean.getQuestionAnswer().get(category);				 
			 }
		 }
	     
	     return new ResponseModel(outputResponse);
	     
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseModel("Error. Please try again");
		}
	  }
	
	private String detectCategory(DoccatModel model, String[] lemmas) throws IOException {
		// Initialize document categorizer tool
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);	 
		// Get best possible category.
		double[] probabilitiesOfOutcomes = myCategorizer.categorize(lemmas);
		String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);	 
		return category;
	}

	private String[] nlpLemma(String[] tokens) throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"); 
	    TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(is));  		
	    InputStream posis = this.getClass().getClassLoader().getResourceAsStream("en-pos-maxent.bin"); 
	    POSModel posModel = new POSModel(posis);
	    POSTaggerME pos = new POSTaggerME(posModel);
	    String partsOfSpeech[] = pos.tag(tokens);
	    
	    //Lemmatization
        InputStream lemmais = this.getClass().getClassLoader().getResourceAsStream("en-lemmatizer.dict");         
        DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(lemmais);
        String[] lemmas = lemmatizer.lemmatize(tokens, partsOfSpeech);
        return lemmas;	    
	}
		
	
	private String[] nlpTokens(String sentence) throws IOException {
	    InputStream is = this.getClass().getClassLoader().getResourceAsStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"); 
	    TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(is));	    
		String[] tokens = myCategorizer.tokenize(sentence);
		return tokens; 
	}
		
	 private String[] nlpSentences(String input) throws IOException {
		 InputStream is = this.getClass().getClassLoader().getResourceAsStream("opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin"); 
	     SentenceModel sentModel = new SentenceModel(is);		    
		 SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
		 String sentences[] = sentenceDetector.sentDetect(input);
		 return sentences;
	 }
	
	  public void nlpLanguage() throws IOException {
	    InputStream is = this.getClass().getClassLoader().getResourceAsStream("langdetect-183.bin"); 
	    LanguageDetectorModel langModel = new LanguageDetectorModel(is); 
	    String input = "مرحبا هناك كيف حالك"; 
	    LanguageDetector langDetector = new LanguageDetectorME(langModel); 
	    Language language = langDetector.predictLanguage(input); 

	    System.out.println("Language Detected: " + language.getLang() +" Confidence Score: " + language.getConfidence());

	    Language[] languages = langDetector.predictLanguages(input);
	    System.out.println("Language Possibilities: " + Arrays.toString(languages));
	  }
	  
	  private String[] nlpPOS(String[] tokens) throws IOException {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"); 
		    TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(is));		
		    InputStream posis = this.getClass().getClassLoader().getResourceAsStream("opennlp-en-ud-ewt-pos-1.0-1.9.3.bin"); 
		    POSModel posModel = new POSModel(posis);
		    POSTaggerME pos = new POSTaggerME(posModel);
		    String partsOfSpeech[] = pos.tag(tokens);
		    return partsOfSpeech;
		}

}
