/****************************************************************
 * Copyright (C) 2006 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */

/**
 * updater.nsi is actually an updater/installer/language-pack. There are 2 
 * possibilites:
 * 1) If LAMS 2.x is installed and this is a newer version, the updater will be 
 *    run
 
 
 * 2) If there is no LAMS 2.x installation, a full install will take place
 *
 * Builds to win_installer\build\LAMS-updater-$VERSION.exe
 * You must change the $VERSION to comply with this version (Line 49 approx)
 * Change the LANGUAGE_PACK_VERSION To the date you compile YYYY-MM-DD
 * Change the DATE_TIME_STAMP to the same as can be found in lams_common\db\sql\insert_windows_config_data.sql
 */

# includes
!include "TextFunc.nsh"
!include "includes\Functions.nsh"
!include "MUI.nsh"
!include "LogicLib.nsh"

# functions from TextFunc.nsh
!insertmacro FileJoin
!insertmacro LineFind

# constants
!define VERSION "2.3"
!define PREVIOUS_VERSION "2.2"
!define LANGUAGE_PACK_VERSION "2008-12-05"
!define LANGUAGE_PACK_VERSION_INT "20081205"
!define DATE_TIME_STAMP "200812050000"
######################## Added in the extra .0 for 2.3 for constitency 
!define SERVER_VERSION_NUMBER "${VERSION}.0.${DATE_TIME_STAMP}"
!define BASE_VERSION "2.0"
!define SOURCE_JBOSS_HOME "D:\jboss-4.0.2"  ; location of jboss where lams was deployed
!define SOURCE_LAMS_EAR "${SOURCE_JBOSS_HOME}\server\default\deploy\lams.ear\"
!define SOURCE_JBOSS_LIB "${SOURCE_JBOSS_HOME}\server\default\lib"
!define REG_HEAD "Software\LAMS Foundation\LAMSv2"
!define BUILD_REPOSITORY "D:\repository"

# project directories
!define BASE_DIR "..\..\"
!define BUILD_DIR "${BASE_DIR}\build\"
!define BASE_PROJECT_DIR "${BASE_DIR}\..\"
!define DOCUMENTS "..\documents"
!define GRAPHICS "..\graphics"
!define TEMPLATES "..\templates"
!define CONF "..\conf"
!define LIB "..\lib"
!define ANT "ant"
!define SQL "sql"
!define DATABASE "..\..\database\"


# installer settings
!define MUI_ICON "${GRAPHICS}\lams2.ico"
!define MUI_UNICON "${GRAPHICS}\lams2.ico"
Name "LAMS ${VERSION}"
;BrandingText "LAMS ${VERSION} -- built on ${__TIMESTAMP__}"
BrandingText "LAMS ${VERSION} -- built on ${__DATE__} ${__TIME__}"
OutFile "${BUILD_DIR}\LAMS-${VERSION}.exe"
InstallDir "C:\lams"
InstallDirRegKey HKLM "${REG_HEAD}" ""
LicenseForceSelection radiobuttons "I Agree" "I Do Not Agree" 
InstProgressFlags smooth


# set warning when cancelling install
!define MUI_ABORTWARNING

# set welcome page
!define MUI_WELCOMEPAGE_TITLE "LAMS ${VERSION} Install/Update Wizard"
!define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation/update to LAMS ${VERSION}.\r\n\r\n\
    Please ensure you have a copy of MySQL 5.x installed and running, and Java JDK version 1.5.x. or 1.6.x\r\n\r\n\
    Click Next to continue."


# set instfiles page to wait when done
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# display finish page stuff
!define MUI_FINISHPAGE_RUN $INSTDIR\lams-start.exe
!define MUI_FINISHPAGE_RUN_TEXT "Start LAMS now"
;!define MUI_FINISHPAGE_TEXT "The LAMS Server has been successfully installed on your computer."
!define MUI_FINISHPAGE_SHOWREADME $INSTDIR\readme.txt
!define MUI_FINISHPAGE_SHOWREADME_TEXT "Open the readme file"
!define MUI_FINISHPAGE_LINK "Visit LAMS Community"
!define MUI_FINISHPAGE_LINK_LOCATION "http://www.lamscommunity.org"

# installer screen progression
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${DOCUMENTS}\license.txt"
Page custom PreComponents PostComponents
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE DirectoryLeave
!define MUI_PAGE_CUSTOMFUNCTION_PRE skipPage
!insertmacro MUI_PAGE_DIRECTORY
Page custom PreLAMSConfig PostLAMSConfig
Page custom PreMySQLConfig PostMySQLConfig
Page custom PreLAMS2Config PostLAMS2Config
Page custom PreWildfireConfig PostWildfireConfig
Page custom PreFinal PostFinal
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

# uninstaller screens
!insertmacro MUI_UNPAGE_WELCOME
UninstPage custom un.PreUninstall un.PostUninstall
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH


# supported translations
!insertmacro MUI_LANGUAGE "English" # first language is the default language


# reserve files
#
ReserveFile "lams.ini"
ReserveFile "lams-update.ini"
ReserveFile "lams2.ini"
ReserveFile "mysql.ini"
ReserveFile "wildfire.ini"
ReserveFile "lams_components.ini"
ReserveFile "final.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
!insertmacro MUI_RESERVEFILE_LANGDLL

# variables
Var MYSQL_DIR           ; path to user's mysql directory
Var MYSQL_ROOT_PASS     ; root pass for mysql
Var MYSQL_HOST          ; ip address for mysql
Var MYSQL_PORT          ; mysql port
Var DB_NAME             ; db name for lams
Var DB_USER             ; db user for lams
Var DB_PASS             ; db pass for lams
Var JDK_DIR             ; path to user's JDK directory  
Var LAMS_DOMAIN         ; server URL for lams
Var LAMS_PORT           ; PORT for lams usually 8080
Var LAMS_LOCALE         ; default language locale on startup
Var LAMS_REPOSITORY     ; path to repository on user's box
Var LAMS_USER           ; user name for lams system administrater
Var LAMS_PASS           ; password for lams system administrater
Var WILDFIRE_DOMAIN     ; wildfire URL
Var WILDFIRE_USER       ; wildfire username
Var WILDFIRE_PASS       ; wildfie password
Var WINTEMP             ; temp dir
Var RETAIN_DIR          ; path to directory to retain files on uninstall
Var RETAIN_FILES        ; bool value to devide whether to retain files
Var IS_UPDATE           ; bool value to determine whether this is an update
Var INCLUDE_JSMATH      ; bool value to determine whether to include JBOSS
Var TOOL_SIG            ; tool signature used for tool deployer
Var TOOL_DIR            ; tool directory used for tool deployer
Var TIMESTAMP           ; timestamp
Var BACKUP              ; bool value to determine whether the updater will backup
Var OLD_LANG_VERSION    ; int to that gets the old language version from the regestry



# installer sections
SectionGroup "LAMS ${VERSION} Update (Requires LAMS 2.0)" update
    
    Section "!lamsCore" lamsCore
        ${if} $IS_UPDATE == "1"
            Detailprint "Installing LAMS ${VERSION} core"
            
            ; Backing up existing lams installation
            call backupLams
           
            ;ReadRegStr $MYSQL_HOST HKLM "${REG_HEAD}" "mysql_host"
            ; TODO Change after 2.1, get the port from the registry instead or hard coding it
            ;ReadRegStr $MYSQL_PORT HKLM "${REG_HEAD}" "mysql_port"
            ;strcpy $MYSQL_PORT 3360
            
            ;${if} $MYSQL_HOST == ""
            ;    strcpy $MYSQL_HOST "localhost"
            ;${endif}
            
            
            ; removing temporary jboss files
            clearerrors
            Detailprint "Removing $INSTDIR\jboss-4.0.2\server\default\tmp "
            rmdir /r "$INSTDIR\jboss-4.0.2\server\default\tmp"
            Detailprint "Removing $INSTDIR\jboss-4.0.2\server\default\work\jboss.web\localhost"
            rmdir /r "$INSTDIR\jboss-4.0.2\server\default\work\jboss.web\localhost"
            iferrors error continue
            error:
                MessageBox MB_OKCANCEL|MB_ICONQUESTION "Could not remove all of LAMS temporary files. Check all other programs are closed and LAMS is not running $\r$\n$\r$\n If you have just shutdown LAMS then LAMS may still be shutting down - please cancel the installation and then wait for a minute and try to run the installer again. If this problem reoccurs, please delete the following directories manually:$\r$\n$INSTDIR\jboss-4.0.2\server\default\work\jboss.web\localhost $\r$\n$INSTDIR\jboss-4.0.2\server\default\tmp $\r$\n$\r$\nClick ok to continue or cancel to stop installation" IDOK continue IDCANCEL cancel
                cancel:
                Abort
            continue: 
            
            ; setting up ant
            call setupant
            
            ; Updating the the core lams jars/wars
            call updateCoreJarsWars
            
            ; Updating lams-central.war
            call updateLamsCentral
            
            ; Updating lams-www.war
            call updateLamswww
            
            ; Call specific update method for this version
            #call update22Specific
        ${endif}
    SectionEnd
    
    Section "!lamsTools" lamsTools
        
        ${if} $IS_UPDATE == "1"           
            Detailprint "Installing LAMS ${VERSION} tools"
            
            ; Generating the deploy.xml of the tools to support this version
            ; Then Calls deploy tools 
            call createAndDeployTools
            
            ;updates tool contexts for non-default tools
            call insertCustomToolContexts
            
            # update application.xml 
            call updateApplicationXML
            
            ; get the language files locations specific to this server from the database 
            ; for combined activities eg forum and scribe
            ; unpack to $INSTDIR\library\llidx
            call copyllid
            
            ; Updating the database to support version
            ; from 2.2 on, this only handles non auto-patch scripts
            call updateCoreDatabase 
                            
            # changing the instdir back to the original inst dir
            ReadRegStr $INSTDIR HKLM "${REG_HEAD}" "dir_inst"
            
            Call WriteRegEntries
        
            SetOutPath $INSTDIR
            File /a "${BUILD_DIR}\lams-start.exe"
            File /a "${BUILD_DIR}\lams-stop.exe"
            File /a "${BUILD_DIR}\lams-backup.exe"
            File /a "${BUILD_DIR}\lams-restore.exe"
            File /a "${DOCUMENTS}\license.txt"
            File /a "${DOCUMENTS}\license-wrapper.txt"
            File /a "${DOCUMENTS}\readme.txt"
            Call SetupStartMenu
            WriteUninstaller "$INSTDIR\lams-uninstall.exe"
        ${endif}
    SectionEnd

SectionGroupEnd


