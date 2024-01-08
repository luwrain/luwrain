#!/bin/bash -e
# Copyright 2024 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, GPL v.3

sox -D -n -b 32 -r 44100 -c 2 01.wav \
    synth 1 sin %-6 \
    fade t 0 0.5 0.5

sox -D -n -b 32 -r 44100 -c 2 02.wav \
    synth 1 sin %-13 \
        fade t 0 0.5 0.5 pad 0.07

sox -D -n -b 32 -r 44100 -c 2 03.wav \
    synth 1 sin %-9 \
    fade t 0 0.5 0.5 pad 0.14

sox -D -n -b 32 -r 44100 -c 2 04.wav \
    synth 1 sin %-25 sin %-1 sin sin %3 sin %6 \
        fade l 0 0.5 0.5 gain -10 pad 0.08 1


sox -D 01.wav 02.wav 03.wav 04.wav -m 05.wav
sox -D 05.wav 06.wav pad 0 1
sox 06.wav 07.wav reverb 75 50 50 50 100
sox -D --norm=-0.5 07.wav list-item-important.wav
#rm -f 0?.wav
