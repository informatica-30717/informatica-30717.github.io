size(500, 200);

int N = 2;
float offsetX = 0;
float offsetY = 0;

for (int i = 1; i <= N; i++) {
  rect(50 + offsetX, 20 + offsetY, 80, 80);

  offsetX += 100;
  offsetY += 10;
}