SectionGroup "LAMS ${VERSION} Full Install" fullInstall
    Section "JBoss 4.0.2" jboss
        ${if} $IS_UPDATE == "0"
            DetailPrint "Setting up JBoss 4.0.2"
            SetOutPath $INSTDIR
            File /a /r /x all /x minimal /x robyn /x log /x tmp /x work /x jsMath.war /x *.bak ${SOURCE_JBOSS_HOME}
                       
            ; Configuring jboss.jar
            ; Copy jboss-cache.jar, jgroups.jar from server/all/lib to server/default/lib
            copyfiles "$INSTDIR\jboss-4.0.2\server\all\lib\jboss-cache.jar" "$INSTDIR\jboss-4.0.2\server\default\lib"
            copyfiles "$INSTDIR\jboss-4.0.2\server\all\lib\jgroups.jar" "$INSTDIR\jboss-4.0.2\server\default\lib"
        ${endif}
    SectionEnd

    Section "LAMS ${VERSION}" lams
        ${if} $IS_UPDATE == "0"
            Detailprint "Installing LAMS ${VERSION}"
            
            SetOutPath $INSTDIR
            
            ${if} $RETAIN_FILES == "1"
                Createdirectory "$WINTEMP\lams"
                CopyFiles /silent $INSTDIR\backup "$WINTEMP\lams"
                DetailPrint "$WINTEMP\lams\backup"
            ${endif}
            
            Call DeployConfig
            Call ImportDatabase
            
            CreateDirectory "$INSTDIR\temp"
            CreateDirectory "$INSTDIR\dump"
            CreateDirectory "$LAMS_REPOSITORY"
            
            # Copy repository for sample lessons
            SetOutPath $LAMS_REPOSITORY
            File /a /r ${BUILD_REPOSITORY}\*
            
            # Log mode is set to INFO in this log4j.xml
            SetOutPath "$INSTDIR\jboss-4.0.2\server\default\conf"
            File /a "${CONF}\log4j.xml"
            
            Call WriteRegEntries
            
            SetOutPath $INSTDIR
            File /a "${BUILD_DIR}\lams-start.exe"
            File /a "${BUILD_DIR}\lams-stop.exe"
            File /a "${BUILD_DIR}\lams-backup.exe"
            File /a "${BUILD_DIR}\lams-restore.exe"
            File /a "${DOCUMENTS}\license.txt"
            File /a "${DOCUMENTS}\license-wrapper.txt"
            File /a "${DOCUMENTS}\readme.txt"
            Call SetupStartMenu
            
            ${if} $RETAIN_FILES == "1"
                Call OverWriteRetainedFiles
            ${endif}
            
            WriteUninstaller "$INSTDIR\lams-uninstall.exe"
        ${endif}
    SectionEnd
    
    Section  "Install as Service" service
        ${if} $IS_UPDATE == "0"
            DetailPrint "Setting up lams ${VERSION} as a service."
            SetOutPath "$INSTDIR\jboss-4.0.2\bin"
            File /a "${BASE_DIR}\wrapper-windows-x86-32-3.2.3\bin\wrapper.exe"
            File /a "/oname=$INSTDIR\jboss-4.0.2\bin\InstallLAMS-NT.bat" "${BASE_DIR}\wrapper-windows-x86-32-3.2.3\bin\InstallTestWrapper-NT.bat"
            File /a "/oname=$INSTDIR\jboss-4.0.2\bin\UninstallLAMS-NT.bat" "${BASE_DIR}\wrapper-windows-x86-32-3.2.3\bin\UninstallTestWrapper-NT.bat"
            SetOutPath "$INSTDIR\jboss-4.0.2\lib"
            File /a "${BASE_DIR}\wrapper-windows-x86-32-3.2.3\lib\wrapper.dll"
            File /a "${BASE_DIR}\wrapper-windows-x86-32-3.2.3\lib\wrapper.jar"
            CreateDirectory "$INSTDIR\jboss-4.0.2\conf"
            CopyFiles "$INSTDIR\wrapper.conf" "$INSTDIR\jboss-4.0.2\conf\wrapper.conf"
            CreateDirectory "$INSTDIR\jboss-4.0.2\logs"
            nsExec::ExecToStack '$INSTDIR\jboss-4.0.2\bin\InstallLAMS-NT.bat'
            Pop $0
            ${If} $0 == 0
                DetailPrint "LAMSv2 successfully setup as a service. ($0)"
            ${Else}
                DetailPrint "LAMSv2 was not setup as a service. ($0)"
                MessageBox MB_OK|MB_ICONEXCLAMATION "LAMSv2 was not installed as a service.  However you may start LAMS by double-clicking $INSTDIR\jboss-4.0.2\bin\run.bat."
            ${EndIf} 
        ${endif}           
    SectionEnd
SectionGroupEnd

SectionGroup "jsMath (optional)"
    
    Section "jsMath (expanded)" jsmathe
        ${if} $INCLUDE_JSMATH == 1
            DetailPrint "Including jsMath in LAMS ${VERSION}"
            SetOutPath "$TEMP"
            File /a  "${BASE_PROJECT_DIR}jsmath\build\lib\jsMath.war"
            CreateDirectory "$INSTDIR\jboss-4.0.2\server\default\deploy\jsMath.war"
            SetOutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\jsMath.war"
            DetailPrint "$JDK_DIR\bin\jar xvf $TEMP\jsMath.war"
            DetailPrint "Expanding jsMath.war... This may take several minutes"
            nsExec::ExecToStack "$JDK_DIR\bin\jar xf $TEMP\jsMath.war"
            Pop $0
            Pop $1
            ${If} $0 != 0
                DetailPrint "Failed to expand jsMath.war."
                DetailPrint "Error: $1"
            ${EndIf}
            Delete "$TEMP\jsMath.war"
         ${endif}
    SectionEnd
SectionGroupEnd


# functions
#

Function .onInit
    
    # Checking to see if LAMS is installed 
    call checkRegistry
    
    ${if} $IS_UPDATE == "1"
        # check if LAMS is stopped
        SetOutPath $TEMP
        File "${BUILD_DIR}\LocalPortScanner.class"
        ReadRegStr $0 HKLM "${REG_HEAD}" "lams_port"
        ReadRegStr $1 HKLM "${REG_HEAD}" "dir_jdk"
        Goto checklams
        
        checklams:
            nsExec::ExecToStack "$1\bin\java LocalPortScanner $0"
            Pop $2
            ${If} $2 == 2
                MessageBox MB_YESNO|MB_ICONQUESTION "LAMS appears to be running.  Stop LAMS and continue with update? (Will take a few seconds)" \
                    IDYES stoplams \
                    IDNO quit
            ${EndIf}
            Goto continue
        stoplams:
            nsExec::ExecToStack 'sc stop LAMSv2'
            Pop $0
            Pop $1
            DetailPrint "Sent stop command to LAMS service."
            # sleep for 10s to ensure that JBoss closes properly
            sleep 10000
            Goto checklams
        quit:
            Delete "$TEMP\LocalPortScanner.class"
            MessageBox MB_OK|MB_ICONSTOP "LAMS ${VERSION} update cannot continue while LAMS is running."
            Abort
        continue:
            Delete "$TEMP\LocalPortScanner.class"
            
        # Reading the registry values
        Detailprint "Reading existing LAMS data from registry"
        call readRegistry
        
        
        # clearing the update-logs directory
        Detailprint "Removing existing update logs"
        rmdir /r "$INSTDIR\update-logs"
        SectionSetSize ${jboss} 0
        SectionSetSize ${lams} 0
        SectionSetSize ${service} 0
     ${else}
        SectionSetSize ${lamsCore} 0
        SectionSetSize ${lamsTools} 0
     ${endif}
    
    
    # extract custom page display config
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "lams.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "lams-update.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "lams2.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "mysql.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "wildfire.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "lams_components.ini"
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "final.ini"
    
    # set jsmath exploded size (assumes 4KB cluster size on destination hdd)
    ;SectionSetSize ${jsmathe} 81816
    
FunctionEnd


################################################################################
# USER INTERFACE CODE                                                          #
################################################################################

# Skips the directory page if this is an update
Function skipPage
    ${if} $IS_UPDATE == "1"
        Abort
    ${endif}
FunctionEnd

Function checkRegistry
    # Check the current version installed (if any)
    ReadRegStr $0 HKLM "${REG_HEAD}" "version" 
    
    ${if} $0 == ""
        # LAMS 2.0 is not installed, do full install
        strcpy $IS_UPDATE "0"
    ${else}
        # LAMS is installed. Check if the current version is installed
        ${VersionCompare} $0 ${VERSION} $1
        ${if} $1 == 0
        ${orif} $1 == 1
            MessageBox MB_OK|MB_ICONSTOP "You already have LAMS $0 Installed on your computer."
            Abort
        ${elseif} $0 == ${PREVIOUS_VERSION}
            # This is the correct version to update to
            strcpy $IS_UPDATE "1" 
        ${else}
            MessageBox MB_OK|MB_ICONSTOP "Your existing version of LAMS ($0) is not compatible with this update. $\r$\n$\r$\nPlease update to LAMS-${PREVIOUS_VERSION} before running this update."
            Abort 
        ${endif}
    ${endif}
FunctionEnd


Function CheckJava
    # check for JDK
    ${If} $JDK_DIR == ""
        ReadRegStr $JDK_DIR HKLM "SOFTWARE\JavaSoft\Java Development Kit\1.6" "JavaHome"
        ${If} $JDK_DIR == ""
            ReadRegStr $JDK_DIR HKLM "SOFTWARE\JavaSoft\Java Development Kit\1.5" "JavaHome"
            ${if} $JDK_DIR == ""
                MessageBox MB_OK|MB_ICONSTOP "Could not find a Java JDK 1.5 or 1.6 installation.  Please enter where you have java 5 or 6 installed."
            ${EndIf}
        ${EndIf}
    ${endif}

FunctionEnd

Function Checkjava2    
    # check java version using given dir
    nsExec::ExecToStack '$JDK_DIR\bin\javac.exe -version'
    Pop $0
    Pop $1
    ${StrStr} $0 $1 "1.6"
    ${If} $0 == ""
        ${StrStr} $0 $1 "1.5"
        ${If} $0 == ""
            MessageBox MB_OK|MB_ICONEXCLAMATION "Could not find a Java 5 or Java 6 installation in the given directory. $\r$\nPlease check your Java installation and try again.$\r$\n$\r$\n$JDK_DIR"
            Abort
        ${EndIf}
    ${EndIf}
FunctionEnd


Function CheckMySQL  
        # check mysql version is 5.0.x
        Setoutpath "$TEMP\lams\"
        
        #StrLen $9 $MYSQL_ROOT_PASS
        
        ${if} $IS_UPDATE == "1" 
           Strcpy $0 '$JDK_DIR\bin\java.exe -cp ".;$TEMP\lams\mysql-connector-java-3.1.12-bin.jar" checkmysqlversion "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8" "$DB_USER" "$DB_PASS"'
        ${elseif} $MYSQL_HOST != "localhost"
           Strcpy $0 '$JDK_DIR\bin\java.exe -cp ".;$TEMP\lams\mysql-connector-java-3.1.12-bin.jar" checkmysqlversion "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8" "$DB_USER" "$DB_PASS"' 
        ${else}
           Strcpy $0 '$JDK_DIR\bin\java.exe -cp ".;$TEMP\lams\mysql-connector-java-3.1.12-bin.jar" checkmysqlversion "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/?characterEncoding=utf8" "root" "$MYSQL_ROOT_PASS"'
        ${endif}
        
        File "${BUILD_DIR}\checkmysqlversion.class"
        File "${LIB}\mysql-connector-java-3.1.12-bin.jar"
        nsExec::ExecToStack $0
        Pop $0
        Pop $1
        ${If} $0 != 0
            ${StrStr} $3 $1 "UnknownHostException"
            ${if} $3 == "" 
                MessageBox MB_OK|MB_ICONEXCLAMATION "An error occurred whilst checking your mysql configuration $\r$\n$\r$\nError: $1"
            ${else}
                MessageBox MB_OK|MB_ICONEXCLAMATION "An error occurred whilst checking your mysql configuration $\r$\n$\r$\nError: Could not connect to MySql host: $MYSQL_HOST. Please check your database configurations and try again."
            ${endif}               
            Abort
        ${EndIf}
        Delete "$TEMP\lams\checkmysql.class"
        Delete "$TEMP\mysql-connector-java-3.1.12-bin.jar"  
FunctionEnd

Function PreComponents
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 4" "Text" "LAMS ${VERSION} Full Install"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 1" "Text" "- JBoss 4.0.2"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 2" "Text" "- LAMS ${VERSION} Core"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 3" "Text" "- Install LAMS as a service"
        !insertmacro MUI_HEADER_TEXT "LAMS ${VERSION} Components" "No installation of LAMS 2.0 was found on your computer. A full ${VERSION} installation required to run LAMS"
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "lams_components.ini"
    ${else}
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 4" "Text" "LAMS ${VERSION} Update"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 1" "Text" "- LAMS ${VERSION} Core"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams_components.ini" "Field 2" "Text" "- LAMS ${VERSION} Tools"
        !insertmacro MUI_HEADER_TEXT "LAMS ${VERSION} Components" "Lams ${PREVIOUS_VERSION} is installed. Proceeding with update to ${VERSION}"
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "lams_components.ini"
    ${endif}
FunctionEnd 

Function PostComponents
        !insertmacro MUI_INSTALLOPTIONS_READ $INCLUDE_JSMATH "lams_components.ini" "Field 5" "State"
        ${if} $INCLUDE_JSMATH == "1"
            SectionSetSize ${jsmathe} 81816
        ${else}
            SectionSetSize ${jsmathe} 0
        ${endif}

