# Practice 9 snippets

These files are included from `practicas/practica9.qmd` with code blocks like:

````
```{.java include="../resources/practica9/snippets/example.java"}
```
````

The include is resolved by `resources/filters/include-code.lua`. HTML and Reveal inline the snippet contents; PDF emits a compact `\PracticeCode{...}` command in the generated `.tex`.
