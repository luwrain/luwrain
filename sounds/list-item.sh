#!/bin/bash -e

sox -D -n -b 32 -r 44100 -c 2 01.wav \
    synth 1 sin %-1 synth 1 sin fmod %-13 \
    fade l 0 0.5 0.5

sox -D -n -b 32 -r 44100 -c 2 02.wav \
    synth 1 sin %-4 synth 1 sin fmod %-16 \
        fade l 0 0.5 0.5 pad 0.04

sox -D -n -b 32 -r 44100 -c 2 03.wav \
    synth 1 sin %-1 synth 1 sin fmod %-13 \
        fade l 0 0.5 0.5 pad 0.08 1

sox -D 01.wav 02.wav 03.wav -m 04.wav
sox -D 04.wav 05.wav pad 0 1
sox 05.wav 06.wav reverb 65 50 50 100 100 -10
sox -D --norm=-0.5 06.wav list-item.wav
rm -f 0?.wav
