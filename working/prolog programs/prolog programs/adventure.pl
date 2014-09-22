
main :- init_facts,
		help,
		go_loop.


go_loop :-
	repeat,
	write($>> $),
	read(X),
	do(X),
	(X == quit; done).
	
%
% Initial facts that are known but could change throughout the game
% are placed here.
%

init_facts :-
	assertz(you(airport)),
	assertz(loc(map,store)),
	assertz(loc(key,store)),
	assertz(loc(rope,path)),
	assertz(loc(jewel,cave)),
	assertz(loc('magic mushroom',hole)),
	assertz(size(big)).
	

%
%	These are the basic facts that will no change so they are compiled
%

area(airport).
area(store).
area(city).
area(forest).
area(path).
area('foot of the mountain').
area('top of the mountain').
area(cave).

connect(airport,store).
connect_oneway(airport,city).
connect(city,forest).
connect(city,path).
connect(forest,'foot of the mountain').
connect('foot of the mountain','top of the mountain').
connect('top of the mountain',cave).

item(map).
item(key).
item(rope).
item(jewel).
item('magic mushroom').
item(apple).

can_eat(apple).
can_eat('magic mushroom').

object(tree).
object(hole).

loc(tree,forest).
loc(hole,path).

% The following code is all the rules when a user
% tries to more from one area to another.

pathway(X,Y) :- connect(X,Y).			 
pathway(X,Y) :- connect(Y,X).			 
pathway(X,Y) :- connect_oneway(X,Y).





look :-
	you(Here),
	type(['Current location: ', Here]),nl,
	write('Items currently here:'),nl,
	list_items(Here),nl,
	write('Areas you can go to from here:'),nl,
	list_pathways.
	
list_items(Area):-
  loc(X,Area),
  tab(2),write(X),nl,
  fail.
list_items(_).

list_pathways:-
  you(Here),
  pathway(Here,X),
  tab(2),write(X),nl,
  fail.
list_pathways(_).
	
help :- type(['Welcome to an exciting text-based adventure game']),
		type(['Your goal is to find a jewel hidden somewhere in the game']),
		type(['']),
		type(['The following some of the commands you will find useful:']),
		type(['look.         --> Look around and see what''s around you']),
		type(['goto(''   '').  --> Moves you to a different area']),
		type(['take(''   '').  --> Grabs an item that you are in and puts it in your inventory']),
		type(['eat(''    '').  --> Eats something in your inventory']),
		type(['use(''    '').  --> Uses an item in your inventory']),
		type(['look(''   '').  --> Look closer at a certain item']),
		type(['inventory.    --> Displays the items in your inventory']),
		type(['help.         --> Displays this help message']),
		type(['quit.         --> Quits the game.']).
	
do(take(X)) :- !, take(X),!.
do(get(X)) :- !, take(X),!.
do(goto(X)) :- !,goto(X).
do(eat(X)) :- !,eat(X).
do(shake(X)) :- !,shake(X).
do(look_in(X)) :- !,look(X),!.
do(look(X)) :- !,look(X),!.
do(look_at(X)) :- !,look(X),!. 
do(use(X)) :- !,use(X).
do(look) :- !,look.
do(inventory) :- inventory,!.
do(help) :- !,help.
do(quit) :- !,quit.
do(X) :- type(['You can''t ', X, ' in this game!!']).


			


quit :- type(['quitter!']).


puzzle(goto(city)) :- you(airport), loc(map,inventory),
							tab(2),write('You can now find your way with the map.'),nl,
							!.
puzzle(goto(city)) :- loc(map,inventory),!.
puzzle(goto(city)) :- tab(3),write('You can''t leave the airport yet'),nl,
							tab(3),write('You should try and find a map so that'), nl,
							tab(3),write('you don''t get lost'),nl,
							!,fail.


puzzle(goto('top of the mountain')) :- loc(rope,cliff),
													tab(2),write('You climb up the rope'),nl,!.
