hanoi(N) :- move(N, left, right, centre).
move(0, _, _, _) :- !.
move(N, A, B, C) :-
  M is N-1,
  move(M, A, C, B),
  format("move a disc from the ~w pole to the ~w pole\n", [A,B]),
  move(M, C, B, A).