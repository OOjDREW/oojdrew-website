%query road(Halifax, Fredericton).

print :-  write('ben').
print2 :- write('hi').

% facts

der(C, 0) :-  number(C).

road(halifax, truro).
road(truro, moncton).
road(moncton, fredericton).
%rule
road(X, Z) :- road(X, Y),road(Y, Z), write('There is a road').


init_facts :- assertz(size(big)), retract(size(_)) = big, write("hi").

small :- assertz(size(small)).
