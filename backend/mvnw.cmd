@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PS_CMD__=

@IF "%DEBUG%"=="true" @ECHO ON

@REM set %HOME% to equivalent of $HOME
@IF "%HOME%" == "" (SET "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
@IF EXIST "%USERPROFILE%\.mavenrc_pre.bat" echo Warning: The syntax of .mavenrc_pre.bat is deprecated, please use .mavenrc_pre.cmd instead. >&2
@IF EXIST "%USERPROFILE%\.mavenrc_pre.bat" call "%USERPROFILE%\.mavenrc_pre.bat" %*
@IF EXIST "%USERPROFILE%\.mavenrc_pre.cmd" call "%USERPROFILE%\.mavenrc_pre.cmd" %*
@IF EXIST "%HOMEDRIVE%%HOMEPATH%\.mavenrc_pre.cmd" call "%HOMEDRIVE%%HOMEPATH%\.mavenrc_pre.cmd" %*

@setlocal

@SET WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM Extension to allow argument expansion
@SET "MAVEN_CMD_LINE_ARGS=%*"

@IF EXIST %WRAPPER_JAR% (
    @GOTO runMaven
)

@REM Find powershell
@SET "PATH=%SystemRoot%\system32;%SystemRoot%;%SystemRoot%\System32\Wbem;%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\"
@FOR %%X in (powershell.exe) do (set __MVNW_PS_CMD__=%%~$PATH:X)
@IF DEFINED __MVNW_PS_CMD__ (
    @GOTO runPowershell
)

@ECHO Cannot find powershell.exe in PATH. >&2
@EXIT /B 1

:runPowershell
@SET "WRAPPER_PROPERTIES=%~dp0.mvn\wrapper\maven-wrapper.properties"
@FOR /F "tokens=2 delims==" %%A in ('findstr /I "wrapperUrl" "%WRAPPER_PROPERTIES%"') do @SET WRAPPER_URL=%%A
@FOR /F "tokens=2 delims==" %%A in ('findstr /I "distributionUrl" "%WRAPPER_PROPERTIES%"') do @SET DISTRIBUTION_URL=%%A

powershell -NoProfile -ExecutionPolicy Bypass -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%') }"

:runMaven
@IF NOT EXIST %WRAPPER_JAR% (
    @ECHO Error: Could not find or download %WRAPPER_JAR% >&2
    @EXIT /B 1
)

@REM Provide a default value for JAVA_HOME
@IF NOT "%JAVA_HOME%"=="" (
    @SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
) ELSE (
    @SET "JAVACMD=java.exe"
)

@IF EXIST "%~dp0.mvn\jvm.config" (
    @FOR /F "usebackq delims=" %%A in ("%~dp0.mvn\jvm.config") do @SET JVM_CONFIG_MAVEN_PROPS=%%A
)

@SET "MAVEN_BASEDIR=%~dp0"
@IF "%MAVEN_BASEDIR:~-1%"=="\" SET "MAVEN_BASEDIR=%MAVEN_BASEDIR:~0,-1%"

"%JAVACMD%" %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS% "-Dmaven.multiModuleProjectDirectory=%MAVEN_BASEDIR%" -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %MAVEN_CMD_LINE_ARGS%
@IF ERRORLEVEL 1 GOTO error
@GOTO end

:error
@SET ERROR_CODE=1

:end
@EXIT /B %ERROR_CODE%
