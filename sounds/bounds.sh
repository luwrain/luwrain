#!/bin/bash -e
# Copyright 2024 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, GPL v.3

sox -D -n -b 32 -c 2 -r 44100 01.wav \
    synth 1 pl %-2 sin %10 sin %13 sin %16 sin fmod %-2-%-26 gain -3 \
    fade t 0 0.2 0.1 treble -10 pad 0 2
sox -D 01.wav 02.wav \
    reverb 85 100 35 50 50
sox -D --norm=-0.5 02.wav -b 16 -c 2 -r 44100 bounds.wav
rm -f 0?.wav
