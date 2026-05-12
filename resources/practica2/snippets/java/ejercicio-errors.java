public class Ejercicio
{
    public static void main(String[] args)
    {
        int n, x, y, z;
        n = 10;
        x = 0;
        y = 1;
        z = 1;

        if (n == 0) || (n == 1)
        {
            x = 1
        }
        else
        {
            for (i=2; i<=n; i++)
            {
                int s = y + z;
                x = s;
                z = y;
                y = x;
            }
        }

        System.out.println(Programa terminado);
    }
}
