#!/usr/bin/env bash

chmod a+x checkincheck.sh

clean_build=1

while test $# -gt 0; do
  case "$1" in
    -h|--help)
      echo "options:"
      echo "-h, --help       show this help"
      echo "-f, --fast       skip clean  build"
      exit 0
      ;;
    -f|--fast)
      clean_build=0
      echo "fast build"
      shift
      ;;
     esac
done

if clean_build; then
  sbt clean test
fi

cd ../merchandise-in-baggage || exit

printf "#####################\ncd to $PWD for contract verifier tests... \n#####################\n"

export PACTTEST="../merchandise-in-baggage-internal-frontend/pact/"

if clean_build; then
  sbt clean test
fi

sbt "testOnly *VerifyContractSpec;"


cd ../merchandise-in-baggage-internal-frontend || exit