FunctionEnd

Function DirectoryLeave
    # check for spaces in instdir
    ${StrStr} $0 $INSTDIR " "
    ${If} $0 != ""
        MessageBox MB_OK|MB_ICONEXCLAMATION "Please choose a location without a space."
        Abort
    ${EndIf}
    # check LAMS 1
    ReadRegStr $0 HKLM "SOFTWARE\LAMS\Server" "install_dir"
    ReadRegStr $1 HKLM "SOFTWARE\LAMS\Server" "install_status"
    ReadRegStr $2 HKLM "SOFTWARE\LAMS\Server" "install_version"
    ${If} $1 == "successful"
        MessageBox MB_OK|MB_ICONINFORMATION "If you have LAMS $2 on your system, remember not to run it at the same time as LAMS ${VERSION} (unless you know what you're doing)."
        ${If} $0 == $INSTDIR
            MessageBox MB_OK|MB_ICONEXCLAMATION "There appears to be a LAMS $2 installation at $INSTDIR - please chose another location."
            Abort
        ${EndIf}
    ${EndIf}
    
    Strcpy $RETAIN_FILES "0"

    
    IfFileExists "$INSTDIR\backup\backup.zip" backupExists end
    backupExists:
        ; CHECK if there are files retained from a previous uninstall
        ; THEN after installation, overwrite retained files and free files from temp folder temp folder 
        strcpy $6 ""
        MessageBox MB_YESNO|MB_ICONQUESTION "Installer has detected database, repository and uploaded files retained from a previous install, do you wish to use them?" \
                    IDYES retainFiles \
                    IDNO noRetain
        noRetain:
            MessageBox MB_OK "Files will be backed up at $INSTDIR\backup"
            goto end
        retainFiles:
            Strcpy $WINTEMP "C:\WINDOWS\Temp"
            Strcpy $RETAIN_FILES "1"
            #CopyFiles $INSTDIR $WINTEMP
            #MessageBox MB_OK|MB_ICONEXCLAMATION "$RETAIN_FILES \n $RETAIN_REP $\n $RETAIN_CONF $\n $RETAIN_DB"
    end:
FunctionEnd


Function PreMySQLConfig
    ${if} $IS_UPDATE == "0"
        #Call CheckMySQL
        # check for MySQL in registry
        ReadRegStr $MYSQL_DIR HKLM "SOFTWARE\MySQL AB\MySQL Server 5.0" "Location"
        ${If} $MYSQL_DIR == ""
            ReadRegStr $MYSQL_DIR HKLM "SOFTWARE\MySQL AB\MySQL Server 5.1" "Location"
            ${If} $MYSQL_DIR == ""
                    #messageBox MB_OK|MB_ICONSTOP "Could not find a MySQL installation.  Please ensure you have MySQL 5.0 or 5.1 installed."
            ${endif}
        ${endif}
        
        !insertmacro MUI_INSTALLOPTIONS_WRITE "mysql.ini" "Field 3" "State" "$MYSQL_DIR"
        !insertmacro MUI_HEADER_TEXT "Setting Up MySQL Database Access (2/4)" "Choose a MySQL database and user account for LAMS.  If unsure, use the defaults."
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "mysql.ini"
    ${else}
        #Call CheckMySQL
    ${endif}
FunctionEnd

Function PostMySQLConfig
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_DIR "mysql.ini" "Field 3" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_ROOT_PASS "mysql.ini" "Field 5" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $DB_NAME "mysql.ini" "Field 7" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $DB_USER "mysql.ini" "Field 9" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $DB_PASS "mysql.ini" "Field 10" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_HOST "mysql.ini" "Field 14" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_PORT "mysql.ini" "Field 16" "State"
        
        
        #TODO should automatically check presence of database on remote computer
        ${if} $MYSQL_HOST != 'localhost'
            ; check the length of the root password to see if we need it
            StrLen $9 $MYSQL_ROOT_PASS
       
            ; TRY ACCESSING LAMS DATABASE USING USER
            DetailPrint "Cannot access remote mysql using root user"
            DetailPrint "Attempting to access LAMS database using LAMS user"
            
            StrCpy $0 '$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -sN -u$DB_USER -p$DB_PASS -e "select 1" $DB_NAME' 
            DetailPrint $0
            nsExec::ExecToStack $0
            Pop $0
            Pop $1
            ${If} $0 == 1
                DetailPrint "Cannot access remote lams database using lams mysql user"
                MessageBox MB_OK|MB_ICONINFORMATION  "Could not find '$DB_NAME' database on remote host using user '$DB_USER'. $\r$\n\
                  Please ensure that you have created the $DB_NAME database on your MySQL host $MYSQL_HOST \
                  and you have given the '$DB_USER' user remote permissions before proceeding."                
                Abort
            ${EndIf}
        ${endif}
        
        #Call CheckMySQL
        # Checking if the given database name already exists in the mysql database list
        /*ifFileExists "$MYSQL_DIRdata\$DB_NAME\*.*" databaseNameExists continue1
        databaseNameExists:
            MessageBox MB_OK|MB_ICONSTOP "Database $DB_NAME already exists. Please try a different database name"
            quit   
        continue1:
    ${else}
        # Checking if the given database name already exists in the mysql database list
        ifFileExists "$MYSQL_DIR\data\$DB_NAME\*.*" continue NoDatabaseNameExists
        NoDatabaseNameExists:
                MessageBox MB_OK|MB_ICONSTOP "Could not find database $DB_NAME. Please check your database settings and try again"
            quit   
        continue:
        */
    ${endif}
    
    # check mysql version is 5.0.x
    call CheckMySQL 
    
    # check root password, server status
    /*StrLen $0 $MYSQL_ROOT_PASS
    ${If} $0 == 0
        nsExec::ExecToStack '$MYSQL_DIR\bin\mysqladmin ping -u root'
    ${Else}
        nsExec::ExecToStack '$MYSQL_DIR\bin\mysqladmin ping -u root -p$MYSQL_ROOT_PASS'
    ${EndIf}
    Pop $0
    Pop $1
    # check root password is correct
    ${StrStr} $2 $1 "Access denied"
    ${If} $2 != ""
        MessageBox MB_OK|MB_ICONEXCLAMATION "The MySQL root password appears to be incorrect - please re-enter your password."
        ${if} $IS_UPDATE == "0"
            Abort
        ${else}
            quit
        ${endif}
    ${EndIf}
    # check mysql is running
    ${StrStr} $2 $1 "mysqld is alive"
    ${If} $2 == ""
        MessageBox MB_OK|MB_ICONEXCLAMATION "MySQL does not appear to be running - please make sure it is running before continuing."
        ${if} $IS_UPDATE == "0"
            Abort
        ${else}
            quit
        ${endif}
    ${EndIf}
    */
FunctionEnd


Function PreLAMSConfig
    Call CheckJava
    ${if} $IS_UPDATE == "0"

        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams.ini" "Field 2" "State" "$JDK_DIR"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams.ini" "Field 4" "State" "$INSTDIR\repository"
        !insertmacro MUI_HEADER_TEXT "Setting Up LAMS (1/4)" "Configure the LAMS Server.  If unsure, use the defaults."
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "lams.ini"
    ${else}
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams-update.ini" "Field 2" "State" "$JDK_DIR"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams-update.ini" "Field 6" "State" "$MYSQL_HOST"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "lams-update.ini" "Field 7" "State" "$MYSQL_PORT"
        !insertmacro MUI_HEADER_TEXT "Java setup" "If you have changed your java installation since installing LAMS ${PREVIOUS_VERSION}, please enter the new details."
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "lams-update.ini"
    ${endif}
FunctionEnd


Function PostLAMSConfig
    
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_READ $JDK_DIR "lams.ini" "Field 2" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_REPOSITORY "lams.ini" "Field 4" "State"
        
        # check java version using given dir
        Call Checkjava2
    ${else}
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_HOST "lams-update.ini" "Field 6" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $MYSQL_PORT "lams-update.ini" "Field 7" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $JDK_DIR "lams-update.ini" "Field 2" "State"
        #!insertmacro MUI_INSTALLOPTIONS_READ $LAMS_REPOSITORY "lams-update.ini" "Field 4" "State"
        
        # check java version using given dir
        Call Checkjava2
        
        Setoutpath "$TEMP\lams\"
        File "${BUILD_DIR}\checkmysql.class"
        File "${LIB}\mysql-connector-java-3.1.12-bin.jar"
        nsExec::ExecToStack '$JDK_DIR\bin\java.exe -cp ".;$TEMP\lams\mysql-connector-java-3.1.12-bin.jar" checkmysql "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8" $DB_USER $DB_PASS ${PREVIOUS_VERSION}'
        Pop $0
        Pop $1
        
        ${If} $0 != 0
            ${StrStr} $3 $1 "UnknownHostException"
            
            ${if} $3 == "" 
                MessageBox MB_OK|MB_ICONEXCLAMATION "An error occurred whilst checking your mysql configuration $\r$\n$\r$\nError: $1"
            ${else}
                MessageBox MB_OK|MB_ICONEXCLAMATION "An error occurred whilst checking your mysql configuration $\r$\n$\r$\nError: Could not connect to MySql host: $MYSQL_HOST. Please check your database configurations and try again."
            ${endif}               
            Abort
        ${EndIf}
        
        Delete "$TEMP\lams\checkmysql.class"
        Delete "$TEMP\mysql-connector-java-3.1.12-bin.jar"
        
    ${endif}
    
    done:
    
FunctionEnd


Function PreLAMS2Config
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_HEADER_TEXT "Setting Up LAMS (3/4)" "Configure the LAMS Server, and choose an admin username and password."
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "lams2.ini"
    ${endif}
FunctionEnd


Function PostLAMS2Config
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_DOMAIN "lams2.ini" "Field 8" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_PORT "lams2.ini" "Field 9" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_LOCALE "lams2.ini" "Field 12" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_USER "lams2.ini" "Field 2" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $LAMS_PASS "lams2.ini" "Field 5" "State"
    ${endif}
FunctionEnd


Function PreWildfireConfig
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_HEADER_TEXT "Setting Up Wildfire Chat Server (4/4)" "Configure Wildfire, chat server for LAMS.  If unsure, use the default."
        !insertmacro MUI_INSTALLOPTIONS_DISPLAY "wildfire.ini"
    ${endif}
FunctionEnd


Function PostWildfireConfig
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_READ $WILDFIRE_DOMAIN "wildfire.ini" "Field 2" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $WILDFIRE_USER "wildfire.ini" "Field 5" "State"
        !insertmacro MUI_INSTALLOPTIONS_READ $WILDFIRE_PASS "wildfire.ini" "Field 7" "State"

        # check wildfire is running by checking client connection port 5222
        SetOutPath $TEMP
        File "${BUILD_DIR}\LocalPortScanner.class"
        nsExec::ExecToStack '$JDK_DIR\bin\java LocalPortScanner 5222'
        Pop $0
        Pop $1
        ${If} $0 == 0
            MessageBox MB_OKCANCEL|MB_ICONQUESTION "Wildfire does not appear to be running - LAMS will be OK, but chat rooms will be unavailable." IDOK noWildfire IDCANCEL cancel
            cancel:
                Abort
            noWildfire:
        ${EndIf}
     ${endif}
FunctionEnd

Function PreFinal
    ${if} $IS_UPDATE == "0"
        !insertmacro MUI_INSTALLOPTIONS_WRITE "final.ini" "Field 2" "Text" "Click 'Install' to commence installation of LAMS ${VERSION}"
    ${else} 
        !insertmacro MUI_INSTALLOPTIONS_WRITE "final.ini" "Field 2" "Text" "Click 'Install' to commence update to LAMS ${VERSION}"
    ${endif}   
    !insertmacro MUI_HEADER_TEXT "LAMS ${VERSION}" "Configuration Completed"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY "final.ini"
FunctionEnd

