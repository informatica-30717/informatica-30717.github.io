Scanner sc = new Scanner(System.in);
System.out.print("Introduce un numero entero: ");
int n = sc.nextInt();

int max = ...;     // calcular el tamano del vector de impares
boolean[] numero = new boolean[max + 1];

for (int i = 0; i <= max; i++) {
    numero[i] = true;
}

for (int i = 0; ... <= n; i++) {
    int k = ...;
    while (... <= n) {
        numero[...] = false;
        k = k + 1;
    }
}

int contadorPrimos = 1;
System.out.print("2 es primo \n");

for (int i = 0; i <= ...; i++) {
    if (numero[i] != false) {
        contadorPrimos = contadorPrimos + 1;
        System.out.println(... + " es primo");
    }
}

System.out.println();
System.out.println("Total: " + contadorPrimos + " numeros primos");
