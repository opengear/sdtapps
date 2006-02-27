;NSIS Modern User Interface
;Basic Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI


  !define AppName "Opengear SDTConnector"
  !define ShortName "SDTConnector"
  !define Vendor "Opengear"
  !define MUI_BRANDINGTEXT "${AppName}"
  !define JRE_VERSION "1.4.2"
  CRCCheck On
  !include "MUI.nsh"
  !include "Sections.nsh"


;--------------------------------
;General

  ;Name and file
  Name "${AppName}"
  OutFile "..\dist\${ShortName}Setup.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${AppName}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\${AppName}" ""

;  SetCompressor /SOLID lzma
;  SetCompressor off
;  !define MUI_ICON "..\images\program.ico"
;  !define MUI_UNICON "..\images\program.ico"
;  !define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"

;--------------------------------
;Variables

  Var MUI_TEMP
  Var STARTMENU_FOLDER

;--------------------------------
;Interface Settings
  !define MUI_ABORTWARNING

;--------------------------------
;Pages

 !insertmacro MUI_PAGE_WELCOME

;  !insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Docs\Modern UI\License.txt"
;  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${AppName}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU ${ShortName} $STARTMENU_FOLDER
  
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "install" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File ..\dist\SDTConnector.exe

  ;Store installation folder
  WriteRegStr HKCU "Software\${AppName}" "" $INSTDIR
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" \
                 "DisplayName" "${AppName}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" \
                 "UninstallString" "$INSTDIR\uninstall.exe"
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  ;Setup start menu
  !insertmacro MUI_STARTMENU_WRITE_BEGIN SDTConnector 
    
  ;Create shortcuts
  CreateShortCut "$DESKTOP\${ShortName}.lnk" "$INSTDIR\${ShortName}.exe" ""
  CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\SDTConnector.lnk" "$INSTDIR\SDTConnector.exe"
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...
  Delete "$INSTDIR\SDTConnector.exe"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir "$INSTDIR\doc"
  RMDir "$INSTDIR"
  Delete "$DESKTOP\${ShortName}.lnk"

  !insertmacro MUI_STARTMENU_GETFOLDER SDTConnector $MUI_TEMP
    
  Delete "$SMPROGRAMS\$MUI_TEMP\Uninstall.lnk"
  Delete "$SMPROGRAMS\$MUI_TEMP\${ShortName}.lnk"
  ;Delete empty start menu parent diretories
  StrCpy $MUI_TEMP "$SMPROGRAMS\$MUI_TEMP"
  startMenuDeleteLoop:
    ClearErrors
    RMDir $MUI_TEMP
    GetFullPathName $MUI_TEMP "$MUI_TEMP\.."
    
    IfErrors startMenuDeleteLoopDone
  
    StrCmp $MUI_TEMP $SMPROGRAMS startMenuDeleteLoopDone startMenuDeleteLoop
  startMenuDeleteLoopDone:

  DeleteRegKey /ifempty HKCU "Software\${AppName}"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\SDTConnector"
SectionEnd