Function PostFinal

    Call GetLocalTime
    Pop "$0" ;Variable (for day)
    Pop "$1" ;Variable (for month)
    Pop "$2" ;Variable (for year)
    Pop "$3" ;Variable (for day of week name)
    Pop "$4" ;Variable (for hour)
    Pop "$5" ;Variable (for minute)
    Pop "$6" ;Variable (for second)
    
    strlen $7 $0
    ${if} $7 == 1
        strcpy $0 "0$0"
    ${endif}
    
    strlen $7 $1
    ${if} $7 == 1
        strcpy $1 "0$1"
    ${endif}
    
    strlen $7 $4
    ${if} $7 == 1
        strcpy $4 "0$4"
    ${endif}
    
    strlen $7 $5
    ${if} $7 == 1
        strcpy $1 "0$5"
    ${endif}
    
    
    strcpy $TIMESTAMP "$2$1$0$4$5"

    strcpy $BACKUP "0"
    ${if} $IS_UPDATE == "1"
        MessageBox MB_YESNOCANCEL|MB_ICONQUESTION "Do you wish to backup your LAMS installation? (Recommended) $\r$\nBackup dir: $INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak $\r$\n$\r$\nNOTE: You must have MySql installed on this machine to do this." IDNO continue IDCANCEL cancel
            # check mysql version is 5.0.x
#            nsExec::ExecToStack '$MYSQL_DIR\bin\mysqladmin --version'
            call CheckMySQL
            /*Pop $0
            Pop $1
            ${if} $0 != 0
                MessageBox MB_OK|MB_ICONSTOP "Error checking mysql, please check that MySql is installed in the same location as written in the registry under ${REG_HEAD}"
                abort
            ${endif}
            ${If} $1 == "" ; if mySQL install directory field is empty, do not continue
                MessageBox MB_OK|MB_ICONSTOP "No MySql installation found"
                abort
            ${EndIf}
            ${StrStr} $0 $1 "5.0"
            ${If} $0 == "" ; if not 5.0.x, check 5.1.x
                ${StrStr} $0 $1 "5.1"
                ${If} $0 == ""
                    MessageBox MB_OK|MB_ICONSTOP "Your MySQL version does not appear to be compatible with LAMS (5.0.x or 5.1.x): $\r$\n$1"
                    MessageBox MB_OK|MB_ICONSTOP "Your MySQL directory does not appear to be valid, please enter a valid MySQL directory before continuing.$\r$\n$\r$\n$1"
                    ${if} $IS_UPDATE == "0"
                        Abort
                    ${else}
                        quit
                    ${endif}
                ${EndIf}
            ${EndIf}
            */
            strcpy $BACKUP "1"
            goto continue
            cancel:
                Abort
            continue:
    ${endif}
    
     goto done
    error:
        DetailPrint "Error getting system time"
        MessageBox MB_OK|MB_ICONSTOP "Error getting system time $\r$\nError:$\r$\n$\r$\n$1 $\r$\n$\r$\n$0"
        Abort "Error getting system time"
    done: 
FunctionEnd

################################################################################
# END USER INTERFACE CODE                                                      #
################################################################################




################################################################################
# CODE USED FOR UPDATER                                                        #
################################################################################

Function setupant
    
    # extract support files to write configuration
    SetOutPath $INSTDIR
    File /r "${BASE_DIR}\apache-ant-1.6.5"
    
    # Extract the ant scripts 
    SetOutPath "$TEMP\lams"
    
    File "${ANT}\update-deploy-tools.xml"
    
    # use Ant to write config to files
    clearerrors
    FileOpen $0 "$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" w
    IfErrors 0 +2
        goto error
    FileWrite $0 "@echo off$\r$\nset JAVACMD=$JDK_DIR\bin\java$\r$\n"
    FileClose $0
    ${FileJoin} "$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" "$INSTDIR\apache-ant-1.6.5\bin\ant.bat" ""

    goto done
    error:
        DetailPrint "Error setting up ant"
        MessageBox MB_OK|MB_ICONSTOP "Error setting up ant "
        Abort "Lams configuration failed"
    done:
Functionend


; Backs up existing lams installation
Function backupLams
    ${if} $BACKUP == "1"
        ${if} $MYSQL_HOST == "localhost"
            clearerrors
            iffileexists "$INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak\*.*" backupExists continue
            backupExists:
                DetailPrint "Lams backup failed"
                MessageBox MB_OK|MB_ICONSTOP "Lams backup failed, please delete or change the name of the backup file before continuing with the update$\r$\n$INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak"
                Abort "LAMS configuration failed"
            continue:
            
            DetailPrint "Backing up lams at: $INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak. This may take a few minutes"
            SetDetailsPrint listonly
            copyfiles /SILENT $INSTDIR  $INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak 86000
            SetDetailsPrint both
            iferrors error1 continue1
            error1:
                DetailPrint "Backup failed"
                MessageBox MB_OK|MB_ICONSTOP "LAMS backup to $INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak failed. Check that all other applications are closed and LAMS is not running." 
                Abort
            continue1: 
            
            DetailPrint 'Dumping database to: $INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak'
            setoutpath "$INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak"
            Strcpy $4 '"$MYSQL_DIR\bin\mysqldump" -h "$MYSQL_HOST" -P "$MYSQL_PORT" -r "$INSTDIR-${PREVIOUS_VERSION}-$TIMESTAMP.bak\dump.sql" $DB_NAME -u $DB_USER -p$DB_PASS'
            DetailPrint $4
            nsExec::ExecToStack $4
            Pop $0
            Pop $1
            ${If} $0 == "yes"
                goto error
            ${EndIf}
              
            goto done
            error:
                DetailPrint "Database dump failed"
                MessageBox MB_OK|MB_ICONSTOP "Database dump failed $\r$\nError:$\r$\n$\r$\n$1"
                Abort "Database dump failed"
            done:
        ${else}
            MessageBox MB_YESNO|MB_ICONQUESTION "Unable to backup LAMS. MYSQL_HOST is not set to localhost. Manual backup required. $\r$\n$\r$\nWould you like to quit the update and backup LAMS manually? If you choose 'No', the update will proceed and your existing LAMS installation will be overwritten." IDYES quit IDNO continue2
                quit:
                    Abort
                continue2:
        ${endif}
    ${endif}
FunctionEnd


; Updating the the core lams jars/wars
; Needs lams_build/build.xml tasks assemble-ear and deploy-ear-updater to be executed first
Function updateCoreJarsWars
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear"
    ;File "${SOURCE_LAMS_EAR}\*.*"
    File /a /r /x tmp /x lams-www.war /x lams-central.war /x META-INF /x jsMath.war /x *.bak ${SOURCE_LAMS_EAR}\*.*
    
    Setoutpath "$INSTDIR\jboss-4.0.2\server\default\lib"
    File "${SOURCE_JBOSS_LIB}\lams-session.jar"
    File "${SOURCE_JBOSS_LIB}\lams-valve.jar"
    
    createdirectory "$INSTDIR\update-logs" 
FunctionEnd

; Updating lams-central.war
Function updateLamsCentral
    strcpy $0 "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-central.war"
    SetoutPath $0
    File /r /x MANIFEST.MF "${SOURCE_LAMS_EAR}\lams-central.war\*"
FunctionEnd

; Updating lams-www.war
Function updateLamswww 
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\images\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\images\learner.logo.swf"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\images\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\images\about.logo.swf"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\images\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\images\monitor.logo.swf"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\images\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\images\preloader.logo.swf"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\images\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\images\template*.gif"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\htmltemplates.xml"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\WEB-INF\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\WEB-INF\lams.tld"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\WEB-INF\tags\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\WEB-INF\tags\*"
    
    SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
    File "${SOURCE_LAMS_EAR}\lams-www.war\copyright.jsp"
    
    ;SetoutPath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
    ;File "${DOCUMENTS}\news-${PREVIOUS_VERSION}.html"
    ;if news and news-${PREVIOUS_VERSION} are the same

    # Commented out below, see LI-157
    /*
    nsExec::ExecToStack 'fc $INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\news.html $INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\news-${PREVIOUS_VERSION}.html'
    pop $0
    pop $1
    ${StrStr} "$3" "$1" "FC: no differences encountered"
    ;if there aren't any differences output the new news.html
    ${if} "$3" != ""
        File "${BASE_PROJECT_DIR}\lams_www\conf\lams\news.html"
    ${endif}
    */
FunctionEnd


; Updating the database to support version
Function updateCoreDatabase

    # generate a sql file to update the configuration table
    Createdirectory "$TEMP\lams\sql\"
    ClearErrors
    
    
    FileOpen $0 "$TEMP\lams\sql\updateConfiguration.sql" w
    IfErrors 0 +3
        Detailprint "Problem opening updateConfiguration.sql to write"
        goto error
    FileWrite $0 "update lams_configuration set config_value='${SERVER_VERSION_NUMBER}' where config_key='LearnerClientVersion' OR config_key='ServerVersionNumber' OR config_key='MonitorClientVersion' OR config_key='AuthoringClientVersion';$\r$\n"
    Filewrite $0 "update lams_configuration set config_value='${VERSION}' where config_key='Version';$\r$\n"
    Filewrite $0 "update lams_configuration set config_value='${LANGUAGE_PACK_VERSION}' where config_key='DictionaryDateCreated';$\r$\n"
    
    Fileclose $0
    IfErrors 0 +3
        Detailprint "Problem closing updateConfiguration.sql"
        goto error

    
    # generate a properties file 
    ClearErrors
    FileOpen $0 $TEMP\lams\core.properties w
    IfErrors 0 +3
        Detailprint "Problem opening core.properties to write"
        goto error
    
    # convert '\' to '/' for Ant's benefit
    Push $TEMP
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "temp=$2/$\r$\n"
            
    Push $INSTDIR
    Push "\"
    Call StrSlash
    Pop $2
    
    FileWrite $0 "instdir=$2/$\r$\n"
    FileWrite $0 "db.name=$DB_NAME$\r$\n"
    FileWrite $0 "db.username=$DB_USER$\r$\n"
    FileWrite $0 "db.password=$DB_PASS$\r$\n"
    FileWrite $0 "db.Driver=com.mysql.jdbc.Driver$\r$\n"
    FileWrite $0 "db.url=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$${db.name}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=true$\r$\n"
    FileWrite $0 "jboss.deploy=$${instdir}/jboss-4.0.2/server/default/deploy/lams.ear/$\r$\n"

    Fileclose $0
    IfErrors 0 +2
        goto error
    

    # Copying the core sql update scriptes to $TEMP/lams/sql
    setoutpath "$TEMP\lams\sql"
    ;file "${BASE_PROJECT_DIR}\lams_common\db\sql\updatescripts\alter_${VERSION}*.sql" 
    ;file "${BASE_PROJECT_DIR}\lams_common\db\sql\updatescripts\alter_203*.sql" 
    ;file "${BASE_PROJECT_DIR}\lams_common\db\sql\updatescripts\alter_21*.sql" 
    file "${SQL}\updateLocales.sql" 
     
    setoutpath "$TEMP\lams\"
    file "${ANT}\update-core-database.xml"
    

    # Running the ant scripts to create deploy.xml for the normal tools 
    strcpy $0 '"$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" -logfile "$INSTDIR\update-logs\ant-update-core-database.log" -buildfile "$TEMP\lams\update-core-database.xml" update-core-database'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    
    push "$INSTDIR\update-logs\ant-update-core-database.log"
    push "Failed"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    goto done
    error:
        DetailPrint "LAMS core database updates failed"
        MessageBox MB_OK|MB_ICONSTOP "LAMS core database updates failed, check update logs in the installation directory for details $\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed"
    done:
FunctionEnd 

; Updating application.xml
; ANT code implemented in update-deploy-tools.xml
; CODE is version specific

Function updateApplicationXML
    
    # Running the ant scripts to update web.xmls and manifests
    strcpy $0 '"$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" -logfile "$INSTDIR\update-logs\ant-update-application-xml.log" -buildfile "$TEMP\lams\update-deploy-tools.xml" update-application-xml'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    
    push "$INSTDIR\update-logs\ant-update-application-xml.log"
    push "DeployException"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    
    goto done
    error:
        DetailPrint "Application.xml update failed"
        MessageBox MB_OK|MB_ICONSTOP "Application.xml update failed, check update logs in the installation directory for details $\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed"
    done:
FunctionEnd
        
