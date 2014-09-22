father(X) :- hasChild(X), male(X).
hasChild(Y) :- stupid(Y).

stupid(peter).
male(peter).