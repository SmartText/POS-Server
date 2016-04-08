all: src/NLPService.class

stanford-parser-full-2015-12-09.zip:
	wget http://nlp.stanford.edu/software/stanford-parser-full-2015-12-09.zip && unzip stanford-parser-full-2015-12-09.zip

src/NLPService.class: stanford-parser-full-2015-12-09.zip
	javac -cp stanford-parser-full-2015-12-09/*:javax/* src/NLPService.java

clean:
	rm -rf src/NLPService*.class