; Updating the deploy.xml of the tools to support this version using the tool deployer, called by create-deploy-package ant task
; Then deploys the tools using the tool deployer. Called by ant task deploy-tool
Function createAndDeployTools
    
    ; setting the first tool sig for properties files
    #strcpy $TOOL_SIG "lachat11"
    
    # Generate the tools.properties for properties common to all tools
    call generateToolProperties
    
    # Creating all tool packages, and copying to temp/lams
    call createNewToolPackages
    
    /*
    # Copying tool-specific build.property files
    call copyToolBuildProperties
    
    # Get the jars and wars required for each tool
    call extractToolJars
    */
    
    # Get the java libraries needed for the tool deployer
    SetOutPath "$TEMP\lams\lib"
    File "${BASE_PROJECT_DIR}\lams_build\deploy-tool\lib\*.jar"
    

    # Exploding the lams-learning.war and lams-monitoring.war
    strcpy $0 '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile $INSTDIR\update-logs\ant-explode-wars.log -buildfile $TEMP\lams\update-deploy-tools.xml explode-wars'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    push "$INSTDIR\update-logs\ant-explode-wars.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    
    
    # Deoploying 2.3 tool packages
    # assessment, pixlr, mindmap, image gallery, video recorder 
    ############################################################################
    
    strcpy $TOOL_SIG "laasse10"
    strcpy $TOOL_DIR "lams_tool_assessment"
    call filterDeployXML
    call deployTool
    call runUpdateToolContext
    
    strcpy $TOOL_SIG "lapixl10"
    strcpy $TOOL_DIR "lams_tool_pixlr"
    call filterDeployXML
    call deployTool
    call runUpdateToolContext
    
    strcpy $TOOL_SIG "lamind10"
    strcpy $TOOL_DIR "lams_tool_mindmap"
    call filterDeployXML
    call deployTool
    call runUpdateToolContext
    
    strcpy $TOOL_SIG "laimag10"
    strcpy $TOOL_DIR "lams_tool_images"
    call filterDeployXML
    call deployTool
    call runUpdateToolContext
    
    strcpy $TOOL_SIG "lavidr10"
    strcpy $TOOL_DIR "lams_tool_videorecorder"
    call filterDeployXML
    call deployTool
    call runUpdateToolContext
    
    ############################################################################


    strcpy $0 '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile $INSTDIR\update-logs\ant-compress-wars.log -buildfile $TEMP\lams\update-deploy-tools.xml compress-wars'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    push "$INSTDIR\update-logs\ant-compress-wars.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    
    
    goto done
    error:
        DetailPrint "Problem compressing/expanding lams-monitoring.war and lams-learning.war"
        MessageBox MB_OK|MB_ICONSTOP "Problem compressing/expanding lams-monitoring.war and lams-learning.war $\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed"
    done:
    
FunctionEnd

# Running the ant scripts to create deploy.xml for the normal tools 
# Tool created depends on the value of $TOOL_SIG
Function filterDeployXML
    # Running the ant scripts to create deploy.xml for the normal tools 
    strcpy $0 '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile $INSTDIR\update-logs\ant-create-deploy-package-$TOOL_SIG.log -buildfile $TEMP\lams\update-deploy-tools.xml -propertyfile $TEMP\lams\$TOOL_SIG\build.properties create-deploy-package'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    push "$INSTDIR\update-logs\ant-create-deploy-package-$TOOL_SIG.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    

    goto done
    error:
        DetailPrint "Ant create-tools-package failed, check update-logs for details"
        MessageBox MB_OK|MB_ICONSTOP "Ant create-tools-package failed, check update-logs for details$\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed."
    done:
FunctionEnd

# Running the ant scripts to create update the tool context paths
# Tool created depends on the value of $TOOL_SIG
Function runUpdateToolContext
    # Running the ant scripts to create deploy.xml for the normal tools 
    strcpy $0 '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile $INSTDIR\update-logs\ant-update-tool-context-$TOOL_SIG.log -buildfile $TEMP\lams\update-deploy-tools.xml -propertyfile $TEMP\lams\$TOOL_SIG\build.properties update-tool-context'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    push "$INSTDIR\update-logs\ant-update-tool-context-$TOOL_SIG.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error

    goto done
    error:
        DetailPrint "Ant update-tool-context failed, check update-logs for details"
        MessageBox MB_OK|MB_ICONSTOP "Ant update-tool-context failed, check update-logs for details$\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed."
    done:
FunctionEnd

# Deploying the updated tools
Function deployTool
    # Running the ant scripts to create deploy.xml for the normal tools 
    strcpy $0 '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile $INSTDIR\update-logs\ant-deploy-tool-$TOOL_SIG.log -buildfile $TEMP\lams\update-deploy-tools.xml -propertyfile $TEMP\lams\$TOOL_SIG\build.properties deploy-tool'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    DetailPrint "Result: $1"
    ${if} $0 == "fail"
    ${orif} $0 == 1
        goto error
    ${endif}
    push "$INSTDIR\update-logs\ant-deploy-tool-$TOOL_SIG.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    
    goto done
    error:
        DetailPrint "Ant deploy-tool failed, check update-logs for details"
        MessageBox MB_OK|MB_ICONSTOP "Ant deploy-tool failed, check update-logs for details$\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed."
    done:
FunctionEnd


# generates a properties file for all tools
Function generateToolProperties
        
    # generate a properties file 
    ClearErrors
    FileOpen $0 $TEMP\lams\tools.properties w
    IfErrors 0 +2
        goto error
        
    # convert '\' to '/' for Ant's benefit
    Push $TEMP
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "temp=$2/$\r$\n"
            
    Push $INSTDIR
    Push "\"
    Call StrSlash
    Pop $2
    
    FileWrite $0 "instdir=$2/$\r$\n"
    FileWrite $0 "basetooldir=$${temp}/lams/$${signature}$\r$\n"
    FileWrite $0 "build=$${basetooldir}/build/$\r$\n"
    FileWrite $0 "build.deploy=$${build}/deploy$\r$\n"
    FileWrite $0 "build.lib=$${build}/deploy/lib/$\r$\n"
    FileWrite $0 "db.scripts=$${build.deploy}/sql/$\r$\n"
    FileWrite $0 "db.name=$DB_NAME$\r$\n"
    FileWrite $0 "db.username=$DB_USER$\r$\n"
    FileWrite $0 "db.password=$DB_PASS$\r$\n"
    FileWrite $0 "db.Driver=com.mysql.jdbc.Driver$\r$\n"
    FileWrite $0 "db.urlDeployXML=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8&amp;zeroDateTimeBehavior=convertToNull&amp;autoReconnect=true&amp;useUnicode=true$\r$\n"
    FileWrite $0 "db.url=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=true$\r$\n"
    ;FileWrite $0 "db.url=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$${db.name}?characterEncoding=utf8$\r$\n"
    FileWrite $0 "conf.language.dir=$${build.deploy}/language/$\r$\n"
    FileWrite $0 "jboss.deploy=$${instdir}/jboss-4.0.2/server/default/deploy/lams.ear/$\r$\n"
    FileWrite $0 "deploy.tool.dir=$${temp}/lams/$\r$\n"
    FileWrite $0 "toolContext=/lams/tool/$${signature}$\r$\n"
    FileWrite $0 "product=lams-tool-$${signature}$\r$\n"
    
    goto done
    error:
        DetailPrint "File writing to $TEMP\lams\tools.properties failed."
        MessageBox MB_OK|MB_ICONSTOP "LAMS configuration failed.  File write error to $TEMP\lams\tools.properties.$\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed."
    done: 
        
FunctionEnd

# Creates tool packages for new tools so they can be deployed by the tool deployer
Function createNewToolPackages
    
    # Creating 2.3 tool packages
    # assessment, pixlr, mindmap, image gallery, video recorder 
    ############################################################################
    
    # Adding the assessment package -------------------------------------------
  
    strcpy $1 "$TEMP\lams\laasse10"
    
    SetoutPath "$1"
    File "${BASE_PROJECT_DIR}\lams_tool_assessment\build.properties"
    
    SetoutPath "$1\build\deploy\"
    File "${BASE_PROJECT_DIR}\lams_tool_assessment\build\lib\*.jar"
    File "${BASE_PROJECT_DIR}\lams_tool_assessment\build\lib\*.war"
    File "${BASE_PROJECT_DIR}\lams_tool_assessment\build\deploy\deploy.xml"
    
    SetoutPath "$1\build\deploy\sql"
    File /r "${BASE_PROJECT_DIR}\lams_tool_assessment\build\deploy\sql\*"
    
    SetoutPath "$1\build\deploy\language"
    File /r "${BASE_PROJECT_DIR}\lams_tool_assessment\build\deploy\language\*.properties"
    
    # --------------------------------------------------------------------------
    
    # Adding the pixlr package -------------------------------------------
  
    strcpy $1 "$TEMP\lams\lapixl10"
    
    SetoutPath "$1"
    File "${BASE_PROJECT_DIR}\lams_tool_pixlr\build.properties"
    
    SetoutPath "$1\build\deploy\"
    File "${BASE_PROJECT_DIR}\lams_tool_pixlr\build\lib\*.jar"
    File "${BASE_PROJECT_DIR}\lams_tool_pixlr\build\lib\*.war"
    File "${BASE_PROJECT_DIR}\lams_tool_pixlr\build\deploy\deploy.xml"
    
    SetoutPath "$1\build\deploy\sql"
    File /r "${BASE_PROJECT_DIR}\lams_tool_pixlr\build\deploy\sql\*"
    
    SetoutPath "$1\build\deploy\language"
    File /r "${BASE_PROJECT_DIR}\lams_tool_pixlr\build\deploy\language\*.properties"
    
    # --------------------------------------------------------------------------

    # Adding the mindmap package -------------------------------------------
  
    strcpy $1 "$TEMP\lams\lamind10"
    
    SetoutPath "$1"
    File "${BASE_PROJECT_DIR}\lams_tool_mindmap\build.properties"
    
    SetoutPath "$1\build\deploy\"
    File "${BASE_PROJECT_DIR}\lams_tool_mindmap\build\lib\*.jar"
    File "${BASE_PROJECT_DIR}\lams_tool_mindmap\build\lib\*.war"
    File "${BASE_PROJECT_DIR}\lams_tool_mindmap\build\deploy\deploy.xml"
    
    SetoutPath "$1\build\deploy\sql"
    File /r "${BASE_PROJECT_DIR}\lams_tool_mindmap\build\deploy\sql\*"
    
    SetoutPath "$1\build\deploy\language"
    File /r "${BASE_PROJECT_DIR}\lams_tool_mindmap\build\deploy\language\*.properties"
    
    # --------------------------------------------------------------------------
    
    # Adding the images package -------------------------------------------
  
    strcpy $1 "$TEMP\lams\laimag10"
    
    SetoutPath "$1"
    File "${BASE_PROJECT_DIR}\lams_tool_images\build.properties"
    
    SetoutPath "$1\build\deploy\"
    File "${BASE_PROJECT_DIR}\lams_tool_images\build\lib\*.jar"
    File "${BASE_PROJECT_DIR}\lams_tool_images\build\lib\*.war"
    File "${BASE_PROJECT_DIR}\lams_tool_images\build\deploy\deploy.xml"
    
    SetoutPath "$1\build\deploy\sql"
    File /r "${BASE_PROJECT_DIR}\lams_tool_images\build\deploy\sql\*"
    
    SetoutPath "$1\build\deploy\language"
    File /r "${BASE_PROJECT_DIR}\lams_tool_images\build\deploy\language\*.properties"
    
    # --------------------------------------------------------------------------
    
    # Adding the Video Recorder package -------------------------------------------
  
    strcpy $1 "$TEMP\lams\lavidr10"
    
    SetoutPath "$1"
    File "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build.properties"
    
    SetoutPath "$1\build\deploy\"
    File "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build\lib\*.jar"
    File "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build\lib\*.war"
    File "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build\deploy\deploy.xml"
    
    SetoutPath "$1\build\deploy\sql"
    File /r "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build\deploy\sql\*"
    
    SetoutPath "$1\build\deploy\language"
    File /r "${BASE_PROJECT_DIR}\lams_tool_videorecorder\build\deploy\language\*.properties"
    
    # --------------------------------------------------------------------------

    ############################################################################

