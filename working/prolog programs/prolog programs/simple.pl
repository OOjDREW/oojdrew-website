:-op(720,fy,non).
:-op(730,yfx,and).
:-op(740,yfx,or).

:-op(710,yfx,implies).
:-op(710,yfx,equiv).
:-op(740,yfx,xor).

and_d(false,true,false).
and_d(false,false,false).
and_d(true,false,false).
and_d(true,true,true).

or_d(false,true,true).
or_d(false,false,false).
or_d(true,false,true).
or_d(true,true,true).
   
non_d(true,false).
non_d(false,true).

logic_const(true).
logic_const(false).

eval_b(X,X):-logic_const(X).
eval_b(X and Y,R):-eval_b(X,XV),eval_b(Y,YV),and_d(XV,YV,R).
eval_b(X or Y,R):-eval_b(X,XV),eval_b(Y,YV),or_d(XV,YV,R).
eval_b(non X,R):-eval_b(X,XV),non_d(XV,R).

sibling(X,Y) :- parent(Z,X), parent(Z,Y).
parent(X,Y) :- father(X,Y).
parent(X,Y) :- mother(X,Y).
mother(trude, sally).
father(tom, sally).
father(tom, erica).
father(mike, tom).

bob(X,Y) :- ben(X,Y), X<Y.

ben(4,5).

song(title,artist,5).
song(ben,cool,4).
song(ben,stuff,6).



ratingGreaterThan4(X,Y,Z) :- song(X,Y,Z),or_d(Z>4, Z=4).

stuff(ben) :- 4 > 3.
stuff(bob) :- and_d(true,true).


cool(Person) :- dumb(Person).
dumb(Person) :- smart(Person).
smart(ben).

