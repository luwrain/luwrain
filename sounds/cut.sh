#!/bin/bash -e
# Copyright 2024 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, GPL v.3

sox -D -n -r 48000 -c 2 -b 32 01-1.wav \
    synth 5 sin B4 sin B5 synth 5 sin fmod B5 fade l 0 1 1 gain -1 trim 0 0.1
sox -D -n -r 48000 -c 2 -b 32 01-2.wav \
    synth 5 sin B2 synth 5 sin fmod B1 fade l 0 1 1 gain -10 trim 0 0.1
sox -D 01-1.wav 01-2.wav -m 01.wav

sox -D -n -r 48000 -c 2 -b 32 02-1.wav \
    synth 5 sin E4 0 sin E5 synth 5 sin fmod E5 fade l 0 1 1 gain -1 trim 0 1
sox -D -n -r 48000 -c 2 -b 32 02-2.wav \
    synth 5 pluck E5 synth 5 sin fmod E4 gain -25 fade t 0 1 1 trim 0 1
sox -D 02-1.wav 02-2.wav -m 02.wav

sox -D 01.wav 02.wav 03.wav bass 5
sox -D 03.wav 04.wav pad 0 1
sox 04.wav 05.wav REVERB 50 100 100 35 35
sox --norm=-0.5 05.wav -c 2 -b 16 -r 44100 cut.wav
rm -f 0?.wav 0?-?.wav
