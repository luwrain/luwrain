#!/bin/bash -e
# Copyright 2024 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, GPL v.3

sox -D -n -r 48000 -c 2 -b 32 01-1.wav \
    synth 5 sin %7 sin %-1 sin %2 sin fmod %7 \
    fade l 0.05 0.125 0.05 gain -1
sox -D -n -r 48000 -c 2 -b 32 01-2.wav \
    synth 5 sin %7 sin %19 sin fmod %7 \
    fade l 0.03 0.2 0.05 gain -1
sox -D -n -r 48000 -c 2 -b 32 01-3.wav \
    synth 5 sin %-5 sin fmod %7 \
    fade l 0.05 0.2 0.05 gain -20
sox -D -n -r 48000 -c 2 -b 32 01-4.wav \
    synth 5 pl %-5 pl %7 sin fmod %7 \
    fade t 0 0.7 0.5 gain -20 pad 0.035
sox -D 01-1.wav 01-2.wav 01-3.wav 01-4.wav -m 01.wav pad 0.03 1

sox -D -n -r 48000 -c 2 -b 32 02-1.wav \
    synth 5 sin %2 sin %14 pl %14 sin fmod %-10 \
    fade t 0.2 0.5 0.5 gain -20


sox 01.wav 02-1.wav -m 03.wav
sox 03.wav 04.wav REVERB 75 100 50 100 100 5
sox --norm=-0.5 04.wav -c 2 -b 16 -r 44100 message.wav
rm -f 0?.wav 0?-?.wav
