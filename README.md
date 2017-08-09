# LedCalculator
Kalkulator krzywej gamma dla RGB Led

## Wstęp
Narzędzie przeznaczone jest do prostej i łatwej kalkulacji tablicy wartości (lookup table) pozwalającej przeliczyć
oczekiwaną liniową jasność diody na faktyczny poziom sterowania PWM.

Aplikacja przygotowana jest w języku Kotlin 1.1.3-2, przy użyciu frameworku JavaFX 8. Do prawidłowej pracy wymaga JVMa 1.8.90 lub nowszego.

## Budowanie i uruchamianie aplikacji
Przed kompilowaniem aplikacji upewnij się, że masz na komputerze Gradle w wersji 4.0.x lub nowszy oraz Javę 1.8.90+ (w wersji Oracle
lub OpenJDK + OpenJFX). 
Jeśli tak to sklonuj projekt na swój pulpit i wejdź do katalogu z aplikacją. Aby uruchomić aplikację wpisz:

gradle jfxRun

## Parametry w aplikacji
W aplikacji definiuje się 8 parametrów:
- Maks wejście: Maksymalny poziom wejściowy jaki znajdzie się w tablicy. Wprowadzając wartość 15 tablica będzie miała 16 wartości. Od 0 do 15.
- Gamma R/G/B: Współczynnik krzywej gamma, determinujący kształt. Wartość 1,0 to linia prosta. Dla LED przyjmuje się wartości większe od 1,0
- Poziom maks R/G/B: Wartość całkowita maksymalnego stopnia wypełnienia PWM, determinuje także maksymalną jasność diody. Parametr pomocny, 
gdy poszczególne kolory nie świecą w sposób jednolity i któryś trzeba przyciemnić
- PROGMEM: Zaznaczenie tego pola doda tekst PROGMEM do kodu do skopiowania do aplikacji w C/C++ dla AVR-GCC

Wynik obliczeń otrzymasz na ekranie po naciśnięciu przycisku Przelicz.
