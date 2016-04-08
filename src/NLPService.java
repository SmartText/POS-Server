import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.io.StringReader;
import javax.json.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers ;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class NLPService {
  /*
    Pass model name as argument with complete path, http://nlp.stanford.edu/software/lex-parser.shtml
    PCFG Parser: edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz
    RNN  Parser: edu/stanford/nlp/models/lexparser/englishRNN.ser.gz
  */
  static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
  static TreebankLanguagePack tlp = lp.treebankLanguagePack();
  static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

  public static void main(String[] args) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/parse", new Handler());
      server.setExecutor(null);
      server.start();
    } catch(java.io.IOException SocketError) {
      System.out.println(SocketError);
      return;
    }
  }

  static class Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {

      // Get post data from http request to parse sentence
      InputStream in    = t.getRequestBody();
      StringBuilder sb  = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      // Extract headers from exchange and add content-type
      Headers headers = t.getResponseHeaders();
      headers.add("Content-Type", "application/json");
      t.sendResponseHeaders(200, 0);
      OutputStream os = t.getResponseBody();

      for (List<HasWord> sentence : new DocumentPreprocessor(br)) {
        Tree parse = lp.apply(sentence);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        Collection tdl = gs.typedDependenciesCCprocessed();
        String dep     = tdl.toString();

        JsonArrayBuilder tokens_ab = Json.createArrayBuilder();
        ListIterator<HasWord> litr = sentence.listIterator();
        int wordPos = 1;
        while(litr.hasNext()) {
          HasWord word = litr.next();

          JsonObject wordToken = Json.createObjectBuilder()
              .add("word", word.word())
              .add("pos", wordPos)
            .build();
          wordPos += 1;
          tokens_ab.add(wordToken);
        }
        JsonObject sentence_ab = Json.createObjectBuilder()
            .add("tokens", tokens_ab)
            .add("dep", dep)
            .add("tree", parse.toString())
          .build();
        // Write processed sentence as soon as available to create a streaming service
        os.write((sentence_ab.toString()+"\n").getBytes());
      }

      os.close();
    }
  }
  private NLPService() {} // static methods only
}
