@echo off

echo.
echo MSI Factory
echo Building: HawkCD_Agent.msi

echo.
echo - Compiling...
echo.

"C:\Program Files (x86)\MSI Factory\Wix\candle.exe" -out "HawkCD_Agent.wixobj" "HawkCD_Agent.wxs"
if ERRORLEVEL 1 goto error

"C:\Program Files (x86)\MSI Factory\Wix\candle.exe" -out "msifactui.wixobj" "msifactui.wxs"
if ERRORLEVEL 1 goto error

"C:\Program Files (x86)\MSI Factory\Wix\candle.exe" -out "Common.wixobj" "Common.wxs"
if ERRORLEVEL 1 goto error

"C:\Program Files (x86)\MSI Factory\Wix\candle.exe" -out "ErrorText.wixobj" "ErrorText.wxs"
if ERRORLEVEL 1 goto error

"C:\Program Files (x86)\MSI Factory\Wix\candle.exe" -out "ProgressText.wixobj" "ProgressText.wxs"
if ERRORLEVEL 1 goto error

echo.
echo - Linking...
echo.

"C:\Program Files (x86)\MSI Factory\Wix\light.exe" -loc "English-US.wxl" -b "C:\Users\SlaviDimitrov\Desktop\win\Installers" -out "HawkCD_Agent.msi" "HawkCD_Agent.wixobj" "msifactui.wixobj" "Common.wixobj" "ErrorText.wixobj" "ProgressText.wixobj"
if ERRORLEVEL 1 goto error

goto end

:error
echo.
echo - Build failed!
exit /B 1

:end
echo.
echo - Build completed successfully.
