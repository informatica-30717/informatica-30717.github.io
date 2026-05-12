Scanner sc = new Scanner(System.in);
System.out.print("Introduce un numero entero: ");
int n = sc.nextInt();

boolean[] esPrimo = new boolean[n + 1];
for (int i = 2; i <= n; i++) {
    esPrimo[i] = true;
}

for (int i = 2; i * i <= n; i++) {
    if (esPrimo[i]) {
        int j = 2;

        while (i * j <= n) {
            esPrimo[i * j] = false;
            j = j + 1;
        }
    }
}

int contadorPrimos = 0;
for (int i = 2; i <= n; i++) {
    if (esPrimo[i]) {
        contadorPrimos++;
        System.out.println(i + " es primo");
    }
}

System.out.println();
System.out.println("Total: " + contadorPrimos + " numeros primos");
