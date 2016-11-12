


function Restore-Packages 
{

    #restore nuget packages for the solution
    .\.nuget\NuGet.exe restore
}


function Build-HawkServer 
{
    #run msbuild for the solution
    msbuild .\HawkEngine.sln  /t:Rebuild

}

function Build-HawkUI {

    #run the client ui build
        cd .\ui
         grunt clean 
         grunt build
        cd ..
 
    #deploy the ui assets in the serviced directory by the hawk HServer
    Copy-Item -Path .\ui\dist\* -Recurse -Destination .\hawk -Force


}

function Build-Installers{

    #go to installers folder
    cd .\Installers
    
    #build the installers
    msbuild .\CompileInstallers.proj

    cd..

}


function Main
{ 
     Restore-Packages

     Build-HawkServer 

     Build-HawkUI

     Build-Installers 
}