%rooms in the game

room(kitchen).
room(office).
room(hall).
room('dining room').
room(cellar).

%Door ways

door(office, hall).
door(kitchen, office).
door(hall, 'dining room').
door(kitchen, cellar).
door('dining room', kitchen).

%describing things about the objects
edible(apple).
edible(crackers).
tastes_yucky(broccoli).
turned_off(flashlight).

init_facts :- assertz(here(kitchen)),
			  assertz(location(desk, office)),
			  assertz(location(apple, kitchen)),
			  assertz(location(flashlight, desk)),
			  assertz(location('washing machine', cellar)),
			  assertz(location(nani, 'washing machine')),
			  assertz(location(broccoli, kitchen)),
			  assertz(location(crackers, kitchen)),
			  assertz(location(computer, office)).

%start the game
start :- init_facts, look.

%rules

where_food(X,Y) :-  
location(X,Y),
  edible(X).
  
where_food(X,Y) :-
  location(X,Y),
  tastes_yucky(X).

%Which Rooms are connected

connect(X,Y) :- door(X,Y).
connect(X,Y) :- door(Y,X).

%List all the things in a place

list_things(Place) :-
  location(X, Place),
  tab(2),
  write(X),
  nl,
  fail.
  
%Using List all by itself is fine but if we want to use it in
%conjunction with other rules we need to make a list_things(AnyPlace) so that
%it will awlways succeed.

list_things(AnyPlace).

%List all the connections to that place
list_connections(Place) :-
  connect(Place, X),
  tab(2),
  write(X),
  nl,
  fail.
list_connections(_).

%This will allow you to look to see what items are here and where you can go.
look :-
  here(Place),
  write('You are in the '), write(Place), nl,
  write('You can see:'), nl,
  list_things(Place),
  write('You can go to:'), nl,
  list_connections(Place).

%Allows to look in a place
%Really Defeats the purpose of the game
look_in(Place) :- write('You can see:'), nl,
				  list_things(Place).

%Move to a Area and call look to see the new stuff in the room
goto(Place):-  
  can_go(Place),
  move(Place),
  look.
  
%You can only go if its connected to where you are
%If its not connected we can say that you cant go there
%If can_go fails then it will always call the other can go to write a message that it failed

can_go(Place):-
  here(X),
  connect(X, Place).
can_go(Place):-
  write('You can''t get there from here.'), nl,
  fail.

%Move to a new place, were need to change here by removing it from the knowledgebase
%Then we insert a new here by asserta

move(Place):-
  retract(here(X)),
  asserta(here(Place)).

%take an item you find  
take(X):-  
  can_take(X),
  take_object(X).  

%test to see if you can take the item
can_take(Thing) :-
  here(Place),
  location(Thing, Place).
can_take(Thing) :-
  write('There is no '), write(Thing),
  write(' here.'),
  nl, fail.

%remove the item from the location and put it in your inventory
take_object(X):-  retract(location(X,_)),
  assertz(have(X)),
  write('taken'), nl.

% Put something in the a room
drop(Thing):-
  have(Thing),                     
  here(Here),                     
  retract(have(Thing)),
  asserta(location(Thing,Here)).
drop(Thing):-
  respond(['You don''t have the ',Thing]).

% inventory list your possesions

inventory:-
  have(X),                         % make sure you have at least one thing
  write('You have: '),nl,
  list_possessions.
inventory:-
  write('You have nothing'),nl.

list_possessions:-
  have(X),
  tab(2),write(X),nl,
  fail.
list_possessions.
  
  
  
  
  





