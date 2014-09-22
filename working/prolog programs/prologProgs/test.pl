mortal(X) :- person(X).
person(socrates).
person(plato).
person(zeno).
person(aristotle).

mortal_report:-  
  write('Known mortals are:'),nl,
  mortal(X),
  write(X),nl,
  fail.
  
c_to_f(C,F) :-
  F is C * 9 / 5 + 32.

freezing(F) :-
  F =< 32.