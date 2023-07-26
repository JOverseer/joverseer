@echo off
rem This is a convenience/legacy file to invoke joverseer with some of the drawing options off.
rem Which avoids overlapping frames etc with certain nVidia/Java combinations.
setlocal
set first=
if "%1"=="DontUseCustomJava" (
  rem make sure this option is always first.
  set first=DontUseCustomJava
  shift
)

call joverseer.bat %first% DontUse3d %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