FunctionEnd

Function insertCustomToolContexts

    # Running the ant scripts to update web.xmls and manifests
    strcpy $0 '"$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" -logfile "$INSTDIR\update-logs\ant-update-custom-tool-contexts.log" -buildfile "$TEMP\lams\update-deploy-tools.xml" -propertyfile $TEMP\lams\$TOOL_SIG\build.properties update-custom-tool-contexts'
    DetailPrint $0
    nsExec::ExecToStack $0
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    ${if} $0 == "error"
    ${orif} $0 == 1
        goto error
    ${endif}
    DetailPrint "Result: $1"
    
    push "$INSTDIR\update-logs\ant-update-custom-tool-contexts.log"
    push "FAILED"
    Call FileSearch
    Pop $0 #Number of times found throughout
    Pop $3 #Found at all? yes/no
    Pop $2 #Number of lines found in
    StrCmp $3 yes 0 +2
        goto error
    
    goto done
    error:
        DetailPrint "Web.xml update failed"
        MessageBox MB_OK|MB_ICONSTOP "Web.xml update failed, check update logs in the installation directory for details $\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed"
    done:

FunctionEnd

################################################################################
# END CODE USED FOR UPDATER                                                    #
################################################################################





################################################################################
# CODE USED FOR INSTALLER                                                      #
################################################################################    

Function DeployConfig
    # extract support files to write configuration
    SetOutPath $INSTDIR
    File /r "${BASE_DIR}\apache-ant-1.6.5"
    File /r "${BASE_DIR}\zip"
    SetOutPath $TEMP
    File "${ANT}\build.xml"
    File "${TEMPLATES}\mysql-ds.xml"
    File "${TEMPLATES}\server.xml"
    File "${TEMPLATES}\run.bat"
    File "${TEMPLATES}\wrapper.conf"
    File "${TEMPLATES}\index.html"
    File "${SQL}\update_lams_configuration.sql"
    File "${CONF}\log4j.xml"
      
    # create installer.properties
    ClearErrors
    FileOpen $0 $TEMP\installer.properties w
    IfErrors 0 +2
        goto error
        
    # convert '\' to '/' for Ant's benefit
    Push $TEMP
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "TEMP=$2$\r$\n"
            
    Push $INSTDIR
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "URL=http://$LAMS_DOMAIN:$LAMS_PORT/lams/$\r$\n"
    FileWrite $0 "INSTDIR=$2/$\r$\n"
    FileWrite $0 "TEMPDIR=$2/temp$\r$\n"
    FileWrite $0 "DUMPDIR=$2/dump$\r$\n"
    FileWrite $0 "EARDIR=$2/jboss-4.0.2/server/default/deploy/lams.ear$\r$\n"
    FileWrite $0 "DEPLOYDIR=$2/jboss-4.0.2/server/default/deploy$\r$\n"
    FileWrite $0 "TOMCATDIR=$2/jboss-4.0.2/server/default/deploy/jbossweb-tomcat55.sar$\r$\n"
    FileWrite $0 "BINDIR=$2/jboss-4.0.2/bin$\r$\n"
    
    # Use unix slashes for config in wrapper.conf
    Push $JDK_DIR
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "JDK_DIR_UNIX_SLASH=$2$\r$\n"
    
    Push $LAMS_REPOSITORY
    Push "\"
    Call StrSlash
    Pop $2
    FileWrite $0 "REPOSITORYDIR=$2$\r$\n"
        
    StrCpy $LAMS_LOCALE $LAMS_LOCALE 5
    FileWrite $0 "LOCALE=$LAMS_LOCALE$\r$\n"
    ${If} $LAMS_LOCALE == "ar_JO"
        FileWrite $0 "LOCALE_DIRECTION=RTL$\r$\n"
    ${Else}
        FileWrite $0 "LOCALE_DIRECTION=LTR$\r$\n"
    ${EndIf}
      
    FileWrite $0 "WILDFIRE_DOMAIN=$WILDFIRE_DOMAIN$\r$\n"
    FileWrite $0 "WILDFIRE_CONFERENCE=conference.$WILDFIRE_DOMAIN$\r$\n"
    FileWrite $0 "WILDFIRE_USER=$WILDFIRE_USER$\r$\n"
    FileWrite $0 "WILDFIRE_PASS=$WILDFIRE_PASS$\r$\n"
    
    FileWrite $0 "MYSQL_HOST=$MYSQL_HOST$\r$\n"
    FileWrite $0 "MYSQL_PORT=$MYSQL_PORT$\r$\n"
    FileWrite $0 "DB_NAME=$DB_NAME$\r$\n"
    FileWrite $0 "DB_USER=$DB_USER$\r$\n"
    FileWrite $0 "DB_PASS=$DB_PASS$\r$\n"
    FileWrite $0 "DICTIONARY_DATE=${LANGUAGE_PACK_VERSION}$\r$\n"
    FileWrite $0 "SERVER_VERSION=${SERVER_VERSION_NUMBER}$\r$\n"
    FileWrite $0 "LAMS_VERSION=${VERSION}$\r$\n"
    
    FileWrite $0 "LAMS_PORT=$LAMS_PORT$\r$\n"
    FileWrite $0 "LAMS_USER=$LAMS_USER$\r$\n"
    FileWrite $0 "LAMS_PASS=$LAMS_PASS$\r$\n"
    
    FileClose $0
    # for debugging purposes
    CopyFiles "$TEMP\installer.properties" $INSTDIR
        
    # use Ant to write config to files
    FileOpen $0 "$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" w
    IfErrors 0 +2
        goto error
    FileWrite $0 "@echo off$\r$\nset JAVACMD=$JDK_DIR\bin\java$\r$\n"
    FileClose $0
    ${FileJoin} "$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat" "$INSTDIR\apache-ant-1.6.5\bin\ant.bat" ""
    DetailPrint '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat configure-deploy'
    nsExec::ExecToStack '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat configure-deploy'
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    
    
    createdirectory "$INSTDIR\install-logs"
    FileOpen $R0 "$INSTDIR\install-logs\configure-deploy.log" w
    IfErrors 0 +2
        goto error
    FileWrite $R0 $1
    FileClose $R0
    
    ${If} $0 == "error"
        goto error
    ${EndIf}
    ${StrStr} $0 $1 "BUILD SUCCESSFUL"
    ${If} $0 == ""
        goto error
    ${EndIf}
    
    # write my.ini if exists, doesnt write READ-COMMITTED if its already written
    # TODO doesn't take effect until mysql server is restarted
    DetailPrint "Setting MySQL transaction-isolation to READ-COMMITTED"
    iffileexists "$MYSQL_DIR\my.ini" 0 difini
        push "$MYSQL_DIR\my.ini"
        push "transaction-isolation=READ-COMMITTED"
        Call FileSearch
        Pop $0 #Number of times found throughout
        Pop $3 #Found at all? yes/no
        Pop $2 #Number of lines found in
        intcmp $0 0 0 done done
            clearerrors
            ${LineFind} "$MYSQL_DIR\my.ini" "" "1" "WriteMyINI"
            IfErrors nomyini myini            
    difini:
    iffileexists "$WINDIR\my.ini" 0 nomyini
        push "$WINDIR\my.ini"
        push "transaction-isolation=READ-COMMITTED"
        Call FileSearch
        Pop $0 #Number of times found throughout
        Pop $3 #Found at all? yes/no
        Pop $2 #Number of lines found in
        intcmp $0 0 0 done done
            clearerrors
            ${LineFind} "$WINDIR\my.ini" "" "1" "WriteMyINI"
            IfErrors nomyini myini
    nomyini:
        MessageBox MB_OK|MB_ICONEXCLAMATION "Couldn't write to $MYSQL_DIR\my.ini.  Please write this text into your MySQL configuration file and restart MySQL:$\r$\n$\r$\n[mysqld]$\r$\ntransaction-isolation=READ-COMMITTED"       
        goto done
    myini:
        DetailPrint "MySQL will need to be restarted for this to take effect."
        goto done
    error:
        DetailPrint "Ant configure-deploy failed."
        MessageBox MB_OK|MB_ICONSTOP "LAMS configuration failed.  Please check your LAMS configuration and try again.$\r$\nError:$\r$\n$\r$\n$1"
        Abort "LAMS configuration failed."
    done:
FunctionEnd


Function WriteMyINI
    FileWrite $R4 "[mysqld]$\r$\n"
    FileWrite $R4 "transaction-isolation=READ-COMMITTED$\r$\n"
    Push $0
FunctionEnd

Function readRegistry
    ReadRegStr $INSTDIR HKLM "${REG_HEAD}" "dir_inst"
    ReadRegStr $DB_NAME HKLM "${REG_HEAD}" "db_name"
    ReadRegStr $DB_PASS HKLM "${REG_HEAD}" "db_pass"
    ReadRegStr $DB_USER HKLM "${REG_HEAD}" "db_user"
    ReadRegStr $JDK_DIR HKLM "${REG_HEAD}" "dir_jdk"
    ReadRegStr $MYSQL_DIR HKLM "${REG_HEAD}" "dir_mysql"
    ReadRegStr $LAMS_REPOSITORY HKLM "${REG_HEAD}" "dir_repository"
    ReadRegStr $LAMS_DOMAIN HKLM "${REG_HEAD}" "lams_domain"
    ReadRegStr $LAMS_LOCALE HKLM "${REG_HEAD}" "lams_locale"
    ReadRegStr $LAMS_PORT HKLM "${REG_HEAD}" "lams_port"
    #ReadRegStr $VERSION HKLM "${REG_HEAD}" "version"
    ReadRegStr $WILDFIRE_DOMAIN HKLM "${REG_HEAD}" "wildfire_domain"
    ReadRegStr $WILDFIRE_PASS HKLM "${REG_HEAD}" "wildfire_pass"
    ReadRegStr $WILDFIRE_USER HKLM "${REG_HEAD}" "wildfire_user"
    ReadRegStr $OLD_LANG_VERSION HKLM "${REG_HEAD}" "language_pack"
    
    ; Getting the mysql_ip from the registry 
    ReadRegStr $MYSQL_HOST HKLM "${REG_HEAD}" "mysql_host"
    ${if} $MYSQL_HOST == ""
        strcpy $MYSQL_HOST "localhost"
    ${endif}
    
    ; Getting the mysql port from the registry
    ReadRegStr $MYSQL_PORT HKLM "${REG_HEAD}" "mysql_port"
    ${if} $MYSQL_PORT == ""
        strcpy $MYSQL_PORT 3306
    ${endif}
FunctionEnd