puzzle(goto('top of the mountain')) :- tab(2),write('There is a huge cliff. You climb up'),nl,
										tab(2),write('but then fall down. You''re going to'),nl,
										tab(2),write('need to find a way to get up'),nl,
										!,fail.

puzzle(goto('foot of the mountain')) :- you('top of the mountain'),
													tab(2),write('You climb down the rope'),nl,fail.

puzzle(goto(cave)) :- size(small),
							tab(2),write('You are small enough to fit through the small cave hole'),nl,!.
puzzle(goto(cave)) :- tab(2),write('The opening of the cave is too small for you to go into'),nl,
							tab(2),write('If only you could find someway to shrink yourself'),nl,!,fail.

puzzle(_).

how_to_use(tree) :-
		tab(2),write('Try to shake it maybe.'),nl.
how_to_use(hole) :-
		tab(2),write('Maybe you should look in it.'),nl.
how_to_use(_).

%
%	
%

goto(Room) :- can_move(Room), 		% must be a legal move
			puzzle(goto(Room)),			% certain conditions might have to be met
			moveto(Room),
			tab(2),type(['You are now in the ', Room]).						% move to new area

can_move(Room) :- you(Current), pathway(Current,Room),!.
can_move(Room) :- area(Room), type(['You can''t get to ',Room,' from here']),!,fail.
can_move(Room) :- type([Room, ' is not a place you can go into...EVER']),fail.


moveto(Room) :-   
	retract(you(_)),             % leave room you are in
  	asserta(you(Room)).				% put you in this room

take(Thing) :-
		object(Thing),
		tab(2),write('You can''t pick up that, try doing something else to it'),nl,
		how_to_use(Thing),!.

take(Thing) :-
		can_take(Thing),
		puzzle(take(Thing)),
		move(Thing, inventory),
		type(['You took the ', Thing]).

can_take(Thing) :-
	you(Here),inside(Thing,Here),!.
can_take(Thing) :- 
	type([Thing, ' is not here']),fail.
	
inside(Thing,Area) :-
	loc(Thing,Area).
inside(Thing,Area) :-
	loc(Thing,X),
	inside(X,Area).
		
move(Thing, Place) :-
	retract(loc(Thing,_)),
	assertz(loc(Thing,Place)).




use(Thing) :- not(loc(Thing,inventory)),
					type(['You don''t have a ', Thing]),
					!, fail.
			
use(rope) :-
	loc(rope,inventory),
	you('foot of the mountain'),
	move(rope,cliff),
	tab(2),write('You can now reach the mountain top'),nl,
	!.
use(Thing) :-
	tab(2),write('You can''t use that here'),nl.


eat(apple) :- type(['Yum...an apple...but it does nothing']),!.
eat('magic mushroom') :- tab(2),write('Oh no! Why would you eat that?'),nl,
								tab(2),write('You shrunk! Maybe now you can go into smaller places'),nl,
								retract(size(_)),
								assertz(size(small)),!.
eat(X) :- type(['You can''t eat that']).


shake(tree) :- not(loc(apple,forest)),
				not(loc(apple,inventory)),
				tab(2),write('You shook the tree'), nl,
				tab(2),write('an apple fell on the ground'), nl,
				assertz(loc(apple,forest)),!.
shake(tree) :- tab(2),write('The tree is out of apples right now'),nl.
shake(X) :- type(['You try to shake ', X, '....']),
				type(['And fail miserably']).


look(hole) :- tab(2),write('In the hole you found:'),nl,
					list_items(hole),nl,!.
look(X) :- type(['There is nothing interesting about that']),nl.

inventory :-
	tab(2),write('Current Inventory:'),nl,
	list_items(inventory).	
inventory(_).

done :- loc(jewel,inventory),
		tab(2),write('Congratulations!'),nl,
		tab(2),write('You found the jewel and have beat the game!'),nl.

type([]):-
  write('.'),nl.
type([H|T]):-
  write(H),
  type(T).
  