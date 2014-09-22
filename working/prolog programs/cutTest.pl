data(one).
data(two).
data(three).

cut_test_a(X) :-
  data(X),!.
cut_test_a('last clause').

cut_test_b(X) :-
  data(X),
  !.
cut_test_b('last clause').

cut_test_c(X,Y) :-
  data(X),
  !,
  data(Y).
cut_test_c('last clause').