private int shadeOndas(
    double x, double y, double z,
    double nx, double ny, double nz)
{
  double escala = 0.18;

  double onda1 = Math.sin((x + y) * escala);
  double onda2 = Math.cos((y - z) * escala * 1.7);
  double onda3 = Math.sin(Math.sqrt(x * x + y * y + z * z) * escala * 2.4);

  double r = 0.5 + 0.5 * Math.sin(...);
  double g = 0.5 + 0.5 * Math.cos(...);
  double b = 0.5 + 0.5 * Math.sin(...);

  return pack(r, g, b);
}
