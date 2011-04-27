#!/bin/sh

cd $ALGOTRADER_HOME

mvn -o -q -f bin/pom.xml \
dependency:build-classpath \
-Dmdep.outputFile=cp.txt

nohup java \
-cp `cat bin/cp.txt` \
-Dsimulation=true \
-DdataSource.dataSet=1year \
com.algoTrader.starter.SimulationStarter \
simulateWithCurrentParams
