#!/bin/bash -e
# Copyright 2024 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, GPL v.3

sox -D -n -r 48000 -c 2 -b 32 01-1.wav \
    synth 5 sin %7 sin %11 sin %14 sin fmod %7 \
    fade l 0.05 0.125 0.05 gain -1 pad 0 2

sox -D -n -r 48000 -c 2 -b 32 01-2.wav \
    synth 5 sin %19 sin fmod %7 \
    fade l 0.05 0.2 0.05 gain -2

sox -D -n -r 48000 -c 2 -b 32 01-3.wav \
    synth 5 sin %-5 sin fmod %7 \
    fade l 0.05 0.2 0.05 gain -20

sox -D -n -r 48000 -c 2 -b 32 01-4.wav \
    synth 5 pl %-5 pl %7 sin fmod %7 \
    fade t 0 0.7 0.5 gain -27.5 pad 0.05
sox -D 01-1.wav 01-2.wav 01-3.wav 01-4.wav -m 01.wav pad 1

sox 01.wav 03.wav
sox 03.wav 04.wav REVERB 75 50 50 50 30
sox --norm=-0.5 04.wav -c 2 -b 16 -r 44100 message.wav
rm -f 0?.wav 0?-?.wav
