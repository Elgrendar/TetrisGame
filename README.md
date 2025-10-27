# Tetris - RafaCampanero

Proyecto Tetris en Java 21 + JavaFX.
Estructura, ejecución y persistencia en JSON.

## Ejecutar
1. Desde VSCode: abrir la carpeta del proyecto.
2. Ejecutar: `mvn javafx:run` (usa el main `com.rafatetris.App`).
3. Alternativamente usa el Run/Launch de VSCode (launch.json incluido).

## Datos
Los highscores se guardan en `data/highscores.json` (se crea automáticamente).

## Sonidos
Añade archivos WAV en `src/main/resources/sounds/` con nombres `move.wav`, `rotate.wav`, `clear.wav` (puedes cambiar los nombres en `SoundManager`).

## Internacionalización
Strings en `src/main/resources/strings_*.properties`. Por defecto usa `es` (español).

## Licencia & GitHub
Sube a: https://github.com/elgrendar
