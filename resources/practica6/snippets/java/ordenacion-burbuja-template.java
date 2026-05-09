import java.util.*;

public class OrdenacionBurbuja {
  public static void ordenar(int[] v) {
    // Tu codigo aqui.
    // Recuerda que puedes acceder al tamano del vector
    // con v.length y a cada elemento con v[i].
  }

  public static void main(String[] args) {
    Random r = new Random();
    Scanner sc = new Scanner(System.in);

    System.out.print("Dimension de la lista de numeros: ");
    int size = sc.nextInt();

    int[] v = new int[size];
    for (int i = 0; i < size; i++) {
      int x = r.nextInt();
      x = Math.abs(x) % 50;
      v[i] = x;
    }

    System.out.println("Vector inicial");
    for (int i = 0; i < size; i++)
      System.out.print(v[i] + "\t");

    ordenar(v);

    System.out.println();
    System.out.println("Vector ordenado");
    for (int i = 0; i < size; i++)
      System.out.print(v[i] + "\t");
  }
}
