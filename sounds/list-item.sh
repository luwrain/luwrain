#!/bin/bash -e

sox -D -n -b 32 -r 44100 -c 2 01.wav \
    synth 1 sin %-4 synth 1 sin fmod F3 \
    fade l 0 0.5 0.5

sox -D -n -b 32 -r 44100 -c 2 02.wav \
    synth 1 sin %-1 synth 1 sin fmod %-13 \
        fade l 0 0.5 0.5 pad 0.04

sox -D -n -b 32 -r 44100 -c 2 03.wav \
    synth 1 sin %-4 synth 1 sin fmod F3 \
        fade l 0 0.5 0.5 pad 0.08 1

sox 01.wav 02.wav 03.wav -m 04.wav
sox --norm=-1 04.wav 05.wav reverb 50 100 100 100 5



