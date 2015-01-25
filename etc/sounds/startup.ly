
\version "2.14.2"

#(define (add-sustain-event music t)
  (set! music
    (append music
      (list (make-music 'SustainEvent 'span-direction t))))
  music)

sr = #(define-music-function (parser location note) (ly:music?)
  (ly:music-compress note (ly:make-moment 1 2))
  (if (eq? (ly:music-property note 'name) 'SequentialMusic)
    (make-music 'SequentialMusic 'elements (list 
      (make-music 'EventChord 'elements  
        (add-sustain-event (ly:music-property (car (ly:music-property note 'elements)) 'elements) 1))
      (make-music 'EventChord 'elements  
        (add-sustain-event (ly:music-property (car (ly:music-property note 'elements)) 'elements) -1)))) 
    (make-music 'SequentialMusic 'elements (list 
      (make-music 'EventChord 'elements  
        (add-sustain-event (ly:music-property note 'elements) 1))
      (make-music 'EventChord 'elements  
        (add-sustain-event (ly:music-property note 'elements) -1))))))

mr = \ffff
rr= \ff
rl = \pp

normalTempo =   \tempo 4 = 160

initialKeyRight = {
  \key f \major
  \time 4/4
  \clef treble
}

initialKeyLeft = {
  \key f \major
  \time 4/4
  \clef bass
}

partOneRight = {
  \partial 2 as'8( ces''8 es''8 as''8 
  ges''4.) f''8( es''4 es''8( ces''8 
  <f'' as''>2
}

partOneRightMidi = {
  \normalTempo
  s8 \rr \sustainOn s8 s8 s8 
  \sr s4. \mr s8 \rr \sr s4 \mr s8 \rr s8 
  s2 \mr \sustainOff
}

partOneDynamics = {
}

partOneLeft = {
  \partial 2r2
  <es ges bes es'>1
  <des, f' as'>2
}

partOneLeftMidi = {
  s2
  s1 \rl
  s2
}

\score {
  \new PianoStaff <<
    \new Staff = "upper" {
      \initialKeyRight
      \partOneRight
    }
    \new Dynamics = "dynamics" {
      \partOneDynamics
    }
    \new Staff = "lower" {
      \initialKeyLeft
      \partOneLeft
    }
  >>
  \layout {}
}

\score {
  \unfoldRepeats {
    \new PianoStaff <<
      \set PianoStaff.instrumentName = "piano"
      \new Staff = "upper" <<
	{
	  \partOneRightMidi
	}
	{
	  \partOneRight
	}
      >>
      \new Staff = "lower" <<
	{
	  \partOneLeftMidi
	}
	{
	  \initialKeyLeft
	  \partOneLeft
	}
      >>
    >>
  }
  \midi { }
}
