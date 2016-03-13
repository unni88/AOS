#!/bin/bash
# My first script
cd ClientProgramUnnikrishnan
ant
ant createjar
cd ..
cd ServerProgramUnnikrishnan
ant
ant createjar