[Setup]
AppCopyright=Marios Skounakis
AppName=JOverseer
AppVerName=JOverseer version 1.0.12
DefaultDirName={pf}\JOverseer\
DefaultGroupName=JOverseer
OutputBaseFilename=joverseer-setup
#define InputDirName "I:\MiddleEarth\joverseer\main\resources"
#define InputResourceDirName "I:\MiddleEarth\joverseer\main\resources"
#define InputOrderChecker "I:\MiddleEarth\joverseer\main\resources\metadata\orderchecker"
#define InputBuild "I:\MiddleEarth\JOverseerWorkspace"
#define InputLibs "I:\MiddleEarth\JOverseerWorkspace\RequiredLibs"
#define InputOrderCheckerJar "I:\MiddleEarth\OrderCheckerWorkspace\Orderchecker\out"


DiskSpanning=false
DiskSliceSize=8000000

[Icons]
Name: "{group}\jOverseer"; Filename: "{app}\jOverseer.exe"; WorkingDir: "{app}"
Name: "{group}\Uninstall jOverseer"; Filename: "{uninstallexe}"

[Files]
Source: {#InputOrderchecker}\*; DestDir: {app}\bin\metadata\orderchecker
Source: {#InputResourceDirName}\MailSender\MailSender.exe; DestDir: {app}\bin\mailSender
Source: {#InputResourceDirName}\layout\default.layout; DestDir: {app}\layout
Source: {#InputLibs}\*.jar; DestDir: {app}
Source: {#InputBuild}\update.jar; DestDir: {app}
Source: {#InputOrderCheckerJar}\OrderChecker.jar; DestDir: {app}
Source: {#InputBuild}\joverseer.jar; DestDir: {app}
Source: {#InputResourceDirName}\images\joverseer.ico; DestDir: {app}
Source: {#InputBuild}\jOverseer.exe; DestDir: {app}
Source: {#InputBuild}\jOverseerUpdater.exe; DestDir: {app}
Source: {#InputResourceDirName}\executable\joverseer.bat; DestDir: {app}
[Dirs]
Name: {app}\bin
Name: {app}\bin\mailSender
Name: {app}\bin\metadata
Name: {app}\bin\metadata\orderchecker
Name: {app}\layout


