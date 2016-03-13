#!/bin/bash
# My first script
cd ConfigureFile
ant
ant createjar
cd ..
cd Peer
ant
ant createjar