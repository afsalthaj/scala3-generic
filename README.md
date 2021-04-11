## Scala3 Stuff!

* Type Computation (bounds, tuplemap, tuple-inverse-map)
* Type Class Derivation (A scala2 style scala3 typeclass derivation)
* Poly in Scala3 (A scala2 style poly with scala3 tuples)
* Extension methods (in different examples)
* Polymorphic function types (tuple map)
* Inline-transparent (compile time smart constructors)
* macros (expr, quoting and slicing)

### Usage

```scala
nix-shell
bloop server // once off
sbt bloopInstall // once off
bloop compile root
bloop test root
```

Use with VScode for better experience.
Make sure pass JAVA_HOME of your nix in VSCode, and import "Metals" from marketplace. 
