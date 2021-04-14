#!/usr/bin/env bash

chmod a+x checkincheck.sh

sbt clean test

cd ../merchandise-in-baggage || exit

printf "#####################\ncd to $PWD for contract verifier tests... \n#####################\n"

export PACTTEST="../merchandise-in-baggage-internal-frontend/pact/"

sbt clean "testOnly *VerifyContractSpec;"


cd ../merchandise-in-baggage-internal-frontend || exit
