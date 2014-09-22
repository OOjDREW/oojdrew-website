 
%Rooms in the game

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

init_facts :- assertz(here(kitchen)),
			  
			  assertz(location(desk, office)),
			  assertz(location(apple, kitchen)),
			  
			  assertz(turned_off(flashlight)),
			  
			  assertz(location('washing machine', cellar)),
			  assertz(location(nani, 'washing machine')),
			  assertz(location(broccoli, kitchen)),
			  assertz(location(crackers, kitchen)),
							  
			  %These items are contained within other places
			  
			  assertz(location(envelope, desk)),
			  assertz(location(stamp, envelope)),
			  assertz(location(key, envelope)),
			  assertz(location(flashlight, desk)),
			  
			  assertz(is_close(office,hall)),
			  assertz(is_close(kitchen, office)),
			  assertz(is_close(hall, 'dining room')),
			  assertz(is_close(kitchen, cellar)),
			  assertz(is_close('dining room', kitchen)),
			  
			  assertz(have(knife)),
			  assertz(is_open('blank','blank')), %needed to prevent undefined predicates
			  assertz(location(computer, office)).

%start the game
start :- init_facts, command_loop.

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

%Using List all by itself is fine but if we want to use it in
%conjunction with other rules we need to make a list_things(AnyPlace) so that
%it will awlways succeed.

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
%look_in(Place) :- write('You can see:'), nl,
%				  list_things(Place).

%Move to a Area and call look to see the new stuff in the room
goto(Place):-
  puzzle(goto(Place)),  %Just used for testing the puzzle predicate, can re-write later
  can_go(Place),
  move(Place),
  look.
  
%You can only go if its connected to where you are
%If its not connected we can say that you cant go there
%If can_go fails then it will always call the other can go to write a message that it failed

can_go(Place):-
  here(X),
  (is_open(X,Place);is_open(Place,X)),
  connect(X, Place).
  
can_go(Place):-
  here(X),
  is_close(X,Place),
  write('The door is closed.'), nl,fail;
  write('You cant get there from here.'),nl,fail.
 
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
%need to remove all the object from the game incase of the compound case
%Like when you look in the desk.
%needs to be fixed.
take_object(X):-
  retract(location(X,_)),
  assertz(have(X)),
  write('taken'), nl,
  take_object(X).
  

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

%Turn on the flash light if you have it
%Need the negation when its turned on

turn_on_flashlight :- have(flashlight),
						retract(turned_off(flashlight)),
						assertz(turned_on(flashlight)),
						write('You turned on the flashlight').
turn_on_flashlight :- write('You do not have a flashlight').  

turn_off_flashlight :- have(flashlight),
						retract(turned_on(flashlight)),
						assertz(turned_off(flashlight)),
						write('You turned off the flashlight').
turn_off_flashlight :- write('You do not have a flashlight').  
  
open_door(Place):-
  here(X),
  retract(is_close(X,Place)),
  assertz(is_open(X,Place)),
  write('You opened the door').
  
close_door(Place):-
 here(X),
 retract(is_open(X,Place)),
 assertz(is_close(X,Place)),
 write('You closed the door').

 %List all the things in a place

list_things(Place) :-
  location(X, Place),
  tab(2),
  write(X),
  nl,
  fail.
list_things(_). 

%allows the discover of new items in a location
assert_things(Place):-
  here(Loc),
  location(X,Place),
  not(location(X,Loc)),
  not(have(X)),
  assertz(location(X,Loc)),
  fail.
assert_things(_).

%This rule allows us to search the tings for items 

look_in(Thing):-
  location(_,Thing),               % make sure there's at least one
  write('The '),write(Thing),write(' contains:'),nl,
  list_things(Thing),
  assert_things(Thing).
look_in(Thing):-
  write(['There is nothing in the ',Thing]).

%I cant get this recursion to work, not sure whats wrong
%But I can change the depth of searching to only one object

is_contained_in(T1,T2) :-
  location(X,T2),
  write(X),nl,
  is_contained_in(T1,X).

puzzle(goto(cellar)):-
  have(flashlight),
  turned_on(flashlight),
  !.
  
puzzle(goto(cellar)):-
  write('It''s dark and you are afraid of the dark.'),
  !, fail.

puzzle(goto('dining room')):-
  have(key),
  !.
  
puzzle(goto('dining room')):-
  write('The door to the cellar is looked'),
  !, fail.  
  
puzzle(_).


%Need to figure out how all this stuff works now

do(goto(X)):-goto(X),!.
do(go(X)):-goto(X),!.
do(inventory):-inventory,!.
do(look):-look,!.
do(open_door(Door)) :- open_door(Door),!.


do(take(X)) :- take(X), !.
do(end).
do(_) :- write('Invalid command').
  
command_loop:- 
 write('Welcome to Nani Search'), nl,
  repeat,
  write('<Command Input> '),
  read(X),
  puzzle(X),
  do(X), nl,
  end_condition(X).
  
end_condition(end).
end_condition(_) :-
  have(apple),
  write('Congratulations You found the apple you win').
  
%LOOK AT THE DATA STRUCTURE SECTION TO PUT MORE DESCRIPTION IN THE ITEMS
%LIKE YOU CAN ONLY CARRY SMALL ITEMS  
  
  
  
  
  
  
  