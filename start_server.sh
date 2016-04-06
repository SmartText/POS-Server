#!/bin/bash

java -cp src:stanford-parser-full-2015-12-09/*:javax/*: NLPService edu/stanford/nlp/models/lexparser/englishRNN.ser.gz