Function ImportDatabase
    SetOutPath $TEMP
    File "${DATABASE}\dump.sql"
    File "${SQL}\removeConfigs.sql"
    
    # Only do this if mysql is set up on local host 
    #######################################################
    ${If} $MYSQL_HOST == 'localhost'
        # $9 == 0 for empty password
        StrLen $9 $MYSQL_ROOT_PASS
    
        StrCpy $0 '$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "CREATE DATABASE $DB_NAME DEFAULT CHARACTER SET utf8" -u root'
        DetailPrint $0
        ${If} $9 != 0
            StrCpy $0 '$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "CREATE DATABASE $DB_NAME DEFAULT CHARACTER SET utf8" -u root -p$MYSQL_ROOT_PASS' 
        ${EndIf}
        
        nsExec::ExecToStack $0
        Pop $0
        Pop $1
        ${If} $0 == 1
            goto error
        ${EndIf}
        
        StrCpy $0 `$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "GRANT ALL PRIVILEGES ON *.* TO $DB_USER@localhost IDENTIFIED BY '$DB_PASS'" -u root`
        DetailPrint $0
        ${If} $9 != 0
            StrCpy $0 `$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "GRANT ALL PRIVILEGES ON *.* TO $DB_USER@localhost IDENTIFIED BY '$DB_PASS'" -u root -p$MYSQL_ROOT_PASS`
        ${EndIf}
        nsExec::ExecToStack $0
        Pop $0
        Pop $1
        ${If} $0 == 1
            goto error
        ${EndIf}
        
        StrCpy $0 '$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "REVOKE PROCESS,SUPER ON *.* from $DB_USER@localhost" -u root'
        DetailPrint $0
        ${If} $9 != 0
            StrCpy $0 '$MYSQL_DIR\bin\mysql -h$MYSQL_HOST -P$MYSQL_PORT -e "REVOKE PROCESS,SUPER ON *.* from $DB_USER@localhost" -u root -p$MYSQL_ROOT_PASS' 
        ${EndIf}
        nsExec::ExecToStack $0
        Pop $0
        Pop $1
        ${If} $0 == 1
            goto error
        ${EndIf}
        
        StrCpy $0 '$MYSQL_DIR\bin\mysqladmin -h$MYSQL_HOST -P$MYSQL_PORT flush-privileges -u root'
        DetailPrint $0
        ${If} $9 != 0
            StrCpy $0 '$MYSQL_DIR\bin\mysqladmin -h$MYSQL_HOST -P$MYSQL_PORT flush-privileges -u root -p$MYSQL_ROOT_PASS' 
        ${EndIf}
        nsExec::ExecToStack $0
        Pop $0
        Pop $1
        ${If} $0 == 1
            goto error
        ${EndIf}
        
         ${if} $RETAIN_FILES == '1'      
            #unzip repository files
            setoutpath "$INSTDIR\backup"
            strcpy $4 '$INSTDIR\zip\7za.exe x -aoa "backup.zip"'
            nsExec::ExecToStack $4
            pop $5
            pop $6
            ${if} $5 != 0
                Detailprint  "7za Unzip error: $\r$\n$\r$\n$6"
                MessageBox MB_OK|MB_ICONSTOP "7za Unzip error:$\r$\nError:$\r$\n$\r$\n$6"
                Abort "Lams configuration failed"
            ${endif}
             
            #replace the install dump with the retained dump    
            #MessageBox MB_OK|MB_ICONEXCLAMATION "Rebuilding datbase"   
            CopyFiles "$INSTDIR\backup\lamsDump.sql" "$TEMP\dump.sql"     
            DetailPrint "Using retained database: $INSTDIR\backup\lamsDump.sql"
            
            RMdir /r  "$INSTDIR\backup\jboss-4.0.2"
            RMdir /r  "$INSTDIR\backup\repository"
            delete  "$INSTDIR\backup\lamsDump.sql"
            
            Detailprint  "finished copying lamsdump.sql"
         ${endif}
     ${endif}
     #######################################################
       
    SetOutPath $TEMP
    # use Ant to import database
    DetailPrint '$INSTDIR\apache-ant-1.6.5\bin\ant.bat -logfile "$INSTDIR\install-logs\import-db.log" import-db'
    nsExec::ExecToStack '$INSTDIR\apache-ant-1.6.5\bin\newAnt.bat -logfile "$INSTDIR\install-logs\import-db.log" import-db'
    # MessageBox MB_OK|MB_ICONEXCLAMATION "Database has been filled"
    Pop $0 ; return code, 0=success, error=fail
    Pop $1 ; console output
    
    ${if} $0 != 0
        goto error
    ${endif}

    goto done 
    error:
        MessageBox MB_OK|MB_ICONSTOP "Database setup failed.  Please check your MySQL configuration and try again.$\r$\nError:$\r$\n$\r$\n$1"
        Abort "Database setup failed."
    done:
    
FunctionEnd


Function WriteRegEntries
    WriteRegStr HKLM "${REG_HEAD}" "dir_jdk" $JDK_DIR
    WriteRegStr HKLM "${REG_HEAD}" "dir_mysql" "$MYSQL_DIR\"
    WriteRegStr HKLM "${REG_HEAD}" "mysql_host" "$MYSQL_HOST"
    WriteRegStr HKLM "${REG_HEAD}" "mysql_port" "$MYSQL_PORT"
    WriteRegStr HKLM "${REG_HEAD}" "dir_inst" $INSTDIR
    WriteRegStr HKLM "${REG_HEAD}" "dir_repository" $LAMS_REPOSITORY
    WriteRegStr HKLM "${REG_HEAD}" "version" "${VERSION}"
    WriteRegStr HKLM "${REG_HEAD}" "server_version" "${SERVER_VERSION_NUMBER}"
    WriteRegStr HKLM "${REG_HEAD}" "db_name" $DB_NAME
    WriteRegStr HKLM "${REG_HEAD}" "db_user" $DB_USER
    WriteRegStr HKLM "${REG_HEAD}" "db_pass" $DB_PASS
    WriteRegStr HKLM "${REG_HEAD}" "lams_domain" $LAMS_DOMAIN
    WriteRegStr HKLM "${REG_HEAD}" "lams_port" $LAMS_PORT
    WriteRegStr HKLM "${REG_HEAD}" "lams_locale" $LAMS_LOCALE
    WriteRegStr HKLM "${REG_HEAD}" "language_pack" ${LANGUAGE_PACK_VERSION_INT}
    WriteRegStr HKLM "${REG_HEAD}" "wildfire_domain" $WILDFIRE_DOMAIN
    WriteRegStr HKLM "${REG_HEAD}" "wildfire_user" $WILDFIRE_USER
    WriteRegStr HKLM "${REG_HEAD}" "wildfire_pass" $WILDFIRE_PASS
FunctionEnd

Function OverWriteRetainedFiles
    # overwriting retain files (moved to windows/temp) to install directory files
    ${if} $RETAIN_FILES == "1"
        #MessageBox MB_OK|MB_ICONSTOP "repository files to be retained"
        
        #unzip repository files
        setoutpath "$INSTDIR\backup"
        strcpy $4 '$INSTDIR\zip\7za.exe x -aoa "backup.zip"'
        nsExec::ExecToStack $4
        #pop $0
        #pop $1
        #MessageBox MB_OK|MB_ICONSTOP "Extracting retained files: $0$\n$1"
        
        #copy repository and uploaded files to install directory
        CopyFiles "$WINTEMP\lams\backup\repository" "$INSTDIR\" 
        DetailPrint "$INSTDIR\repository"
        CopyFiles "$WINTEMP\lams\backup\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war" "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\"
        DetailPrint "Overwrite $INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war"
    ${endif}
    RMdir "$WINTEMP\lams"
FunctionEnd

Function SetupStartMenu
    CreateDirectory "$SMPROGRAMS\LAMSv2"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Access LAMS.lnk" "http://$LAMS_DOMAIN:$LAMS_PORT/lams/"
    CreateShortCut "$SMPROGRAMS\LAMSv2\LAMS Community.lnk" "http://www.lamscommunity.org"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Start LAMS.lnk" "$INSTDIR\lams-start.exe"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Stop LAMS.lnk" "$INSTDIR\lams-stop.exe"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Backup LAMS.lnk" "$INSTDIR\lams-backup.exe"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Restore previous LAMS version.lnk" "$INSTDIR\lams-restore.exe"
    CreateShortCut "$SMPROGRAMS\LAMSv2\Uninstall LAMS.lnk" "$INSTDIR\lams-uninstall.exe"
FunctionEnd


# cleanup functions
Function RemoveTempFiles
    Delete "$TEMP\LocalPortScanner.class"
    Delete "$TEMP\mysql-ds.xml"
    Delete "$TEMP\server.xml"
    Delete "$TEMP\run.bat"
    Delete "$TEMP\wrapper.conf"
    Delete "$TEMP\dump.sql"
    Delete "$TEMP\build.xml"
    Delete "$TEMP\installer.properties"
    Delete "$INSTDIR\wrapper.conf"
    Delete "$TEMP\index.html"
    Delete "$TEMP\lamsauthentication.xml"
    Delete "$TEMP\update_lams_configuration.sql"
    Delete "$INSTDIR\update_lams_configuration.sql"
    RMDIR /r "$WINTEMP\lams"
FunctionEnd

Function .onInstFailed
    ${if} $IS_UPDATE == 0
        ;remove all files from the instdir excluding the backed up files
        RMDir /r "$INSTDIR\jboss-4.0.2"
        RMDir /r "$INSTDIR\dump"
        RMDir /r "$INSTDIR\repository"
        RMDir /r "$INSTDIR\temp"
        RMDir /r "$INSTDIR\build"
        Delete "$INSTDIR\index.html"
        Delete "$INSTDIR\installer.properties"
        Delete "$INSTDIR\lams-start.exe"
        Delete "$INSTDIR\lams-stop.exe"
        Delete "$INSTDIR\lams-backup.exe"
        Delete "$INSTDIR\lams-restore.exe"
        Delete "$INSTDIR\lams_uninstall.exe"
        Delete "$INSTDIR\license.txt"
        Delete "$INSTDIR\readme.txt"

        strcpy $0 $MYSQL_DIR
        strcpy $1 $DB_NAME
        strcpy $2 $DB_USER
        strcpy $3 $DB_PASS
    
        StrLen $9 $3
        StrCpy $4 '$0\bin\mysql -e "DROP DATABASE $1" -u $2'
        DetailPrint $4
        ${If} $9 != 0
            StrCpy $4 '$0\bin\mysql -e "DROP DATABASE $1" -u $2 -p$3' 
        ${EndIf}
        
        ReadRegStr $MYSQL_HOST HKLM "${REG_HEAD}" "mysql_host"
        ${If} $MYSQL_HOST == 'localhost'
            nsExec::ExecToStack $4
            Pop $0
            Pop $1
            ${If} $0 == 1
                MessageBox MB_OK|MB_ICONEXCLAMATION "Couldn't remove LAMS database:$\r$\n$\r$\n$1"
                DetailPrint "Failed to remove LAMS database."
            ${EndIf}
        ${EndIf}
        
        Call RemoveTempFiles
        DeleteRegKey HKLM "${REG_HEAD}"
     ${else}
        ; Do cleanup for failed update
        rmdir /r "$INSTDIR\apache-ant-1.6.5"
        RMDir /r "$INSTDIR\zip"
        RMDir /r "$EXEDIR\zip"
        RMDir /r "$EXEDIR\build"
        rmdir /r "$TEMP\installer.properties"
        rmdir /r "$TEMP\lams"
        ${if} $OLD_LANG_VERSION != ""
            WriteRegStr HKLM "${REG_HEAD}" "language_pack" $OLD_LANG_VERSION
        ${endif} 
        WriteRegStr HKLM "${REG_HEAD}" "version" ${PREVIOUS_VERSION}
        delete "$INSTDIR\LanguagePack.xml"

     ${endif}
FunctionEnd

Function .onInstSuccess
    
    DetailPrint "LAMS ${VERSION} configuration successful."
    ${if} $IS_UPDATE == 0
        Call RemoveTempFiles
        RMDir /r "$INSTDIR\apache-ant-1.6.5"
        RMDir /r "$INSTDIR\zip"
        RMDir /r "$INSTDIR\backup\repository"
        RMDIR /r "$INSTDIR\backup\jboss-4.0.2"
        rmdir /r "$TEMP\lams"
        Delete "$INSTDIR\backup\lamsDump.sql"
    ${else}
        ;cleanup for update successfull install
        rmdir /r "$INSTDIR\apache-ant-1.6.5"
        RMDir /r "$INSTDIR\zip"
        RMDir /r "$EXEDIR\zip"
        RMDir /r "$EXEDIR\build"
        rmdir /r "$TEMP\installer.properties"
        rmdir /r "$TEMP\lams"
    ${endif}
FunctionEnd

################################################################################
# END CODE USED FOR INSTALLER                                                  #
################################################################################

################################################################################
# CODE USED FOR LANGUAGE PACK                                                  #
################################################################################

