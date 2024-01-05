#!/bin/bash -e

sox -D -n -b 32 -c 2 -r 44100 01.wav \
    synth 1 sin %-1 sin %2 sin %5 sin fmod %-1  gain -3 \
    fade l 0.1 0.2 0.1 pad 0 1
sox -D 01.wav 02.wav \
    reverb 50 10 50 30 100 1
sox -D --norm=-0.5 02.wav -b 16 -c 2 -r 44100 inaccessible.wav
rm -f 0?.wav

