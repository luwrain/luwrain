%% Copyright 2012 Michael Pozhidaev <msp@altlinux.org>
%%
%% This file is part of the Luwrain.
%%
%% Luwrain is free software; you can redistribute it and/or
%% modify it under the terms of the GNU General Public
%% License as published by the Free Software Foundation; either
%% version 3 of the License, or (at your option) any later version.
%%
%% Luwrain is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
%% General Public License for more details.

\version "2.14.2"

melody = {
  \tempo 4 = 200
c'''16 \sustainOn as''16 f''16 c''16 \sustainOff
}

\score {
  \new PianoStaff <<
    \set PianoStaff.instrumentName = "piano"
    \new Staff = "upper" {
      \melody
    }
  >>
  \midi { }
}