; first, finds the location of the language files in the database
; then copy the required files to the dirname
Var CSllid
Var FSllid
Var RFllid
Function copyllid
    setoutpath "$TEMP\lams"
    File "${BUILD_DIR}\GetLlidFolderNames.class"
    
    strcpy $1 "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=true"
    ReadRegStr $3 HKLM "${REG_HEAD}" "dir_jdk"
    # execute llid finder
    Detailprint '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Chat and Scribe" "$1" "$DB_USER" "$DB_PASS"'
    nsExec::ExecToStack '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Chat and Scribe" "$1" "$DB_USER" "$DB_PASS"'
    pop $0
    pop $CSllid
    ${if} $0 != '0'
        Messagebox MB_OK|MB_ICONSTOP "Error while finding Chat and Scrbe llid folders"
        Abort
    ${endif}
    
    setoutpath "$TEMP\lams"
    File "${BUILD_DIR}\GetLlidFolderNames.class"
    
    strcpy $1 "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=true"
    ReadRegStr $3 HKLM "${REG_HEAD}" "dir_jdk"
    # execute llid finder
    Detailprint '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Forum and Scribe" "$1" "$DB_USER" "$DB_PASS"'
    nsExec::ExecToStack '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Forum and Scribe" "$1" "$DB_USER" "$DB_PASS"'
    pop $0
    pop $FSllid
    ${if} $0 != '0'
        Messagebox MB_OK|MB_ICONSTOP "Error while finding Forum and Scribe llid folders"
        Abort
    ${endif}
    
    strcpy $1 "jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$DB_NAME?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=true"
    ReadRegStr $3 HKLM "${REG_HEAD}" "dir_jdk"
    # execute llid finder
    Detailprint '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Resource and Forum" "$1" "$DB_USER" "$DB_PASS"'
    nsExec::ExecToStack '"$3\bin\java.exe" -cp ".;lib\mysql-connector-java-3.1.12-bin.jar" GetLlidFolderNames "Resources and Forum" "$1" "$DB_USER" "$DB_PASS"'
    pop $0
    pop $RFllid
    ${if} $0 != '0'
        Messagebox MB_OK|MB_ICONSTOP "Error while finding Resource and Forum llid folders"
        Abort
    ${endif}
    
    setoutpath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\org\lamsfoundation\lams\library\llid$CSllid"
    file /a "${BASE_PROJECT_DIR}\lams_build\librarypackages\chatscribe\language\lams\*"
    
    setoutpath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\org\lamsfoundation\lams\library\llid$FSllid"
    file /a "${BASE_PROJECT_DIR}\lams_build\librarypackages\forumscribe\language\lams\*"
    
    setoutpath "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\org\lamsfoundation\lams\library\llid$RFllid"
    file /a "${BASE_PROJECT_DIR}\lams_build\librarypackages\shareresourcesforum\language\lams\*"
    
FunctionEnd

################################################################################
# END CODE USED FOR LANGUAGE PACK                                              #
################################################################################


################################################################################
# CODE USED FOR UNINSTALLER                                                    #
################################################################################
# Uninstaller
#
Var UNINSTALL_RETAIN
#Var UNINSTALL_DB
#Var UNINSTALL_RP
#Var UNINSTALL_CF

Function un.onInit
    ;!insertmacro MUI_LANGDLL_DISPLAY
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "uninstall.ini"
    
    # check if LAMS is stopped
    SetOutPath $TEMP
    File "${BUILD_DIR}\LocalPortScanner.class"
    ReadRegStr $0 HKLM "${REG_HEAD}" "lams_port"
    ReadRegStr $1 HKLM "${REG_HEAD}" "dir_jdk"
    Goto checklams
    
    checklams:
        nsExec::ExecToStack "$1\bin\java LocalPortScanner $0"
        Pop $2
        ${If} $2 == 2
            MessageBox MB_YESNO|MB_ICONQUESTION "LAMS appears to be running.  Do you wish to stop LAMS?" \
                IDYES stoplams \
                IDNO quit
        ${EndIf}
        Goto continue
    stoplams:
        nsExec::ExecToStack 'sc stop LAMSv2'
        Pop $0
        Pop $1
        DetailPrint "Sent stop command to LAMS service."
        sleep 10000
        Goto checklams
        # sleep for 10s to ensure that JBoss closes properly
        
    quit:
        Delete "$TEMP\LocalPortScanner.class"
        MessageBox MB_OK|MB_ICONSTOP "Uninstall cannot continue while LAMS is running."
        Abort
    
    continue:
        Delete "$TEMP\LocalPortScanner.class"
FunctionEnd

Function un.PreUninstall
    !insertmacro MUI_HEADER_TEXT "Remove LAMS Database" "Choose whether to remove the LAMS database."
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY "uninstall.ini"
FunctionEnd

Function un.PostUninstall      
    !insertmacro MUI_INSTALLOPTIONS_READ $UNINSTALL_RETAIN "uninstall.ini" "Field 1" "State"
    ReadRegStr $MYSQL_HOST HKLM "${REG_HEAD}" "mysql_host"
    ${if} $UNINSTALL_RETAIN == 1
        ${if} $MYSQL_HOST != 'localhost'
            MessageBox MB_YESNO|MB_ICONQUESTION "Unable to backup LAMS. MYSQL_HOST is not set to localhost. Manual backup required. $\r$\n$\r$\nWould you like to quit the uninstaller and backup LAMS manually? If you choose 'No', the unistallation will proceed and lams will not be backed up." IDYES quit IDNO continue
                quit:
                    Quit
                continue:
        ${endif}
    ${endif}   
FunctionEnd


# http://nsis.sourceforge.net/StrStr
#
!define un.StrStr "!insertmacro un.StrStr"
 
!macro un.StrStr ResultVar String SubString
  Push `${String}`
  Push `${SubString}`
  Call un.StrStr
  Pop `${ResultVar}`
!macroend

Function un.StrStr
/*After this point:
  ------------------------------------------
  $R0 = SubString (input)
  $R1 = String (input)
  $R2 = SubStringLen (temp)
  $R3 = StrLen (temp)
  $R4 = StartCharPos (temp)
  $R5 = TempStr (temp)*/
 
  ;Get input from user
  Exch $R0
  Exch
  Exch $R1
  Push $R2
  Push $R3
  Push $R4
  Push $R5
 
  ;Get "String" and "SubString" length
  StrLen $R2 $R0
  StrLen $R3 $R1
  ;Start "StartCharPos" counter
  StrCpy $R4 0
 
  ;Loop until "SubString" is found or "String" reaches its end
  ${Do}
    ;Remove everything before and after the searched part ("TempStr")
    StrCpy $R5 $R1 $R2 $R4
 
    ;Compare "TempStr" with "SubString"
    ${IfThen} $R5 == $R0 ${|} ${ExitDo} ${|}
    ;If not "SubString", this could be "String"'s end
    ${IfThen} $R4 >= $R3 ${|} ${ExitDo} ${|}
    ;If not, continue the loop
    IntOp $R4 $R4 + 1
  ${Loop}
 
/*After this point:
  ------------------------------------------
  $R0 = ResultVar (output)*/
 
  ;Remove part before "SubString" on "String" (if there has one)
  StrCpy $R0 $R1 `` $R4
 
  ;Return output to user
  Pop $R5
  Pop $R4
  Pop $R3
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd


Section "Uninstall"

    strcpy $WINTEMP "C:\WINDOWS\Temp"
    RMDir /r "$WINTEMP\lams\backup"
    CreateDirectory "$WINTEMP\lams\backup"
       
    #strcpy $RETAIN_DIR "$INSTDIR\backup_$TIMESTAMP"
    strcpy $RETAIN_DIR "$INSTDIR\backup"
    CreateDirectory $RETAIN_DIR

    ReadRegStr $MYSQL_HOST HKLM "${REG_HEAD}" "mysql_host"
    ReadRegStr $MYSQL_PORT HKLM "${REG_HEAD}" "mysql_port"
    
    ;Now copy files that are to be retained to the temp folder
    ${If} $UNINSTALL_RETAIN == 1
        #MessageBox MB_OK|MB_ICONEXCLAMATION "retaining repository"
        ; KEEP repository and uploaded files
        ; Copy repository and jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war to backup
        ReadRegStr $6 HKLM "${REG_HEAD}" "dir_repository"
       
        CreateDirectory "$RETAIN_DIR\repository"
        CreateDirectory "$RETAIN_DIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
        
        #MessageBox MB_OK|MB_ICONEXCLAMATION "Copying files to $RETAIN_DIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
        copyfiles /silent $6 "$RETAIN_DIR"
        copyfiles /silent "$INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\*" "$RETAIN_DIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-www.war\"
        
        DetailPrint 'Saving repository and uploaded files to: $RETAIN_DIR'
        #MessageBox MB_OK|MB_ICONEXCLAMATION "Copying files from instdir to temp"
        copyfiles /silent "$RETAIN_DIR\*" "$WINTEMP\lams\backup"
    ${EndIf}
    
    setoutpath $temp
    RMdir /r "$INSTDIR\jboss-4.0.2\"
    RMdir /r "$INSTDIR\"
    #MessageBox MB_OK|MB_ICONEXCLAMATION "INSTDIR DELETED FFS!"
    
    ; NOT SURE IF THIS SECTION OF CODE IS NECCESSARY
    ReadRegStr $0 HKLM "${REG_HEAD}" "dir_conf"
    RMDir /r $0
    
    ; RESTORE Retained folders to their original localtion then delete temp files
    ; DUMP database into backup folder
    ReadRegStr $0 HKLM "${REG_HEAD}" "dir_mysql"
    ReadRegStr $1 HKLM "${REG_HEAD}" "db_name"
    ReadRegStr $2 HKLM "${REG_HEAD}" "db_user"
    ReadRegStr $3 HKLM "${REG_HEAD}" "db_pass"
    ${If} $UNINSTALL_RETAIN == 1
        #Messagebox MB_OK|MB_ICONEXCLAMATION "retaining db"
        ; DUMP the database file into the retained install directory 
           
        CreateDirectory "$RETAIN_DIR" 
        Strcpy $4 "$0\bin\mysqldump -r $WINTEMP\lams\backup\lamsDump.sql $1 -u $2 -p$3"
        nsExec::ExecToStack $4
        Pop $8
        Pop $9
        DetailPrint 'Dumping database to: $RETAIN_DIR'
        
        Setoutpath $INSTDIR
        File /r "${BASE_DIR}\zip"
        
        #ZIP UP ALL RETAINED FILES 
        IfFileExists "$WINTEMP\lams\backup\backup.zip" removeZip leaveFolder
        removeZip:
            delete "$WINTEMP\lams\backup\backup.zip"
        leaveFolder:
        
        SetOutPath "$WINTEMP\lams\backup"
        Strcpy $4 '$INSTDIR\zip\7za.exe a -r -tzip "$RETAIN_DIR\backup.zip" "*"'
        nsExec::ExecToStack $4 
        rmdir /r "$INSTDIR\zip"
        #pop $5
        #pop $6
        #MessageBox MB_OK|MB_ICONEXCLAMATION "ZIP RESULT: $5$\n$6$\n"
        rmdir $RETAIN_DIR
    ${EndIf}
    RMDir /r "$WINTEMP\lams"
    
    StrLen $9 $3
    StrCpy $4 '$0\bin\mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -e "DROP DATABASE $1" -u $2'
    DetailPrint $4
    ${If} $9 != 0
        StrCpy $4 '$0\bin\mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -e "DROP DATABASE $1" -u $2 -p$3' 
    ${EndIf}
    nsExec::ExecToStack $4
    Pop $0
    Pop $1
    ${If} $0 == 1
        MessageBox MB_OK|MB_ICONEXCLAMATION "Couldn't remove LAMS database:$\r$\n$\r$\n$1"
        DetailPrint "Failed to remove LAMS database."
    ${EndIf} 
    
    ; batch file doesn't want to work when called with ExecToStack
    ; nsExec::ExecToStack '$INSTDIR\jboss-4.0.2\bin\UninstallLAMS-NT.bat'
    nsExec::ExecToStack 'sc delete LAMSv2'
    Pop $0
    Pop $1
    ; can't call StrStr from within uninstaller unless it's a un. function
    ${un.StrStr} $2 $1 "SUCCESS"
    ${If} $2 == ""
        MessageBox MB_OK|MB_ICONEXCLAMATION "Couldn't remove LAMSv2 service.$\r$\n$\r$\n$1"
        DetailPrint "Failed to remove LAMSv2 service."
    ${Else}
        DetailPrint "Removed LAMSv2 service."
    ${EndIf}
    
    DeleteRegKey HKLM "${REG_HEAD}"
    DetailPrint "Removed registry entries."
    RMDir /r "$SMPROGRAMS\LAMSv2"
    DetailPrint "Removed start menu entries."
    DetailPrint "Uninstall complete."
SectionEnd

################################################################################
# END CODE USED FOR UNINSTALLER                                                #
################################################################################