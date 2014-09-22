child(doug,bob).
child(doug,ben).
Child(doug,jane).

male(doug).
female(jane).
male(bob).
male(ben).

father(X) :- child(X,Y),!, not(female(X)).



