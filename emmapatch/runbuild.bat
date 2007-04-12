
SETLOCAL

REM === Local Paths ===

SET JDK_PATH=C:\devtools\jdk1.4.2_12\bin
SET ANT_PATH=C:\devtools\apache-ant-1.6.5\bin
SET CVS_PATH=C:\devtools\cygwin\bin


SET PATH=%JDK_PATH%;%ANT_PATH%;%CVS_PATH%

ant main

ENDLOCAL
