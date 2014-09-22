command_loop:-  
repeat,
  write('Enter command (end to exit): '),
  read(X),
  write(X), nl,
  X = end.