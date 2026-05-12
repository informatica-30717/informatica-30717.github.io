Scanner sc = new Scanner(System.in);
System.out.print("Introduce un numero entero: ");
int n = sc.nextInt();

int[] numero = new int[n + 1];
for (int i = 0; i <= n; i++) {
    numero[i] = i;
}

for (int i = 2; i * i <= n; i++) {
    if (numero[i] != 0) {
        int j = 2;

        while (i * j <= n) {
            numero[i * j] = 0;
            j = j + 1;
        }
    }
}

int contadorPrimos = 0;
for (int i = 2; i <= n; i++) {
    if (numero[i] != 0) {
        contadorPrimos++;
        System.out.println(i + " es primo");
    }
}

System.out.println();
System.out.println("Total: " + contadorPrimos + " numeros primos");
