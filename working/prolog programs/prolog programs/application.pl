%song(Title,Artist?rating:Integer). Say the rating is between 1-5.

%song(Time,Pink_Floyd:String,3:Integer).
%song(Money,Pink_Floyd:String,4:Integer).
%song(Man_on_the_Moon:String,REM,2:Integer).

%Now we need to make rules to do the song comparisons.  The first one will be based on a
%rating.

%next_song_rating(?title) :- song(?title,?artist,?rating),
%greaterThan(?rating,2:Integer).

%So if your query next_song_rating(?title) in the TD engine of OO jDREW title will be
%bound to time and money.

%If you want the next song to be based on the artist of the current song you can use.  

%next_song_artist(?title) :- song(?title, ?artist, ?rating),
%stringEqualIgnoreCase(?artist,Pink_Floyd:String).

song(title,artist,5).
song(ben,cool,4).
song(ben,stuff,6).

ratingGreaterThan4(X,Y,Z) :- song(X,Y,Z);(Z>4, Z=4).

stuff(ben) :- 4 > 3.
stuff(bob) :- and_d(true,true).