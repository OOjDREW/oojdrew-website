%Rules
drives(Person):- driversLicense(Person), ownsCar(Person).
driversLicense(Person):-over16(Person).
	
%Facts
ownsCar(peter).
over16(peter).
over16(jill).
