all: src/NLPService.class

stanford-parser-full-2015-12-09.zip:
	wget http://nlp.stanford.edu/software/stanford-parser-full-2015-12-09.zip && unzip stanford-parser-full-2015-12-09.zip
	rm -rf stanford-parser-full-2015-12-09/*src*
	rm -rf stanford-parser-full-2015-12-09/*.java
	rm -rf stanford-parser-full-2015-12-09/*.txt
	rm -rf stanford-parser-full-2015-12-09/*.pdf
	rm -rf stanford-parser-full-2015-12-09/Makefile
	rm -rf stanford-parser-full-2015-12-09/*.sh
	rm -rf stanford-parser-full-2015-12-09/*.bat
	rm -rf stanford-parser-full-2015-12-09/*.def
	rm -rf stanford-parser-full-2015-12-09/*javadoc.jar
	rm -rf stanford-parser-full-2015-12-09/*sources.jar
	rm -rf stanford-parser-full-2015-12-09/*.xml
	rm -rf stanford-parser-full-2015-12-09/{bin,conf,data}
	rm -rf stanford-parser-full-2015-12-09.zip

src/NLPService.class: stanford-parser-full-2015-12-09.zip
	javac -cp stanford-parser-full-2015-12-09/*:javax/* src/NLPService.java

clean:
	rm -rf src/NLPService*.class
