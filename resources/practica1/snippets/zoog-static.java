size(600, 600);

// Fondo blanco
background(255);

// Coordenadas desde el centro
ellipseMode(CENTER);
rectMode(CENTER);

// CUERPO
stroke(0);
fill(150);
rect(200, 260, 50, 260);

// CABEZA
fill(255);
ellipse(200, 160, 130, 160);

// OJOS
fill(0);
ellipse(170, 160, 30, 60);
ellipse(230, 160, 30, 60);

// PIERNAS
stroke(0);
line(175, 390, 150, 420);
line(226, 390, 250, 420);
